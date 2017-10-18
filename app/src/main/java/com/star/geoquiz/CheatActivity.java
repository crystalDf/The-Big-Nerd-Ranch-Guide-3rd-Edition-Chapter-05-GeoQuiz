package com.star.geoquiz;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private static final String EXTRA_ANSWER_IS_TRUE =
            "com.star.geoquiz.answer_is_true";

    private static final String EXTRA_ANSWER_SHOWN =
            "com.star.geoquiz.answer_shown";

    private static final String ANSWER_TEXT = "answer_text";

    private boolean mAnswerIsTrue;

    private TextView mAnswerTextView;
    private Button mShowAnswerButton;

    private TextView mApiLevelTextView;

    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);

        return intent;
    }

    public static boolean wasAnswerShown(Intent intent) {
        return intent.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);

        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button) findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(v -> {
            if (mAnswerIsTrue) {
                mAnswerTextView.setText(R.string.true_button);
            } else {
                mAnswerTextView.setText(R.string.false_button);
            }

            setAnswerShowResult(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = mShowAnswerButton.getWidth() / 2;
                int cy = mShowAnswerButton.getHeight() / 2;
                float radius = (float) Math.sqrt(Math.pow(cx, 2) + Math.pow(cy, 2));
                Animator animator = ViewAnimationUtils.createCircularReveal(
                        mShowAnswerButton, cx, cy, radius, 0
                );
                animator.setDuration(4000);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mShowAnswerButton.setVisibility(View.INVISIBLE);
                    }
                });
                animator.start();
            } else {
                mShowAnswerButton.setVisibility(View.INVISIBLE);
            }
        });

        mApiLevelTextView = (TextView) findViewById(R.id.api_level);
        mApiLevelTextView.setText(getString(R.string.api_level, Build.VERSION.SDK_INT));

        if (savedInstanceState != null) {
            String answerText = savedInstanceState.getString(ANSWER_TEXT);

            if (!TextUtils.isEmpty(answerText)) {
                mAnswerTextView.setText(answerText);

                setAnswerShowResult(true);
            }
        }
    }

    private void setAnswerShowResult(boolean isAnswerShown) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ANSWER_TEXT, mAnswerTextView.getText().toString());
    }
}
