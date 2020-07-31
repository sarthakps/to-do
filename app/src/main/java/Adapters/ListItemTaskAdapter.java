package Adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;
import com.github.lguipeng.library.animcheckbox.AnimCheckBox;

import java.util.List;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final TaskObject task = taskObjectList.get(position);

        holder.taskDescription.setText(task.getTaskDescription());

        holder.isFinishedCheckbox.setOnCheckedChangeListener(new AnimCheckBox.OnCheckedChangeListener() {
            @Override
            public void onChange(AnimCheckBox view, boolean checked) {
                if(checked){
                    holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    task.setTaskFinished(true);
                    db.updateTaskToFinished(task.getID());
                } else {
                    holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    task.setTaskFinished(false);
                    db.updateTaskToUnfinished(task.getID());
                }
            }
        });

        if(task.getDay() != 0){
            holder.dueDateTime.setText(task.getMonthNameFormattedDate() + ", " + task.get12hrTimeWithAmPm());
            holder.dueDateTime.setVisibility(View.VISIBLE);
        }

        if(task.isTaskFinished()){
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.isFinishedCheckbox.setChecked(true, false);
        } else {
            holder.taskDescription.setPaintFlags(holder.taskDescription.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.isFinishedCheckbox.setChecked(false, false);
        }
    }

    @Override
    public int getItemCount() {
        return taskObjectList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        AnimCheckBox isFinishedCheckbox;
        TextView taskDescription, dueDateTime;

       public ViewHolder(@NonNull View view) {
           super(view);

           isFinishedCheckbox = view.findViewById(R.id.item_todos_checkbox);
           taskDescription = view.findViewById(R.id.item_todos_task);
           dueDateTime = view.findViewById(R.id.item_todos_dueDateTime);
       }
   }

}
