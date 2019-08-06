package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FakeNewsInfoActivity extends AppCompatActivity {

    @BindView(R.id.btnDone) Button btnDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_news_info);

        ButterKnife.bind(this);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FakeNewsInfoActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}
