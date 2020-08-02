package Database;

public interface BaseDatabase {
    int VERSION = 1;
    String DATABASE_NAME = "todo.db";
    String LISTS_TABLE = "lists";
    String TASKS_TABLE = "todo";
    String REMINDERS_TABLE = "reminders";

    //Reminders table columns
    String REMINDERS_ID = "ID";
    String REMINDERS_TASK_ID = "TASKSID";
    String REMINDERS_DESCRIPTION = "DESCRIPTION";
    String REMINDERS_DAY = "DAY";
    String REMINDERS_MONTH = "MONTH";
    String REMINDERS_YEAR = "YEAR";
    String REMINDERS_HOUR = "HOUR";
    String REMINDERS_MINUTE = "MINUTE";
}
