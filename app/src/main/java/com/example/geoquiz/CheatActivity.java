package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.CheatActivity.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.geoquiz.CheatActivity.answer_shown";

    private static final String ARG_ANSWER_SHOW = "answer_shown";

    private boolean mAnswerBoolean;
    private boolean mAnswerShown;
    private TextView mAnswerTextView;
    private Button mShowAnswerButton;


    public static Intent newIntent(Context contextPackage, boolean answerTrue){
        Intent intent = new Intent(contextPackage, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE,answerTrue);
        return intent;
    }

    private void setAnswerShownResult(boolean isAnswerShown){
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK,data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        if (savedInstanceState!=null){
            mAnswerShown = savedInstanceState.getBoolean(ARG_ANSWER_SHOW);
        }

        mAnswerBoolean = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE,false);


        mAnswerTextView = findViewById(R.id.answer_text_view);
        if (mAnswerShown){
            if (mAnswerBoolean){
                mAnswerTextView.setText(R.string.true_button);
            }
            else {
                mAnswerTextView.setText(R.string.false_button);
            }
        }
        mShowAnswerButton = findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAnswerShown){
                    if (mAnswerBoolean){
                        mAnswerTextView.setText(R.string.true_button);
                    }
                    else {
                        mAnswerTextView.setText(R.string.false_button);
                    }
                    setAnswerShownResult(true);
                    mAnswerShown = true;


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        int cx = mShowAnswerButton.getWidth() / 2;
                        int cy = mShowAnswerButton.getHeight() / 2;
                        float radius = mShowAnswerButton.getWidth();
                        Animator anim = ViewAnimationUtils
                                .createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mShowAnswerButton.setVisibility(View.INVISIBLE);
                            }
                        });
                        anim.start();
                    }
                    else {
                        mShowAnswerButton.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAnswerShown){
            outState.putBoolean(ARG_ANSWER_SHOW, mAnswerShown);
        }
    }

    public static boolean wasAnswerShown(Intent result){
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN,false);
    }
}