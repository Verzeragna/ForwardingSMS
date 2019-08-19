package ru.sergeiandreev.forwardingsms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    DBHelper DbHelper;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Здесь мы получаем сообщение с помощью метода intent.getExtras().get("pdus"), который возвращает массив объектов в формате PDU — эти объекты мы потом приводим к типу SmsMessage с помощью метода createFromPdu().
            Object[] pduArray = (Object[]) intent.getExtras().get("pdus");
            SmsMessage[] messages = new SmsMessage[pduArray.length];
            for (int i = 0; i < pduArray.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) pduArray[i]);
            }

            //Здесь мы составляем текст сообщения (в случае, когда сообщение было длинным и пришло в нескольких смс-ках, каждая отдельная часть хранится в messages[i]) и вызываем метод abortBroadcast(), чтобы предотвратить дальнейшую обработку сообщения другими приложениями.
            String sms_from = messages[0].getDisplayOriginatingAddress();
                StringBuilder bodyText = new StringBuilder();
                for (int i = 0; i < messages.length; i++) {
                    bodyText.append(messages[i].getMessageBody());
                }
                String body = bodyText.toString();

                displayDataBase(sms_from, body);

    }

    public void displayDataBase(String sms_from, String sms_body){

        SQLiteDatabase database = DbHelper.getWritableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,null,null,null,null,null);// читаем из БД все данные. Класс cursor можно рассматривать, как набор строк с данными

        if (cursor.moveToFirst()){ //проверка на выполнение запроса
            //получаем порядковые номера столбцов по их именам
            int senderIndex = cursor.getColumnIndex(DBHelper.KEY_SENDER);
            int recieverIndex = cursor.getColumnIndex(DBHelper.KEY_RECIEVER);
            int checkIndex = cursor.getColumnIndex(DBHelper.KEY_CHECKER);
            int i=0;
            int j=0;
            do {
                String sender = cursor.getString(senderIndex);
                String reciever = cursor.getString(recieverIndex);
                String check = cursor.getString(checkIndex);
                if (sender.equalsIgnoreCase(sms_from) && check.equalsIgnoreCase("true")){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(reciever, null, sms_body, null, null);
                }

            }while (cursor.moveToNext());

            cursor.close();
            DbHelper.close();

        }
    }
}
