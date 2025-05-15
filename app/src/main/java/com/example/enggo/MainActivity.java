package com.example.enggo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.activities.DictionaryActivity;
import com.example.enggo.activities.FlashCardActivity;
import com.example.enggo.activities.LoginActivity;
import com.example.enggo.activities.SentenceLevelActivity;
import com.example.enggo.adapters.MessageAdapter;
import com.example.enggo.data.ContentRequest;
import com.example.enggo.data.ContentResponse;
import com.example.enggo.data.LoginRequest;
import com.example.enggo.data.LoginResponse;
import com.example.enggo.helpers.ChatbotRetrofitClient;
import com.example.enggo.helpers.RetrofitClient;
import com.example.enggo.models.Message;
import com.example.enggo.models.User;
import com.example.enggo.service.ChatbotApiService;
import com.example.enggo.service.UserApiService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private FrameLayout rootLayout;
    private View bubbleView, chatBoxView;
    private ChatbotApiService chatbotApiService;
    private EditText editTextMessage;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private List<Message> messageList = new ArrayList<>();
    private View blockerView;

    private View navigationView;
    private LinearLayout navSentenceBuilder, navDictionary, navFlashcard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        chatbotApiService = ChatbotRetrofitClient.getInstance().create(ChatbotApiService.class);
        mapping();
        setUpChatbot();


        rootLayout.addView(navigationView);
        addNavigationLogic();
    }

    private void mapping() {
        rootLayout = findViewById(R.id.rootLayout);

        navigationView = LayoutInflater.from(this).inflate(R.layout.layout_navigation, rootLayout, false);
        navSentenceBuilder = navigationView.findViewById(R.id.navSentenceBuilder);
        navDictionary = navigationView.findViewById(R.id.navDictionary);
        navFlashcard = navigationView.findViewById(R.id.navFlashcard);
    }

    private void setUpChatbot() {
        bubbleView = LayoutInflater.from(this).inflate(R.layout.bubble_layout, rootLayout, false);
        chatBoxView = LayoutInflater.from(this).inflate(R.layout.chatbox_layout, rootLayout, false);

        rootLayout.addView(bubbleView);

        editTextMessage = chatBoxView.findViewById(R.id.editTextMessage);
        Button buttonClose = chatBoxView.findViewById(R.id.buttonCloseChat);
        Button buttonSendMessage = chatBoxView.findViewById(R.id.buttonSendMessage);

        recyclerView = chatBoxView.findViewById(R.id.recyclerviewMessageBox);
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(messageAdapter);

        buttonClose.setOnClickListener(v -> {
            rootLayout.removeView(chatBoxView);
            rootLayout.addView(bubbleView);
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
                    if (response.isSuccessful() && response.body() != null) {
                        String reply = response.body().candidates.get(0).content.parts.get(0).text;

                        Message aiMessage = new Message(reply, Message.TYPE_AI);
                        messageList.add(aiMessage);
                        messageAdapter.notifyItemInserted(messageList.size() - 1);
                        recyclerView.scrollToPosition(messageList.size() - 1);
                    } else {
                        String errorMessage = "Gửi tin nhắn thất bại!";
                        try {
                            if (response.errorBody() != null) {
                                JSONObject errorObj = new JSONObject(response.errorBody().string());
                                errorMessage = "Gửi tin nhắn thất bại! " + errorObj.optString("message");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ContentResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        ImageView bubbleIcon = bubbleView.findViewById(R.id.bubbleIcon);
        bubbleIcon.setOnTouchListener(new View.OnTouchListener() {
            private float downX, downY;
            private int lastAction;
            private FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bubbleView.getLayoutParams();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
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
                            rootLayout.addView(chatBoxView);
                        }
                        return true;
                }
                return false;
            }
        });
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
            View child = rootLayout.getChildAt(i);
            child.setVisibility(View.VISIBLE);
        }
    }

    private void addNavigationLogic() {
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
