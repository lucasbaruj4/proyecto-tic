package com.example.freetime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.freetime.dao.ActivityDao;
import com.example.freetime.dao.UserProgressDao;
import com.example.freetime.entities.Activity;
import com.example.freetime.entities.UserProgress;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ActivityReminderWorker extends Worker {

    private final ActivityDao activityDao;
    private final UserProgressDao userProgressDao;

    public ActivityReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        activityDao = AppDatabase.getDatabase(context).activityDao();
        userProgressDao = AppDatabase.getDatabase(context).userProgressDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        // Obtener el userId desde SharedPreferences
        int userId = context
                .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                .getInt("user_id", -1);

        //Log.d("ActivityReminderWorker", "UserId obtained from SharedPreferences: " + userId);

        if (userId == -1) {
            return Result.failure();
        }

        // Obtener la fecha de hoy
        String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        List<Activity> todaysActivities = activityDao.getActivitiesForDate(todayDate, userId);

        for (Activity activity : todaysActivities) {
            UserProgress progress = userProgressDao.getProgressByUserAndActivity(userId, activity.activityId, todayDate);

            if (progress == null || !progress.wasNotified) {
                setNotificationAlarm(context, activity, todayDate, userId);

                if (progress == null) {
                    progress = new UserProgress(userId, activity.activityId, todayDate, false, true, null, null, "PENDIENTE");
                } else {
                    progress.wasNotified = true;
                }
                userProgressDao.upsertProgress(progress);
            }
        }
        return Result.success();
    }

    private void setNotificationAlarm(Context context, Activity activity, String date, int userId) {
        String[] timeParts = activity.startTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.putExtra("notificationId", activity.activityId);
        intent.putExtra("title", context.getString(R.string.notification_title, activity.name));
        intent.putExtra("content", context.getString(R.string.notification_content));
        intent.putExtra("userId", userId); // Asegura que el userId se pasa correctamente aquí
        intent.putExtra("activityName", activity.name);

       // Log.d("ActivityReminderWorker", "Setting alarm for activity: " + activity.name + ", userId: " + userId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, activity.activityId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
