package com.codepath.fbu_newsfeed;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Matrix;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.Helpers.BitmapScaler;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EditProfileActivity extends AppCompatActivity {
    private static final String TAG = "EditProfileActivity";
    // PICK_PHOTO_CODE is a constant integer
    public final static int RESULT_LOAD_IMG = 1046;

    ParseUser mUser;
    ParseFile currentImage;
    public String photoFileName = "photo.jpg";

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

        String userId = getIntent().getStringExtra("user_id");

        try {
            mUser = getUser(userId);
            Log.d(TAG, "we're getting this user: " + userId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting user, showing current user", e);
            mUser = ParseUser.getCurrentUser();
        }

        tvUsername.setText(mUser.getString(User.KEY_USERNAME));
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
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                File photoFile = getPhotoFileUri(imageUri.toString());
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
               // Bitmap rawTakenImage = BitmapScaler.rotateBitmapOrientation(imageUri.toString());
               // Bitmap resizedTakenImage = BitmapScaler.scaleToFitWidth(rawTakenImage, 110);

//                try {
//                    writeResizedFileToDisk(resizedTakenImage);
//                } catch (Exception e) {
//                    Log.e(TAG, "Error writing resized file to disk", e);
//                }

                Glide.with(this).load(selectedImage).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(ivProfileImage);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] scaledData = bos.toByteArray();

                currentImage = new ParseFile(scaledData);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
//
//    private void writeResizedFileToDisk(Bitmap resizedBitmap) throws Exception {
//        // Configure byte output stream
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//// Compress the image further
//        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
//// Create a new file for the resized bitmap (`getPhotoFileUri` defined above)
//        File resizedFile = getPhotoFileUri(photoFileName + "_resized");
//        resizedFile.createNewFile();
//        FileOutputStream fos = new FileOutputStream(resizedFile);
//// Write the bytes of the bitmap to file
//        fos.write(bytes.toByteArray());
//        fos.close();
//    }


    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }
}
