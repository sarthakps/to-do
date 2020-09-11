package Objects;

import com.example.todo.R;

public class ThemeObject {

    private int imageRes;

    private boolean isSelected = false;

    public ThemeObject(int imageRes, boolean isSelected) {
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

    public int getPrimaryAccentForTheme() {
        switch (imageRes) {
            case R.drawable.office3:
                return R.color.white;
            case R.drawable.books1:
                return R.color.white;
            case R.drawable.books2:
                return R.color.white;
            case R.drawable.books3:
                return R.color.white;
            case R.drawable.cabin1:
                return R.color.green_cream;
            case R.drawable.city1:
                return R.color.white;
            case R.drawable.city3:
                return R.color.white;
            case R.drawable.city4:
                return R.color.white;
            case R.drawable.flowers1:
                return R.color.white;
            case R.drawable.forest1:
                return R.color.white;
            case R.drawable.forest2:
                return R.color.white;
            case R.drawable.forest4:
                return R.color.white;
            case R.drawable.interior1:
                return R.color.white;
            case R.drawable.interior2:
                return R.color.white;
            case R.drawable.kitchen1:
                return R.color.white;
            case R.drawable.kitchen2:
                return R.color.teal_blue;
            case R.drawable.kitchen3:
                return R.color.brownWashed;
            case R.drawable.landscape1:
                return R.color.white;
            case R.drawable.landscape2:
                return R.color.brownWashed;
            case R.drawable.landscape3:
                return R.color.white;
            case R.drawable.mountain1:
                return R.color.white;
            case R.drawable.office1:
                return R.color.white;
            case R.drawable.office2:
                return R.color.white;
            case R.drawable.office4:
                return R.color.green_cream;
            case R.drawable.plain1:
                return R.color.white;
            case R.drawable.plant2:
                return R.color.deep_green;
            case R.drawable.plant3:
                return R.color.deep_green;
            case R.drawable.plant4:
                return R.color.deep_green;
            case R.drawable.sea1:
                return R.color.brownWashed;
            case R.drawable.sea2:
                return R.color.white;
            case R.drawable.sea3:
                return R.color.white;
            case R.drawable.sky1:
                return R.color.white;

            default:
                return R.color.white;
        }
    }

    public int getSecondaryAccentForTheme() {
        switch (imageRes) {
            case R.drawable.office3:
                return R.color.white;
            case R.drawable.books1:
                return R.color.teal_blue;
            case R.drawable.books2:
                return R.color.white;
            case R.drawable.books3:
                return R.color.green_cream;
            case R.drawable.cabin1:
                return R.color.deep_green;
            case R.drawable.city1:
                return R.color.teal_blue;
            case R.drawable.city3:
                return R.color.greyish_blue;
            case R.drawable.city4:
                return R.color.white;
            case R.drawable.flowers1:
                return R.color.redWashed;
            case R.drawable.forest1:
                return R.color.brownWashed;
            case R.drawable.forest2:
                return R.color.green_cream;
            case R.drawable.forest4:
                return R.color.white;
            case R.drawable.interior1:
                return R.color.white;
            case R.drawable.interior2:
                return R.color.white;
            case R.drawable.kitchen1:
                return R.color.green_cream;
            case R.drawable.kitchen2:
                return R.color.teal_blue;
            case R.drawable.kitchen3:
                return R.color.brownWashed;
            case R.drawable.landscape1:
                return R.color.green_cream;
            case R.drawable.landscape2:
                return R.color.teal_blue;
            case R.drawable.landscape3:
                return R.color.white;
            case R.drawable.mountain1:
                return R.color.white;
            case R.drawable.office1:
                return R.color.slate_gray;
            case R.drawable.office2:
                return R.color.white;
            case R.drawable.office4:
                return R.color.green_cream;
            case R.drawable.plain1:
                return R.color.greyish_blue;
            case R.drawable.plant2:
                return R.color.deep_green;
            case R.drawable.plant3:
                return R.color.deep_green;
            case R.drawable.plant4:
                return R.color.deep_green;
            case R.drawable.sea1:
                return R.color.brownWashed;
            case R.drawable.sea2:
                return R.color.white;
            case R.drawable.sea3:
                return R.color.white;
            case R.drawable.sky1:
                return R.color.gunmetal;

            default:
                return R.color.white;
        }
    }

}
