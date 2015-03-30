
package com.shenkar.tamar.todolist_vr6;


        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;

public class ReminderNotification extends BroadcastReceiver{

    public ReminderNotification() {
        // TODO Auto-generated constructor stub
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        // The PendingIntent to launch our activity if the user selects this notification
        Task task = (Task)intent.getSerializableExtra("task");
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        // Set the info for the views that show in the notification panel.

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Build notification
        Notification noti = new Notification.Builder(context)
                .setContentTitle("Time Reminder").setContentText(task.getTaskDescription())
                .setSmallIcon(R.drawable.task_launcher).setContentIntent(contentIntent)
                .build();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, noti);


    }

}