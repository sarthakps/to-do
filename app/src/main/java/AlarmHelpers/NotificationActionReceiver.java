package AlarmHelpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import Database.BaseDatabase;
import Database.DatabaseManager;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("ACTION");
        int taskID = Integer.parseInt(intent.getStringExtra(BaseDatabase.TASKS_ID));

        Log.e("NAR", action + " " + taskID);

        if(action.equals("mark_as_done")){
            DatabaseManager db = new DatabaseManager(context);
            db.updateTaskToFinished(taskID);
        }
    }
}
