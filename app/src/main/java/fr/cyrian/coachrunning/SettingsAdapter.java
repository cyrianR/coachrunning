package fr.cyrian.coachrunning;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SettingsAdapter extends BaseAdapter {

    private Context context;
    private List<SettingItem> settingItemList;
    private LayoutInflater inflater;

    // constructor
    public SettingsAdapter(Context context, List<SettingItem> settingItemList) {
        this.context = context;
        this.settingItemList = settingItemList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return settingItemList.size();
    }

    @Override
    public SettingItem getItem(int position) {
        return settingItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.setting_adapter_item, null);

        // get infos about item
        SettingItem currentItem = getItem(position);
        String itemName = currentItem.getName();

        //change item name view
        TextView itemNameView = view.findViewById(R.id.item_name);
        itemNameView.setText(itemName);

        // click listener
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // change password
                if (position == 0) {
                    Intent in = new Intent(context,InputPasswordActivity.class);
                    in.putExtra("classId", 0);
                    context.startActivity(in);
                }
                // change locked app
                if (position == 1) {
                    Intent in = new Intent(context,InputPasswordActivity.class);
                    in.putExtra("classId", 1);
                    context.startActivity(in);
                }
                // change personnal infos
                if (position == 2) {
                    Intent in = new Intent(context,InputPasswordActivity.class);
                    in.putExtra("classId", 2);
                    context.startActivity(in);
                }
                // see use info activity
                if (position == 3) {
                    Intent in = new Intent(context,UseInfoActivity.class);
                    context.startActivity(in);
                }
            }
        });

        return view;
    }
}
