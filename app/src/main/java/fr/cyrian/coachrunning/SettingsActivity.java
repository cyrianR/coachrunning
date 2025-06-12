package fr.cyrian.coachrunning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // set back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // list of settings items
        List<SettingItem> settingItemList = new ArrayList<>();
        settingItemList.add(new SettingItem("Modifier le mot de passe"));
        settingItemList.add(new SettingItem("Applications bloquées"));
        settingItemList.add(new SettingItem("Modifier les informations personnelles "));
        settingItemList.add(new SettingItem("Informations d'utilisation"));
        // get list view
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new SettingsAdapter(this,settingItemList));
    }

    @Override
    public boolean onSupportNavigateUp(){
        Intent in = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(in);
        finish();
        return true;
    }
}