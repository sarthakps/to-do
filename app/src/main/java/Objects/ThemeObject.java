package Objects;

import android.graphics.drawable.Drawable;

public class ThemeObject {

    private int imageRes;
    private boolean isSelected = false;

    public ThemeObject (int imageRes, boolean isSelected){
        this.imageRes = imageRes;
        this.isSelected = isSelected;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
