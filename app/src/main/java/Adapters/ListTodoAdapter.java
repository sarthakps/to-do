package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.R;

import java.util.List;

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
        return new ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

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

    static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon, ADD, EXPAND;
        private TextView name, testText;
        private RecyclerView recyclerView;
        private LinearLayoutCompat expandableLayout;

        public ViewHolder(@NonNull View view) {
            super(view);
//            this.testText = view.findViewById(R.id.textTest);
            this.ADD = view.findViewById(R.id.list_todo_add_ListUnderGroup);
            this.EXPAND = view.findViewById(R.id.list_todo_btn_expand_group);
            this.icon = view.findViewById(R.id.list_todo_icon);
            this.name = view.findViewById(R.id.list_todo_name);
            this.recyclerView = view.findViewById(R.id.list_todo_expanded_list);
            this.expandableLayout = view.findViewById(R.id.list_todo_expandable_layout);
        }
    }
}