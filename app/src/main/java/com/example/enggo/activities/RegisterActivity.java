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

import com.example.enggo.MainActivity;
import com.example.enggo.R;
import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.RegisterRequest;
import com.example.enggo.data.VerifyRegisterRequest;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.models.User;
import com.example.enggo.service.UserApiService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private TextView textViewLogin;
    private TextView textViewTermsAndPrivacy;
    private Button buttonRegister;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextRePassword;
    private EditText editTextOtp;
    private Button buttonSendOtp;
    private EditText editTextEmail;
    private UserApiService userApiService;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userApiService = RetrofitClient.getInstance().create(UserApiService.class);

        mapping();
        setupTextViewLogin();
        setupTextViewTermsAndPrivacy();
        setUpButtonRegister();
        setUpButtonSendOtp();
        setUpAnonymous();
    }

    private void setUpAnonymous()
    {
        user = (User)getIntent().getSerializableExtra("user");
        if (user != null)
        {
            setUpButtonUpgradeAnonymous();
            setUpButtonSendOtpUpgrade();
        }
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

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void mapping()
    {
        textViewLogin = findViewById(R.id.textViewLogin);
        textViewTermsAndPrivacy = findViewById(R.id.textViewTermsAndPrivacy);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextRePassword = findViewById(R.id.editTextRePassword);
        editTextOtp = findViewById(R.id.editTextOtp);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonSendOtp = findViewById(R.id.buttonSendOtp);
        editTextEmail = findViewById(R.id.editTextEmail);
    }



    private void setupTextViewLogin()
    {
        String fullText = "Đã có tài khoản? Đăng nhập";
        SpannableString spannable = new SpannableString(fullText);

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#828282")),
                0, 17, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                17, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#000000"));
            }
        };

        spannable.setSpan(clickableSpan, 17, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewLogin.setText(spannable);
        textViewLogin.setMovementMethod(LinkMovementMethod.getInstance());
        textViewLogin.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupTextViewTermsAndPrivacy()
    {
        String fullText = "Bằng cách nhấn đăng ký, bạn đồng ý với Điều khoản Dịch vụ và Chính sách Quyền riêng tư";
        SpannableString spannable = new SpannableString(fullText);

        spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#828282")),
                0, 40, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                39, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(new StyleSpan(Typeface.BOLD),
                61, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        ClickableSpan clickableSpanTerms = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#000000"));
            }
        };

        ClickableSpan clickableSpanPrivacy = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
                ds.setColor(Color.parseColor("#000000"));
            }
        };

        spannable.setSpan(clickableSpanTerms, 39, 57, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(clickableSpanPrivacy, 61, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewTermsAndPrivacy.setText(spannable);
        textViewTermsAndPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        textViewTermsAndPrivacy.setHighlightColor(Color.TRANSPARENT);
    }

    private void setUpButtonRegister()
    {
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String rePassword = editTextRePassword.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String otp = editTextOtp.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Chưa nhập tài khoản!");
            }
            else if (username.length() < 8) {
                editTextUsername.setError("Tài khoản phải có ít nhất 8 ký tự!");
            }
            else if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Chưa nhập mật khẩu!");
            }
            else if (password.length() < 8) {
                editTextPassword.setError("Mật khẩu phải có ít nhất 8 ký tự!");
            }
            else if (!password.equals(rePassword)) {
                editTextRePassword.setError("Mật khẩu nhập lại không khớp!");
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
                editTextPassword.setEnabled(false);
                editTextRePassword.setEnabled(false);
                editTextEmail.setEnabled(false);

                RegisterRequest registerRequest = new RegisterRequest(username, password, email);
                Call<ApiResponse> call = userApiService.registerUser(registerRequest);
                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công. Otp đã gửi đến Email của bạn.", Toast.LENGTH_SHORT).show();
                            editTextOtp.setVisibility(View.VISIBLE);
                            buttonSendOtp.setVisibility(View.VISIBLE);
                        } else {
                            String errorMessage = "Đăng ký thất bại!";
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject errorObj = new JSONObject(response.errorBody().string());
                                    errorMessage = "Đăng ký thất bại: " + errorObj.optString("message", errorMessage);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            editTextUsername.setEnabled(true);
                            editTextPassword.setEnabled(true);
                            editTextRePassword.setEnabled(true);
                            editTextEmail.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        editTextUsername.setEnabled(true);
                        editTextPassword.setEnabled(true);
                        editTextRePassword.setEnabled(true);
                        editTextEmail.setEnabled(true);
                    }
                });
            }
            else
            {
                if (TextUtils.isEmpty(otp))
                {
                    editTextOtp.setError("Chưa nhập Otp!");
                }
                else
                {
                    VerifyRegisterRequest verifyRegisterRequest = new VerifyRegisterRequest(username, email, password, otp);
                    Call<ApiResponse> call = userApiService.verifyRegister(verifyRegisterRequest);
                    call.enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            } else {
                                String errorMessage = "Đăng ký thất bại!";
                                try {
                                    if (response.errorBody() != null) {
                                        JSONObject errorObj = new JSONObject(response.errorBody().string());
                                        errorMessage = "Đăng ký thất bại! " + errorObj.optString("message");
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
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
                            Toast.makeText(RegisterActivity.this, "OTP đã gửi thành công.", Toast.LENGTH_SHORT).show();
                        } else {
                            String message = "Gửi OTP thất bại!";
                            if (body != null && body.get("message") != null) {
                                message = String.valueOf(body.get("message"));
                            }
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMessage = "Lỗi gửi OTP!";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                errorMessage = "Lỗi gửi OTP! " + errorObj.optString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setUpButtonUpgradeAnonymous() {
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String rePassword = editTextRePassword.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String otp = editTextOtp.getText().toString().trim();

            if (TextUtils.isEmpty(username)) {
                editTextUsername.setError("Chưa nhập tài khoản!");
            } else if (username.length() < 8) {
                editTextUsername.setError("Tài khoản phải có ít nhất 8 ký tự!");
            } else if (TextUtils.isEmpty(password)) {
                editTextPassword.setError("Chưa nhập mật khẩu!");
            } else if (password.length() < 8) {
                editTextPassword.setError("Mật khẩu phải có ít nhất 8 ký tự!");
            } else if (!password.equals(rePassword)) {
                editTextRePassword.setError("Mật khẩu nhập lại không khớp!");
            } else if (TextUtils.isEmpty(email)) {
                editTextEmail.setError("Chưa nhập Email!");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editTextEmail.setError("Email không đúng định dạng!");
            } else if (editTextOtp.getVisibility() != View.VISIBLE) {
                Call<ApiResponse> call = userApiService.upgradeAnonymous(
                        user.getUsername(),
                        user.getPassword(),
                        email
                );
                Log.d("RegisterActivity", "upgradeAnonymous params -> username: "
                        + user.getUsername() + ", password: " + user.getPassword()
                        + ", newEmail: " + email);

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "OTP đã gửi đến email. Vui lòng kiểm tra.", Toast.LENGTH_SHORT).show();
                            editTextOtp.setVisibility(View.VISIBLE);
                            buttonSendOtp.setVisibility(View.VISIBLE);
                            editTextUsername.setEnabled(false);
                            editTextPassword.setEnabled(false);
                            editTextRePassword.setEnabled(false);
                            editTextEmail.setEnabled(false);
                        } else {
                            showErrorFromResponse(response, "Nâng cấp thất bại!");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Call<ApiResponse> call = userApiService.verifyUpgradeAnonymous(
                        user.getUsername(),
                        user.getPassword(),
                        username,
                        password,
                        email,
                        otp
                );

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Nâng cấp thành công.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        } else {
                            showErrorFromResponse(response, "Xác minh OTP thất bại!");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void setUpButtonSendOtpUpgrade() {
        buttonSendOtp.setOnClickListener(v -> {
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", editTextEmail.getText().toString().trim());

            userApiService.sendOtp(requestBody).enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful() && response.body() != null &&
                            "success".equals(response.body().get("status"))) {
                        Toast.makeText(RegisterActivity.this, "OTP đã gửi thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Gửi OTP thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void showErrorFromResponse(Response<?> response, String defaultMsg) {
        String errorMessage = defaultMsg;
        try {
            if (response.errorBody() != null) {
                JSONObject errorObj = new JSONObject(response.errorBody().string());
                errorMessage = errorObj.optString("message", defaultMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
    }
}