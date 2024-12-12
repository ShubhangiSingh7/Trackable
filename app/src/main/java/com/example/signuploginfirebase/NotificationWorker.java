package com.example.signuploginfirebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Retrieve the data from the WorkRequest
        String routineName = getInputData().getString("routine_name");

        // Display the notification
        sendNotification(routineName);

        return Result.success();
    }

    private void sendNotification(String routineName) {
        NotificationManager notificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create Notification Channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "routine_notifications",
                    "Routine Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for routine reminders");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Build the Notification
        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "routine_notifications")
                .setSmallIcon(R.drawable.icon) // Replace with your notification icon
                .setContentTitle("Routine Reminder")
                .setContentText("Don't forget: " + routineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        // Play the sound (e.g., beep or alarm sound)
        MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.timer_end_sound); // Replace with your sound file
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(mp -> mp.release());

        // Show the notification
        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
