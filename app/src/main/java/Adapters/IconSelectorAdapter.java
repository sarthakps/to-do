package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.HomePage;
import com.example.todo.R;

import java.util.List;

import Objects.ThemeObject;

public class IconSelectorAdapter extends RecyclerView.Adapter<IconSelectorAdapter.ViewHolder> {

    private List<String> icons;
    private int resource;
    private Context context;

    public IconSelectorAdapter(Context context, int resource, List<String> icons){
        this.context = context;
        this.resource = resource;
        this.icons = icons;

        HomePage.setSelectedIcon(icons.get(30));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconSelectorAdapter.ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.icon.setImageResource(context.getResources().getIdentifier(icons.get(position), "drawable", context.getPackageName()));

        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomePage.setSelectedIcon(icons.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;

        public ViewHolder(@NonNull View view) {
            super(view);
            icon = view.findViewById(R.id.list_icon_selector_image);
        }
    }
}
