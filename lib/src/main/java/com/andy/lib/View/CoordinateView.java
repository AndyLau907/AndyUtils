package com.andy.lib.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.andy.lib.R;

import java.util.ArrayList;

/**
 * 坐标轴绘图
 * Created by andy on 2019/4/26.
 */
public class CoordinateView extends View {

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
    //线条颜色
    private int lineColor;
    //坐标轴颜色
    private int coordinateColor;
    //坐标点颜色
    private int pointColor;

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
        lineColor = ta.getColor(R.styleable.CoordinateView_line_color, Color.GREEN);
        coordinateColor = ta.getColor(R.styleable.CoordinateView_coordinate_color, Color.BLACK);
        pointColor = ta.getColor(R.styleable.CoordinateView_point_color, Color.RED);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
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
