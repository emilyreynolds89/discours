package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AhoyOnboarderActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Fake News", "More Americans think fake news is a bigger problem than racism, climate change, and illegal immigration.", R.drawable.shareimg5);
        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Solution", "Our app is designed for promoting healthy discussions with friends about news.", R.drawable.img3);
        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Sharing", "Post any article from the web instantly using Web Share.", R.drawable.boards3);

        ahoyOnboarderCard1.setBackgroundColor(R.color.white);
        ahoyOnboarderCard2.setBackgroundColor(R.color.white);
        ahoyOnboarderCard3.setBackgroundColor(R.color.white);

        ahoyOnboarderCard1.setIconLayoutParams(750, 1100, 8, 8, 8, 8);
        ahoyOnboarderCard2.setIconLayoutParams(570, 1100, 8, 8, 8, 8);
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
        colorList.add(R.color.colorBoardGood);

        setColorBackground(colorList);
        setInactiveIndicatorColor(R.color.grey_600);
        setActiveIndicatorColor(R.color.white);

        Typeface face = Typeface.create(Typeface.SERIF, Typeface.NORMAL);
        setFont(face);
        setFinishButtonDrawableStyle(ContextCompat.getDrawable(this, R.drawable.finish_button));

        setOnboardPages(pages);
    }

    @Override
    public void onFinishButtonPressed() {
        startActivity(new Intent(OnboardingActivity.this, HomeActivity.class));
        finish();
    }


}
