package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {

    ParseUser mUser;

    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
    @BindView(R.id.etBio) EditText etBio;
    @BindView(R.id.btnUpload) Button btnUpload;
    @BindView(R.id.btnSubmit) Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        mUser = (User) getIntent().getSerializableExtra("user");

        tvFullName.setText(mUser.getString(User.KEY_FULLNAME));
        tvUsername.setText(mUser.getUsername());

        if (mUser.getParseFile("profileImage") != null) {
            Glide.with(this).load(mUser.getParseFile("profileImage").getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: figure out how to upload a new image
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: update user object
            }
        });

    }
}
