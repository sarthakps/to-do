package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todo.HomePage;
import com.example.todo.ListActivity;
import com.example.todo.R;

import java.util.List;

import Database.DatabaseManager;
import Objects.ThemeObject;

public class ThemeChangerAdapter extends RecyclerView.Adapter<ThemeChangerAdapter.ViewHolder> {

    private List<ThemeObject> images;
    private int resource;
    private Context context;

    private int selectedPosition;
    private int ID;

    public ThemeChangerAdapter(Context context, int resource, List<ThemeObject> images){
        this.context = context;
        this.resource = resource;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeChangerAdapter.ViewHolder(LayoutInflater.from(context).inflate(resource, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.image.setImageResource(images.get(position).getImageRes());

        if(images.get(position).isSelected()){
            holder.isSelectedRadio.setVisibility(View.VISIBLE);
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //deselecting old selection
                images.get(selectedPosition).setSelected(false);
                notifyItemChanged(selectedPosition);

                selectedPosition = position;
                images.get(position).setSelected(true);
                ListActivity.setBackground(images.get(position).getImageRes());
                holder.isSelectedRadio.setVisibility(View.VISIBLE);
                notifyDataSetChanged();

                updateThemeInDatabase(position);
            }
        });
    }

    @Override
    public int getItemCount() {
         return images.size();
    }

    public int selectCurrentTheme(int themeRes){
        for(int i = 0; i < images.size(); i++){
            if(images.get(i).getImageRes() == themeRes){
                images.get(i).setSelected(true);
                selectedPosition = i;
                notifyDataSetChanged();
                return i;
            }
        }
        return 0;
    }

    public void setID(int ID){
        this.ID = ID;
    }

    private void updateThemeInDatabase(int position){
        String selectedTheme = context.getResources().getResourceName(images.get(position).getImageRes());
        DatabaseManager db = new DatabaseManager(context);
        db.updateTheme(ID, selectedTheme);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image, isSelectedRadio;

        public ViewHolder(@NonNull View view) {
            super(view);
            image = view.findViewById(R.id.list_theme_image);
            isSelectedRadio = view.findViewById(R.id.list_theme_image_selected_radio);
        }
    }
}
