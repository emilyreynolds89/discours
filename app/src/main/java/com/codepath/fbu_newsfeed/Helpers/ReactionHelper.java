package com.codepath.fbu_newsfeed.Helpers;

import android.util.Log;

import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ReactionHelper {
    public static final String TAG = "ReactionHelper";

    public static int createReaction(String type, Share share) {
        Log.d(TAG, "Creating reaction of type: " + type);
        Reaction newReaction = new Reaction(ParseUser.getCurrentUser(), share, type);
        newReaction.saveInBackground();
        int count = share.incrementCount(type);
        share.saveInBackground();
        return count;
    }

    public static int destroyReaction(Reaction reaction, String type, Share share) {
        Log.d(TAG, "Destroying reaction of type: " + type);
        reaction.deleteInBackground();
        int count = share.decrementCount(type);
        share.saveInBackground();
        return count;
    }

    public static Reaction getReaction(String type, Share share, ParseUser user) {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.whereEqualTo(Reaction.KEY_SHARE, share);
        reactionQuery.whereEqualTo(Reaction.KEY_USER, user);
        reactionQuery.whereEqualTo(Reaction.KEY_TYPE, type);

        try {
            List<Reaction> result = reactionQuery.find();
            if (result.size() > 0 ) {
                return result.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return null;
        }
    }

}
