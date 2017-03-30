package com.monsoonblessing.kevinfaust.smartparkowner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserRegistrationActivity extends AppCompatActivity {


    private static final String TAG = "RegisterActivity";

    @BindView(R.id.activity_register)
    LinearLayout rootView;
    @BindView(R.id.email_field)
    EditText emailField;
    @BindView(R.id.password_field)
    EditText passwordField;
    @BindView(R.id.confirm_password_field)
    EditText confirmPasswordField;

    private ProgressDialog mProgressDialog;

    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }


    @OnClick(R.id.register_btn)
    void OnRegister() {
        startRegister();
    }


    private void startRegister() {

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirm_password = confirmPasswordField.getText().toString().trim();

        if (hasValidFrields(email, password, confirm_password)) {

            mProgressDialog.setMessage("Setting your account up");
            mProgressDialog.show();

            // log user in
            mFirebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    Log.d(TAG, "account creation completed");

                    if (task.isSuccessful()) {
                        Log.d(TAG, "account creation completed successfully");

                        mProgressDialog.dismiss();
                        // user logged in
                        Intent loginIntent = new Intent(UserRegistrationActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                        finish();
                    } else {
                        showShortSnackbar(rootView, "User with this email already exists");
                        mProgressDialog.dismiss();
                    }

                }
            });
        }
    }


    public boolean hasValidFrields(String email, String password, String confirm_password) {

        boolean hasValidFields = false;

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirm_password)) {

            // make sure password is at least 5 characters
            if (password.length() > 5) {

                if (password.equals(confirm_password)) {

                    // we passed all the validation checks
                    hasValidFields = true;

                } else {
                    showShortSnackbar(rootView, "Passwords not matching");
                }
            } else {
                showShortSnackbar(rootView, "Password must be at least 5 characters");
            }
        } else {
            showShortSnackbar(rootView, "Please fill out all fields");
        }

        return hasValidFields;
    }


    public void showShortSnackbar(View root, String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_SHORT).show();
    }


}



