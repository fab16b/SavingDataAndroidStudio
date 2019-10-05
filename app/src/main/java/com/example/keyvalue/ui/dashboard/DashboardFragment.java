package com.example.keyvalue.ui.dashboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.keyvalue.R;

import java.util.Locale;

public class DashboardFragment extends Fragment {
    private CountDownTimer countDownTimer;
    private Button startButton;
    private Button pushButton;
    private int timer = 30;
    private int newTimesPressed = 0;
    private int highestValue = 0;

    private TextView mTextViewCountDown;
    private boolean mTimerRunning;
    private long mTimeLeftInMilles = timer;

    private DashboardViewModel dashboardViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        mTextViewCountDown = root.findViewById(R.id.count_down);

        pushButton = root.findViewById(R.id.push_button);
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                int defaultValue = getResources().getInteger(R.integer.saved_times_pressed_default_key);
                int timesPressed = sharedPref.getInt(getString(R.string.saved_button_press_count_key), defaultValue);

                newTimesPressed = newTimesPressed +1;

                if (newTimesPressed > highestValue){
                    highestValue = newTimesPressed;
                }

                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_button_press_count_key), newTimesPressed);
                editor.apply();

                textView.setText("Button has been pressed " + Integer.toString(newTimesPressed) + " times");
            }
        });

        startButton = root.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pushButton.setEnabled(true);
                startButton.setEnabled(false);
                timer = 30;
                countDownTimer.start();
            }
        });

        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMilles = millisUntilFinished;
                updateCountDownText();
                timer--;
                pushButton.setEnabled(true);

                mTimerRunning = true;

            }

            @Override
            public void onFinish() {
                startButton.setEnabled(true);
                pushButton.setEnabled(false);
                newTimesPressed = 0;
                TextView highSoreView = root.findViewById(R.id.high_score);
                highSoreView.setText("High Score is: " + highestValue); //set text for text view
            }

            private void updateCountDownText(){
                int minutes = (int) (mTimeLeftInMilles / 1000) / 60;
                int seconds = (int) (mTimeLeftInMilles / 1000) % 60;

                String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
                mTextViewCountDown.setText(timeLeftFormatted);
            }
        };

        return root;
    }

}