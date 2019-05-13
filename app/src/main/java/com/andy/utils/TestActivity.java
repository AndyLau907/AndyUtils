package com.andy.utils;


import android.view.View;
import android.widget.Button;

import com.andy.lib.Base.BaseActivity;
import com.andy.lib.View.CoordinateView;

/**
 * Created by andy on 2019/4/27.
 */
public class TestActivity extends BaseActivity {
    CoordinateView coordinateView;
    Button ssBtn,previousBtn,nextBtn,ascBtn,descBtn;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        super.initViews();

        coordinateView = findViewById(R.id.coordinateVIew);
        ssBtn=findViewById(R.id.start_btn);
        previousBtn=findViewById(R.id.previous_btn);
        nextBtn=findViewById(R.id.next_btn);
        ascBtn=findViewById(R.id.asc_btn);
        descBtn=findViewById(R.id.desc_btn);

        if(coordinateView.isAuto()){
            ssBtn.setText("暂停");
        }else{
            ssBtn.setText("开始");
        }
        coordinateView.setListener(new CoordinateView.DataChangeListener() {
            @Override
            public void onDataEnd() {
                ssBtn.setText("开始");
            }

            @Override
            public void onDateChanged(int groupId) {

            }

        });
    }

    @Override
    protected void initClick() {
        super.initClick();
        ssBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(coordinateView.isAuto()){
                    ssBtn.setText("开始");
                    coordinateView.pauseAuto();
                }else{
                    ssBtn.setText("暂停");
                    coordinateView.startAuto();
                }

            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinateView.previousGroup();
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinateView.nextGroup();
            }
        });

        ascBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinateView.ascScale();
            }
        });

        descBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                coordinateView.descScale();
            }
        });
    }
}
