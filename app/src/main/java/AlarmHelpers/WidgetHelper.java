package AlarmHelpers;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.todo.R;

import java.util.ArrayList;
import java.util.List;

import Adapters.ListItemTaskAdapter;
import Objects.TaskObject;

public class WidgetHelper extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget);

        List<TaskObject> taskObjectsList = new ArrayList<>();

        ListItemTaskAdapter adapter = new ListItemTaskAdapter(context, R.layout.item_task, taskObjectsList);

        appWidgetManager.updateAppWidget(appWidgetIds[0], view);

    }
}
