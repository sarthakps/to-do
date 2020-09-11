package Objects;

import java.util.Calendar;

public class Reminder {

    int taskID;
    int day, month, year, hour, minute;     //month is indexed from 1   i.e. 1-12
    String taskDescription;

    public Reminder(){}

    public Reminder(int taskID, int day, int month, int year, int hour, int minute, String taskDescription){
        this.taskID = taskID;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.taskDescription = taskDescription;
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

    public Calendar getCalendar(){
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar;
    }

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
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