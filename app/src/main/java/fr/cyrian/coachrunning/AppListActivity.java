package fr.cyrian.coachrunning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity implements MyActionCallback{

    public PackageManager pm;
    ApplicationInfo ai;
    Drawable appIcon;
    ArrayList<String[]> newarr = new ArrayList<String[]>();
    DataFile datafile = new DataFile("applist.txt");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);

        // get package manager
        pm = this.getApplicationContext().getPackageManager();

        // get old list of app (those in the file applist.txt) and separate package name and boolean value
        String[] oldfile = datafile.getFileContent();
        ArrayList<String[]> oldarr = new ArrayList<String[]>();
        for (String i : oldfile) {
            String[] line = i.split(";");
            oldarr.add(line);
        }

        // get every user installed apps packages names in a list
        ArrayList<String> packagesNames = new ArrayList<String>();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo aii : packages) {
            if ((aii.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                // updated system app
            }else if ((aii.flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                // system apps
            } else {
                // user installed apps
                packagesNames.add(aii.packageName);
            }
        }

        // create new list with new installed app (boolean = false in default) and
        // get back previous installed apps (which are in the applist.txt file) with
        // there boolean value
        for (String pkgName : packagesNames){ // for each installed apps' package names
            int m = 0; // count index
            String oldpkg; // a package in applist.txt
            if (oldarr.size()==0){ // case applist.txt empty
                oldpkg = "oufgbnp!::)";
            } else {
                oldpkg = oldarr.get(0)[0];
            }
            Boolean isInFile = (oldpkg.contains(pkgName) || pkgName.contains(oldpkg));
            while (!isInFile) { // while we don't find a current installed package in the applist.txt
                m++;
                if (oldarr.size()==0){ // case txt empty
                    oldpkg = "oufgbnp!::)";
                } else {
                    oldpkg = oldarr.get(m)[0];
                }
                isInFile = (oldpkg.contains(pkgName) || pkgName.contains(oldpkg));
                if (m+1 >= oldarr.size()){ // if every package in applist.txt doesn't fit with the current installed package
                    break;
                }
            }
            if (isInFile) { // if current installed package is finally in the applist.txt
                String[] newline = {oldpkg,oldarr.get(m)[1]};
                newarr.add(newline);
            } else { // otherwise
                String[] newline = {pkgName,"false"};
                newarr.add(newline);
            }
        }

        // list of appList item to send to the adapter
        List<AppListItem> appListItemList = new ArrayList<>();
        for (String[] i : newarr) {
            String pkgName = i[0];
            Boolean isChecked = Boolean.valueOf(i[1]);
            appListItemList.add(new AppListItem(getAppName(pkgName),pkgName,getAppIcon(pkgName),isChecked));
        }

        // get list view and send to adapter
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new AppListAdapter(this,appListItemList,this,newarr));
    }

    // get the app's name of a given package
    public String getAppName(String pkgName) {
        ai = null;
        try {
            ai = pm.getApplicationInfo(pkgName,0);
        }catch(final PackageManager.NameNotFoundException e){
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "unknown");
    }

    // get the app's icon of a given package
    public Drawable getAppIcon(String pkgName) {
        ai = null;
        try {
            ai = pm.getApplicationInfo(pkgName,0);
            appIcon = pm.getApplicationIcon(ai);
        }catch (final PackageManager.NameNotFoundException e){
            ai = null;
            appIcon = null;
        }
        return appIcon;
    }

    @Override
    public void onCheckboxClick(int position, boolean isChecked) {
        String pkg = newarr.get(position)[0];
        if(isChecked){
            newarr.set(position, new String[]{pkg, "true"});
        } else {
            newarr.set(position, new String[]{pkg, "false"});
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        datafile.removeContent();
        for (String[] line : newarr){
            datafile.writeLine(line[0] + ";" + line[1]);
        }
    }
}