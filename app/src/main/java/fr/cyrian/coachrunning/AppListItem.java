package fr.cyrian.coachrunning;

import android.graphics.drawable.Drawable;

public class AppListItem {

    private String name;
    private String pkgName;
    private boolean isChecked;
    private Drawable img;

    public AppListItem(String name, String pkgName, Drawable img, Boolean isChecked) {
        this.img = img;
        this.name = name;
        this.pkgName = pkgName;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public Drawable getImg() {
        return img;
    }

    public String getPkgName() {
        return pkgName;
    }

    public Boolean isChecked() {
        return isChecked;
    }
}
