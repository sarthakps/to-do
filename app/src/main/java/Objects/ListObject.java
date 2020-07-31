package Objects;

public class ListObject {

    private int id;
    private int icon;
    private String name;
    private boolean isGroup = false;
    private boolean isExpanded = false;


    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }

    //constructor
    public ListObject(int id, String name, int icon, boolean isGroup){
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.isGroup = isGroup;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void isGroup(boolean group) {
        isGroup = group;
    }
}
