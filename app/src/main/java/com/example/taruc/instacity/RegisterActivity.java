package com.example.taruc.instacity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private EditText email,password,confirmPassword;
    private Button register;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.registerPassword);
        confirmPassword = (EditText) findViewById(R.id.registerConfirmPassword);
        register = (Button) findViewById(R.id.register);
        loadingBar = new ProgressDialog(this);

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                CreateNewAccount();
            }
        });
    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            SendUserToMainActivity();
        }
    }

    private void CreateNewAccount() {
        String UserEmail=email.getText().toString();
        String UserPassword = password.getText().toString();
        String UserConfirmPassword = confirmPassword.getText().toString();

        if(TextUtils.isEmpty(UserEmail)){
            Toast.makeText(this,"Please enter your email...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(UserPassword)){
            Toast.makeText(this,"Please enter your password...",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(UserConfirmPassword)){
            Toast.makeText(this,"Please enter your confirm password...",Toast.LENGTH_SHORT).show();
        }else if(!UserPassword.equals(UserConfirmPassword)){
            Toast.makeText(this,"Your password do not match with confirm password...",Toast.LENGTH_SHORT).show();
        }
        else{
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating you new account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(UserEmail,UserPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                SendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this,"You are authenticated successfully...",Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this,"Error Occur :"+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(this,feed.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
