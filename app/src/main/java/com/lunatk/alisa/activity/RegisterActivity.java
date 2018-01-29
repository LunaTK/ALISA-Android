package com.lunatk.alisa.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.lunatk.alisa.network.NetworkThread;
import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 1. 19..
 */

public class RegisterActivity extends AppCompatActivity {

    public static final int ACTIVITY_CODE = 0x01;
    public static Handler mainHandler;
    EditText et_id, et_pw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);

        getSupportActionBar().setTitle("Register");

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setResult(msg.what);
                finish();
            }
        };

    }

    public void register(View v){
        NetworkThread.requestRegister(mainHandler, et_id.getText().toString(), et_pw.getText().toString());
    }
}
