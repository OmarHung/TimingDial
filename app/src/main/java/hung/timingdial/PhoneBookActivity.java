package hung.timingdial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PhoneBookActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView PhoneBookListView;
    private SimpleAdapter arrayAdapter;
    private String[] Name,PhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        PhoneBookListView = (ListView) findViewById(R.id.phone_book_listview);

        List<HashMap<String , String>> list = new ArrayList<>();
        //使用List存入HashMap，用來顯示ListView上面的文字。
        Name = getContactsName();
        PhoneNumber  = getContactsNumber();
        for(int i = 0 ; i < Name.length ; i++){
            HashMap<String , String> hashMap = new HashMap<>();
            hashMap.put("name" , Name[i]);
            hashMap.put("phonenumber" , PhoneNumber[i]);
            //把title , text存入HashMap之中
            list.add(hashMap);
            //把HashMap存入list之中
        }
        // 橋接器的視覺
        arrayAdapter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,new String[]{"name", "phonenumber"},new int[]{android.R.id.text1 , android.R.id.text2});
        // 設定橋接給ListView
        PhoneBookListView.setAdapter(arrayAdapter);

        PhoneBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(PhoneBookActivity.this)
                        .setTitle("選擇聯絡人")
                        .setMessage("聯絡人："+Name[position]+"\n"+"電話："+PhoneNumber[position])
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();	//取得Bundle
                                bundle.putString("name", Name[position]);
                                bundle.putString("phonenumber", PhoneNumber[position]);
                                intent.putExtras(bundle);
                                setResult(RESULT_FIRST_USER, intent);
                                finish();
                                Log.e("setResult", Name[position] + " " + PhoneNumber[position]);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
            }
        });
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_phone_book, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
    //取得所有聯絡人姓名
    public String[] getContactsName() {
        // 電話簿
        String[] phoneBook;
        // 取得裝置上內容
        ContentResolver contentResolver = getContentResolver();
        // 指到電話簿
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        // 取得電話簿數量
        phoneBook = new String[cursor.getColumnCount()];
        for (int index = 0; index < phoneBook.length; index++) {
            // 持續走訪
            cursor.moveToNext();
            // 取得姓名
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneBook[index] = name;
        }
        // 回傳值
        return phoneBook;
    }
    //取得所有聯絡人電話
    public String[] getContactsNumber() {
        // 電話簿
        String[] phoneBook;
        // 取得裝置上內容
        ContentResolver contentResolver = getContentResolver();
        // 指到電話簿
        Cursor cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        // 取得電話簿數量
        phoneBook = new String[cursor.getColumnCount()];
        for (int index = 0; index < phoneBook.length; index++) {
            // 持續走訪
            cursor.moveToNext();
            // 取得電話
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneBook[index] = number;
        }
        // 回傳值
        return phoneBook;
    }
}
