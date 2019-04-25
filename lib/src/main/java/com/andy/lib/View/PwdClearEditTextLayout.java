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
 * 密码输入框 自带清除按钮和显示密码按钮
 * Created by andy on 2018/12/25.
 */

public class PwdClearEditTextLayout extends LinearLayout {
    //输入框
    private EditText editText;
    //右侧删除按钮
    private ImageView clearImage;
    //下划线
    private View line;
    //显示密码按钮
    private ImageView pwdImage;
    //内层linearlayout
    private LinearLayout layout;
    //是否第一次测量，避免重复添加子view
    private boolean isOnceMeasure = true;
    //edittext id
    @SuppressWarnings("ResourceType")
    public final static int textId = 4;
    //imgview id
    @SuppressWarnings("ResourceType")
    public final static int imgviewId = 5;
    //line id
    @SuppressWarnings("ResourceType")
    public final static int lineId = 6;
    //pwdimg id
    @SuppressWarnings("ResourceType")
    public final static int pwdimgId = 7;
    //密码是否可见
    private boolean isPwdVisible = false;

    public PwdClearEditTextLayout(Context context) {
        this(context, null);
    }

    public PwdClearEditTextLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PwdClearEditTextLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a =context.obtainStyledAttributes(attrs,R.styleable.PwdClearEditTextLayout);
        String hint = a.getString(R.styleable.PwdClearEditTextLayout_pwd_text_hint);
        int textColor = a.getColor(R.styleable.PwdClearEditTextLayout_pwd_text_color,getResources().getColor(R.color.black));
        boolean singleLine = a.getBoolean(R.styleable.PwdClearEditTextLayout_pwd_single_line,true);
        int textHintColor=a.getColor(R.styleable.PwdClearEditTextLayout_pwd_text_hint_color,getResources().getColor(R.color.black));
        int textGravity=a.getInteger(R.styleable.PwdClearEditTextLayout_pwd_text_gravity,Gravity.CENTER_VERTICAL);
        int textLeftPadding=a.getDimensionPixelSize(R.styleable.PwdClearEditTextLayout_pwd_text_left_padding,DensityUtil.dip2px(getContext(), 10));
        int textRightPadding=a.getDimensionPixelSize(R.styleable.PwdClearEditTextLayout_pwd_text_right_padding,DensityUtil.dip2px(getContext(), 10));
        int textTopPadding=a.getDimensionPixelSize(R.styleable.PwdClearEditTextLayout_pwd_text_top_padding,0);
        int textBottomPadding=a.getDimensionPixelSize(R.styleable.PwdClearEditTextLayout_pwd_text_bottom_padding,0);
        int lineColor = a.getColor(R.styleable.PwdClearEditTextLayout_pwd_line_color,getResources().getColor(R.color.loginEditTextLine));
//        int inputType = a.getInteger(R.styleable.PwdClearEditTextLayout_input_type,InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        boolean isPwdVisable=a.getBoolean(R.styleable.PwdClearEditTextLayout_pwd_is_pwd_visible,false);

        editText = new EditText(getContext());
        editText.setBackground(null);
        editText.setSingleLine(singleLine);
        editText.setTextColor(textColor);
        editText.setHint(hint);
        editText.setHintTextColor(textHintColor);
        editText.setGravity(textGravity);
        //设置输入类型为字符型密码 设置后输入内容会被替换为*来显示
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setId(textId);
        editText.setPadding(textLeftPadding, textTopPadding,textRightPadding, textBottomPadding);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //当输入框内容为空的时候 同时隐藏删除按钮和密码显示按钮
                String text = s.toString();
                if (text.isEmpty()) {
                    clearImage.setVisibility(GONE);
                    pwdImage.setVisibility(GONE);
                } else {
                    clearImage.setVisibility(VISIBLE);
                    pwdImage.setVisibility(VISIBLE);
                }
            }
        });

        clearImage = new ImageView(getContext());
        clearImage.setImageResource(R.mipmap.login_clear);
        clearImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                clearImage.setVisibility(GONE);
            }
        });
        clearImage.setVisibility(GONE);
        clearImage.setId(imgviewId);


        line = new View(getContext());
        line.setBackgroundColor(lineColor);
        line.setId(lineId);

        layout = new LinearLayout(getContext());
        layout.setOrientation(HORIZONTAL);
        layout.setWeightSum(1.0f);
        layout.setGravity(Gravity.CENTER);
        //初始化密码显示按钮
        pwdImage = new ImageView(getContext());
        pwdImage.setVisibility(GONE);
        pwdImage.setId(pwdimgId);
        //初始的时候 按钮的图片设置为“密码隐藏”的图片 即闭眼图片
        if(!isPwdVisable){
            pwdImage.setImageResource(R.mipmap.pwd_invisible);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }else{
            pwdImage.setImageResource(R.mipmap.pwd_visible);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }
        pwdImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //发生点击事件后 如果当前密码是可见的
                if (isPwdVisible) {
                    //那么点击后 设置成不可见的
                    pwdImage.setImageResource(R.mipmap.pwd_invisible);
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                } else {
                    //反之 则设置成可见的
                    //设置按钮图片为睁眼的图片
                    pwdImage.setImageResource(R.mipmap.pwd_visible);
                    //设置输入内容的类型为可见的字符密码类型
                    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                }
                //切换输入内容的显示类型之后 输入光标会重置到内容开头
                //所以再设置一下点击后的光标  将位置设置到输入内容的最后
                editText.setSelection(editText.getText().toString().length());
                //将密码是否可见标志取反
                isPwdVisible = !isPwdVisible;
            }
        });

        this.setOrientation(VERTICAL);
        this.setWeightSum(1.0f);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isOnceMeasure) {
            LayoutParams textLp = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            LayoutParams imgLp = new LayoutParams(DensityUtil.dip2px(getContext(), 25), DensityUtil.dip2px(getContext(), 25));
            LayoutParams layoutLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
            LayoutParams lineLp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 1));
            LayoutParams imgLp1 = new LayoutParams(DensityUtil.dip2px(getContext(), 25), DensityUtil.dip2px(getContext(), 25));
            textLp.weight = 1.0f;
            layout.addView(editText, textLp);
            imgLp.setMarginStart(DensityUtil.dip2px(getContext(), 5));
            layout.addView(clearImage, imgLp);
            imgLp1.setMarginStart(DensityUtil.dip2px(getContext(), 5));
            imgLp1.setMarginEnd(DensityUtil.dip2px(getContext(), 5));
            layout.addView(pwdImage, imgLp1);
            layoutLp.weight = 1.0f;
            this.addView(layout, layoutLp);
            this.addView(line, lineLp);

            isOnceMeasure = false;
        }

        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    public String getText() {
        return editText.getText().toString();
    }

    public void setText(String str) {
        editText.setText(str);
    }

    public void setHint(String hint){
        editText.setHint(hint);
    }
}
