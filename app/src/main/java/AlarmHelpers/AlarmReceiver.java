package AlarmHelpers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.ListActivity;
import com.example.todo.R;


import Database.AlarmDatabaseHelper;
import Database.BaseDatabase;
import Database.DatabaseManager;
import Objects.Reminder;

public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskID = Integer.parseInt(intent.getStringExtra(AlarmDatabaseHelper.REMINDERS_TASK_ID));
        Log.e("NOTIF", "taskID " + taskID);

        DatabaseManager db = new DatabaseManager(context);
        Reminder reminder = db.getReminder(taskID);

        Log.e("NOTIF: ", "5");

        Log.e("NOTIF: ", "6");

        // creating intent to open ListActivity on clicking the notification
        Intent editIntent = new Intent(context, ListActivity.class);
        editIntent.putExtra(BaseDatabase.TASKS_ID, db.getListIDfromTaskID(reminder.getTaskID()));
        PendingIntent mClick = PendingIntent.getActivity(context, reminder.getTaskID(), editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //creating intent for action button | mark as done
        Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
        actionIntent.putExtra("ACTION", "mark_as_done");
        actionIntent.putExtra(BaseDatabase.TASKS_ID, taskID + "");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, taskID, actionIntent, 0);


        Log.e("NOTIF: ", "7");

        // Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_alarm)
                .setContentTitle("To-Do App")
                .setContentText(reminder.getTaskDescription())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .addAction(R.drawable.icon_done, "Mark as done", actionPendingIntent);

        Log.e("NOTIF: ", reminder.getTaskDescription() + "null");

        Log.e("NOTIF: ", "8");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setContentIntent(mClick);
        notificationManager.notify(taskID, builder.build());

    }

    public void setAlarm(Context context, Reminder reminder, int listID) {
        Log.e("NOTIF: ", reminder.getTaskID() + "");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmDatabaseHelper.REMINDERS_TASK_ID, reminder.getTaskID() + "");
        intent.putExtra("listID", listID + "");
        mPendingIntent = PendingIntent.getBroadcast(context, reminder.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e("NOTIF: ", "3");
        // Start alarm using notification time

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP,  reminder.getCalendar().getTimeInMillis(), mPendingIntent);

        Log.e("NOTIF: ", "4");
        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, AlarmHelpers.BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int taskID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, taskID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}