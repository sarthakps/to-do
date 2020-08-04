package Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.DueToday;
import com.example.todo.DueTomorrow;
import com.example.todo.ImportantTasks;
import com.example.todo.ListActivity;
import com.example.todo.R;

import java.util.List;

import Database.BaseDatabase;
import Objects.ListObject;

public class ListTodoAdapter extends RecyclerView.Adapter<ListTodoAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private List<ListObject> list;

    public ListTodoAdapter(Context context, int resource, List<ListObject> list) {
        this.context = context;
        this.resource = resource;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(position).getId() == -200) {
                    //Today Activity Intent
                    Intent intent = new Intent(context, DueToday.class);
                    Activity activity = (Activity) context;
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_in_from_left);
                }

                else if (list.get(position).getId() == -400) {
                    //Tomorrow Activity Intent
                    Intent intent = new Intent(context, DueTomorrow.class);
                    Activity activity = (Activity) context;
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_in_from_left);
                }

                else if (list.get(position).getId() == -600) {
                    //Important Activity Intent
                    Intent intent = new Intent(context, ImportantTasks.class);
                    Activity activity = (Activity) context;
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_in_from_left);
                }

                else {
                    Activity activity = (Activity) context;
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("title", list.get(position).getName());
                    intent.putExtra(BaseDatabase.TASKS_PARENT_ID, list.get(position).getId());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_in_from_left);
                }

            }
        });

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.view.performClick();
            }
        });

        ListObject item = list.get(position);

        holder.icon.setImageResource(item.getIcon());
        holder.name.setText(item.getName());

//        holder.testText.setVisibility(View.VISIBLE);

        if (item.isGroup()) {
            holder.EXPAND.setVisibility(View.VISIBLE);
            holder.EXPAND.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandLayout(list.get(position), holder);
                }
            });
        }
    }

    private void expandLayout(ListObject item, ViewHolder holder) {

        if (!item.isExpanded()) {
            item.setExpanded(true);
            holder.ADD.setVisibility(View.VISIBLE);
            holder.ADD.animate().alpha(1).setDuration(500);
            holder.EXPAND.animate().rotationBy(-90).setDuration(350);
            holder.expandableLayout.setVisibility(View.VISIBLE);
            initSubList(item, holder.recyclerView);
        } else {
            item.setExpanded(false);
            holder.ADD.animate().alpha(0).setDuration(500);
            holder.ADD.setVisibility(View.INVISIBLE);
            holder.EXPAND.animate().rotationBy(+90).setDuration(350);
            holder.expandableLayout.setVisibility(View.GONE);
        }

    }

    private void initSubList(ListObject item, RecyclerView recyclerView) {
        //load Todo Lists of the group from database and set adapter
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private ImageView icon, ADD, EXPAND;
        private TextView name, testText;
        private RecyclerView recyclerView;
        private LinearLayoutCompat expandableLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
            this.view = view;
            this.ADD = view.findViewById(R.id.list_todo_add_ListUnderGroup);
            this.EXPAND = view.findViewById(R.id.list_todo_btn_expand_group);
            this.icon = view.findViewById(R.id.list_todo_icon);
            this.name = view.findViewById(R.id.list_todo_name);
            this.recyclerView = view.findViewById(R.id.list_todo_expanded_list);
            this.expandableLayout = view.findViewById(R.id.list_todo_expandable_layout);
        }
    }
}