package Database;

public interface BaseDatabase {
    int VERSION = 1;
    String DATABASE_NAME = "todo.db";
    String LISTS_TABLE = "lists";
    String TASKS_TABLE = "todo";
    String REMINDERS_TABLE = "reminders";

    //Global Constants
    int TODAY_ID = -200;
    int TOMORROW_ID = -400;
    int IMPORTANT_ID = -600;

    //Reminders table columns
    String REMINDERS_ID = "ID";
    String REMINDERS_TASK_ID = "TASKSID";
    String REMINDERS_DESCRIPTION = "DESCRIPTION";
    String REMINDERS_DAY = "DAY";
    String REMINDERS_MONTH = "MONTH";
    String REMINDERS_YEAR = "YEAR";
    String REMINDERS_HOUR = "HOUR";
    String REMINDERS_MINUTE = "MINUTE";

    //Tasks table columns
    String TASKS_ID = "ID";
    String TASKS_PARENT_ID = "PARENTID";
    String TASKS_DESCRIPTION = "DESCRIPTION";
    String TASKS_DATE = "DUEDATE";
    String TASKS_TIME = "DUETIME";
    String TASKS_IS_FINISHED = "ISFINISHED";
    String TASKS_IS_IMPORTANT = "ISIMPORTANT";
    String TASKS_IS_NOTIF_PENDING = "ISNOTIFICATIONPENDING";

}
