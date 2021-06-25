package com.hackathon.recumeet.UserAuth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.hackathon.recumeet.MainActivity;
import com.hackathon.recumeet.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText emailEdit, passEdit;
    String email, password;
    CardView directRegTv;
    TextView loginBtn;
    private static ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        Init();

        loginBtn.setOnClickListener(v -> {
            showSimpleProgressDialog(LoginActivity.this, "Loading", "Logging In", false);
            email = emailEdit.getText().toString();
            password = passEdit.getText().toString();
            if(email.length() == 0 || !isEmailValid(email)){
                Toast.makeText(LoginActivity.this, "Authentication failed : Invalid Email Id", Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();
            } else if(password.length() == 0){
                Toast.makeText(LoginActivity.this, "Authentication failed : Invalid Password", Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();
            } else {
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        removeSimpleProgressDialog();
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        removeSimpleProgressDialog();
                    }
                });
            }
        });

        directRegTv.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public static boolean isEmailValid(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    public static void showSimpleProgressDialog(Context context, String title, String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (Exception ie) {
            ie.printStackTrace();
        }
    }

    private void Init() {
        mAuth = FirebaseAuth.getInstance();
        emailEdit = findViewById(R.id.email_login);
        passEdit = findViewById(R.id.pass_login);
        directRegTv = findViewById(R.id.direct_reg);
        loginBtn = findViewById(R.id.btn_login);
    }
}