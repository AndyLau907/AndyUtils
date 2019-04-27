package com.andy.lib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.andy.lib.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 坐标轴绘图
 * Created by andy on 2019/4/26.
 */
public class CoordinateView extends View {

    private final static String TAG = "CoordinateView";
    private Context mContext;
    //文件路径
    private String pointFilePath;
    //伸缩比例
    private float scale;
    //X轴最大值
    private int maxX;
    //y轴最大值
    private int maxY;
    //坐标点集合
    private ArrayList<Point> pointsList;
    //view宽
    private int viewWidth;
    //view高
    private int viewHeight;
    //x坐标轴长度
    private int width;
    //y坐标轴长度
    private int height;
    //线条颜色
    private int lineColor;
    //坐标轴颜色
    private int coordinateColor;
    //坐标点颜色
    private int pointColor;
    //坐标轴线条宽度
    private float coordinateWidth = 3.0f;
    //箭头单倍宽
    private float triangleHalfWidth = 20.0f;

    public CoordinateView(Context context) {
        this(context, null);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CoordinateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.CoordinateView);

        pointFilePath = ta.getString(R.styleable.CoordinateView_file_path);
        lineColor = ta.getColor(R.styleable.CoordinateView_coordinate_line_color, Color.GREEN);
        coordinateColor = ta.getColor(R.styleable.CoordinateView_coordinate_color, Color.BLACK);
        pointColor = ta.getColor(R.styleable.CoordinateView_point_color, Color.RED);

        pointsList = new ArrayList<>();
        initPointFromAssets();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        viewHeight = getHeight();
        viewWidth = getWidth();
        height = (int) (viewHeight - coordinateWidth - triangleHalfWidth);
        width = (int) (viewWidth - coordinateWidth - triangleHalfWidth);
        Log.e(TAG,viewHeight+","+viewWidth);
        initMaxAndScale();
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize((viewHeight>viewWidth?viewHeight:viewWidth)/27);
        textPaint.setColor(pointColor);
        String xStr=(int)(maxX)+"";
        String yStr=(int)(maxY)+"";
        canvas.drawText("0",0,viewHeight-triangleHalfWidth/2,textPaint);
        canvas.drawText(xStr,width-triangleHalfWidth*2,height-triangleHalfWidth*2,textPaint);
        canvas.drawText(yStr,triangleHalfWidth*2,triangleHalfWidth*1.5f,textPaint);
        //坐标轴画笔
        Paint coordinatePaint = new Paint();
        coordinatePaint.setColor(coordinateColor);
        coordinatePaint.setStyle(Paint.Style.FILL);
        coordinatePaint.setStrokeWidth(coordinateWidth);
        coordinatePaint.setAntiAlias(true);
        canvas.drawLine(triangleHalfWidth, viewHeight - triangleHalfWidth, viewWidth, viewHeight - triangleHalfWidth, coordinatePaint);
        canvas.drawLine(triangleHalfWidth, viewHeight - triangleHalfWidth, triangleHalfWidth, 0, coordinatePaint);
        //变细画笔 画出箭头
        coordinatePaint.setStrokeWidth(2.0f);
        coordinatePaint.setStyle(Paint.Style.STROKE);
        Path yPath = new Path();
        yPath.moveTo(triangleHalfWidth, 0);
        yPath.lineTo(0, triangleHalfWidth);
        yPath.moveTo(triangleHalfWidth, 0);
        yPath.lineTo(triangleHalfWidth * 2, triangleHalfWidth);
        yPath.close();
        canvas.drawPath(yPath, coordinatePaint);
        //x箭头
        Path xPath = new Path();
        xPath.moveTo(viewWidth, viewHeight - triangleHalfWidth);
        xPath.lineTo(viewWidth - triangleHalfWidth, viewHeight);
        xPath.moveTo(viewWidth, viewHeight - triangleHalfWidth);
        xPath.lineTo(viewWidth - triangleHalfWidth, viewHeight - triangleHalfWidth * 2);
        xPath.close();
        canvas.drawPath(xPath, coordinatePaint);

        //描点和连线
        Paint pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setStrokeWidth(6.0f);
        pointPaint.setAntiAlias(true);
        Path pointPath = new Path();
        //Log.e(TAG, "size=" + pointsList.size());

        for (int i = 0; i < pointsList.size(); i++) {
            Point point = pointsList.get(i);
            Log.e(TAG, "(" + point.x + "," + point.y + ")");
            if (i == 0) {
                pointPath.moveTo(point.x, point.y);
            }else{
                pointPath.lineTo(point.x, point.y);
            }
            canvas.drawCircle(point.x, point.y, 6.0f, pointPaint);
        }
        Paint pointLinePaint = new Paint();
        pointLinePaint.setAntiAlias(true);
        pointLinePaint.setStyle(Paint.Style.STROKE);
        pointLinePaint.setStrokeWidth(4.0f);
        pointLinePaint.setColor(lineColor);
        canvas.drawPath(pointPath, pointLinePaint);
    }

    /**
     * 初始化横纵坐标轴最大值和伸缩比例
     */
    private void initMaxAndScale() {
        float mX = -1f, mY = -1f;
        for (Point point : pointsList) {
            if (mX < point.x) {
                mX = point.x;
            }
            if (mY < point.y) {
                mY = point.y;
            }
        }
        //坐标轴各留出100的空间
        float scaleX = (width - 100) / mX;
        float scaleY = (height - 100) / mY;

        scale = scaleX < scaleY ? scaleX : scaleY;
        //最大坐标刻度
        maxX = (int) (100 / scale + mX);
        maxY = (int) (100 / scale + mY);
        //重新按比例设置点坐标 相对于view
        for (Point point : pointsList) {
            point.x = point.x * scale + coordinateWidth + triangleHalfWidth;
            point.y = height - point.y * scale;
        }
    }

    /**
     * 从文件读取数据
     */
    private void initPointFromAssets() {
        if (pointFilePath == null || pointFilePath.isEmpty()) {
            return;
        }

        try {
            InputStreamReader inputReader = new InputStreamReader(getResources().getAssets().open(pointFilePath));
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            while ((line = bufReader.readLine()) != null) {
                Log.e(TAG, line);
                String temp[] = line.split(",");

                if (temp.length!=2) {
                    throw new RuntimeException("文件数据格式有误！");
                }
                float px = Float.valueOf(temp[0]);
                float py = Float.valueOf(temp[1]);
                pointsList.add(new Point(px, py));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 坐标点
     */
    class Point {
        float x;
        float y;

        Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

}
