package hung.timingdial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity {
    private ListView TimeList;
    private List<Map<String, String>> items;
    private TimeListAdapter timeListAdapter;
    private MyDB myDB=null;
    private Toolbar toolbar;
    final public static int REQUEST_CODE_ASK_CALL_PHONE = 123;
    String strInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(MainActivity.this,android.Manifest.permission.CALL_PHONE);
            if(checkCallPhonePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_CODE_ASK_CALL_PHONE);
            }
        }
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
                strInput = items.get(position).get("phone").toString();
                //Toast.makeText(getApplicationContext(), items.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
            }
        });
        TimeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("確定刪除？")
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.delete(position+1);
                                if(items.size()>0) {
                                    items.clear();
                                    for (int i = 0; i < myDB.getCount(); i++) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("time", myDB.getItem(i, 1));
                                        map.put("name", myDB.getItem(i, 2));
                                        map.put("phone", myDB.getItem(i, 3));
                                        map.put("switch", myDB.getItem(i, 4));
                                        items.add(map);
                                    }
                                    timeListAdapter.notifyDataSetChanged();
                                }
                                myDB.getID();
                            }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                //Toast.makeText(getApplicationContext(), "Long Click "+items.get(position).get("name").toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
    /*
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "CALL_PHONE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void onResume() {
        super.onResume();
        Log.e("onResume", "onResume");
        if(items.size()>0) {
            items.clear();
            for (int i = 0; i < myDB.getCount(); i++) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("time", myDB.getItem(i, 1));
                map.put("name", myDB.getItem(i, 2));
                map.put("phone", myDB.getItem(i, 3));
                map.put("switch", myDB.getItem(i, 4));
                items.add(map);
            }
            timeListAdapter.notifyDataSetChanged();
        }
        myDB.getID();
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
