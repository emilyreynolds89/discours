package com.codepath.fbu_newsfeed.Helpers;

import android.util.Log;

import com.codepath.fbu_newsfeed.Models.Reaction;
import com.codepath.fbu_newsfeed.Models.Share;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReactionHelper {
    private static final String TAG = "ReactionHelper";

    public static int createReaction(Reaction.ReactionType type, Share share) {
        Log.d(TAG, "Creating reaction of type: " + type);
        Reaction newReaction = new Reaction(ParseUser.getCurrentUser(), share, type);
        newReaction.saveInBackground();
        int count = share.incrementCount(Reaction.enumToString(type));
        share.saveInBackground();
        return count;
    }

    public static int destroyReaction(Reaction reaction, Reaction.ReactionType type, Share share) {
        Log.d(TAG, "Destroying reaction of type: " + type);
        reaction.deleteInBackground();
        int count = share.decrementCount(Reaction.enumToString(type));
        share.saveInBackground();
        return count;
    }

    public static Reaction getReaction(Reaction.ReactionType type, Share share, ParseUser user) {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.whereEqualTo(Reaction.KEY_SHARE, share);
        reactionQuery.whereEqualTo(Reaction.KEY_USER, user);
        reactionQuery.whereEqualTo(Reaction.KEY_TYPE, Reaction.enumToString(type));

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
    public static Map<Reaction.ReactionType, Reaction> getReactions(Share share, ParseUser user) {
        ParseQuery<Reaction> reactionQuery = ParseQuery.getQuery(Reaction.class);

        reactionQuery.whereEqualTo(Reaction.KEY_SHARE, share);
        reactionQuery.whereEqualTo(Reaction.KEY_USER, user);

        try {
            Map<Reaction.ReactionType, Reaction> map = new HashMap<>();
            List<Reaction> result = reactionQuery.find();
            for (int i = 0; i < result.size(); i++) {
                map.put(Reaction.stringToEnum(result.get(i).getType()), result.get(i));
            }
            return map;
        } catch (Exception e) {
            Log.d(TAG, "Error finding reactions: " + e.getMessage());
            return null;
        }

    }

}
