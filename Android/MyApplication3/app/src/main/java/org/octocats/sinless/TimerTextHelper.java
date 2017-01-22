package org.octocats.sinless;

import android.os.Handler;
import android.widget.TextView;

/**
 * Created by ritwi on 22-01-2017.
 */

public class TimerTextHelper implements Runnable {
    private final Handler handler = new Handler();
    private final TextView textView;
    private volatile long startTime;
    private volatile long elapsedTime;

    public TimerTextHelper(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void run() {
        long millis = System.currentTimeMillis() - startTime;
        int seconds = (int) (millis / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        textView.setText(String.format("%d:%02d", minutes, seconds));

        if (elapsedTime == -1) {
            handler.postDelayed(this, 500);
        }
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.elapsedTime = -1;
        handler.post(this);
    }

    public void stop() {
        this.elapsedTime = System.currentTimeMillis() - startTime;
        handler.removeCallbacks(this);
    }

    public long getElapsedTime() {
        return elapsedTime;
    }
}