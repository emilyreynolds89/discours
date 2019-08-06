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

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Welcome to Discours", "Our app is a social platform for having healthy discussions with friends about news.", R.drawable.img1);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Sharing", "Post any article from the web instantly.", R.drawable.img2);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Ratings", "Calculates the bias and fact metrics of posts.", R.drawable.img3);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

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
        colorList.add(R.color.colorAccentDark);
        colorList.add(R.color.colorAccentBold);
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
