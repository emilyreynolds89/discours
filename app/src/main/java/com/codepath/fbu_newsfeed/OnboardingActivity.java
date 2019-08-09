package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            final Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Fake News", "The rise of fake news not only proliferates misinformation, but also makes it more difficult to see the truth.", R.drawable.shareimg5);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Solution", "Discours displays the bias and credibility of sources in order to promote healthy and informed discussions about news.", R.drawable.boards13);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Sharing", "Post any article directly from the web using Web Share.", R.drawable.boards3);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

        ahoyOnboarderCard1.setIconLayoutParams(750, 1100, 8, 8, 8, 8);
        ahoyOnboarderCard2.setIconLayoutParams(915, 1050, 8, 8, 8, 8);
        ahoyOnboarderCard3.setIconLayoutParams(915, 1100, 8, 8, 8, 8);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.grey_600);
        }

        setFinishButtonTitle("Finish");
        showNavigationControls(true);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.colorBoardBad);
        colorList.add(R.color.colorAccentDark);
        colorList.add(R.color.colorAccentBold);

        setColorBackground(colorList);
        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.white);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.lato);
        setFont(typeface);
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.finish_button));

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(OnboardingActivity.this, HomeActivity.class));
        finish();
    }


}
