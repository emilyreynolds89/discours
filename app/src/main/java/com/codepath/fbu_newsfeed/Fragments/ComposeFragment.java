package com.codepath.fbu_newsfeed.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Helpers.BiasHelper;
import com.codepath.fbu_newsfeed.HomeActivity;
import com.codepath.fbu_newsfeed.Models.Article;
import com.codepath.fbu_newsfeed.Models.Bias;
import com.codepath.fbu_newsfeed.Models.Fact;
import com.codepath.fbu_newsfeed.Models.Share;
import com.codepath.fbu_newsfeed.Models.Source;
import com.codepath.fbu_newsfeed.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ComposeFragment extends Fragment {
    public static final String TAG = "ComposeFragment";

    private Article article;

    @BindView(R.id.ivArticlePreviewCreate) ImageView ivArticlePreview;
    @BindView(R.id.tvArticleTitle) TextView tvArticleTitle;
    @BindView(R.id.tvFactCheckCreate) TextView tvFactCheck;
    @BindView(R.id.ivBias) ImageView  ivBias;
    @BindView(R.id.ibInformation) ImageButton ibInformation;
    @BindView(R.id.etCaptionCreate) EditText etCaption;
    @BindView(R.id.btShareArticleCreate) Button btnShare;
    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable("article");
        View view =  inflater.inflate(R.layout.fragment_compose, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((HomeActivity) getActivity()).bottomNavigationView.getMenu().getItem(2).setChecked(true);

        final Fact.TruthLevel factCheck = article.getTruth();
        final Bias.BiasType bias = article.getBias();
        final ParseFile imageFile = article.getImage();
        final String title = article.getTitle();
        final String summary = article.getSummary();
        final Source source = article.getSource();

        tvFactCheck.setText(Fact.enumToString(factCheck));
        tvArticleTitle.setText(title);

        String imageUrl = article.getImageUrl();
        if (imageFile != null ) {
            Glide.with(getContext()).load(imageFile.getUrl()).into(ivArticlePreview);
        } else if (imageUrl != null) {
            Glide.with(getContext()).load(imageUrl).into(ivArticlePreview);
        }

        int biasValue = article.getIntBias();
        BiasHelper.setBiasImageView(ivBias, biasValue);

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String caption = etCaption.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                shareArticle(caption, article);
            }
        });

        ibInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInformationDialog();
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void showInformationDialog() {
        FragmentManager fm = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        InformationDialogFragment informationDialog = InformationDialogFragment.newInstance();
        informationDialog.show(fm, "fragment_information");
    }

    private void shareArticle(String caption, Article article) {
        article.setCount(article.getCount() + 1);
        Share share = new Share(ParseUser.getCurrentUser(), article, caption);
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ComposeFragment", "Share article success");
                    ((HomeActivity) getActivity()).bottomNavigationView.setSelectedItemId(R.id.action_home);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(ComposeFragment.this).commit();
                } else {
                    Log.e("ComposeFragment", "Error in sharing article");
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error in sharing article", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
