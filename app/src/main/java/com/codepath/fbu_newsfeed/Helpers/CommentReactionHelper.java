package com.codepath.fbu_newsfeed.Helpers;

import android.util.Log;

import com.codepath.fbu_newsfeed.Models.Comment;
import com.codepath.fbu_newsfeed.Models.CommentReaction;
import com.codepath.fbu_newsfeed.Models.User;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CommentReactionHelper {
    private static final String TAG = "CommentReactionHelper";

    public static int createCommentReaction(Comment comment) {
        CommentReaction commentReaction = new CommentReaction((User) ParseUser.getCurrentUser(), comment);
        commentReaction.saveInBackground();
        int count = comment.updateCount(true);
        comment.saveInBackground();
        return count;
    }

    public static int destroyCommentReaction(CommentReaction commentReaction, Comment comment) {
        commentReaction.deleteInBackground();
        int count = comment.updateCount(false);
        comment.saveInBackground();
        return count;
    }

    public static CommentReaction getCommentReaction(ParseUser user, Comment comment) {
        ParseQuery<CommentReaction> commentReactionQuery = ParseQuery.getQuery(CommentReaction.class);

        commentReactionQuery.whereEqualTo(CommentReaction.KEY_USER, user);
        commentReactionQuery.whereEqualTo(CommentReaction.KEY_COMMENT, comment);

        try {
            List<CommentReaction> result = commentReactionQuery.find();
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
