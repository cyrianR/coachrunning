package fr.cyrian.coachrunning;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends BaseAdapter {

    private Context context;
    private List<AppListItem> appListItemList;
    private LayoutInflater inflater;
    private MyActionCallback myActionCallback;
    private ArrayList<String[]> newarr;

    // constructor
    public AppListAdapter(Context context, List<AppListItem> appListItemList, MyActionCallback mActionCallback, ArrayList<String[]> newarr) {
        this.context = context;
        this.appListItemList = appListItemList;
        this.inflater = LayoutInflater.from(context);
        this.myActionCallback = mActionCallback;
        this.newarr = newarr;
    }

    @Override
    public int getCount() {
        return appListItemList.size();
    }

    @Override
    public AppListItem getItem(int position) {
        return appListItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        view = inflater.inflate(R.layout.app_list_adapter_item, null);

        // get infos about item
        AppListItem currentItem = getItem(position);

        String itemName = currentItem.getName();
        String pkgName = currentItem.getPkgName();
        Boolean isChecked = currentItem.isChecked();
        Drawable itemIcon = currentItem.getImg();

        //change check
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkbox);
        checkbox.setChecked(Boolean.valueOf(newarr.get(position)[1]));

        //change item name view
        TextView itemNameView = view.findViewById(R.id.item_name);
        itemNameView.setText(itemName);

        // change item img view
        ImageView itemIconView = view.findViewById(R.id.item_icon);
        itemIconView.setImageDrawable(itemIcon);

        // click listener
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pkg = newarr.get(position)[0];
                if(((CompoundButton) v).isChecked()) {
                    //Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
                    //checkbox.setChecked(isChecked);
                    newarr.set(position, new String[]{pkg, "true"});
                }else {
                    //Toast.makeText(context, "uncheked", Toast.LENGTH_SHORT).show();
                    //checkbox.setChecked(!isChecked);
                    newarr.set(position, new String[]{pkg, "false"});
                }
                myActionCallback.onCheckboxClick(position,((CompoundButton) v).isChecked());
            }
        });
        return view;
    }
}









