package com.example.enggo.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.enggo.MainActivity;
import com.example.enggo.R;
import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.LoginRequest;
import com.example.enggo.data.LoginResponse;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.models.User;
import com.example.enggo.service.UserApiService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonLogin, buttonLoginAnonymous, buttonLoginWithGoogle;
    private TextView textViewRegister;
    private TextView textViewForgotPassword;
    private UserApiService userApiService;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userApiService = RetrofitClient.getInstance().create(UserApiService.class);
        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        autoLogin();
        mapping();
        setUpTextViewRegister();
        setUpTextViewForgotPassword();
        setUpButtonLogin();
        setUpButtonLoginAnonymous();
        setUpGoogle();
        setButtonLoginWithGoogle();
    }

    private void setUpGoogle()
    {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("187794834711-fovk8v7b6s6fdf80shdssj975s5e1nqj.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d("LoginActivity", "Launcher được gọi và kết quả OK");
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("LoginActivity", "Lấy được account: " + account.getEmail());
                            String idToken = account.getIdToken();
                            Log.d("LoginActivity", "Gửi token tới API: " + idToken);
                            sendLoginWithGoogleApi(idToken);
                        } catch (ApiException e) {
                            Log.e("LoginActivity", "Google Sign-In thất bại, mã lỗi: " + e.getStatusCode(), e);
                            Toast.makeText(this, "Google login failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("LoginActivity", "Google Sign-In bị hủy hoặc lỗi: resultCode = " + result.getResultCode());
                    }
                }
        );
    }

    private void setButtonLoginWithGoogle()
    {
        buttonLoginWithGoogle.setOnClickListener(v -> {
            googleSignInClient.signOut()
                    .addOnCompleteListener(this, task -> {
                        Intent signInIntent = googleSignInClient.getSignInIntent();
                        googleSignInLauncher.launch(signInIntent);
                    });
        });
    }

    private void sendLoginWithGoogleApi(String idToken) {
        Map<String, String> body = new HashMap<>();
        body.put("idToken", idToken);

        userApiService.loginWithGoogle(body).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null && loginResponse.getUser() != null) {
                        User user = loginResponse.getUser();

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user.getUsername());
                        editor.putString("password", user.getPassword());
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Dữ liệu phản hồi không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMessage = "Đăng nhập thất bại!";
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            JSONObject errorObj = new JSONObject(errorBody);
                            errorMessage = "Đăng nhập thất bại! " + errorObj.optString("message");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();

                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("LoginActivity", "Lỗi kết nối: ", t);
                Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoLogin()
    {
        String savedUsername = sharedPreferences.getString("username", null);
        String savedPassword = sharedPreferences.getString("password", null);

        Log.d("LoginActivity", "Saved Username: " + savedUsername);
        Log.d("LoginActivity", "Saved Password: " + savedPassword);

        if (savedUsername != null && savedPassword != null) {
            login(savedUsername, savedPassword);
        }
    }

    public void setUpButtonLoginAnonymous()
    {
        buttonLoginAnonymous.setOnClickListener(v -> {
            String savedUsername = sharedPreferences.getString("usernameAnonymous", null);
            String savedPassword = sharedPreferences.getString("passwordAnonymous", null);
            if (savedUsername != null && savedPassword != null) {
                login(savedUsername, savedPassword);
            }
            else
            {
                Call<LoginResponse> call = userApiService.loginUserAnonymous();
                call.enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        if (response.isSuccessful())
                        {
                            Toast.makeText(LoginActivity.this, "Đăng nhập Ẩn danh thành công.", Toast.LENGTH_SHORT).show();
                            User user = response.body().getUser();

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("usernameAnonymous", user.getUsername());
                            editor.putString("passwordAnonymous", user.getPassword());
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("user", user);
                            startActivity(intent);
                        }
                        else {
                            String errorMessage = "Đăng nhập Ẩn danh thất bại!";
                            try {
                                if (response.errorBody() != null) {
                                    JSONObject errorObj = new JSONObject(response.errorBody().string());
                                    errorMessage = "Đăng nhập Ẩn danh thất bại! " + errorObj.optString("message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("usernameAnonymous");
                            editor.remove("passwordAnonymous");
                            editor.apply();
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        Log.e("LoginActivity", "Lỗi kết nối: ", t);
                        Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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

    private void mapping()
    {
        textViewRegister = findViewById(R.id.textViewRegister);
        textViewForgotPassword = findViewById(R.id.textViewForgotPassword);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLoginAnonymous = findViewById(R.id.buttonLoginAnonymous);
        buttonLoginWithGoogle = findViewById(R.id.buttonLoginWithGoogle);
    }

    private void setUpTextViewRegister()
    {
        textViewRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void setUpTextViewForgotPassword()
    {
        textViewForgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
    }

    private void setUpButtonLogin()
    {
        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            login(username, password);
        });
    }

    private void login(String username, String password)
    {
        if (TextUtils.isEmpty(username))
        {
            editTextUsername.setError("Chưa nhập tài khoản!");
        }
        else if (TextUtils.isEmpty(password))
        {
            editTextPassword.setError("Chưa nhập mật khẩu!");
        }
        else
        {
            LoginRequest loginRequest = new LoginRequest(username, password);
            Call<LoginResponse> call = userApiService.loginUser(loginRequest);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful())
                    {
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công.", Toast.LENGTH_SHORT).show();
                        User user = response.body().getUser();

                        if (!user.getUsername().startsWith("anonymous")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("username", user.getUsername());
                            editor.putString("password", user.getPassword());
                            editor.apply();
                        }

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                    }
                    else {
                        String errorMessage = "Đăng nhập thất bại!";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                errorMessage = "Đăng nhập thất bại! " + errorObj.optString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("username");
                        editor.remove("password");
                        editor.remove("usernameAnonymous");
                        editor.remove("passwordAnonymous");
                        editor.apply();
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Log.e("LoginActivity", "Lỗi kết nối: ", t);
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}