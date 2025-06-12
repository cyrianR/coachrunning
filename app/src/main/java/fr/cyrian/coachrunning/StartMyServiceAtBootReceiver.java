package fr.cyrian.coachrunning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Objects;

public class StartMyServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent serviceIntent = new Intent(context, LockService.class);
                context.startForegroundService(serviceIntent);
            } else {
                Intent serviceIntent = new Intent(context, LockService.class);
                context.startService(serviceIntent);
            }
        }
    }

}
