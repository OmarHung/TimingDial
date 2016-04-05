package hung.timingdial;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

/**
 * Created by Hung on 2016/4/4.
 */
public class TimeListAdapter extends BaseAdapter {
    private MyDB myDB;
    private Context context;
    private LayoutInflater mInflater;
    private List<Map<String, String>> myItems;
    private boolean mySwitchValue=false;
    public TimeListAdapter(Context c, List<Map<String, String>> items) {
        context=c;
        mInflater = LayoutInflater.from(c);
        myItems = items;
    }
    @Override
    public int getCount() {
        return myItems.size();
    }
    @Override
    public Object getItem(int position) {
        return myItems.get(position);
    }
    @Override
    public long getItemId(int position) {
        return myItems.indexOf(getItem(position));
    }
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if(view == null){
            view = mInflater.inflate(R.layout.listview_layout,viewGroup,false);

            viewHolder = new ViewHolder();
            viewHolder.txtName = (TextView)view.findViewById(R.id.txt_name);
            viewHolder.txtTime = (TextView)view.findViewById(R.id.txt_time);
            viewHolder.txtPhoneNumber = (TextView)view.findViewById(R.id.txt_phone_number);
            viewHolder.mSwitch = (Switch)view.findViewById(R.id.switch1);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }
        viewHolder.txtName.setText(myItems.get(position).get("name").toString());
        viewHolder.txtTime.setText(myItems.get(position).get("time").toString());
        viewHolder.txtPhoneNumber.setText(myItems.get(position).get("phone").toString());
        mySwitchValue=false;
        if(myItems.get(position).get("switch").toString().equals("T")) mySwitchValue=true;
        //Log.e("setChecked", position + "  " + myItems.get(position).get("name").toString()+" "+mySwitchValue+" "+viewHolder.mSwitch.isChecked());
        viewHolder.mSwitch.setChecked(mySwitchValue);
        //Log.e("setChecked", position + "  " + myItems.get(position).get("name").toString() + " " + mySwitchValue + " " + viewHolder.mSwitch.isChecked());
        viewHolder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowSwitch="F";
                if(viewHolder.mSwitch.isChecked()) nowSwitch="T";
                myDB = new MyDB(context);
                myDB.open();
                myDB.update(position+1, myItems.get(position).get("time").toString(), myItems.get(position).get("name").toString(), myItems.get(position).get("phone").toString(), nowSwitch);
                //Log.e("LOG", position + "  " + myItems.get(position).get("name").toString() + " " + viewHolder.mSwitch.isChecked());
            }
        });
        return view;
    }
    private class ViewHolder {
        TextView txtName;
        TextView txtTime;
        TextView txtPhoneNumber;
        Switch mSwitch;
    }
}
