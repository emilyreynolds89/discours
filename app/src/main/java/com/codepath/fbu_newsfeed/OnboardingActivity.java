package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Fake News", "Misinformation ", R.drawable.shareimg5);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Mission", "Our app is designed for promoting healthy discussions with friends about news.", R.drawable.img3);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Sharing", "Post any article from the web instantly.", R.drawable.shareimg1);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

        ahoyOnboarderCard1.setIconLayoutParams(750, 1400, 8, 8, 8, 8);
        ahoyOnboarderCard2.setIconLayoutParams(570, 1100, 8, 8, 8, 8);
        ahoyOnboarderCard3.setIconLayoutParams(570, 1100, 8, 8, 8, 8);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);

        for (AhoyOnboarderCard page : pages) {
            page.setTitleColor(R.color.black);
            page.setDescriptionColor(R.color.grey_600);
            //page.setTitleTextSize(dpToPixels(8, this));
        }

        setFinishButtonTitle("Finish");
        showNavigationControls(true);

        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.colorBoardBad);
        colorList.add(R.color.colorAccentDark);
        colorList.add(R.color.colorAccentLight);

        setColorBackground(colorList);
        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.white);
        //setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.));

        Typeface face = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
        setFont(face);

        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(OnboardingActivity.this, HomeActivity.class));

    }


}
