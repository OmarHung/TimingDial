package hung.timingdial;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Hung on 2016/4/4.
 */
public class MyPreferenceFragment extends PreferenceFragment {
    Preference Time,Phone;
    Calendar c = Calendar.getInstance();
    TimePickerDialog timePickerDialog;
    GregorianCalendar calendar = new GregorianCalendar();
    @Override
    public void onCreate(final Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.new_dial);
        Time=(Preference)findPreference("TIME");
        String Myhour=String.valueOf(c.get(Calendar.HOUR_OF_DAY)),Myminute=String.valueOf(c.get(Calendar.MINUTE));
        if(c.get(Calendar.HOUR_OF_DAY)<10) Myhour="0"+c.get(Calendar.HOUR_OF_DAY);
        if(c.get(Calendar.MINUTE)<10) Myminute="0"+c.get(Calendar.MINUTE);
        Time.setTitle(Myhour+":"+Myminute);
        Phone=(Preference)findPreference("PHONE");

        Time.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String Myhour=String.valueOf(hourOfDay),Myminute=String.valueOf(minute);
                        if(hourOfDay<10) Myhour="0"+hourOfDay;
                        if(minute<10) Myminute="0"+minute;
                        Time.setTitle(Myhour+":"+Myminute);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
                timePickerDialog.show();
                Toast.makeText(getActivity(),"TIME",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Phone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final String[] Items = {"從通訊錄","輸入電話"};
                new AlertDialog.Builder(getActivity())
                        .setTitle("電話輸入方式")
                        .setItems(Items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:

                                        break;
                                    case 1:
                                        LayoutInflater inflater = LayoutInflater.from(getActivity());
                                        final View v = inflater.inflate(R.layout.dialog_layout, null);
                                        //語法一：new AlertDialog.Builder(主程式類別).XXX.XXX.XXX;
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("請輸入電話")
                                                .setView(v)
                                                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        EditText editName = (EditText) (v.findViewById(R.id.edt_name));
                                                        EditText editPhone = (EditText) (v.findViewById(R.id.edt_phone));
                                                        Phone.setTitle(editName.getText().toString());
                                                        Phone.setSummary(editPhone.getText().toString());
                                                    }
                                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).show();
                                        break;
                                }
                            }
                        })
                        .show();
                Toast.makeText(getActivity(),"PHONE",Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}