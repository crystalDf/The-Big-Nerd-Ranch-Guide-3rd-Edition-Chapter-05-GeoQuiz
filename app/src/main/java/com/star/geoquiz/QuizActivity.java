package com.star.geoquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class QuizActivity extends AppCompatActivity {

    private static final String KEY_INDEX = "index";
    private static final String KEY_QUESTIONS_ANSWERED_CORRECTLY = "questionsAnsweredCorrectly";
    private static final String KEY_QUESTIONS_ANSWERED = "questionsAnswered";
    private static final String KEY_ALL_QUESTIONS_ANSWERED = "allQuestionsAnswered";
    private static final String KEY_IS_CHEATERS = "isCheater";
    private static final String KEY_CHEAT_LEFT_TIMES = "cheatLeftTimes";

    private static final int REQUEST_CODE_CHEAT = 0;

    private static final int CHEAT_TIMES = 3;

    private Button mTrueButton;
    private Button mFalseButton;

    private TextView mQuestionTextView;

    private Button mNextButton;
    private Button mPrevButton;

    private ImageButton mPrevImageButton;
    private ImageButton mNextImageButton;

    private Button mCheatButton;

    private TextView mCheatLeftTimesTextView;

    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true),
    };

    private int mCurrentIndex = 0;

    private boolean[] mIsCheaters = new boolean[mQuestionBank.length];

    private boolean[] mQuestionsAnsweredCorrectly = new boolean[mQuestionBank.length];
    private boolean[] mQuestionsAnswered = new boolean[mQuestionBank.length];
    private boolean mAllQuestionsAnswered;

    private int mCheatLeftTimes = CHEAT_TIMES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mQuestionsAnsweredCorrectly = savedInstanceState.getBooleanArray(KEY_QUESTIONS_ANSWERED_CORRECTLY);
            mQuestionsAnswered = savedInstanceState.getBooleanArray(KEY_QUESTIONS_ANSWERED);
            mAllQuestionsAnswered = savedInstanceState.getBoolean(KEY_ALL_QUESTIONS_ANSWERED);

            mIsCheaters = (boolean[]) savedInstanceState.getSerializable(KEY_IS_CHEATERS);
            mCheatLeftTimes = savedInstanceState.getInt(KEY_CHEAT_LEFT_TIMES);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(v -> getNextQuestion());

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(v -> checkAnswer(true));

        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(v -> checkAnswer(false));

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(v -> getNextQuestion());

        mPrevButton = (Button) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(v -> getPrevQuestion());

        mNextImageButton = (ImageButton) findViewById(R.id.next_image_button);
        mNextImageButton.setOnClickListener(v -> getNextQuestion());

        mPrevImageButton = (ImageButton) findViewById(R.id.prev_image_button);
        mPrevImageButton.setOnClickListener(v -> getPrevQuestion());

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(v -> {
            boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
            Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
            startActivityForResult(intent, REQUEST_CODE_CHEAT);
        });

        mCheatLeftTimesTextView = (TextView) findViewById(R.id.cheat_left_times_text_view);

        updateQuestion();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_QUESTIONS_ANSWERED_CORRECTLY, mQuestionsAnsweredCorrectly);
        outState.putBooleanArray(KEY_QUESTIONS_ANSWERED, mQuestionsAnswered);
        outState.putBoolean(KEY_ALL_QUESTIONS_ANSWERED, mAllQuestionsAnswered);
        outState.putBooleanArray(KEY_IS_CHEATERS, mIsCheaters);
        outState.putInt(KEY_CHEAT_LEFT_TIMES, mCheatLeftTimes);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_CHEAT && resultCode == RESULT_OK) {
            if (data != null) {
                mIsCheaters[mCurrentIndex] = CheatActivity.wasAnswerShown(data);
                mCheatLeftTimes--;
                updateQuestion();
            }
        }
    }

    private void getNextQuestion() {

        while (true) {
            mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
            if (!mQuestionsAnswered[mCurrentIndex]) {
                break;
            }
        }

        updateQuestion();
    }

    private void getPrevQuestion() {

        while (true) {
            mCurrentIndex = (mCurrentIndex - 1 + mQuestionBank.length) % mQuestionBank.length;
            if (!mQuestionsAnswered[mCurrentIndex]) {
                break;
            }
        }

        updateQuestion();
    }

    private void updateQuestion() {
        int questionResId = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(questionResId);

        mCheatLeftTimesTextView.setText(getString(R.string.cheat_left_times, mCheatLeftTimes));

        mTrueButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mQuestionsAnswered[mCurrentIndex]);
        mCheatButton.setEnabled((!mIsCheaters[mCurrentIndex]) && (mCheatLeftTimes > 0));

        mPrevButton.setEnabled(!mAllQuestionsAnswered);
        mNextButton.setEnabled(!mAllQuestionsAnswered);
        mPrevImageButton.setEnabled(!mAllQuestionsAnswered);
        mNextImageButton.setEnabled(!mAllQuestionsAnswered);
    }

    private void checkAnswer(boolean userPressedTrue) {

        mTrueButton.setEnabled(false);
        mFalseButton.setEnabled(false);
        mCheatButton.setEnabled(false);

        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        mQuestionsAnsweredCorrectly[mCurrentIndex] = (userPressedTrue == answerIsTrue);

        int messageResId = (userPressedTrue == answerIsTrue) ?
                R.string.correct_toast : R.string.incorrect_toast;

        if (mIsCheaters[mCurrentIndex]) {
            messageResId = R.string.judgment_toast;
        }

        Toast.makeText(QuizActivity.this, messageResId,
                Toast.LENGTH_SHORT).show();

        updateAnswered();

        if (mAllQuestionsAnswered) {

            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(false);
            mPrevImageButton.setEnabled(false);
            mNextImageButton.setEnabled(false);

            Toast.makeText(QuizActivity.this,
                    "Score: " + new DecimalFormat("######0.00").format(getScore()),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateAnswered() {
        mQuestionsAnswered[mCurrentIndex] = true;

        mAllQuestionsAnswered = true;

        for (boolean answered : mQuestionsAnswered) {
            if (!answered) {
                mAllQuestionsAnswered = false;
                break;
            }
        }
    }

    private double getScore() {
        int numberOfCorrect = 0;

        for (boolean correct : mQuestionsAnsweredCorrectly) {
            if (correct) {
                numberOfCorrect++;
            }
        }

        Toast.makeText(QuizActivity.this,
                "Number of Correct Answer: " + numberOfCorrect,
                Toast.LENGTH_SHORT).show();

        return ((double) numberOfCorrect) / mQuestionBank.length * 100;
    }
}
