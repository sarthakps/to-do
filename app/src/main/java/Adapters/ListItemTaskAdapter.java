package Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.ListActivity;
import com.example.todo.R;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.Calendar;
import java.util.List;

import AlarmHelpers.AlarmReceiver;
import Database.DatabaseManager;
import Objects.TaskObject;

public class ListItemTaskAdapter extends RecyclerView.Adapter<ListItemTaskAdapter.ViewHolder> {

    Context context;
    int resource;
    List<TaskObject> taskObjectList;
    DatabaseManager db;

    public ListItemTaskAdapter(@NonNull Context context, int resource, List<TaskObject> taskObjectList) {
        this.context = context;
        this.resource = resource;
        this.taskObjectList = taskObjectList;
        db = new DatabaseManager(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final TaskObject task = taskObjectList.get(position);

        holder.taskDescription.setText(task.getTaskDescription());

        holder.isFinishedCheckbox.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(AnimCheckBox view, boolean checked) {
                if (checked) {
                    holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    task.setTaskFinished(true);
                    db.updateTaskToFinished(task.getID());
                } else {
                    holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    task.setTaskFinished(false);
                    db.updateTaskToUnfinished(task.getID());
                }
            }
        });

        holder.isImportantCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    task.setMarkedImportant(false);
                    db.updateTaskToImportant(task.getID());
                } else {
                    task.setMarkedImportant(false);
                    db.updateTaskToNotImportant(task.getID());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_task_popup(position);
            }
        });

        if (task.getDay() != 0) {
            if (task.getMonthNameFormattedDate().equals("Today"))
                holder.dueDateTime.setTextColor(context.getColor(R.color.redBright));
            else if (task.getMonthNameFormattedDate().equals("Tomorrow"))
                holder.dueDateTime.setTextColor(context.getColor(R.color.orange));

            holder.dueDateTime.setText(task.getMonthNameFormattedDate() + "  " + task.get12hrTimeWithAmPm());
            holder.dueDateTime.setVisibility(View.VISIBLE);
        }

        if (task.isTaskFinished()) {
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.isFinishedCheckbox.setChecked(true, false);
        } else {
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.isFinishedCheckbox.setChecked(false, false);
        }

        if (task.isMarkedImportant()){
            holder.isImportantCheckbox.setChecked(true);
        } else {
            holder.isImportantCheckbox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return taskObjectList.size();
    }

    private void edit_task_popup(final int position){
        TaskObject task = taskObjectList.get(position);
        Toast.makeText(context, "ItemView Clicked", Toast.LENGTH_SHORT).show();

        View view_add_task = LayoutInflater.from(context).inflate(R.layout.popup_add_todo_task, null);

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setView(view_add_task);

        final AlertDialog dialog = builder.create();

        final TextView dateTimeStaticTextView = view_add_task.findViewById(R.id.popup_add_task_pick_date_static_text);
        final TextView clearDueDate = view_add_task.findViewById(R.id.popup_add_task_clear_duedate);
        final EditText taskDescription = view_add_task.findViewById(R.id.popup_add_task_taskDesc);


        final String[] dateTime = new String[2];


        //populate field
        taskDescription.setText(task.getTaskDescription());
        dateTime[0] = task.getDate();
        dateTime[1] = task.getTime();


        view_add_task.findViewById(R.id.popup_add_task_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_task_in_database(taskDescription.getText().toString(), dateTime, position);
                dialog.dismiss();
            }
        });

        view_add_task.findViewById(R.id.popup_add_task_pick_date_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_time_popup(dateTimeStaticTextView, clearDueDate, dateTime);
            }
        });

        view_add_task.findViewById(R.id.popup_add_task_pick_date_static_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date_time_popup(dateTimeStaticTextView, clearDueDate, dateTime);
            }
        });

        clearDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateTime[0] = dateTime[1] = "0";
                dateTimeStaticTextView.setText("Pick a date");
                clearDueDate.setVisibility(View.GONE);
            }
        });

        dialog.show();
    }

    private void update_task_in_database(String taskDescription, String[] dateTime, int position){
        if(!taskDescription.equals("")){

            String date = dateTime[0];
            String time = dateTime[1];

            TaskObject task = taskObjectList.get(position);
            task.setTaskDescription(taskDescription);
            task.setHasDueDate(!"0".equals(date));

            String[] dateData = date.split("-");
            String[] timeData = time.split(":");

            if (dateData.length > 1) {
                task.setDay(Integer.parseInt(dateData[0]));
                task.setMonth(Integer.parseInt(dateData[1]));
                task.setYear(Integer.parseInt(dateData[2]));
            }

            if (timeData.length > 1) {
                task.setHour(Integer.parseInt(timeData[0]));
                task.setMinute(Integer.parseInt(timeData[1]));
            }

            DatabaseManager db = new DatabaseManager(context);
            db.UpdateTaskInDatabase(task.getID(), taskDescription, date, time, task.getCalendar());

            AlarmReceiver alarmReceiver = new AlarmReceiver();
            alarmReceiver.cancelAlarm(context, task.getID());

            if (!date.equals("0") && !time.equals("0")) {
                alarmReceiver.setAlarm(context, db.getReminder(task.getID()), db.getListIDfromTaskID(task.getID()));
            }

            notifyItemChanged(position);
        }
    }

    private void date_time_popup(final TextView dateTimeTextView, final TextView clearDueDate, final String[] dateTime) {
        final View view_datetime_popup = LayoutInflater.from(context).inflate(R.layout.date_time_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view_datetime_popup);

        final AlertDialog dialog = builder.create();

        final DatePicker datePicker = view_datetime_popup.findViewById(R.id.popup_datetime_date_picker);

        //set date picker's date to tomorrow
        view_datetime_popup.findViewById(R.id.popup_datetime_tomorrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                c.add(Calendar.DAY_OF_YEAR, 1);
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });

        //set date picker's date to today
        view_datetime_popup.findViewById(R.id.popup_datetime_today).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                datePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
            }
        });

        final TimePicker timePicker = view_datetime_popup.findViewById(R.id.popup_datetime_time_picker);
        timePicker.setCurrentHour(12);
        timePicker.setCurrentMinute(0);

        //done button listener
        view_datetime_popup.findViewById(R.id.popup_datetime_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                datetime.set(Calendar.MINUTE, timePicker.getCurrentMinute());

                String am_pm;
                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else
                    am_pm = "PM";

                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                dateTime[0] = day + "-" + month + "-" + year;
                dateTime[1] = hour + ":" + minute;

                if (hour > 11) {
                    hour -= 12;
                }

                if (minute > 9) {
                    dateTimeTextView.setText(day + "-" + month + "-" + year + "  " + hour + ":" + minute + " " + am_pm);
                } else {
                    dateTimeTextView.setText(day + "-" + month + "-" + year + "  " + hour + ":" + "0" + minute + " " + am_pm);
                }

                clearDueDate.setVisibility(View.VISIBLE);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        AnimCheckBox isFinishedCheckbox;
        TextView taskDescription, dueDateTime;
        CheckBox isImportantCheckbox;

        public ViewHolder(@NonNull View view) {
            super(view);
            itemView = view;
            isFinishedCheckbox = view.findViewById(R.id.item_todos_checkbox);
            taskDescription = view.findViewById(R.id.item_todos_task);
            dueDateTime = view.findViewById(R.id.item_todos_dueDateTime);
            isImportantCheckbox = view.findViewById(R.id.item_todos_star);
        }
    }

}
