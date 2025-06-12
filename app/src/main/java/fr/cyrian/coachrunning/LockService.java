package fr.cyrian.coachrunning;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LockService extends Service implements SensorEventListener {

    String CURRENT_PACKAGE_NAME = "";
    String topPackageName = "";
    public static LockService instance;
    private static Timer timer = new Timer();
    DataFile datafile = new DataFile("applist.txt");
    DataFile datafile2 = new DataFile("count.txt");
    Boolean hasToBLock;
    String recentAppName;
    Integer countMilli;
    String[] fileContent;
    ArrayList<String> truePkgNames = new ArrayList<>();
    private SensorManager sensorManager;
    private Sensor countSensor;
    public float steps;
    public float oldsteps = 0;
    public float oldsteps2 = 0;
    public double oldspeed = 0.0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy(){

        // erase timer when service destroyed
        try {
            timer.cancel();
            timer.purge();
        } catch (Exception e) {
            e.printStackTrace();
        }
        toastHandler.removeCallbacksAndMessages(null);
        toastHandler2.removeCallbacksAndMessages(null);

        hasToBLock = false;
        recentAppName = "";

        sensorManager.unregisterListener(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scheduleMethod();
        CURRENT_PACKAGE_NAME = getApplicationContext().getPackageName();
        instance = this;
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        steps = 0;
        oldsteps = 0;
        countMilli = 0;
        fileContent = datafile.getFileContent();
        truePkgNames.clear();
        for (String line : fileContent){
            String[] lineTab = line.split(";");
            if (lineTab[1].contains("true")){
                truePkgNames.add(lineTab[0]);
            }
        }

        // start a foreground service isn't the same for versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startOwnForeground();
        }
        else {
            Intent bIntent = new Intent(LockService.this, MainActivity.class);
            PendingIntent pbIntent = PendingIntent.getActivity(LockService.this, 0, bIntent,0);
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "ID" );

            notification.setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                    .setContentTitle("CoachRunning is processing in background")
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setContentIntent(pbIntent).build();
            startForeground(1, notification.build());
        }

        // Create sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if(countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
        }

    }


    public double getSpeed(double length){
        double speed = 0;
        if (oldsteps != 0) {
            double delta_steps = steps - oldsteps;
            speed = (0.010872*delta_steps*length)/(20-delta_steps*length*9*0.0001);
            oldsteps = steps;
        } else {
            oldsteps = steps;
        }
        return speed;
    }

    // TEST
    public Double[] getSpeedTest(double length, String gender){
        double speed1 = 0;
        double speed2 = 0;
        double speed3 = 0;
        double speed4 = 0;
        double speed5 = 0;
        double speed6 = 0;
        double speed7 = 0;
        double speed8 = 0;
        double speed9 = 0;
        double delta_steps = 0;
        if (oldsteps2 != 0) {
            delta_steps = steps - oldsteps2;
            if (gender.equals("male")) {
                speed1 = delta_steps*length*0.415*360*0.00001;
                speed4 = (delta_steps*33.3*0.0036)/(1-7.2*0.0036*delta_steps);
                speed8 = (delta_steps*37.4*0.0036)/(1-6.85*0.0036*delta_steps);
                speed9 = (delta_steps*16.4*0.0036)/(1-8.49*0.0036*delta_steps);
                if (delta_steps < 11) {
                    speed6 = 0;
                } else {
                    speed6 = (delta_steps*1.609*3600*0.1 - 63.4*96.56064)/(1916 - 14.1*0.3937*length);
                }
                if (delta_steps == 0) {
                    speed5 = 0;
                    speed7 = 0;
                }else {
                    if (delta_steps > 29) {
                        speed7 = ((1-0.00918*delta_steps))/(0.0016272*delta_steps);
                    } else {
                        speed7 = ((1-0.00918*delta_steps)-Math.sqrt((0.00918*delta_steps-1)*(0.00918*delta_steps-1)-0.00062914*delta_steps*delta_steps))/(0.0016272*delta_steps);
                    }
                    speed5 = ((1-0.00918*delta_steps)-Math.sqrt((0.00918*delta_steps-1)*(0.00918*delta_steps-1)-0.00062914*delta_steps*delta_steps))/(0.0016272*delta_steps);
                }
            } else {
                speed1 = delta_steps*length*0.413*360*0.00001;
                speed4 = (delta_steps*34.1*0.0036)/(1-7.59*0.0036*delta_steps);
                speed8 = (delta_steps*37.9*0.0036)/(1-7.39*0.0036*delta_steps);
                speed9 = (delta_steps*12.9*0.0036)/(1-9.22*0.0036*delta_steps);
                if (delta_steps < 11) {
                    speed6 = 0;
                } else {
                    speed6 = (delta_steps*1.609*3600*0.1 - 63.4*96.56064)/(1949 - 14.1*0.3937*length);
                }
                if (delta_steps == 0) {
                    speed5 = 0;
                    speed7 = 0;
                }else {
                    if (delta_steps > 29) {
                        speed7 = ((1-0.006084*delta_steps))/(0.0020664*delta_steps);
                    } else {
                        speed7 = ((1-0.006084*delta_steps)-Math.sqrt((0.006084*delta_steps-1)*(0.006084*delta_steps-1)-0.0008912*delta_steps*delta_steps))/(0.0020664*delta_steps);
                    }
                    speed5 = ((1-0.006084*delta_steps)-Math.sqrt((0.006084*delta_steps-1)*(0.006084*delta_steps-1)-0.0008912*delta_steps*delta_steps))/(0.0020664*delta_steps);
                }
            }
            speed2 = (delta_steps*54.3*0.0036)/(1 - 4.5*0.0036*delta_steps);
            if (oldspeed < 7.5) {
                if (gender.equals("male")) {
                    speed3 = (delta_steps*1.609*3600*0.1 - 63.4*96.56064)/(1916 - 14.1*0.3937*length);
                } else {
                    speed3 = (delta_steps*1.609*3600*0.1 - 63.4*96.56064)/(1949 - 14.1*0.3937*length);
                }
                oldspeed = speed3;
            } else {
                speed3 = (delta_steps*1.609*3600*0.1 - 143.6*96.56064)/(1084 - 13.5*0.3937*length);
                oldspeed = speed3;
            }
            oldsteps2 = steps;
        } else {
            oldsteps2 = steps;
        }
        Double[] tab = {delta_steps,speed1,speed2,speed3,speed4,speed5,speed6,speed7,speed8,speed9};
        return tab;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        steps = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // launch timer
    private void scheduleMethod() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new mainTask(),0,200);
        timer.scheduleAtFixedRate(new mainTask2(),0,10*1000);
    }

    private class mainTask extends TimerTask {
        public void run() {
            toastHandler.sendEmptyMessage(0);
        }
    }

    private class mainTask2 extends TimerTask {
        public void run() { toastHandler2.sendEmptyMessage(0);}
    }

    // handler to repeat action
    @SuppressLint("HandlerLeak")
    private final Handler toastHandler2 = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {

            // Get preferences
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            double length = Double.valueOf(prefs.getString("length","170"));
            double weight = Double.valueOf(prefs.getString("weight", "60"));
            String gender = prefs.getString("gender_preference","male");
            double difficulty = Double.valueOf(prefs.getString("difficulty_preference","1.0"));

            // Get speed
            double speed = getSpeed(length);

            // TEST
            Double[] tab = getSpeedTest(length,gender);
            writeSpeed(String.format("%.03f",tab[0]).replace(",",".") + ";"
                    + String.format("%.03f",tab[1]).replace(",",".") + ";"
                    + String.format("%.03f",tab[2]).replace(",",".") + ";"
                    + String.format("%.03f",tab[3]).replace(",",".") + ";"
                    + String.format("%.03f",tab[4]).replace(",",".") + ";"
                    + String.format("%.03f",tab[5]).replace(",",".") + ";"
                    + String.format("%.03f",tab[6]).replace(",",".") + ";"
                    + String.format("%.03f",tab[7]).replace(",",".") + ";"
                    + String.format("%.03f",tab[8]).replace(",",".") + ";"
                    + String.format("%.03f",tab[9]).replace(",","."), true);



            // Don't accept speed under 1km/h
            double cal10S;
            if (speed >= 0.5) {
                // Define lists of calories parameters
                int[] weightTab = {50, 60, 70, 80, 90, 100, 110, 120, 130};
                int[] speedTab = {3, 6, 8, 10, 13, 15, 17};
                int[][] maleTab = {{152, 182, 213, 243, 275, 305, 335, 365, 395},                              // MAYBE PUT TABS IN TOP
                        {245, 293, 341, 390, 440, 490, 540, 590, 640},
                        {400, 480, 560, 640, 720, 800, 880, 960, 1040},
                        {520, 624, 728, 832, 935, 1039, 1143, 1247, 1351},
                        {640, 768, 896, 1024, 1152, 1280, 1408, 1536, 1664},
                        {760, 912, 1064, 1216, 1368, 1520, 1672, 1824, 1976},
                        {880, 1056, 1232, 1408, 1585, 1761, 1937, 2113, 2289}};
                int[][] femaleTab = {{145, 174, 203, 232, 262, 292, 322, 352, 382},
                        {233, 279, 325, 372, 419, 466, 513, 560, 607},
                        {381, 457, 534, 610, 686, 762, 838, 914, 990},
                        {496, 595, 694, 793, 893, 993, 1093, 1193, 1293},
                        {611, 733, 855, 978, 1100, 1222, 1344, 1466},
                        {725, 870, 1015, 1161, 1306, 1451, 1596, 1741, 1886},
                        {839, 1007, 1175, 1344, 1512, 1680, 1848, 2016, 2184}};

                // Get index of closer weight and closer speed in list
                int weightIndex = getCLoserIndex(weight, weightTab);
                int speedIndex = getCLoserIndex(speed, speedTab);

                // Get exact calories for 1h of practice
                int cal1H;
                if (gender.equals("male")) {
                    cal1H = maleTab[speedIndex][weightIndex];
                } else {
                    cal1H = femaleTab[speedIndex][weightIndex];
                }

                // The calories for 10s multiply by difficulty that we have to add
                cal10S = (cal1H / 360.0) * difficulty;

                // Get line count file
                String[] lineStr = datafile2.getFileContent();
                Double cal = Double.valueOf(lineStr[0].split(";")[0].replace(",","."));
                Double sec = Double.valueOf(lineStr[0].split(";")[1].replace(",","."));

                // New calories
                Double newcal = cal + cal10S;

                // Set new line count file
                datafile2.removeContent();
                // Add 1h every 500 cal
                if (newcal >= 500.0){
                    Double newsec = sec + 3600.0;
                    datafile2.writeLine(String.format("%.03f",newcal-500.0).replace(",",".") + ";" + String.format("%.03f",newsec).replace(",","."));
                } else {
                    datafile2.writeLine(String.format("%.03f",newcal).replace(",",".") + ";" + sec);
                }
            } else {
                cal10S = 0;
            }
        }
    };

    // handler to repeat action
    @SuppressLint("HandlerLeak")
    private final Handler toastHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg){
            recentAppName = getRecentApps(getApplicationContext());
            hasToBLock = false;
            countMilli += 500;

            if( countMilli >= 10000){
                countMilli = 0;
                fileContent = datafile.getFileContent();
                truePkgNames.clear();
                for (String line : fileContent){
                    String[] lineTab = line.split(";");
                    if (lineTab[1].contains("true")){
                        truePkgNames.add(lineTab[0]);
                    }
                }
            }

            for (String pkg : truePkgNames) {
                if (recentAppName.contains(pkg)){
                    hasToBLock = true;
                }
            }
            if (hasToBLock) {
                String[] lineStr = datafile2.getFileContent();
                Double cal = Double.valueOf(lineStr[0].split(";")[0].replace(",","."));
                Double sec = Double.valueOf(lineStr[0].split(";")[1].replace(",","."));
                if (!(sec > 0.0)){
                    Intent in = new Intent(getApplicationContext(),LockActivity.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(in);
                }
                Double newsec = sec - 0.5;
                if (newsec < 0.0){
                    newsec = 0.0;
                }

                datafile2.removeContent();
                datafile2.writeLine(String.format("%.03f",cal).replace(",",".") + ";" + String.format("%.03f",newsec).replace(",","."));
            }
        }
    };

    // Get recent activity package name
    public String getRecentApps(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

            long time = System.currentTimeMillis();

            UsageEvents usageEvents = mUsageStatsManager.queryEvents(time-1000*30, System.currentTimeMillis()+(10*1000));
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
            }

            if (event != null && !TextUtils.isEmpty(event.getPackageName()) && event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                if (AndroidUtils.isRecentActivity(event.getClassName())) {
                    topPackageName = event.getPackageName();
                    return event.getClassName();
                }
                topPackageName = event.getPackageName();
                return event.getPackageName();
            } else {
                //topPackageName = "";
            }
        }else {
            ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;

            if (AndroidUtils.isRecentActivity(componentInfo.getClassName())) {
                topPackageName = componentInfo.getPackageName();
                return componentInfo.getClassName();
            }

            topPackageName = componentInfo.getPackageName();
        }
        return topPackageName;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                .setContentTitle("CoachRunning is processing in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setShowWhen(false)
                .build();
        startForeground(2, notification);
    }


    public int getCLoserIndex(double value, int[] valuesTab){
        int closerIndex = 0;
        double minDiff = Math.abs(valuesTab[0]-value);
        for (int i=1 ; i <= valuesTab.length-1 ; i++){
            double diff = Math.abs(valuesTab[i]-value);
            if (diff <= minDiff){
                minDiff = diff;
                closerIndex = i;
            }
        }
        return closerIndex;
    }

    public void writeSpeed(String speed, boolean append ) {
        File chemin = this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File fichier = new File(chemin, "speedtest.txt");
        try {
            FileWriter fw = new FileWriter(fichier.getAbsoluteFile(), append);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter p = new PrintWriter(bw);
            p.println(speed);
            bw.close();
            p.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}