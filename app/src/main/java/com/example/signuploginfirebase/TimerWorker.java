package com.example.signuploginfirebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class TimerWorker extends Worker {

    private static final String CHANNEL_ID = "timer_channel";
    private static final int NOTIFICATION_ID = 1;

    public TimerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Get the timer duration and routine name from input data
        long durationInMillis = getInputData().getLong("TIME_LEFT", 0);
        String routineName = getInputData().getString("routineName");

        // Create notification manager to update notifications
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if needed (for devices >= API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Timer Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        // Start a countdown timer to update the notification
        new CountDownTimer(durationInMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                updateNotification(notificationManager, millisUntilFinished, routineName);
            }

            @Override
            public void onFinish() {
                playSound();
                updateNotification(notificationManager, 0, routineName);
                // No need for stopSelf(); just let the worker finish.
            }
        }.start();

        // Indicate that the work is finished
        return Result.success();
    }

    private void updateNotification(NotificationManager notificationManager, long timeLeftInMillis, String routineName) {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format("%02d:%02d", minutes, seconds);

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle("Timer Running for " + routineName)
                .setContentText("Time left: " + timeFormatted)
                .setSmallIcon(R.drawable.ic_timer)
                .setOngoing(true)  // Prevent dismissal
                .build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void playSound() {
        // Play sound when timer finishes
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.timer_end_sound);
        mediaPlayer.setOnCompletionListener(mp -> mp.release());
        mediaPlayer.start();
    }
}
