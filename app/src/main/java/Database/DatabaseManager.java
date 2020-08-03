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
                    "ISFINISHED INTEGER," +
                    "PARENTID INTEGER," +
                    "DUEDATE TEXT," +
                    "DUETIME TEXT," +
                    "ISIMPORTANT INTEGER DEFAULT 0)");
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
        values.put("DESCRIPTION", taskDescription);
        values.put("ISFINISHED", isFinished);
        values.put("PARENTID", parentID);
        values.put("DUEDATE", dueDate);
        values.put("DUETIME", dueTime);

        db.insert(TASKS_TABLE, null, values);

        final String MY_QUERY = "SELECT last_insert_rowid() FROM " + TASKS_TABLE;
        Cursor cur = this.getWritableDatabase().rawQuery(MY_QUERY, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }

    public Cursor getAllLists() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + LISTS_TABLE, null);
    }

    public Cursor getAllTasksUnderList(int parentID) {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE PARENTID = " + parentID, null);
    }

    public Cursor getTasksDueToday(String date) {
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE DUEDATE = ?", new String[]{date});
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

    public void removeListAndChildTasks(int ID) {
        this.getWritableDatabase().delete(LISTS_TABLE, "ID = " + ID, null);
        this.getWritableDatabase().delete(TASKS_TABLE, "PARENTID = " + ID, null);
    }

    public void updateTaskToFinished(int ID) {
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 1);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public void updateTaskToUnfinished(int ID) {
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 0);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public void updateTaskToImportant(int ID) {
        ContentValues values = new ContentValues();
        values.put("ISIMPORTANT", 1);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public void updateTaskToNotImportant(int ID) {
        ContentValues values = new ContentValues();
        values.put("ISIMPORTANT", 0);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public String getListTheme(int id) {
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + LISTS_TABLE + " WHERE ID = " + id, null);
        cursor.moveToNext();
        return cursor.getString(3);
    }

    public void updateTheme(int ID, String themeName) {
        ContentValues values = new ContentValues();
        values.put("THEME", themeName);
        this.getWritableDatabase().update(LISTS_TABLE, values, "ID = " + ID, null);
    }

    public void renameList(int ID, String title) {
        ContentValues values = new ContentValues();
        values.put("NAME", title);
        this.getWritableDatabase().update(LISTS_TABLE, values, "ID = " + ID, null);
    }

    public int getListIDfromTaskID(int taskID){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE ID = " + taskID, null);
        cursor.moveToNext();
        return cursor.getInt(cursor.getColumnIndex("PARENTID"));
    }

}