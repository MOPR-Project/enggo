package com.example.enggo;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.activities.DictionaryActivity;
import com.example.enggo.activities.FlashCardActivity;
import com.example.enggo.activities.LoginActivity;
import com.example.enggo.activities.RegisterActivity;
import com.example.enggo.activities.SentenceLevelActivity;
import com.example.enggo.adapters.MessageAdapter;
import com.example.enggo.data.ApiResponse;
import com.example.enggo.data.ContentRequest;
import com.example.enggo.data.ContentResponse;
import com.example.enggo.data.UpdateProfileRequest;
import com.example.enggo.helpers.ChatbotRetrofitClient;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.models.Message;
import com.example.enggo.models.User;
import com.example.enggo.service.ChatbotApiService;
import com.example.enggo.service.UserApiService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeParseException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FrameLayout rootLayout;
    private View bubbleView, chatBoxView, profileView;
    private ChatbotApiService chatbotApiService;
    private EditText editTextMessage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private BottomNavigationView bottomNavigationView;
    private User user;
    private MenuItem profileItem;
    private EditText editTextUsername;
    private EditText editEmail;
    private EditText editTextName;
    private EditText editTextDateOfBirth;
    private Spinner spinnerGender;
    private ArrayAdapter<String> genderAdapter;
    private ImageView imageViewAvatar;
    private UserApiService userApiService;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private Button buttonLogout, buttonEditProfile, buttonSaveProfile, buttonRegister;
    private SharedPreferences sharedPreferences;

    private TextView textViewStreak;

    private View navigationView;
    private LinearLayout navSentenceBuilder, navDictionary, navFlashcard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        chatbotApiService = ChatbotRetrofitClient.getInstance().create(ChatbotApiService.class);
        userApiService = RetrofitClient.getInstance().create(UserApiService.class);
        user = (User) getIntent().getSerializableExtra("user");

        mapping();
        addNavigationLogic();
        setUpChatbot();
        setUpNavigationBar();
        setUpTextViewStreak();
        setUpProfile();
        setUpAnonymous();
    }

    private void setUpTextViewStreak() {
        textViewStreak.setText("x" + user.getStreak());
    }

    private void setUpAnonymous() {
        if (user.getUsername().startsWith("anonymous")) {
            buttonEditProfile.setVisibility(View.GONE);
            buttonLogout.setVisibility(View.GONE);
            buttonRegister = profileView.findViewById(R.id.buttonRegister);
            setUpButtonRegister();
            buttonRegister.setVisibility(View.VISIBLE);
            imageViewAvatar.setEnabled(false);
        }
    }

    private void setUpButtonRegister() {
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });
    }

    private void mapping() {
        rootLayout = findViewById(R.id.rootLayout);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        profileItem = bottomNavigationView.getMenu().findItem(R.id.profile);

        profileView = LayoutInflater.from(this).inflate(R.layout.profile_layout, rootLayout, false);
        navigationView = LayoutInflater.from(this).inflate(R.layout.layout_navigation, rootLayout, false);

        navSentenceBuilder = navigationView.findViewById(R.id.navSentenceBuilder);
        navDictionary = navigationView.findViewById(R.id.navDictionary);
        navFlashcard = navigationView.findViewById(R.id.navFlashcard);

        editTextUsername = profileView.findViewById(R.id.editTextUsername);
        editEmail = profileView.findViewById(R.id.editEmail);
        editTextName = profileView.findViewById(R.id.editTextName);
        editTextDateOfBirth = profileView.findViewById(R.id.editTextDateOfBirth);
        spinnerGender = profileView.findViewById(R.id.spinnerGender);
        imageViewAvatar = profileView.findViewById(R.id.imageViewAvatar);
        buttonLogout = profileView.findViewById(R.id.buttonLogout);
        buttonEditProfile = profileView.findViewById(R.id.buttonEditProfile);
        buttonSaveProfile = profileView.findViewById(R.id.buttonSaveProfile);
        textViewStreak = findViewById(R.id.textViewStreak);
    }

    private void setUpNavigationBar() {
        bottomNavigationView.setItemIconTintList(null);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.profile) {
                removeAllViews();
                safeAddView(profileView);
                return true;
            }
            if (id == R.id.home) {
                removeAllViews();
                addNavigationLogic();
                safeAddView(bubbleView);
                return true;
            }
            if (id == R.id.notifications) {
                removeAllViews();
                return true;
            }
            return false;
        });
    }

    private Bitmap getCircularBitmap(Bitmap bitmap) {
        int size = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        float radius = size / 2f;
        canvas.drawCircle(radius, radius, radius, paint);

        return output;
    }

    private void setUpProfile() {
        setUpSpinnerGender();
        setUpEditTextDateOfBirth();
        setUpImageViewAvatar();
        logoutButton();
        loadAvatar();
        loadUserProfile();
        setUpEditProfile();
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

    private void logoutButton() {
        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("username");
            editor.remove("password");
            editor.apply();
            Toast.makeText(MainActivity.this, "Đăng xuất thành công.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void setUpImageViewAvatar() {
        imageViewAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            uploadAvatarToServer(imageUri);
        }
    }

    private void uploadAvatarToServer(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                Toast.makeText(this, "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                return;
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }
            inputStream.close();

            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            File tempFile = new File(getCacheDir(), "avatar_temp.jpg");
            OutputStream outputStream = new FileOutputStream(tempFile);
            outputStream.write(imageBytes);
            outputStream.close();

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
            MultipartBody.Part avatarPart = MultipartBody.Part.createFormData("avatar", tempFile.getName(), requestFile);

            RequestBody username = RequestBody.create(MediaType.parse("text/plain"), user.getUsername());
            RequestBody password = RequestBody.create(MediaType.parse("text/plain"), user.getPassword());

            userApiService = RetrofitClient.getInstance().create(UserApiService.class);
            Call<ApiResponse> call = userApiService.updateAvatar(username, password, avatarPart);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        String base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                        user.setAvatar(base64Image);
                        loadAvatar();

                        Toast.makeText(getApplicationContext(), "Avatar cập nhật thành công.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Cập nhật Avatar thất bại!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getApplicationContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            Toast.makeText(this, "Lỗi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setUpEditTextDateOfBirth() {
        editTextDateOfBirth.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;

                            editTextDateOfBirth.setText(date);
                        }
                    }, year, month, dayOfMonth);

            datePickerDialog.show();
        });
    }

    private void setUpSpinnerGender() {
        genderAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.gender_options)) {

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(genderAdapter);
    }

    private void loadUserProfile() {
        String username = user.getUsername();
        String email = user.getEmail();
        String name = user.getName();
        String dateOfBirth = "Ngày sinh";
        if (user.getDateOfBirth() != null && !user.getDateOfBirth().isEmpty()) {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // định dạng ban đầu từ API
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // định dạng bạn muốn hiển thị

            LocalDate parsedDate = LocalDate.parse(user.getDateOfBirth(), inputFormatter);

            dateOfBirth = parsedDate.format(outputFormatter);
        }
        String gender = user.getGender();

        int position = genderAdapter.getPosition(gender);
        if (position >= 0) {
            spinnerGender.setSelection(position);
        }

        spinnerGender.setEnabled(false);

        editTextUsername.setText(username);
        editEmail.setText(email);
        editTextName.setText(name);
        editTextDateOfBirth.setText(dateOfBirth);


        navigationView = LayoutInflater.from(this).inflate(R.layout.layout_navigation, rootLayout, false);
        navSentenceBuilder = navigationView.findViewById(R.id.navSentenceBuilder);
        navDictionary = navigationView.findViewById(R.id.navDictionary);
        navFlashcard = navigationView.findViewById(R.id.navFlashcard);

    }

    private void setUpChatbot() {
        bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, rootLayout, false);
        chatBoxView = LayoutInflater.from(this).inflate(R.layout.chatbox_layout, rootLayout, false);

        safeAddView(bubbleView);

        editTextMessage = chatBoxView.findViewById(R.id.editTextMessage);
        Button buttonClose = chatBoxView.findViewById(R.id.buttonCloseChat);
        Button buttonSendMessage = chatBoxView.findViewById(R.id.buttonSendMessage);

        recyclerView = chatBoxView.findViewById(R.id.recyclerviewMessageBox);
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonClose.setOnClickListener(v -> {
            rootLayout.removeView(chatBoxView);
            safeAddView(bubbleView);
            showAllViews();
        });

        buttonSendMessage.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (TextUtils.isEmpty(message)) {
                editTextMessage.setError("Chưa nhập nội dung tin nhắn!");
                return;
            }

            Message userMessage = new Message(message, Message.TYPE_USER);
            messageList.add(userMessage);
            messageAdapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);
            editTextMessage.setText("");

            List<ContentRequest.Part> parts = Collections.singletonList(new ContentRequest.Part(message));
            ContentRequest.Content content = new ContentRequest.Content(parts);
            ContentRequest request = new ContentRequest(Collections.singletonList(content));

            Call<ContentResponse> call = chatbotApiService.generateContent("AIzaSyBdeG6_4-LMTqq8DpU03c0EufReVhAO1-4", request);
            call.enqueue(new Callback<ContentResponse>() {
                @Override
                public void onResponse(Call<ContentResponse> call, Response<ContentResponse> response) {
                    String reply = "Gửi tin nhắn thất bại!";
                    if (response.isSuccessful() && response.body() != null) {
                        reply = response.body().candidates.get(0).content.parts.get(0).text;
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                reply += " " + errorObj.optString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Message aiMessage = new Message(reply, Message.TYPE_AI);
                    messageList.add(aiMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }

                @Override
                public void onFailure(Call<ContentResponse> call, Throwable t) {
                    Message aiMessage = new Message("Lỗi kết nối: " + t.getMessage(), Message.TYPE_AI);
                    messageList.add(aiMessage);
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            });
        });

        ImageView bubbleIcon = bubbleView.findViewById(R.id.bubbleIcon);
        bubbleIcon.setOnTouchListener(new View.OnTouchListener() {
            private float downX, downY;
            private int lastAction;
            private FrameLayout.LayoutParams params;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (params == null) {
                    params = (FrameLayout.LayoutParams) bubbleView.getLayoutParams();
                }

                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getRawX();
                        downY = event.getRawY();
                        lastAction = MotionEvent.ACTION_DOWN;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX() - downX;
                        float moveY = event.getRawY() - downY;

                        params.leftMargin += moveX;
                        params.topMargin += moveY;
                        bubbleView.setLayoutParams(params);

                        downX = event.getRawX();
                        downY = event.getRawY();
                        lastAction = MotionEvent.ACTION_MOVE;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            hideAllViewsExcept(bubbleView);
                            rootLayout.removeView(bubbleView);
                            safeAddView(chatBoxView);
                        }
                        return true;
                }
                return false;
            }
        });
    }

    private void removeAllViews() {
        rootLayout.removeAllViews();
    }

    private void hideAllViewsExcept(View exceptView) {
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            View child = rootLayout.getChildAt(i);
            if (child != exceptView) {
                child.setVisibility(View.GONE);
            }
        }
    }

    private void showAllViews() {
        for (int i = 0; i < rootLayout.getChildCount(); i++) {
            rootLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void safeAddView(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        rootLayout.addView(view);
    }

    private void loadAvatar() {
        String avatarBase64 = user.getAvatar();
        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] avatarBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

                Bitmap circleBitmap = getCircularBitmap(avatarBitmap);

                Drawable avatarDrawable = new BitmapDrawable(getResources(), circleBitmap);
                profileItem.setIcon(avatarDrawable);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi giải mã ảnh!", Toast.LENGTH_SHORT).show();
            }
        }

        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                byte[] avatarBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarBytes, 0, avatarBytes.length);

                Bitmap circularBitmap = getCircularBitmap(avatarBitmap);

                imageViewAvatar.setImageBitmap(circularBitmap);

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi giải mã ảnh!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setUpEditProfile() {
        buttonEditProfile.setOnClickListener(v -> {
            buttonEditProfile.setVisibility(View.GONE);
            editTextName.setEnabled(true);
            editTextDateOfBirth.setEnabled(true);
            spinnerGender.setEnabled(true);
            buttonSaveProfile.setVisibility(View.VISIBLE);
        });

        buttonSaveProfile.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String dateOfBirthInput = editTextDateOfBirth.getText().toString().trim();
            final String[] formattedDate = {null};

            if (!dateOfBirthInput.isEmpty()) {
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = inputFormat.parse(dateOfBirthInput);
                    formattedDate[0] = outputFormat.format(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String gender = spinnerGender.getSelectedItem().toString();
            UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(
                    user.getUsername(), user.getPassword(), name, formattedDate[0], gender
            );

            Call<ApiResponse> call = userApiService.updateProfile(updateProfileRequest);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Chỉnh sửa thông tin thành công.", Toast.LENGTH_SHORT).show();

                        buttonEditProfile.setVisibility(View.VISIBLE);
                        editTextName.setEnabled(false);
                        editTextDateOfBirth.setEnabled(false);
                        spinnerGender.setEnabled(false);
                        buttonSaveProfile.setVisibility(View.GONE);

                        user.setName(name);
                        user.setDateOfBirth(formattedDate[0]);
                        user.setGender(gender);

                        loadUserProfile();
                    } else {
                        String errorMessage = "Chỉnh sửa thông tin thất bại!";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                errorMessage = "Chỉnh sửa thông tin thất bại! " + errorObj.optString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void addNavigationLogic() {
        safeAddView(navigationView);

        navSentenceBuilder.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SentenceLevelActivity.class);
            startActivity(intent);
        });

        navDictionary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DictionaryActivity.class);
            startActivity(intent);
        });

        navFlashcard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FlashCardActivity.class);
            startActivity(intent);
        });
    }
}
