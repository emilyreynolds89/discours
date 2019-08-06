package com.codepath.fbu_newsfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.codepath.fbu_newsfeed.Models.Quiz;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DetailActivity";

    @BindView(R.id.ivQuizImage) ImageView ivQuizImage;
    @BindView(R.id.tvQuizQuestion) TextView tvQuizQuestion;
    @BindView(R.id.btnTrue) Button btnTrue;
    @BindView(R.id.btnFake) Button btnFake;
    @BindView(R.id.btnNext) Button btnNext;
    @BindView(R.id.tvQuizCorrectness) TextView tvQuizCorrectness;
    @BindView(R.id.tvQuizMessage) TextView tvQuizMessage;
    @BindView(R.id.btnStart) Button btnStart;
    @BindView(R.id.tvStartTitle) TextView tvStartTitle;
    @BindView(R.id.ivStartImage) ImageView ivStartImage;
    @BindView(R.id.tvStartMessage) TextView tvStartMessage;
    @BindView(R.id.tvEndTitle) TextView tvEndTitle;
    @BindView(R.id.tvEndMessage) TextView tvEndMessage;
    @BindView(R.id.btnFinish) Button btnFinish;
    @BindView(R.id.ivEndImage) ImageView ivEndImage;
    @BindView(R.id.scoreView) View scoreView;
    @BindView(R.id.tvScore) TextView tvScore;

    private ArrayList<Quiz> quizzes = new ArrayList<>();
    private Quiz quiz;

    private int score = 0;
    private int count = 0;
    private int numQuestions;

    //private Animation close_anim, open_anim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        ButterKnife.bind(this);

        queryQuizzes();

        //close_anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.btn_close);
        //open_anim = AnimationUtils.loadAnimation(getBaseContext(), R.anim.btn_open);

        btnStart.setOnClickListener(this);
        btnTrue.setOnClickListener(this);
        btnFake.setOnClickListener(this);
        btnNext.setOnClickListener(this);
    }

    private void queryQuizzes() {
        ParseQuery<Quiz> query = ParseQuery.getQuery(Quiz.class);
        query.include(Quiz.KEY_IMAGE);
        query.include(Quiz.KEY_FAKE);
        query.include(Quiz.KEY_MESSAGE);

        query.findInBackground(new FindCallback<Quiz>() {
            @Override
            public void done(List<Quiz> newQuizzes, ParseException e) {
                if (e == null) {
                    numQuestions = newQuizzes.size();
                    quizzes.addAll(newQuizzes);
                    quiz = quizzes.remove(0);
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private Quiz getQuiz(String quizId) throws ParseException {
        ParseQuery<Quiz> query = ParseQuery.getQuery(Quiz.class);
        query.include(Quiz.KEY_IMAGE);
        query.include(Quiz.KEY_FAKE);
        query.include(Quiz.KEY_MESSAGE);
        query.whereEqualTo("objectId", quizId);
        return query.getFirst();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                setStartVisibility(View.GONE);
                //setStartAnim(close_anim);
                btnStart.setClickable(false);

                setQuizViews();

                setBackgroundVisibility(View.VISIBLE);
                //setBackgroundAnim(open_anim);

                setAnswerButtonVisibility(View.VISIBLE);
                //setAnswerButtonAnim(open_anim);
                setAnswerButtonClickable(true);

                break;

            case R.id.btnTrue:
                setCorrectness(false);
                presentResults();
                break;

            case R.id.btnFake:
                setCorrectness(true);
                presentResults();
                break;

            case R.id.btnNext:
                count += 1;
                if (count < numQuestions) {
                    quiz = quizzes.remove(0);
                    setQuizViews();
                    presentNewQuestion();
                } else {
                    presentEndOfQuiz();
                }
                break;

            case R.id.btnFinish:
                Intent intent = new Intent(QuizActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void presentEndOfQuiz() {
        setBackgroundVisibility(View.INVISIBLE);
        setResultVisibility(View.INVISIBLE);
        //setResultAnim(close_anim);

        tvScore.setText(Integer.toString(score) + " / 4  Correct");

        tvEndTitle.setVisibility(View.VISIBLE);
        ivEndImage.setVisibility(View.VISIBLE);
        tvEndMessage.setVisibility(View.VISIBLE);
        scoreView.setVisibility(View.VISIBLE);
        btnFinish.setVisibility(View.VISIBLE);

        //tvEndTitle.startAnimation(open_anim);
        //ivEndImage.startAnimation(open_anim);
        //tvEndMessage.startAnimation(open_anim);
        //scoreView.startAnimation(open_anim);
        //btnFinish.startAnimation(open_anim);
    }

    private void presentNewQuestion() {
        setResultVisibility(View.INVISIBLE);
        //setResultAnim(close_anim);

        setAnswerButtonVisibility(View.VISIBLE);
        //setAnswerButtonAnim(open_anim);
        setAnswerButtonClickable(true);
    }

    private void setCorrectness(boolean correct) {
        if (!correct) {
            tvQuizCorrectness.setText("INCORRECT");
        } else {
            tvQuizCorrectness.setText("CORRECT");
            score += 1;
        }
    }

    private void presentResults() {
        setAnswerButtonVisibility(View.INVISIBLE);
        //setAnswerButtonAnim(close_anim);
        setAnswerButtonClickable(false);

        setResultVisibility(View.VISIBLE);
        //setResultAnim(open_anim);
        btnNext.setClickable(true);
    }

    private void setQuizViews() {
        try {
            Quiz queriedQuiz = getQuiz(quiz.getObjectId());

            ParseFile image = queriedQuiz.getImage();

            ivQuizImage.setVisibility(View.INVISIBLE);
            //ivQuizImage.startAnimation(close_anim);

            if (image != null ) {
                Glide.with(getBaseContext()).load(image.getUrl()).into(ivQuizImage);
            }

            ivQuizImage.setVisibility(View.VISIBLE);
            //ivQuizImage.startAnimation(open_anim);

            tvQuizMessage.setText(queriedQuiz.getMessage());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setStartVisibility(int visibility) {
        btnStart.setVisibility(visibility);
        tvStartTitle.setVisibility(visibility);
        ivStartImage.setVisibility(visibility);
        tvStartMessage.setVisibility(visibility);
    }

    private void setStartAnim(Animation anim) {
        btnStart.startAnimation(anim);
        tvStartTitle.startAnimation(anim);
        ivStartImage.startAnimation(anim);
        tvStartMessage.startAnimation(anim);
    }

    private void setBackgroundVisibility(int visibility) {
        ivQuizImage.setVisibility(visibility);
        tvQuizQuestion.setVisibility(visibility);
    }

    private void setBackgroundAnim(Animation anim) {
        ivQuizImage.startAnimation(anim);
        tvQuizQuestion.startAnimation(anim);
    }

    private void setAnswerButtonVisibility(int visibility) {
        btnFake.setVisibility(visibility);
        btnTrue.setVisibility(visibility);
    }

    private void setAnswerButtonAnim(Animation anim) {
        btnFake.startAnimation(anim);
        btnTrue.startAnimation(anim);
    }

    private void setAnswerButtonClickable(boolean clickable) {
        btnFake.setClickable(clickable);
        btnTrue.setClickable(clickable);
    }

    private void setResultVisibility(int visibility) {
        tvQuizCorrectness.setVisibility(visibility);
        tvQuizMessage.setVisibility(visibility);
        btnNext.setVisibility(visibility);
    }

    private void setResultAnim(Animation anim) {
        tvQuizCorrectness.startAnimation(anim);
        tvQuizMessage.startAnimation(anim);
        btnNext.startAnimation(anim);
    }
}
