/*
 * Nombre del proyecto: FreeTime
 * Autores: Lucas Baruja, Leonardo Duarte, Ezequiel Arce, Iván Samudio
 * Descripción: Recibe y gestiona las notificaciones enviadas a los usuarios.
 * Fecha de creación: 04/11/2024
 * Forma de utilizar: Configurado para recibir notificaciones del sistema.
 */

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
