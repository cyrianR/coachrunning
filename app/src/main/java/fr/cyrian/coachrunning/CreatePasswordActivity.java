package fr.cyrian.coachrunning;

import androidx.appcompat.app.AppCompatActivity;
import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

public class CreatePasswordActivity extends AppCompatActivity {

    // initialize pattern lock view
    PatternLockView mPatternLockView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        // create new password
        mPatternLockView = (PatternLockView) findViewById(R.id.pattern_lock_view);
        mPatternLockView.addPatternLockListener(new PatternLockViewListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onProgress(List<PatternLockView.Dot> progressPattern) {
            }

            @Override
            public void onComplete(List<PatternLockView.Dot> pattern) {
                // save pattern in shared preferences
                SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("password", PatternLockUtils.patternToString(mPatternLockView,pattern));
                editor.apply();


                /*if (PatternLockUtils.patternToString(mPatternLockView,pattern) != "0") {
                    Log.e("TAG",PatternLockUtils.patternToString(mPatternLockView,pattern));*/
                    // intent to navigate to home screen when password added
                    Intent in = new Intent(getApplicationContext(),MainActivity.class);
                    Toast.makeText(getApplicationContext(), "Mot de passe enregistré", Toast.LENGTH_SHORT).show();
                    startActivity(in);
                    finish();
                /*} else {
                    Toast.makeText(getApplicationContext(), "Mot de passe non valide", Toast.LENGTH_SHORT).show();
                }*/

            }

            @Override
            public void onCleared() {
            }
        });

    }


}












