package ru.sergeiandreev.forwardingsms;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class AddActivity extends Activity {

    private static final int CONTACT_PICK_RESULT = 999;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 999;
    private static final int MY_PERMISSIONS_SEND_SMS = 999;

    public EditText mTaskName;
    public EditText mSender;
    public EditText mReciever;
    public Button mDeletebutton;
    public Button mCancelbutton;
    public String rTaskName;
    public CheckBox mActive;
    Long mRowIndex;
    DBHelper DbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        DbHelper = new DBHelper(this);
        mDeletebutton = (Button)findViewById(R.id.delete);
        mCancelbutton = (Button)findViewById(R.id.cancel);
        mTaskName = (EditText) findViewById(R.id.title0);
        mSender = (EditText) findViewById(R.id.title1);
        mReciever = (EditText) findViewById(R.id.title2);
        mActive = (CheckBox) findViewById(R.id.active);
        mActive.setChecked(true);
        rTaskName=getIntent().getExtras().getString("name");
        if (rTaskName.length()>0){
            EditMode(rTaskName);
        }
    }

public void EditMode(String TaskName){

        mDeletebutton.setVisibility(View.VISIBLE);
        mCancelbutton.setVisibility(View.VISIBLE);

        SQLiteDatabase database = DbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);// читаем из БД все данные. Класс cursor можно рассматривать, как набор строк с данными

    if (cursor.moveToFirst()){ //проверка на выполнение запроса
        //получаем порядковые номера столбцов по их именам
        int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
        int titleIndex = cursor.getColumnIndex(DBHelper.KEY_TITLE);
        int senderIndex = cursor.getColumnIndex(DBHelper.KEY_SENDER);
        int recieverIndex = cursor.getColumnIndex(DBHelper.KEY_RECIEVER);
        int checkIndex = cursor.getColumnIndex(DBHelper.KEY_CHECKER);
        do {
            String title = cursor.getString(titleIndex);
            String sender = cursor.getString(senderIndex);
            String reciever = cursor.getString(recieverIndex);
            String check = cursor.getString(checkIndex);
            if (title.equalsIgnoreCase(TaskName)){
                mRowIndex = cursor.getLong(idIndex);
                mTaskName.setText(title);
                mSender.setText(sender);
                mReciever.setText(reciever);
                if (check.equalsIgnoreCase("true")){
                    mActive.setChecked(true);
                }else{
                    mActive.setChecked(false);
                }
            }

        }while (cursor.moveToNext());

        cursor.close();
        DbHelper.close();

    }

}

    public void onClickRecieve(View view) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.

            }
        } else {
            // Permission has already been granted

        }
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent,CONTACT_PICK_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to

        EditText field = (EditText) findViewById(R.id.title2);
        String mContactId;
        if (requestCode == CONTACT_PICK_RESULT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)

                // Get the URI that points to the selected contact
                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                if (c.moveToNext()) {
                    mContactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                    String hasPhone = c.getString(c.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    if (hasPhone.equalsIgnoreCase("1")) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + mContactId,
                                null,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                null);

                        while (phones.moveToNext()) {
                            String mPhoneNumber = phones.getString(phones.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));
                            field.setText(mPhoneNumber);
                        }
                        phones.close();
                    }


                }
            }

        }

    }

    public void onClickConfirm(View view) {

        if (rTaskName.length()==0) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_SEND_SMS);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.

                }
            } else {
                // Permission has already been granted

            }

            EditText titleText = (EditText) findViewById(R.id.title0);

            EditText field = (EditText) findViewById(R.id.title1);

            EditText PhoneNumber = (EditText) findViewById(R.id.title2);

            String CheckActive;

            SQLiteDatabase database = DbHelper.getWritableDatabase(); //открываем БД для чтения и записи

            ContentValues contentValues = new ContentValues(); //класс используется для добавления новых строк в таблицу. Каждый объект класса это строка с именами столбцов

            contentValues.put(DBHelper.KEY_TITLE, String.valueOf(titleText.getText()));
            contentValues.put(DBHelper.KEY_SENDER, String.valueOf(field.getText()));
            contentValues.put(DbHelper.KEY_RECIEVER, String.valueOf(PhoneNumber.getText()));
            if (mActive.isChecked()){

                CheckActive = "true";
            }else{
                CheckActive = "false";
            }

            contentValues.put(DbHelper.KEY_CHECKER, CheckActive);

            database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);//добавляем запись в БД

            Toast.makeText(this, getString(R.string.task_saved), Toast.LENGTH_SHORT).show();
            DbHelper.close();
            finish();
        }else{

            SQLiteDatabase database = DbHelper.getWritableDatabase();
            String CheckActive;
            ContentValues cv = new ContentValues();
            cv.put(DBHelper.KEY_TITLE, String.valueOf(mTaskName.getText()));
            cv.put(DBHelper.KEY_SENDER, String.valueOf(mSender.getText()));
            cv.put(DBHelper.KEY_RECIEVER, String.valueOf(mReciever.getText()));
            if (mActive.isChecked()){

                CheckActive = "true";
            }else{
                CheckActive = "false";
            }
            cv.put(DbHelper.KEY_CHECKER, CheckActive);
            database.update(DbHelper.TABLE_CONTACTS,cv,DbHelper.KEY_ID + "=" + String.valueOf(mRowIndex),null);
            Toast.makeText(this, getString(R.string.task_udated), Toast.LENGTH_SHORT).show();
            DbHelper.close();
            finish();
        }

    }


    public void onClickDelete(View view) {

        SQLiteDatabase database = DbHelper.getWritableDatabase();

        database.delete(DbHelper.TABLE_CONTACTS, "_id = ?", new String[]{String.valueOf(mRowIndex)});

        Toast.makeText(this,getString(R.string.task_deleted),Toast.LENGTH_SHORT).show();

        DbHelper.close();
        finish();
    }

    public void onClickCancel(View view) {

        DbHelper.close();
        finish();

    }
}