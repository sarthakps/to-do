package Objects;

import android.content.Context;

import com.example.todo.R;

import java.util.ArrayList;
import java.util.List;

public class ConstantsDB {

    static public List<ThemeObject> getThemeObjectsList(){
        List<ThemeObject> list = new ArrayList<>();

        list.add(new ThemeObject(R.drawable.test_theme, false));
        list.add(new ThemeObject(R.drawable.books1, false));
        list.add(new ThemeObject(R.drawable.books2, false));
        list.add(new ThemeObject(R.drawable.books3, false));
        list.add(new ThemeObject(R.drawable.cabin1, false));
        list.add(new ThemeObject(R.drawable.city1, false));
        list.add(new ThemeObject(R.drawable.city2, false));
        list.add(new ThemeObject(R.drawable.city3, false));
        list.add(new ThemeObject(R.drawable.city4, false));
        list.add(new ThemeObject(R.drawable.flowers1, false));
        list.add(new ThemeObject(R.drawable.forest1, false));
        list.add(new ThemeObject(R.drawable.forest2, false));
        list.add(new ThemeObject(R.drawable.forest3, false));
        list.add(new ThemeObject(R.drawable.forest4, false));
        list.add(new ThemeObject(R.drawable.interior1, false));
        list.add(new ThemeObject(R.drawable.interior2, false));
        list.add(new ThemeObject(R.drawable.kitchen1, false));
        list.add(new ThemeObject(R.drawable.kitchen2, false));
        list.add(new ThemeObject(R.drawable.kitchen3, false));
        list.add(new ThemeObject(R.drawable.landscape1, false));
        list.add(new ThemeObject(R.drawable.landscape2, false));
        list.add(new ThemeObject(R.drawable.landscape3, false));
        list.add(new ThemeObject(R.drawable.mountain1, false));
        list.add(new ThemeObject(R.drawable.mountains2, false));
        list.add(new ThemeObject(R.drawable.office1, false));
        list.add(new ThemeObject(R.drawable.office2, false));
        list.add(new ThemeObject(R.drawable.office3, false));
        list.add(new ThemeObject(R.drawable.office4, false));
        list.add(new ThemeObject(R.drawable.office5, false));
        list.add(new ThemeObject(R.drawable.plain1, false));
        list.add(new ThemeObject(R.drawable.plant2, false));
        list.add(new ThemeObject(R.drawable.plant3, false));
        list.add(new ThemeObject(R.drawable.plant4, false));
        list.add(new ThemeObject(R.drawable.sea1, false));
        list.add(new ThemeObject(R.drawable.sea2, false));
        list.add(new ThemeObject(R.drawable.sea3, false));
        list.add(new ThemeObject(R.drawable.sky1, false));

        //add theme images here

        return list;
    }

    static public List<String> getIconsList(){
        List<String> list = new ArrayList<>();

        list.add("icon_airplane");
        list.add("icon_email");
        list.add("icon_briefcase");
        list.add("icon_alarm");
        list.add("icon_feedback");
        list.add("icon_shopping_cart");
        list.add("icon_code");
        list.add("icon_alert_circular");
        list.add("icon_palette");
        list.add("icon_bulb");
        list.add("icon_bookmark");
        list.add("icon_keyboard");
        list.add("icon_pen");
        list.add("icon_search");
        list.add("icon_deck");
        list.add("icon_compass");
        list.add("icon_fitness");
        list.add("icon_camera");
        list.add("icon_computer");
        list.add("icon_headset");
        list.add("icon_game_controller");
        list.add("icon_location");
        list.add("icon_microphone");
        list.add("icon_movies");
        list.add("icon_wrench");
        list.add("icon_group");
        list.add("icon_happy_emoji");
        list.add("icon_hotel");
        list.add("icon_knife_fork");
        list.add("icon_leaf");
        list.add("icon_list_default");

        return list;
    }

}
