package com.example.project.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.project.FirstFragment;
import com.example.project.R;
import com.example.project.Welcome_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class SignupActivity extends AppCompatActivity  {
    private LoginViewModel loginViewModel;
    TextInputLayout user;
    TextInputLayout password;
    TextInputLayout phone;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText phoneEdittext;
    Button loginButton;
    ProgressBar loadingProgressBar;
    Button button;
    private String Id;
    private FirebaseAuth mAuth;
    EditText t1;
    EditText t2;
    EditText t3;
    EditText t4;
    EditText t5;
    EditText t6;
    private LayoutInflater inflater;
    private View view;
    AlertDialog.Builder builder;
    AlertDialog damn;
    Button positive_btn,negative_btn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        loginViewModel = ViewModelProviders.of(this, new LoginViewModelFactory()).get(LoginViewModel.class);
        user = findViewById(R.id.textInputLayout3);
        password = findViewById(R.id.textInputLayout5);
        phone = findViewById(R.id.textInputLayout8);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        phoneEdittext = findViewById(R.id.Phone);
        loginButton = findViewById(R.id.login);
        Id = null;
        loadingProgressBar = findViewById(R.id.loading);
        button = findViewById(R.id.new_to_welcome);
        inflater = getLayoutInflater();
        view = inflater.inflate(R.layout.activity_otp,null);
        t1 = view.findViewById(R.id.otp_edit_box1);
        t2 = view.findViewById(R.id.otp_edit_box2);
        t3 = view.findViewById(R.id.otp_edit_box3);
        t4 = view.findViewById(R.id.otp_edit_box4);
        t5 = view.findViewById(R.id.otp_edit_box5);
        t6 = view.findViewById(R.id.otp_edit_box6);
        mAuth = FirebaseAuth.getInstance();
        button.setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, Welcome_page.class));
            finish();
        });
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
//                usernameEditText.setError(getString(loginFormState.getUsernameError()));
//                user.setError(getString(loginFormState.getUsernameError()));

            }
            if (loginFormState.getPasswordError() != null) {
//                passwordEditText.setError(getString(loginFormState.getPasswordError()));
//                password.setError(getString(loginFormState.getPasswordError()));
            }

        });

        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (usernameEditText.getText().toString().trim().isEmpty()) {
                    usernameEditText.requestFocus();
                    user.setError("Invalid Name");
                    loginButton.setEnabled(false);
                } else {
                    usernameEditText.requestFocus();
                    loginButton.setEnabled(true);
                    user.setError(null);
                }
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (passwordEditText.getText().toString().length() >= 10) {
                    passwordEditText.requestFocus();
                    loginButton.setEnabled(true);
                    password.setError(null);
                } else {
                    passwordEditText.requestFocus();
                    loginButton.setEnabled(false);
                    password.setError("At least 10 characters");
                }
            }
        });
        phoneEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*String text = phoneEdittext.getText().toString().trim();
                if (text.equalsIgnoreCase("+91")) {
                    phoneEdittext.setText("");
                } else {
                    if (!text.startsWith("+91") && text.length() > 0) {
                        phoneEdittext.setText("+91 " + s.toString());
                        Selection.setSelection(phoneEdittext.getText(), phoneEdittext.getText().length());
                    }
                }



                 */
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phoneEdittext.getText().toString().trim().length() == 10) {
                    phoneEdittext.requestFocus();
                    loginButton.setEnabled(true);
                    phone.setError(null);
                } else {
                    phoneEdittext.requestFocus();
                    phone.setError("Invalid Number");
                    loginButton.setEnabled(false);
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getError() != null) {
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null) {
                updateUiWithUser(loginResult.getSuccess());
            }
            setResult(Activity.RESULT_OK);
            //Complete and destroy login activity once successful
            finish();
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());

            }
        };
        loginButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(usernameEditText.getText().toString())) {
                user.setError("Empty Field");
            }
            if (TextUtils.isEmpty(passwordEditText.getText().toString())) {
                password.setError("Empty Field");
            }
            if (TextUtils.isEmpty(phoneEdittext.getText().toString())) {
                phone.setError("Empty Field");
            }
            if (!(usernameEditText.getText().toString().trim().isEmpty()) && (passwordEditText.getText().toString().trim().length() >= 10) && (phoneEdittext.getText().toString().trim().length() == 10)) {
                builder = new AlertDialog.Builder(this);
                builder.setView(view);
                builder.setTitle("Enter OTP");
                builder.setCancelable(false);
                t1.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().trim().isEmpty()){
                            t2.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                t2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().trim().isEmpty()){
                            t3.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                t3.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().trim().isEmpty()){
                            t4.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                t4.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().trim().isEmpty()){
                            t5.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                t5.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(!s.toString().trim().isEmpty()){
                            t6.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" +
                                phoneEdittext.getText().toString(),
                        60,
                        TimeUnit.SECONDS,
                        SignupActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                Id = verificationId;
                            }
                        });
                builder.setPositiveButton("Check", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setNegativeButton("Resend OTP", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
            }
            AlertDialog damn = builder.create();
            damn.show();
            damn.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!t1.getText().toString().trim().isEmpty() &&
                            !t2.getText().toString().trim().isEmpty() &&
                            !t3.getText().toString().trim().isEmpty() &&
                            !t4.getText().toString().trim().isEmpty() &&
                            !t5.getText().toString().trim().isEmpty() &&
                            !t6.getText().toString().trim().isEmpty()){
                        String code = t1.getText().toString().trim() +
                                t2.getText().toString().trim() +
                                t3.getText().toString().trim() +
                                t4.getText().toString().trim() +
                                t5.getText().toString().trim() +
                                t6.getText().toString().trim();
                        if(Id != null){
                            PhoneAuthCredential phoneAuthCredential =  PhoneAuthProvider.getCredential(
                                    Id,
                                    code
                            );
                            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(
                                    task -> {
                                        if(task.isSuccessful()){
                                            Intent intent = new Intent(SignupActivity.this,Welcome_page.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                        }
                                        else{
                                            Toast.makeText(SignupActivity.this,"Invalid Code",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                        }
                    }
                    else{
                        Toast.makeText(SignupActivity.this,"Please Enter Code",Toast.LENGTH_SHORT).show();
                    }
                }
            });
            damn.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" +
                                    phoneEdittext.getText().toString(),
                            60,
                            TimeUnit.SECONDS,
                            SignupActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    Id = verificationId;
                                }
                            });
                }
            });
//           loginViewModel.login(usernameEditText.getText().toString(),passwordEditText.getText().toString());
//           loadingProgressBar.setVisibility(View.VISIBLE);
        });
        /*usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });*/


    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + " " + model.getDisplayName();
        // TODO : initiate successful logged in experience
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }
    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}