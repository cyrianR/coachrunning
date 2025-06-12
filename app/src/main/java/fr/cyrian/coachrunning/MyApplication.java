package fr.cyrian.coachrunning;

import android.app.Application;
import android.content.Context;

// just to get app's context in java classes
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }


}
