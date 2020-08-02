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
import android.media.RingtoneManager;
import android.os.Build;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.todo.R;

import java.util.Calendar;

import Database.AlarmDatabaseHelper;
import Objects.Reminder;
import Objects.TaskObject;

public class AlarmReceiver extends BroadcastReceiver {
    AlarmManager mAlarmManager;
    PendingIntent mPendingIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        int taskID = Integer.parseInt(intent.getStringExtra(AlarmDatabaseHelper.REMINDERS_TASK_ID));

        // Get notification title from Reminder Database
        AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(context);
        Reminder reminder = alarmDatabaseHelper.getReminder(taskID);
        String mTitle = reminder.getTaskDescription();

        // Create intent to open ReminderEditActivity on notification click
//        Intent editIntent = new Intent(context, ReminderEditActivity.class);
//        editIntent.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, Integer.toString(mReceivedID));
//        PendingIntent mClick = PendingIntent.getActivity(context, mReceivedID, editIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Due Tasks")
                .setSmallIcon(R.drawable.icon_alarm)
                .setContentTitle("Test Title")
                .setContentText("Test Content Description For Notification")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        createNotificationChannel(context);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(reminder.getTaskID(), builder.build());

    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Due Tasks", "name", importance);
            channel.setDescription("This is the channel description");
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setAlarm(Context context, Reminder reminder) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Put Reminder ID in Intent Extra
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(AlarmDatabaseHelper.REMINDERS_TASK_ID, Integer.toString(reminder.getTaskID()));
        mPendingIntent = PendingIntent.getBroadcast(context, reminder.getTaskID(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Start alarm using notification time
        mAlarmManager.set(AlarmManager.ELAPSED_REALTIME,
                reminder.getCalendar().getTimeInMillis(),
                mPendingIntent);

        // Restart alarm if device is rebooted
        ComponentName receiver = new ComponentName(context, AlarmHelpers.BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public void cancelAlarm(Context context, int ID) {
        mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Cancel Alarm using Reminder ID
        mPendingIntent = PendingIntent.getBroadcast(context, ID, new Intent(context, AlarmReceiver.class), 0);
        mAlarmManager.cancel(mPendingIntent);

        // Disable alarm
        ComponentName receiver = new ComponentName(context, BootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

}