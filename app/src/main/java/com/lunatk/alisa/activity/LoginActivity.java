package com.lunatk.alisa.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lunatk.alisa.network.OPCode;
import com.lunatk.alisa.network.RequestManager;
import com.lunatk.alisa.util.Utils;
import com.lunatk.alisa.R;

/**
 * Created by LunaTK on 2018. 1. 18..
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private EditText et_id, et_pw;
    public Handler mainHandler;
    Dialog progress;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG," onCreate");
        setContentView(R.layout.activity_login);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progress.cancel();

                if(msg.what == OPCode.REQ_LOGIN && msg.arg1 !=OPCode.ERR){
                    Utils.setLoginInfo(editor, et_id.getText().toString(), et_pw.getText().toString());
                    Utils.setSessionId(editor, msg.arg1);

                    Toast.makeText(LoginActivity.this, "Login Success in Activity",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Utils.removeLoginInfo(editor);
                    Toast.makeText(LoginActivity.this, "Login Failed in Activity",Toast.LENGTH_SHORT).show();
                }
            }
        };

        progress = new Dialog(this, R.style.ProgressDialog);
        progress.setCancelable(false);
        progress.addContentView(new ProgressBar(this), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if(autoLogin()) login(null);

    }

    private boolean autoLogin(){
        String id=null,pw=null;
        id = sharedPreferences.getString("user_id",null);
        pw = sharedPreferences.getString("user_pass",null);
        if(id!=null && pw!=null){ //자동로그인 가능
            et_id.setText(id);
            et_pw.setText(pw);
            return true;
        } else {
            return false;
        }
    }

    public void login(View v){
        Log.d(TAG,"login");
        progress.show();
        RequestManager.requestLogin(mainHandler, et_id.getText().toString(), et_pw.getText().toString());
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
