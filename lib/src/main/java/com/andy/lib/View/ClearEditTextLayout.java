package com.andy.lib.View;


import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.andy.lib.R;
import com.andy.lib.Util.DensityUtil;


/**
 * 用户名输入框 自带清除按钮
 * Created by andy on 2018/12/25.
 */

public class ClearEditTextLayout extends LinearLayout {
    //输入框
    private EditText editText;
    //右侧删除按钮
    private ImageView clearImage;
    //下划线
    private View line;
    //内层linearlayout
    private LinearLayout layout;
    //是否第一次测量，避免重复添加子view
    private boolean isOnceMeasure = true;
    //edittext id
    @SuppressWarnings("ResourceType")
    public final static int textId = 1;
    //imgview id
    @SuppressWarnings("ResourceType")
    public final static int imgviewId = 2;
    //line id
    @SuppressWarnings("ResourceType")
    public final static int lineId = 3;

    public ClearEditTextLayout(Context context) {
        this(context, null);
    }

    public ClearEditTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a =context.obtainStyledAttributes(attrs,R.styleable.ClearEditTextLayout);
        String hint = a.getString(R.styleable.ClearEditTextLayout_text_hint);
        int textColor = a.getColor(R.styleable.ClearEditTextLayout_text_color,getResources().getColor(R.color.black));
        boolean singleLine = a.getBoolean(R.styleable.ClearEditTextLayout_single_line,true);
        int textHintColor=a.getColor(R.styleable.ClearEditTextLayout_text_hint_color,getResources().getColor(R.color.black));
        int textGravity=a.getInteger(R.styleable.ClearEditTextLayout_text_gravity,Gravity.CENTER_VERTICAL);
        int textLeftPadding=a.getDimensionPixelSize(R.styleable.ClearEditTextLayout_text_left_padding,DensityUtil.dip2px(getContext(), 10));
        int textRightPadding=a.getDimensionPixelSize(R.styleable.ClearEditTextLayout_text_right_padding,DensityUtil.dip2px(getContext(), 10));
        int textTopPadding=a.getDimensionPixelSize(R.styleable.ClearEditTextLayout_text_top_padding,0);
        int textBottomPadding=a.getDimensionPixelSize(R.styleable.ClearEditTextLayout_text_bottom_padding,0);
        int lineColor = a.getColor(R.styleable.ClearEditTextLayout_line_color,getResources().getColor(R.color.loginEditTextLine));
        int inputType = a.getInteger(R.styleable.ClearEditTextLayout_input_type,InputType.TYPE_CLASS_TEXT);
        //初始化输入框
        editText = new EditText(getContext());
        //设置背景为空
        editText.setBackground(null);
        //设置内容单行显示
        editText.setSingleLine(singleLine);
        //设置字体颜色
        editText.setTextColor(textColor);
        //默认输入框内容
        editText.setHint(hint);
        //设置默认内容字体颜色
        editText.setHintTextColor(textHintColor);
        //设置输入框内容排列
        editText.setGravity(textGravity);
        //设置输入框内容类型
        editText.setInputType(inputType);
        //设置输入框id
        editText.setId(textId);
        //设置输入框内边距
        editText.setPadding(textLeftPadding, textTopPadding, textRightPadding, textBottomPadding);
        //设置输入内容改变监听事件
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //当内容发生变化后 如果内容为空 那就不用显示右侧删除按钮
                //内容不为空 则显示右侧删除按钮
                String text = s.toString();
                if (text.isEmpty()) {
                    clearImage.setVisibility(GONE);
                } else {
                    clearImage.setVisibility(VISIBLE);
                }
            }
        });
        //初始化删除按钮
        clearImage = new ImageView(getContext());
        //设置删除按钮图片
        clearImage.setImageResource(R.mipmap.login_clear);
        //设置删除按钮点击事件 点击后把输入框内容置空
        //随后隐藏删除按钮
        clearImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                clearImage.setVisibility(GONE);
            }
        });
        //设置删除按钮初始时默认不显示
        clearImage.setVisibility(GONE);
        //设置删除按钮id
        clearImage.setId(imgviewId);

        //初始化下划线
        line = new View(getContext());
        //设置下划线背景色
        line.setBackgroundColor(lineColor);
        //设置下划线id
        line.setId(lineId);

        //初始化内层layout布局
        layout = new LinearLayout(getContext());
        //设置排列方式为横向
        layout.setOrientation(HORIZONTAL);
        //设置总的宽度比重为1
        layout.setWeightSum(1.0f);

        //设置本体layout布局排列方式为纵向
        this.setOrientation(VERTICAL);
        //设置总的高度比重为1
        this.setWeightSum(1.0f);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //如果是第一次测量 才进行下列操作 避免多次测量导致的重复添加子view
        if (isOnceMeasure) {
            //各个子view的参数对象
            LayoutParams textLp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            LayoutParams imgLp = new LayoutParams(DensityUtil.dip2px(getContext(), 25), DensityUtil.dip2px(getContext(), 25));
            LayoutParams layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            LayoutParams lineLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
            //设置输入框所占宽度比重为1
            textLp.weight = 1.0f;
            //在内层layout中添加输入框
            layout.addView(editText, textLp);
            //设置内层布局的内容居中
            layout.setGravity(Gravity.CENTER);
            //设置删除按钮的左右外间距
            imgLp.setMarginStart(DensityUtil.dip2px(getContext(), 5));
            imgLp.setMarginEnd(DensityUtil.dip2px(getContext(), 5));
            //添加删除按钮
            layout.addView(clearImage, imgLp);
            //设置内层layout的高度占比为1
            layoutLp.weight = 1.0f;
            //添加内层layout
            this.addView(layout, layoutLp);
            //添加下划线
            this.addView(line, lineLp);
            //设置不是第一次测量
            isOnceMeasure = false;
        }

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    /**
     * 从输入框中得到其中内容
     * @return
     */
    public String getText() {
        return editText.getText().toString();
    }

    /**
     * 设置输入框内容
     * @param str
     */
    public void setText(String str) {
        editText.setText(str);
    }

    public void setHint(String hint){
        editText.setHint(hint);
    }
}
