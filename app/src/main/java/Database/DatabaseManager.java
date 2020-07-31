package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import Objects.ListObject;

public class DatabaseManager extends SQLiteOpenHelper {

    public static final int VERSION = 1;
    public static final String DATABASE_NAME = "todo.db";
    public static final String LISTS_TABLE = "lists";
    public static final String TASKS_TABLE = "todo";


    public DatabaseManager(@Nullable Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + LISTS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,ICON TEXT,THEME TEXT,ISGROUP INTEGER DEFAULT 0)");
        db.execSQL("create table " + TASKS_TABLE + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,DESCRIPTION TEXT,ISFINISHED INTEGER,PARENTID INTEGER,DUEDATE TEXT,DUETIME TEXT)");
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

    public int AddTaskToDatabase(String taskDescription, boolean isFinished, int parentID, String dueDate, String dueTime){
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

    public Cursor getAllLists(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from " + LISTS_TABLE, null);
    }

    public Cursor getAllTasksUnderList(int parentID){
        return this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE PARENTID = " + parentID, null);
    }

    public Cursor getTasksDueToday(String date){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + TASKS_TABLE + " WHERE DUEDATE = ?", new String[] { date });
        return cursor;
    }

    public void removeTask(int ID){
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TASKS_TABLE, "ID = " + ID, null);
    }

    public void updateTaskToFinished(int ID){
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 1);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public void updateTaskToUnfinished(int ID){
        ContentValues values = new ContentValues();
        values.put("ISFINISHED", 0);

        this.getWritableDatabase().update(TASKS_TABLE, values, "ID = " + ID, null);
    }

    public String getListTheme(int id){
        Cursor cursor = this.getWritableDatabase().rawQuery("SELECT * FROM " + LISTS_TABLE + " WHERE ID = " + id, null);
        cursor.moveToNext();
        return cursor.getString(3);
    }

    public void updateTheme(int ID, String themeName){
        ContentValues values = new ContentValues();
        values.put("THEME", themeName);

        Log.e("sq theme update", ID + "   " + themeName);
        this.getWritableDatabase().update(LISTS_TABLE, values, "ID = " + ID, null);
    }

}