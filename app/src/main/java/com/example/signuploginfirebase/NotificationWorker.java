package com.example.signuploginfirebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

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

        Notification notification = new NotificationCompat.Builder(getApplicationContext(), "routine_notifications")
                .setSmallIcon(R.drawable.icon) // Replace with your notification icon
                .setContentTitle("Routine Reminder")
                .setContentText("Don't forget: " + routineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(1, notification);
        }
    }
}
