package AlarmHelpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.DueToday;
import com.example.todo.ListActivity;
import com.example.todo.R;


import java.util.Calendar;

import Database.BaseDatabase;
import Database.DatabaseManager;
import Objects.Reminder;

public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {

        int listID = Integer.parseInt(intent.getStringExtra(BaseDatabase.TASKS_PARENT_ID));


        // check if daily alarm for dueToday is received
        if (listID == BaseDatabase.TODAY_ID) {
            daily_due_today_notification(context);
            return;
        }


        // get taskID and Reminder(taskID)
        DatabaseManager db = new DatabaseManager(context);
        int taskID = Integer.parseInt(intent.getStringExtra(BaseDatabase.REMINDERS_TASK_ID));
        Reminder reminder = db.getReminder(taskID);
        ;

        // creating intent to open ListActivity on clicking the notification
        Intent editIntent = new Intent(context, ListActivity.class);
        editIntent.putExtra(BaseDatabase.TASKS_PARENT_ID, listID);
        PendingIntent mClick = PendingIntent.getActivity(context, reminder.getTaskID(), editIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        //creating intent for action button | mark as done
        Intent actionIntent = new Intent(context, NotificationActionReceiver.class);
        actionIntent.putExtra("ACTION", "mark_as_done");
        actionIntent.putExtra(BaseDatabase.TASKS_ID, taskID + "");
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, taskID, actionIntent, 0);


        //create RemoteViews for custom collapsed notification layout
        RemoteViews collapsedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);
        collapsedNotification.setTextViewText(R.id.notif_collapsed_task_description, reminder.getTaskDescription());
        collapsedNotification.setImageViewResource(R.id.notif_collapsed_icon, db.getListIcon(listID));


        //create RemoteViews for custom collapsed notification layout
        RemoteViews expandedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_expanded);
        expandedNotification.setTextViewText(R.id.notif_expanded_task_description, reminder.getTaskDescription());
        expandedNotification.setTextViewText(R.id.notif_expanded_list_name, db.getListName(listID));
        expandedNotification.setImageViewResource(R.id.notif_expanded_icon, db.getListIcon(listID));


        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_done)
                .setCustomContentView(collapsedNotification)
                .setCustomBigContentView(expandedNotification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);


        // Create notification from builder and display it
        Log.e("NOTIF: ", "8");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setContentIntent(mClick);
        notificationManager.notify(taskID, builder.build());

        //update in database that the task's notification has been sent/displayed
        db.hasBeenNotified(taskID);
    }

    public void setAlarm(Context context, Reminder reminder, int listID) {

        if (listID == BaseDatabase.TODAY_ID) {
            mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra(BaseDatabase.REMINDERS_TASK_ID, reminder.getTaskID() + "");
            intent.putExtra(BaseDatabase.TASKS_PARENT_ID, listID + "");

            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 7);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);

            mPendingIntent = PendingIntent.getBroadcast(context, BaseDatabase.TODAY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY, mPendingIntent);

            ComponentName receiver = new ComponentName(context, AlarmHelpers.BootReceiver.class);
            PackageManager pm = context.getPackageManager();
            pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

            Log.e("DAILY-ALARM", "SET");
            return;
        }

        Log.e("NOTIF: ", reminder.getTaskID() + "");
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(BaseDatabase.REMINDERS_TASK_ID, reminder.getTaskID() + "");
        intent.putExtra(BaseDatabase.TASKS_PARENT_ID, listID + "");
        mPendingIntent = PendingIntent.getBroadcast(context, reminder.getTaskID(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.e("NOTIF: ", "3");
        // Start alarm using notification time

        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, reminder.getCalendar().getTimeInMillis(), mPendingIntent);

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

    private void daily_due_today_notification(Context context) {

        Log.e("DAILY-ALARM", "");

        // creating intent to open ListActivity on clicking the notification
        Intent editIntent = new Intent(context, DueToday.class);
        PendingIntent mClick = PendingIntent.getActivity(context, 0, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        // get number of tasks due today
        int dueTodayTasks = new DatabaseManager(context).getNumberOfTasksDueToday();


        //create RemoteViews for custom notification layout
        RemoteViews collapsedNotification = new RemoteViews(context.getPackageName(), R.layout.notification_collapsed);
        collapsedNotification.setTextViewText(R.id.notif_collapsed_task_description, "You have " + dueTodayTasks + " due today!");
        //collapsedNotification.setViewVisibility(R.id.notif_collapsed_list_name, View.GONE);
        collapsedNotification.setImageViewResource(R.id.notif_collapsed_icon, R.drawable.icon_alarm);


        // Build the Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_done)
                .setCustomContentView(collapsedNotification)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true);


        // Create notification from builder and display it
        Log.e("NOTIF: ", "8");
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        builder.setContentIntent(mClick);
        notificationManager.notify(BaseDatabase.TODAY_ID, builder.build());
    }

}