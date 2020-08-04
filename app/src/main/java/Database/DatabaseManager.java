package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import AlarmHelpers.AlarmReceiver;
import Objects.ListObject;
import Objects.Reminder;
import Objects.TaskObject;

public class DatabaseManager extends SQLiteOpenHelper implements BaseDatabase{

    Context context;

    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LISTS_TABLE +
                    " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "NAME TEXT," +
                    "ICON TEXT," +
                    "THEME TEXT," +
                    "ISGROUP INTEGER DEFAULT 0)");
        db.execSQL("create table " + TASKS_TABLE +
                    " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "DESCRIPTION TEXT," +
                    "ISFINISHED INTEGER DEFAULT 0," +
                    "PARENTID INTEGER," +
                    "DUEDATE TEXT DEFAULT 0," +
                    "DUETIME TEXT DEFAULT 0," +
                    "ISIMPORTANT INTEGER DEFAULT 0," +
                    "ISNOTIFICATIONPENDING INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + LISTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
        onCreate(db);
    }

    public int AddListToDB(String name, String iconName, String themeName, boolean isGroup) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("NAME", name);
        values.put("ICON", iconName);
        values.put("THEME", themeName);
        values.put("ISGROUP", isGroup);

        db.insert(LISTS_TABLE, null, values);

        final String MY_QUERY = "SELECT last_insert_rowid() FROM " + LISTS_TABLE;
        Cursor cur = this.getWritableDatabase().rawQuery(MY_QUERY, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public int AddTaskToDatabase(String taskDescription, boolean isFinished, int parentID, String dueDate, String dueTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TASKS_DESCRIPTION, taskDescription);
        values.put(TASKS_IS_FINISHED, isFinished);
        values.put(TASKS_PARENT_ID, parentID);
        values.put(TASKS_DATE, dueDate);
        values.put(TASKS_TIME, dueTime);

        if(!dueDate.equals("0") && !dueTime.equals("0")){
            values.put(TASKS_IS_NOTIF_PENDING, "1");
        }

        db.insert(TASKS_TABLE, null, values);

        final String MY_QUERY = "SELECT last_insert_rowid() FROM " + TASKS_TABLE;
        Cursor cur = this.getWritableDatabase().rawQuery(MY_QUERY, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public void UpdateTaskInDatabase(int taskID, String taskDescription, String date, String time, Calendar cal){
        ContentValues values = new ContentValues();
        values.put(TASKS_DESCRIPTION, taskDescription);
        values.put(TASKS_DATE, date);
        values.put(TASKS_TIME, time);

        if(System.currentTimeMillis() - cal.getTimeInMillis() > 0){
            values.put(TASKS_IS_NOTIF_PENDING, "1");
            new AlarmReceiver().setAlarm(context, getReminder(taskID), getListIDfromTaskID(taskID));
        }

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + LISTS_TABLE, null);
    }

    public List<Reminder> getPendingReminders() {
        List<Reminder> reminderList = new ArrayList<>();
        TaskObject task;

        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE ISNOTIFICATIONPENDIND = 1", null);

        if (cursor.moveToFirst()) {
            do {
                task = new TaskObject(cursor.getString(cursor.getColumnIndex(TASKS_DESCRIPTION)),
                                                "1".equals(cursor.getString(cursor.getColumnIndex(TASKS_IS_FINISHED))),
                                                true);
                task.setMarkedImportant("1".equals(cursor.getString(6)));
                task.setID(cursor.getInt(0));
                task.setDate(cursor.getString(4));
                task.setTime(cursor.getString(5));
                reminderList.add(new Reminder(task.getID(), task.getDay(), task.getMonth(), task.getYear(), task.getHour(), task.getMinute(), task.getTaskDescription()));
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

    public Cursor getAllTasksUnderList(int listID) {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE PARENTID = " + listID, null);
    }

    public Cursor getTasksDueToday(String date) {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE DUEDATE = ?", new String[]{date});
    }

    public int getNumberOfTasksDueToday(){
        String date;
        Calendar c = Calendar.getInstance();
        date = c.get(Calendar.DAY_OF_MONTH) + "-" + (c.get(Calendar.MONTH)+1) + "-" + c.get(Calendar.YEAR);
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE DUEDATE = ?", new String[]{date});

        int count = 0;
        while (cursor.moveToNext())
            count++;

        cursor.close();

        return count;
    }

    public Cursor getImportantTasks() {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE ISIMPORTANT=? AND ISFINISHED=?", new String[] {"1", "0"});
    }

    public Reminder getReminder(int taskID){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE ID = " + taskID, null);
        cursor.moveToNext();

        TaskObject task = new TaskObject(cursor.getString(1), "1".equals(cursor.getString(2)), "1".equals(cursor.getString(4)));
        task.setMarkedImportant("1".equals(cursor.getString(6)));
        task.setID(cursor.getInt(0));
        task.setDate(cursor.getString(4));
        task.setTime(cursor.getString(5));

        return new Reminder(task.getID(), task.getDay(), task.getMonth(), task.getYear(), task.getHour(), task.getMinute(), task.getTaskDescription());
    }

    public int getListIcon(int listID){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + LISTS_TABLE + " WHERE ID = " + listID, null);
        if(cursor.moveToNext()){
            return context.getResources().getIdentifier(cursor.getString(2), "drawable", context.getPackageName());
        }

        return context.getResources().getIdentifier("icon_alarm", "drawable", context.getPackageName());
    }

    public void removeTask(int ID) {
        this.getWritableDatabase().delete(TASKS_TABLE, "ID = " + ID, null);
    }

    public void removeListAndChildTasks(int listID) {
        this.getWritableDatabase().delete(LISTS_TABLE, "ID = " + listID, null);
        this.getWritableDatabase().delete(TASKS_TABLE, "PARENTID = " + listID, null);
    }

    public void hasBeenNotified(int taskID){
        ContentValues values = new ContentValues();
        values.put(TASKS_IS_NOTIF_PENDING, "0");

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public void updateTaskToFinished(int taskID) {
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 1);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public void updateTaskToUnfinished(int taskID) {
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 0);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public void updateTaskToImportant(int taskID) {
        ContentValues values = new ContentValues();
        values.put("ISIMPORTANT", 1);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public void updateTaskToNotImportant(int taskID) {
        ContentValues values = new ContentValues();
        values.put("ISIMPORTANT", 0);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + taskID, null);
    }

    public String getListTheme(int listID) {
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + LISTS_TABLE + " WHERE ID = " + listID, null);
        cursor.moveToNext();
        return cursor.getString(3);
    }

    public void updateTheme(int listID, String themeName) {
        ContentValues values = new ContentValues();
        values.put("THEME", themeName);
        this.getWritableDatabase().update(LISTS_TABLE, values, "ID = " + listID, null);
    }

    public void renameList(int listID, String title) {
        ContentValues values = new ContentValues();
        values.put("NAME", title);
        this.getWritableDatabase().update(LISTS_TABLE, values, "ID = " + listID, null);
    }

    public int getListIDfromTaskID(int taskID){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE ID = " + taskID, null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("PARENTID"));
    }

    public String getListName(int listID) {
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + LISTS_TABLE + " WHERE ID = " + listID, null);
        if (cursor.moveToNext())
            return cursor.getString(cursor.getColumnIndex("NAME"));
        else
            return "Due Today";
    }

}