package com.andy.lib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
    //当前X轴伸缩比例
    private float scaleX;
    //当前Y轴伸缩比例
    private float scaleY;
    //X轴最大值
    private float maxX;
    //y轴最大值
    private float maxY;
    //初始X轴最大值
    private int defMaxX;
    //初始Y轴最大值
    private int defMaxY;
    //坐标点集合(原始数据坐标)
    private ArrayList<Point> dataPointList;
    //坐标点集合(view坐标)
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
    //坐标原点
    private Point zeroPoint;
    //每一组要展示的点的数量
    private int groupPoints;
    //单次伸缩比例
    private float onceScale;
    //当前绘制截止的点的索引
    private int index = 0;
    //箭头末端点距离坐标轴默认距离
    //箭头末端点距离坐标轴距离
    private float distance = 20f;
    //重绘时间间隔
    private long delay;
    //是否自动切换数据组
    private boolean isAuto;
    //监听器
    private DataEndListener listener;
    //文字大小默认比例
    private float defTextSize=18f/1100;
    //文字大小比例
    private float textSize;
    //X轴文字左偏移刻度距离默认比例
    private float defTextX=25f/1100f;
    //X轴文字左偏移距离
    private float textX;
    //Y轴文字上偏移刻度距离默认比例
    private float defTextY=5f/1100f;
    //Y轴文字上偏移距离
    private float textY;
    //x轴文字距离X轴下方距离默认比例
    private float defDistanceX=30f/1100f;
    //x轴文字距离X轴下方距离
    private float distanceX;
    //y轴文字距离y轴左方距离默认比例
    private float defDistanceY=60f/1100f;
    //y轴文字距离y轴左方距离
    private float distanceY;
    //刻度长度默认比例
    private float defLength=15f/1100f;
    //刻度长度
    private float length;


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
        defMaxY = ta.getInteger(R.styleable.CoordinateView_default_ymax, 20);
        defMaxX = ta.getInteger(R.styleable.CoordinateView_default_xmax, 20);
        groupPoints = ta.getInteger(R.styleable.CoordinateView_group_points, 7);
        onceScale = ta.getFloat(R.styleable.CoordinateView_once_scale, 0.1f);
        delay = ta.getInteger(R.styleable.CoordinateView_redraw_time, 1000);
        isAuto = ta.getBoolean(R.styleable.CoordinateView_is_Auto, true);

        maxX = defMaxX;
        maxY = defMaxY;
        //distance=defDistance;
        //初始化原点坐标
        zeroPoint = new Point(0, 0);
        //初始化数据
        dataPointList = new ArrayList<>();
        initPointFromAssets();
        if(isAuto){
            startAuto();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        viewHeight = getHeight();
        viewWidth = getWidth();
        height = viewHeight;
        width = viewWidth;

        textSize=defTextSize*Math.max(height,width);
        textX=defTextX*width;
        textY=defTextY*height;
        distanceX=defDistanceX*width;
        distanceY=defDistanceY*height;
        length=defLength*Math.max(height,width);

        Log.e(TAG,textSize+","+textX+","+textY+","+distanceX+","+distanceY+","+length);

        zeroPoint.x = width / 2;
        zeroPoint.y = height / 2;

        Log.e(TAG, viewHeight + "," + viewWidth);
        initScale();
        doChange();

        drawCoordinate(canvas);
        drawPointAndLine(canvas);
        drawText(canvas);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0){
                invalidate();
            }else{
                listener.onDataEnd();
            }

        }
    };

    /**
     * 画刻度和坐标值
     * @param canvas
     */
    private void drawText(Canvas canvas){
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(textSize);
        textPaint.setColor(pointColor);

        Paint linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(4.0f);
        linePaint.setColor(pointColor);

        //画X轴刻度
        float startX=-maxX+maxX/10;
        for(int i=0;i<=18;i++){
            if(i!=9){
                canvas.drawLine(zeroPoint.x+startX*scaleX,zeroPoint.y,zeroPoint.x+startX*scaleX,zeroPoint.y-length,linePaint);
                canvas.drawText(startX+"",zeroPoint.x+startX*scaleX-textX,zeroPoint.y+distanceX,textPaint);
            }
            startX+=maxX/10;
        }
        //画Y轴刻度
        float startViewY=maxY-maxY/10;
        float startY=-maxY+maxY/10;
        for(int i=0;i<=18;i++){
            if(i!=9){
                canvas.drawLine(zeroPoint.x,startViewY*scaleY+zeroPoint.y,zeroPoint.x+length,startViewY*scaleY+zeroPoint.y,linePaint);
                canvas.drawText(startY+"",zeroPoint.x-distanceY,zeroPoint.y+startViewY*scaleY-textY,textPaint);
            }
            startViewY-=maxY/10;
            startY+=maxY/10;
        }
    }
    /**
     * 画坐标轴
     *
     * @param canvas
     */
    private void drawCoordinate(Canvas canvas) {
        Paint coordinatePaint = new Paint();
        coordinatePaint.setAntiAlias(true);
        coordinatePaint.setStyle(Paint.Style.STROKE);
        coordinatePaint.setColor(coordinateColor);
        coordinatePaint.setStrokeWidth(6.0f);
        //x轴
        canvas.drawLine(0, zeroPoint.y, width, zeroPoint.y, coordinatePaint);
        //y轴
        canvas.drawLine(zeroPoint.x, 0, zeroPoint.x, height, coordinatePaint);
        //x轴箭头
        canvas.drawLine(width, zeroPoint.y, width - distance, zeroPoint.y - distance, coordinatePaint);
        //y轴箭头
        canvas.drawLine(zeroPoint.x, 0, zeroPoint.x + distance, distance, coordinatePaint);
    }

    /**
     * 描点和连线
     *
     * @param canvas
     */
    private void drawPointAndLine(Canvas canvas) {
        Paint pointPaint = new Paint();
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setColor(pointColor);
        pointPaint.setStrokeWidth(6.0f);
        pointPaint.setAntiAlias(true);
        //Path pointPath = new Path();
        //Log.e(TAG, "size=" + pointsList.size());
        Paint pointLinePaint = new Paint();
        pointLinePaint.setAntiAlias(true);
        pointLinePaint.setStyle(Paint.Style.STROKE);
        pointLinePaint.setStrokeWidth(4.0f);
        pointLinePaint.setColor(lineColor);

        for (int i = index; i < index + 6; i++) {
            Point point1 = pointsList.get(i);
            Point point2 = pointsList.get(i+1);
            canvas.drawLine(point1.x,point1.y,point2.x,point2.y,pointLinePaint);
            //Log.e(TAG,"point1:("+point1.x+","+point1.y+")"+",point2:("+point2.x+","+point2.y+")");
            if(i+1==index+6){
                canvas.drawCircle(point2.x, point2.y, 6.0f, pointPaint);
            }
            canvas.drawCircle(point1.x, point1.y, 6.0f, pointPaint);
        }
    }

    /**
     * 初始化伸缩比例
     */
    private void initScale() {
        scaleX = width / (maxX * 2);
        scaleY = height / (maxY * 2);
    }

    /**
     * 将原始数据坐标 转换为view的坐标
     */
    private void doChange() {
        pointsList = new ArrayList<>();
        for (Point point : dataPointList) {
            float x = zeroPoint.x + point.x * scaleX;
            float y = zeroPoint.y - point.y * scaleY;
            Point p = new Point(x, y);
            pointsList.add(p);
        }
    }

    /**
     * 放大
     */
    public void ascScale() {
        float x = maxX - onceScale * defMaxX;
        float y = maxY - onceScale * defMaxY;
        maxX = x <= 0 ? maxX : x;
        maxY = y <= 0 ? maxY : y;
    }

    /**
     * 缩小
     */
    public void descScale() {
        maxX = defMaxX * onceScale + maxX;
        maxY = defMaxY * onceScale + maxY;
    }

    /**
     * 显示下一组数据
     */
    public void nextGroup() {
        if (index + 7 < dataPointList.size()) {
            index += 7;
            invalidate();
        } else {
            Toast.makeText(getContext(), "已经是最后一组数据啦~", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始自动切换数据组
     */
    public void startAuto() {
        isAuto = true;
        //初始化循环子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (isAuto) {
                    if(index+7>=dataPointList.size()){
                        Toast.makeText(getContext(),"所有数据组已经显示完毕啦~",Toast.LENGTH_LONG).show();
                        isAuto=false;
                        handler.sendEmptyMessage(1);
                        break;
                    }
                    index += 7;
                    handler.sendEmptyMessage(0);
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 暂停自动切换数据组
     */
    public void pauseAuto(){
        isAuto=false;
    }
    /**
     * 显示上一组数据
     */
    public void previousGroup() {
        if (index - 7 >= 0) {
            index -= 7;
            invalidate();
        } else {
            Toast.makeText(getContext(), "已经是第一组数据啦~", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取是否自动切换
     * @return
     */
    public boolean isAuto(){
        return isAuto;
    }

    /**
     * 返回当前数据组编号
     *
     * @return
     */
    public int getGroupIndex() {
        return index % 7 + 1;
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
            StringBuilder builder = new StringBuilder();
            String data = "";
            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }
            data = builder.toString();
            if (data.isEmpty()) {
                Toast.makeText(getContext(), "数据文件为空！", Toast.LENGTH_SHORT).show();
            }
            data = data.replace("[", " ");
            data = data.replace("]", " ");

            String groups[] = data.split("\\s+");
            for (int i = 1; i < groups.length; i += 2) {
                float x = Float.valueOf(groups[i]);
                float y = Float.valueOf(groups[i + 1]);
                Point point = new Point(x, y);
                dataPointList.add(point);
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


    public interface DataEndListener{
        void onDataEnd();
    }


    public void setListener(DataEndListener listener){
        this.listener=listener;
    }
}
