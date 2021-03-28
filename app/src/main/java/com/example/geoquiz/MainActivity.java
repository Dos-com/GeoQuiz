package com.example.geoquiz;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private Button mTrueButton, mFalseButton, mCheatButton;
    private ImageButton mNextButton, mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_1, true),
            new Question(R.string.question_2, true),
            new Question(R.string.question_3, false),
            new Question(R.string.question_4, false),
            new Question(R.string.question_5, true),
            new Question(R.string.question_6, true),
    };
    private boolean mQuestionAnswered[] = new boolean[mQuestionBank.length];
    private int mCurrentIndex = 0;
    private int mCorrectAnswer = 0;
    private int mCheatingCount = 0;

    private boolean mIsCheater;

    private static final String TAG = "QuizActivity";

    private static final String KEY_INDEX = "index";
    private static final String KEY_IS_CHEATER = "is_cheater";
    private static final String KEY_CHEATING_COUNT = "cheating_count";

    private static final int REQUEST_CODE_CHEAT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate(Bundle) called");

        if (savedInstanceState != null){
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX,0);
            mIsCheater = savedInstanceState.getBoolean(KEY_IS_CHEATER,false);
            mCheatingCount = savedInstanceState.getInt(KEY_CHEATING_COUNT,0);
        }

        mTrueButton = findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionAnswered[mCurrentIndex] = true;
                checkAnswer(true);
                updateQuestion();
            }
        });

        mFalseButton = findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionAnswered[mCurrentIndex] = true;
                checkAnswer(false);
                updateQuestion();
            }
        });

        mQuestionTextView = findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuestionAnswered[mCurrentIndex] = true;
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
                updateQuestion();
            }
        });
        updateQuestion();


        mNextButton = findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex+1)%mQuestionBank.length;
                mIsCheater = false;
                updateQuestion();
            }
        });

        mPrevButton = findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex-1)%mQuestionBank.length;
                if (mCurrentIndex<0){
                    mCurrentIndex*=-1;
                }
                mIsCheater = false;
                updateQuestion();
            }
        });


        mCheatButton = findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheatingCount<3){
                    Intent intent = CheatActivity.newIntent(MainActivity.this,mQuestionBank[mCurrentIndex].isAnswerTrue());
                    startActivityForResult(intent,REQUEST_CODE_CHEAT);
                }
                else {
                    Toast.makeText(MainActivity.this,"More you can\'t see answer",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: ");
        outState.putInt(KEY_INDEX,mCurrentIndex);
        outState.putBoolean(KEY_IS_CHEATER,mIsCheater);
        outState.putInt(KEY_CHEATING_COUNT,mCheatingCount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != Activity.RESULT_OK){
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT){
            mIsCheater = CheatActivity.wasAnswerShown(data);
            if (!mQuestionAnswered[mCurrentIndex]){
                mCheatingCount++;
            }

            if (data==null){
                return;
            }
            if (mIsCheater){
                mQuestionAnswered[mCurrentIndex] = true;
            }
        }
    }

    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        mTrueButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
        mFalseButton.setEnabled(!mQuestionAnswered[mCurrentIndex]);
    }

    private void checkAnswer(boolean userPressedTrue){
        boolean answerTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int counter = 0;
        if (mIsCheater){
            Toast.makeText(this,R.string.judgment_toast,Toast.LENGTH_LONG ).show();
        }
        else if (userPressedTrue == answerTrue){
            mCorrectAnswer++;
        }

        for (boolean b:mQuestionAnswered){
            if (b){
                counter++;
            }
        }

        if (counter == mQuestionBank.length){
            String result = "Your percent of truth answer is "+((mCorrectAnswer*100)/mQuestionBank.length) + "%. " + mCorrectAnswer+"/"+mQuestionBank.length;
            Toast.makeText(this,result,Toast.LENGTH_LONG ).show();
        }
    }
}