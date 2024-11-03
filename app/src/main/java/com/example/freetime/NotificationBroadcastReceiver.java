package com.example.freetime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        String activityName = intent.getStringExtra("activityName");
        int userId = intent.getIntExtra("userId", -1);

        //Log.d("NotificationReceiver", "Received in BroadcastReceiver - Activity Name: " + activityName + ", userId: " + userId);

        NotificationHelper.showNotification(context, notificationId, title, content, userId, activityName);
    }
}
