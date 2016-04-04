package hung.timingdial;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
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
    private Switch mSwitch;
    private List<Map<String, String>> items;
    private TimeListAdapter timeListAdapter;
    private MyDB myDB=null;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);

        TimeList = (ListView)findViewById(R.id.listView);

        items = new ArrayList<Map<String,String>>();
        myDB = new MyDB(this);
        myDB.open();
        for(int i=0;i<myDB.getCount();i++) {
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
                Toast.makeText(getApplicationContext(), items.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
            }
        });
        TimeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), "Long Click "+items.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
           startActivity(new Intent(this,NewDialActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
