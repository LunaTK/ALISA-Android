package com.lunatk.alisa.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.lunatk.alisa.network.RequestManager;
import com.lunatk.alisa.R;

import java.util.concurrent.TimeUnit;

/**
 * Created by LunaTK on 2018. 1. 19..
 */

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    public static final int ACTIVITY_CODE = 0x01;
    public static Handler mainHandler;
    private EditText et_id, et_pw, et_phone;

    private String mVerificationId;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        et_id = findViewById(R.id.et_id);
        et_pw = findViewById(R.id.et_pw);
        et_phone = findViewById(R.id.et_phone);

        mainHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                setResult(msg.arg1);
                finish();
            }
        };

        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ko-kr");

        boolean arr[][] = new boolean[12][22];

    }

    public void register(View v){
        RequestManager.requestRegister(mainHandler, et_id.getText().toString(), et_pw.getText().toString());
    }

    public void sendVerificationSMS(View v){
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+82"+et_phone.getText().toString(), 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG,"onVerificationCompleted : " + phoneAuthCredential.getProvider() + ", " + phoneAuthCredential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d(TAG,"onVerificationFailed : " + e.getMessage());
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Toast.makeText(RegisterActivity.this,"SMS sent : "+s,Toast.LENGTH_SHORT).show();
                Log.d(TAG,"verificationId : " + s);
                mVerificationId = s;
            }
        });
    }

    public void authSMS(View v){
        String code = ((EditText)findViewById(R.id.et_code)).getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        Log.d(TAG, " " + credential.getSmsCode() + ", " + credential.getProvider());

        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            Toast.makeText(RegisterActivity.this,"성공",Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();
                            // ...
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(RegisterActivity.this,"실패",Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

}
