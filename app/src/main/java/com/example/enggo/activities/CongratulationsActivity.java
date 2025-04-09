package com.example.enggo.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.enggo.R;

public class CongratulationsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congratulations);

        Button btnBack = findViewById(R.id.btnBackHome);
        btnBack.setOnClickListener(v -> {
            // Tạm thời chỉ kết thúc activity
            finish();
        });
    }
}
