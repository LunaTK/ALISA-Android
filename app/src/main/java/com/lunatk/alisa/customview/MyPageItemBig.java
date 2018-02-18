package com.lunatk.alisa.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 2. 12..
 */

public class MyPageItemBig extends FrameLayout {

    private View view;
    private ImageView iv_background;
    private TextView tv_title, tv_desc;
    private String title, desc;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(title);
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
        tv_desc.setText(desc);
    }

    public MyPageItemBig(Context context) {
        super(context);
        initView();
    }

    public MyPageItemBig(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.MyPageItemBig);
        setTitle(ta.getString(R.styleable.MyPageItemBig_mpi_title));
        setDesc(ta.getString(R.styleable.MyPageItemBig_mpi_description));
    }

    private void initView() {
        view = inflate(getContext(), R.layout.layout_mypage_item_big, null);
        iv_background = view.findViewById(R.id.iv_background);
        tv_title = view.findViewById(R.id.tv_title);
        tv_desc = view.findViewById(R.id.tv_desc);
        iv_background.setVisibility(INVISIBLE);
        addView(view);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("MyPageItem", "motionEvent : " + event.getAction());
                if(MyPageItemBig.this.isClickable()) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            iv_background.setVisibility(VISIBLE);
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            iv_background.setVisibility(INVISIBLE);
                            break;
                    }
                }
                return false;
            }
        });
    }

}
