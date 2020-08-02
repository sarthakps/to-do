package AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

import Database.AlarmDatabaseHelper;
import Objects.Reminder;

public class BootReceiver extends BroadcastReceiver {

    private AlarmReceiver alarmReceiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(context);
            alarmReceiver = new AlarmReceiver();

            List<Reminder> reminders = alarmDatabaseHelper.getAllReminders();

            for (Reminder rm : reminders) {
                // Cancel existing notification of the reminder by using its ID
                alarmReceiver.cancelAlarm(context, rm.getTaskID());

                // Create a new notification
                alarmReceiver.setAlarm(context, rm);
            }
        }
    }
}