package com.example.jane.timskaapplication101.settings;

/**
 * Created by Jane on 8/18/2015.
 */
public class SettingsItem {

    private String name;
    private boolean checked;
    private int visible;

    public SettingsItem(String name, boolean checked, int visible) {
        this.name = name;
        this.checked = checked;
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
