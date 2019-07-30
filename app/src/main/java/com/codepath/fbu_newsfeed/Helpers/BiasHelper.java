package com.codepath.fbu_newsfeed.Helpers;

import android.widget.ImageView;

import com.codepath.fbu_newsfeed.R;

public class BiasHelper {
    private static final String TAG = "BiasHelper";

    public static void setBiasImageView(ImageView iv, int biasValue) {
        switch (biasValue) {
            case 1:
                iv.setBackgroundResource(R.drawable.liberal_icon);
                break;
            case 2:
                iv.setBackgroundResource(R.drawable.slightly_liberal_icon);
                break;
            case 3:
                iv.setBackgroundResource(R.drawable.moderate_icon);
                break;
            case 4:
                iv.setBackgroundResource(R.drawable.slightly_conserv_icon);
                break;
            case 5:
                iv.setBackgroundResource(R.drawable.conserv_icon);
                break;
            default:
                iv.setBackgroundResource(R.drawable.moderate_icon);
                break;
        }

    }

}
