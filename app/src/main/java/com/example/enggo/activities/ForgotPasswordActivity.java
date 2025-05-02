package com.example.enggo.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enggo.R;
import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.RegisterRequest;
import com.example.enggo.data.VerifyRegisterRequest;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.service.UserApiService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextView textViewLogin;
    private Button buttonChangePassword;
    private EditText editTextUsername;
    private EditText editTextNewPassword;
    private EditText editTextRePassword;
    private EditText editTextOtp;
    private Button buttonSendOtp;
    private EditText editTextEmail;
    private UserApiService userApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.forgotPasswordLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userApiService = RetrofitClient.getInstance().create(UserApiService.class);

        mapping();
        setTextViewLogin();
        setUpButtonChangePassword();
        setUpButtonSendOtp();
    }

    private void mapping()
    {
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        editTextOtp = findViewById(R.id.editTextOtp);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonSendOtp = findViewById(R.id.buttonSendOtp);
        editTextEmail = findViewById(R.id.editTextEmail);
        textViewLogin = findViewById(R.id.textViewLogin);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                    v.clearFocus();
                    hideKeyboard(v);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void setTextViewLogin()
    {
        String fullText = "Đã tìm được mật khẩu? Đăng nhập";
        SpannableString spannable = new SpannableString(fullText);

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#828282")),
                0, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                22, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#000000"));
            }
        };

        spannable.setSpan(clickableSpan, 22, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewLogin.setText(spannable);
        textViewLogin.setMovementMethod(LinkMovementMethod.getInstance());
        textViewLogin.setHighlightColor(Color.TRANSPARENT);
    }

    private void setUpButtonChangePassword()
    {
        buttonChangePassword.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Chưa nhập tài khoản!");
            }
            else if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Chưa nhập Email!");
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Email không đúng định dạng!");
            }
            else if (editTextOtp.getVisibility() != View.VISIBLE)
            {
                editTextUsername.setEnabled(false);
                editTextEmail.setEnabled(false);

                Call<ApiResponse> call = userApiService.forgetPassword(username, email);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công. Otp đã gửi đến Email của bạn.", Toast.LENGTH_SHORT).show();
                            editTextNewPassword.setVisibility(View.VISIBLE);
                            editTextRePassword.setVisibility(View.VISIBLE);
                            editTextOtp.setVisibility(View.VISIBLE);
                            buttonSendOtp.setVisibility(View.VISIBLE);
                        } else {
                            String errorMessage = "Đổi mật khẩu thất bại!";
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject errorObj = new JSONObject(response.errorBody().string());
                                    errorMessage = "Đổi mật khẩu thất bại! " + errorObj.optString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            editTextUsername.setEnabled(true);
                            editTextEmail.setEnabled(true);
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        editTextUsername.setEnabled(true);
                        editTextEmail.setEnabled(true);
                    }
                });
            }
            else
            {
                String newPassword = editTextNewPassword.getText().toString().trim();
                String rePassword = editTextRePassword.getText().toString().trim();
                if (TextUtils.isEmpty(newPassword)) {
                    editTextNewPassword.setError("Chưa nhập mật khẩu!");
                }
                else if (newPassword.length() < 8) {
                    editTextNewPassword.setError("Mật khẩu phải có ít nhất 8 ký tự!");
                }
                else if (!newPassword.equals(rePassword)) {
                    editTextRePassword.setError("Mật khẩu nhập lại không khớp!");
                }
                else
                {
                    editTextNewPassword.setEnabled(false);
                    editTextRePassword.setEnabled(false);

                    String otp = editTextOtp.getText().toString().trim();
                    if (TextUtils.isEmpty(otp))
                    {
                        editTextOtp.setError("Chưa nhập Otp!");
                    }
                    else
                    {
                        Call<ApiResponse> call = userApiService.verifyForgetPassword(username, newPassword, email, otp);
                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this, "Đổi mật khẩu thành công.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));

                                } else {
                                    String errorMessage = "Đổi mật khẩu thất bại!";
                                    try {
                                        if (response.errorBody() != null) {
                                            JSONObject errorObj = new JSONObject(response.errorBody().string());
                                            errorMessage = "Đổi mật khẩu thất bại! " + errorObj.optString("message");
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("LoginError", "Đổi mật khẩu thất bại: " + t.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private void setUpButtonSendOtp()
    {
        buttonSendOtp.setOnClickListener(v -> {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", editTextEmail.getText().toString().trim());

            userApiService.sendOtp(requestBody).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Map<String, Object> body = response.body();
                        if (body != null && "success".equals(body.get("status"))) {
                            Toast.makeText(ForgotPasswordActivity.this, "OTP đã gửi thành công.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = "Gửi OTP thất bại!";
                            if (body != null && body.get("message") != null) {
                                message = String.valueOf(body.get("message"));
                            }
                            Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = "Lỗi gửi OTP!";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                errorMessage = "Lỗi gửi OTP: " + errorObj.optString("message", errorMessage);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(ForgotPasswordActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}