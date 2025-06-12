package fr.cyrian.coachrunning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.andrognito.patternlockview.PatternLockView;
import com.andrognito.patternlockview.listener.PatternLockViewListener;
import com.andrognito.patternlockview.utils.PatternLockUtils;

import java.util.List;

public class InputPasswordActivity extends AppCompatActivity {

    // initialize pattern lock view
    PatternLockView mPatternLockView;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_password);

        // get actual password
        SharedPreferences sharedPreferences = getSharedPreferences("PREFS",0);
        password = sharedPreferences.getString("password","0");

        // check password
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
                if (password.equals(PatternLockUtils.patternToString(mPatternLockView,pattern))) {
                    // if drawn pattern equals to actual pattern then go to home screen
                    Intent in = new Intent(getApplicationContext(),getClassById());
                    startActivity(in);
                    finish();
                } else {
                    // error wrong password message
                    Toast.makeText(InputPasswordActivity.this,"Mot de passe incorrect",Toast.LENGTH_SHORT).show();
                    mPatternLockView.clearPattern();
                }
            }

            @Override
            public void onCleared() {
            }
        });

    }

    // get class that has to be returned when password is correct
    public Class getClassById() {
        Bundle b = getIntent().getExtras();
        int classId = b.getInt("classId");
        if (classId == 0) {
            return CreatePasswordActivity.class;
        }
        if (classId == 1) {
            return AppListActivity.class;
        }
        if (classId == 2) {
            return PersonnalSettings.class;
        }
        return MainActivity.class;
    }

}










