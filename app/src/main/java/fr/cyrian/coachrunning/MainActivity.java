package fr.cyrian.coachrunning;

import static androidx.core.os.HandlerCompat.postDelayed;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity{

    String[] permission={"android.permission.QUERY_ALL_PACKAGES","android.permission.PACKAGE_USAGE_STATS","android.permission.FOREGROUND_SERVICE","android.permission.SYSTEM_ALERT_WINDOW","android.permission.ACTIVITY_RECOGNITION", "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.WRITE_INTERNAL_STORAGE", "android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.GET_TASKS"};
    DataFile datafile2 = new DataFile("count.txt");
    DataFile datafile = new DataFile("applist.txt");
    DataFile dataFileTest = new DataFile("speedtest.txt");
    TextView tv_time;
    String m_text = "";
    ProgressBar bar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ask for runtime permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission, 80);
        }


        // TEST
        dataFileTest.initialize();

        // initialize the app list file
        datafile.initialize();

        // initialize count file
        if(!datafile2.initialize()){
            datafile2.writeLine("0.0;0.0");
        }

        // Run handler to show stats
        showCal.run();

        // ask to create password if doesn't exist
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS", 0);
                String password = sharedPreferences.getString("password", "");
                if (password.equals("")) {
                    Intent in = new Intent(getApplicationContext(),CreatePasswordActivity.class);
                    startActivity(in);
                    finish();
                } else {

                }
            }
        }, 100);


        // Check if package usage stat and system alert window permissions are granted and ask to grant otherwise
        if (!isGranted(AppOpsManager.OPSTR_GET_USAGE_STATS)) {
            askForSpecialPerms(Settings.ACTION_USAGE_ACCESS_SETTINGS, "Autorisez la permission 'Accès aux infos d'utilisation' pour un bon fonctionnement");
        }
        if (!isGranted(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askForSpecialPerms(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, "Autorisez la permission 'Superposition sur d'autres applications' pour un bon fonctionnement");
            }
        }




        // Create settings button listener
        @SuppressLint("WrongViewCast")
        ImageButton buttonRequest = findViewById(R.id.settings_image_button);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(in);
                finish();
            }
        });

        // Ask for ignoring battery optimizations
        Intent intent = new Intent();
        String pkgName = this.getPackageName();
        PowerManager pom = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (pom.isIgnoringBatteryOptimizations(pkgName)){
            //intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        } else {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + pkgName));
            this.startActivity(intent);
        }


        // Run the handler
        handlerToStartService.removeCallbacks(periodicCheckForPerms);
        periodicCheckForPerms.run();

        Button buttonRequestDelete = findViewById(R.id.delete_button);
        buttonRequestDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataFileTest.removeContent();
            }
        });

        Button buttonRequestSave = findViewById(R.id.save_button);
        buttonRequestSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Save");
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_text = input.getText().toString();
                        if (m_text != ""){
                            DataFile exp = new DataFile(m_text + ".txt");
                            exp.initialize();
                            String[] tabSpeed = dataFileTest.getFileContent();
                            for (String line : tabSpeed) {
                                exp.writeLine(line);
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });


    }

    private Boolean checkForPermissions() {
        // Check if physical activity and write storage permissions are granted
        for (String perm : new String[] {"android.permission.ACTIVITY_RECOGNITION","android.permission.WRITE_EXTERNAL_STORAGE"} ){
            if (ContextCompat.checkSelfPermission(this,perm) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Callback after permissions requested
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        // Check if permissions are granted
        if (!checkForPermissions()) {
            openAlertDialog();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Dialog in case of permission are not granted
    private void openAlertDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage("Cette application nécessite l'accès aux contenus multimédias et aux données relatives à l'activité physique");
        adb.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Stop app if permissions are not granted
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog ad = adb.create();
        ad.show();
    }

    // Dialog that send to specific special permission and finish app
    public void askForSpecialPerms(String action, String message) {
        AlertDialog ad = new AlertDialog.Builder(MainActivity.this).create();
        ad.setTitle("Permission needed");
        ad.setMessage(message);
        ad.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent i = new Intent(action);
                        startActivity(i);
                        finish();
                    }
                });
        ad.show();
    }

    // Check if special permission has been granted for app
    public boolean isGranted(String op) {
        AppOpsManager appOps = (AppOpsManager) getApplicationContext().getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(op, android.os.Process.myUid(), getApplicationContext().getPackageName());
        boolean granted = (mode == AppOpsManager.MODE_ALLOWED);
        return granted;
    }

    Handler handlerToStartService = new Handler();
    private final Runnable periodicCheckForPerms = new Runnable() {
        @Override
        public void run() {
            postDelayed(handlerToStartService, periodicCheckForPerms,null, 500);
            if (checkForPermissions() && isGranted(AppOpsManager.OPSTR_GET_USAGE_STATS) && isGranted(AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW)) {
                // Start LockService
                stopService(new Intent(getApplicationContext(), LockService.class));
                startService(new Intent(getApplicationContext(), LockService.class));
                handlerToStartService.removeCallbacksAndMessages(null);
                handlerToStartService.removeCallbacks(periodicCheckForPerms);
            }
        }
    };

    Handler showCalHandler = new Handler();
    private final Runnable showCal = new Runnable() {
        @Override
        public void run() {
            postDelayed(showCalHandler, showCal,null, 500);

            String[] lineStr = datafile2.getFileContent();
            String cal_str = lineStr[0].split(";")[0].replace(",",".");
            String secs_str = lineStr[0].split(";")[1].replace(",",".");

            long sec_long = Math.round(Double.valueOf(secs_str));
            Double cal_db = Double.valueOf(cal_str.replace(",","."));

            long hour = (sec_long / 3600);
            long mins = (sec_long % 3600) / 60;
            long secs = (sec_long % 3600) % 60;

            long progrLong = Math.round((cal_db/500)*100);
            Integer progr = (int) (long) progrLong;

            tv_time = (TextView) findViewById(R.id.tv_time);
            tv_time.setText(String.valueOf(hour) + ":" + String.valueOf(mins) + ":" + String.valueOf(secs));

            MainActivity.this.bar = (ProgressBar) MainActivity.this.findViewById(R.id.progressBar);
            bar.setProgress(progr);
        }
    };

}