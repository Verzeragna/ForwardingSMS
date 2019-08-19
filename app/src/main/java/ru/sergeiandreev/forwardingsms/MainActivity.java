package ru.sergeiandreev.forwardingsms;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends Activity {

    private static final int MY_PERMISSIONS_SEND_SMS = 999;
    public ListView lv;
    private static final int ACTIVITY_EDIT=1;
    DBHelper DbHelper;
    String[] readData;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv=(ListView) findViewById(R.id.list);
        DbHelper = new DBHelper(this);
        displayDataBase();// читаем БД
        getPermissionRecieveSMS();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                String value = (String) adapter.getItem(position);
                    // если пользователь выбрал пункт списка,
                    // то выводим его в TextView.
                        intent.putExtra("name", value);
                        startActivityForResult(intent,ACTIVITY_EDIT);
            }
        });
    }

    private void getPermissionRecieveSMS() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECEIVE_SMS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_SEND_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        } else {
            // Permission has already been granted

        }

    }

    public void AddTask(View view) {
        Intent intent = new Intent(this, AddActivity.class);
        intent.putExtra("name", "");
        startActivityForResult(intent, ACTIVITY_EDIT);
    }

    public void displayDataBase(){

        SQLiteDatabase database = DbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);// читаем из БД все данные. Класс cursor можно рассматривать, как набор строк с данными

        if (cursor.moveToFirst()){ //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
            int i=0;
            int rows = cursor.getCount();
            readData = new String[rows];

                do {

                    readData[i] = cursor.getString(titleIndex);//заполняем массив полученными данными
                    i=++i;

                }while (cursor.moveToNext());

                cursor.close();
                DbHelper.close();
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,readData);//создаем адаптер и заполняем его данными из нашего массива
            lv.setAdapter(adapter);//передаем адаптер в ListView
        }else{
            if (adapter!=null){
                readData = new String[0];
                adapter = new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,readData);
                adapter.notifyDataSetChanged();
                lv.setAdapter(adapter);
            }
        }
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        displayDataBase();
    }
}
