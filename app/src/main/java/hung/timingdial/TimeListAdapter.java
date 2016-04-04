package hung.timingdial;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created by Hung on 2016/4/4.
 */
public class TimeListAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private List<Map<String, String>> myItems;
    private boolean mySwitchValue=false;
    public TimeListAdapter(Context c, List<Map<String, String>> items) {
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
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
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
        viewHolder.mSwitch.setChecked(mySwitchValue);

        return view;
    }
    private class ViewHolder {
        TextView txtName;
        TextView txtTime;
        TextView txtPhoneNumber;
        Switch mSwitch;
    }
}
