package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Objects.Reminder;
import Objects.TaskObject;

public class AlarmDatabaseHelper extends SQLiteOpenHelper implements BaseDatabase {

    public AlarmDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + REMINDERS_TABLE +
                " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "TASKSID INTEGER," +
                "DESCRIPTION TEXT," +
                "DAY INTEGER," +
                "MONTH INTEGER," +
                "YEAR INTEGER," +
                "HOUR INTEGER," +
                "MINUTE INTEGER) ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + REMINDERS_TABLE);
        onCreate(db);
    }

    public Reminder getReminder(int taskID) {
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + REMINDERS_TABLE + " WHERE TASKID = " + taskID, null);
        cursor.moveToNext();

        Reminder reminder = new Reminder(cursor.getInt(cursor.getColumnIndex(REMINDERS_TASK_ID)),
                cursor.getInt(cursor.getColumnIndex(REMINDERS_DAY)),
                cursor.getInt(cursor.getColumnIndex(REMINDERS_MONTH)),
                cursor.getInt(cursor.getColumnIndex(REMINDERS_YEAR)),
                cursor.getInt(cursor.getColumnIndex(REMINDERS_HOUR)),
                cursor.getInt(cursor.getColumnIndex(REMINDERS_MINUTE)),
                cursor.getString(cursor.getColumnIndex(REMINDERS_DESCRIPTION)));

        return reminder;
    }

    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();

        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + REMINDERS_TABLE, null);

        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder(cursor.getInt(cursor.getColumnIndex(REMINDERS_TASK_ID)),
                        cursor.getInt(cursor.getColumnIndex(REMINDERS_DAY)),
                        cursor.getInt(cursor.getColumnIndex(REMINDERS_MONTH)),
                        cursor.getInt(cursor.getColumnIndex(REMINDERS_YEAR)),
                        cursor.getInt(cursor.getColumnIndex(REMINDERS_HOUR)),
                        cursor.getInt(cursor.getColumnIndex(REMINDERS_MINUTE)),
                        cursor.getString(cursor.getColumnIndex(REMINDERS_DESCRIPTION)));
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

    public void addReminder(TaskObject task) {
        ContentValues values = new ContentValues();
        values.put(REMINDERS_TASK_ID, task.getID());
        values.put(REMINDERS_DESCRIPTION, task.getTaskDescription());
        values.put(REMINDERS_DAY, task.getDay());
        values.put(REMINDERS_MONTH, task.getMonth());
        values.put(REMINDERS_YEAR, task.getYear());
        values.put(REMINDERS_HOUR, task.getHour());
        values.put(REMINDERS_MINUTE, task.getMinute());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(REMINDERS_TABLE, null, values);
    }

    public void deleteReminder(int ID) {
        this.getWritableDatabase().delete(REMINDERS_TABLE, REMINDERS_TASK_ID + "=" + ID, null);
    }

    public void updateReminder(TaskObject task) {
        ContentValues values = new ContentValues();
        values.put(REMINDERS_TASK_ID, task.getID());
        values.put(REMINDERS_DESCRIPTION, task.getTaskDescription());
        values.put(REMINDERS_DAY, task.getDay());
        values.put(REMINDERS_MONTH, task.getMonth());
        values.put(REMINDERS_YEAR, task.getYear());
        values.put(REMINDERS_HOUR, task.getHour());
        values.put(REMINDERS_MINUTE, task.getMinute());

        this.getWritableDatabase().insert(REMINDERS_TABLE, null, values);
    }
}
