package hung.timingdial;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private ListView TimeList;
    private List<Map<String, String>> items;
    private TimeListAdapter timeListAdapter;
    private MyDB myDB=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TimeList = (ListView)findViewById(R.id.listView);
        items = new ArrayList<Map<String,String>>();
        myDB = new MyDB(this);
        myDB.open();
        for(int i=0;i<myDB.getCount()-1;i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("time", myDB.getItem(i,1));
            map.put("name", myDB.getItem(i,2));
            map.put("phone",myDB.getItem(i,3));
            map.put("switch",myDB.getItem(i,4));
            items.add(map);
        }
        timeListAdapter = new TimeListAdapter(this,items);
        TimeList.setAdapter(timeListAdapter);

        TimeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),items.get(position).get("name").toString(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
