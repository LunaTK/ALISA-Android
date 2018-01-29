package com.lunatk.alisa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lunatk.alisa.network.NetworkThread;
import com.lunatk.alisa.network.OPCode;
import com.lunatk.mybluetooth.R;

/**
 * Created by LunaTK on 2018. 1. 18..
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText et_id, et_pw;
    public Handler mainHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG," onCreate");
        setContentView(R.layout.activity_login);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);

        getSupportActionBar().setTitle("Login");

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == OPCode.OK){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        NetworkThread.getInstance().start();
    }

    public void login(View v){
        NetworkThread.requestLogin(mainHandler,et_id.getText().toString(), et_pw.getText().toString());
    }

    public void register(View v){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivityForResult(intent, RegisterActivity.ACTIVITY_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RegisterActivity.ACTIVITY_CODE){
            if(resultCode == OPCode.OK){
                Toast.makeText(this, "회원가입 성공", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show();
            }
        }
    }
}