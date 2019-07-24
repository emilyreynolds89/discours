package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    // PICK_PHOTO_CODE is a constant integer
    public final static int RESULT_LOAD_IMG = 1046;

    ParseUser mUser;
    ParseFile currentImage;
    Bitmap currentImageBitmap;

    public String photoFileName = "photo.jpg";

    @BindView(R.id.tvFullName) TextView tvFullName;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivProfileImageNotif) ImageView ivProfileImage;
    @BindView(R.id.etBio) EditText etBio;
    @BindView(R.id.btnUpload) Button btnUpload;
    @BindView(R.id.btnSubmit) Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);

        String userId = getIntent().getStringExtra("user_id");

        try {
            mUser = getUser(userId);
            Log.d(TAG, "we're getting this user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user, showing current user", e);
            mUser = ParseUser.getCurrentUser();
        }

        tvUsername.setText("@" + mUser.getString(User.KEY_USERNAME));
        tvFullName.setText(mUser.getString(User.KEY_FULLNAME));

        currentImage = mUser.getParseFile("profileImage");

        if (currentImage != null) {
            Glide.with(this).load(currentImage.getUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);

        }

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickPhoto();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitUser(etBio.getText().toString(), currentImage);

            }
        });

    }

    // Trigger gallery selection for a photo
    public void onPickPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    private void submitUser(final String bio, final ParseFile image) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(mUser.getObjectId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Found user to submit");
                    user.put(User.KEY_BIO, bio);
                    user.put(User.KEY_PROFILEIMAGE, image);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Updated user");
                                goToProfile();
                            } else {
                                Log.d(TAG, "Error updating user: " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error saving user: " + e.getMessage());
                }
            }
        });
    }

    private void goToProfile() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra("user_id", mUser.getObjectId());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                Glide.with(this).asBitmap().load(imageUri).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        ivProfileImage.setImageBitmap(resource);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] scaledData = bos.toByteArray();
                        currentImage = new ParseFile(scaledData);

                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }
}
