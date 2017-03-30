package com.monsoonblessing.kevinfaust.smartparkowner;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.monsoonblessing.kevinfaust.smartparkowner.Popups.InternetRequiredPopup;
import com.monsoonblessing.kevinfaust.smartparkowner.Popups.PasswordRequestPopup;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/*
A login control activity
--> Users without internet will have the app close
--> Users already logged in will go directly to the main app
 */
public class UserLoginActivity extends AppCompatActivity {

    @BindView(R.id.activity_login)
    LinearLayout rootView;
    @BindView(R.id.email_field)
    EditText emailField;
    @BindView(R.id.password_field)
    EditText passwordField;

    private FirebaseAuth mAuth;

    private ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        mProgressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null)
            launchMainApp(); // user is logged in so send them to main app
    }


    @OnClick(R.id.register_btn)
    void onRegister() {
        if (hasAppPrerequisites())
            startActivity(new Intent(UserLoginActivity.this, UserRegistrationActivity.class));
    }


    @OnClick(R.id.login_btn)
    void onLogin() {
        if (hasAppPrerequisites()) startLogin();
    }


    @OnClick(R.id.forgot_pass_btn)
    void onForgot() {
        if (hasAppPrerequisites()) {
            PasswordRequestPopup d = new PasswordRequestPopup();
            d.show(getSupportFragmentManager(), "PasswordRequestPopup");
        }
    }


    public boolean hasAppPrerequisites() {
        /*
        Checks if app has internet in order to use app (registering/logging in)
         */

        boolean hasAppPrerequisites = false;

        // check permission

        // check if we are connected to the internet
        if (InternetConnectivityUtils.isConnectedToInternet(this)) {
            hasAppPrerequisites = true;
        } else {
            // tell user they need internet to login and continue to app
            new InternetRequiredPopup().newInstance().show(getSupportFragmentManager(), "Internet Required Popup");
        }


        return hasAppPrerequisites;
    }


    public void startLogin() {
        /*
        Logs user in and goes to main app
         */

        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

            mProgressDialog.setMessage("Logging in");
            mProgressDialog.show();

            // log user in
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        // user logged in
                        launchMainApp();

                    } else {
                        Snackbar.make(rootView, "Error logging in", Snackbar.LENGTH_SHORT).show();
                    }

                    mProgressDialog.dismiss();

                }
            });

        } else {
            Snackbar.make(rootView, "Please fill out all fields", Snackbar.LENGTH_SHORT).show();
        }
    }


    private void launchMainApp() {
        /*
        Goes from login screen to main activity
         */
        Intent loginIntent = new Intent(UserLoginActivity.this, MainActivity.class);
        startActivity(loginIntent);
        finish();
    }
}
