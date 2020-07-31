package Objects;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TaskObject {

    int ID;
    boolean isTaskFinished;
    boolean isMarkedImportant;
    boolean hasDueDate;
    int day, month, year, hour, minute;
    String taskDescription;

    public TaskObject(String taskDescription, boolean isTaskFinished, boolean hasDueDate){
        this.taskDescription = taskDescription;
        this.isTaskFinished = isTaskFinished;
        this.hasDueDate = hasDueDate;
    }

    public void setDateTime(int day, int month, int year, int hour, int minute){
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
    }

    public String getMonthNameFormattedDate(){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.MONTH, getMonth());
        String month_name = new SimpleDateFormat("MMMM").format(cal.getTime());

        return getDay() + " " + month_name;
    }

    public String get12hrTimeWithAmPm(){
        Calendar datetime = Calendar.getInstance();
        datetime.set(Calendar.HOUR_OF_DAY, getHour());
        datetime.set(Calendar.MINUTE, getMinute());

        String am_pm;
        if (datetime.get(Calendar.AM_PM) == Calendar.AM)
            am_pm = "AM";
        else
            am_pm = "PM";

        int tempHour = getHour();
        if(tempHour > 11){
            tempHour -= 12;
        }

        if(minute > 9){
            return tempHour + ":" + getMinute() + " " + am_pm;
        } else {
            return tempHour + ":0" + getMinute() + " " + am_pm;
        }

    }

    public void setDate(String date){
        String[] dateArray = date.split("-");

        if(dateArray.length > 1){
            day = Integer.parseInt(dateArray[0]);
            month = Integer.parseInt(dateArray[1]);
            year = Integer.parseInt(dateArray[2]);
        } else {
            day = month = year = 0;
        }
    }

    public void setTime(String time){
        String[] timeArray = time.split(":");

        if(timeArray.length > 1){
            hour = Integer.parseInt(timeArray[0]);
            minute = Integer.parseInt(timeArray[1]);
        } else {
            hour = minute = 0;
        }
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isMarkedImportant() {
        return isMarkedImportant;
    }

    public void setMarkedImportant(boolean markedImportant) {
        isMarkedImportant = markedImportant;
    }

    public boolean isTaskFinished() {
        return isTaskFinished;
    }

    public void setTaskFinished(boolean taskFinished) {
        isTaskFinished = taskFinished;
    }

    public boolean isHasDueDate() {
        return hasDueDate;
    }

    public void setHasDueDate(boolean hasDueDate) {
        this.hasDueDate = hasDueDate;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

}