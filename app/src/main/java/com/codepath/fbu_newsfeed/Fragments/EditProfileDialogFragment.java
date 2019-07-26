package com.codepath.fbu_newsfeed.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.fbu_newsfeed.Models.User;
import com.codepath.fbu_newsfeed.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditProfileDialogFragment extends DialogFragment {
    private static final String TAG = "EditProfileDialogFragment";

    private DialogInterface.OnDismissListener onDismissListener;

    // PICK_PHOTO_CODE is a constant integer
    private final static int RESULT_LOAD_IMG = 1046;

    private ParseUser mUser;
    private ParseFile currentImage;
    Bitmap currentImageBitmap;

    public String photoFileName = "photo.jpg";

    @BindView(R.id.tvFullName)
    TextView tvFullName;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivProfileImageNotif)
    ImageView ivProfileImage;
    @BindView(R.id.etBio)
    EditText etBio;
    @BindView(R.id.btnUpload)
    Button btnUpload;
    @BindView(R.id.btnSubmit) Button btnSubmit;

    private Unbinder unbinder;

    public EditProfileDialogFragment() {}

    public static EditProfileDialogFragment newInstance(String userId) {
        EditProfileDialogFragment frag = new EditProfileDialogFragment();
        Bundle args = new Bundle();
        args.putString("user_id", userId);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mUser = getUser(getArguments().getString("user_id"));
            Log.d(TAG, "we're getting this user: " + mUser.getUsername());
        } catch(Exception e) {
            Log.e(TAG, "Error getting user", e);
            dismiss();
        }

        tvUsername.setText("@" + mUser.getString(User.KEY_USERNAME));
        tvFullName.setText(mUser.getString(User.KEY_FULLNAME));
        etBio.setText(mUser.getString(User.KEY_BIO));

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
    private void onPickPhoto() {
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
                    if (image != null) {
                        user.put(User.KEY_PROFILEIMAGE, image);
                    }
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "Updated user");
                                Toast.makeText(getContext(), "Successfully updated profile", Toast.LENGTH_SHORT).show();
                                dismiss();
                            } else {
                                Log.d(TAG, "Error updating user: " + e.getMessage());
                                Toast.makeText(getContext(), "Error updating profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "Error saving user: " + e.getMessage());
                }
            }
        });
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
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
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getContext(), "You haven't picked an image",Toast.LENGTH_LONG).show();
        }
    }

    private ParseUser getUser(String user_id) throws ParseException {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", user_id);
        return query.getFirst();
    }

}
