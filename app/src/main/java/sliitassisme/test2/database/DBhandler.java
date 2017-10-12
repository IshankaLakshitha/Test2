package sliitassisme.test2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL on 7/18/2017.
 */

public class DBhandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="AssistME.db";//db name


    String CREATE_Scedule_Table = "CREATE TABLE " + TABLE_SCEDULE + "(" + SCEDULE_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SCEDULE_COLUM_DAY + " TEXT ,"+SCEDULE_COLUM_ITEMS + " TEXT ,"+SCEDULE_COLUM_Location +" TEXT ,"+SCEDULE_COLUM_Time + " TEXT ,"+SCEDULE_COLUM_TMode +" TEXT "+ ");";
    private static final String TABLE_SCEDULE="scedule";//table name
    private static final String SCEDULE_DAY_ID="id";
    private static final String SCEDULE_COLUM_DAY="day";
    private static final String SCEDULE_COLUM_ITEMS="items";
    private static final String SCEDULE_COLUM_Location="Location";
    private static final String SCEDULE_COLUM_Time="Time";
    private static final String SCEDULE_COLUM_TMode="Tmode";


    String CREATE_ITEM_DETAILS = "CREATE TABLE " + TABLE_ITEM + "(" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT ,"+ITEM_MAC +" TEXT ,"+GPS +" TEXT "+ ");";
    private static final String TABLE_ITEM="Item";//table name
    private static final String ITEM_ID="itemId";
    private static final String ITEM_NAME="itemName";
    private static final String ITEM_MAC="itemMac";
    private static final String GPS="itemLocation";

    String CREATE_ALARM_TABLE = "CREATE TABLE " + TABLE_ALARM + "(" + ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME + " TEXT ," + LOCATION + " TEXT ,"+TRANSPORTATION +" TEXT ,"+TIME +" TEXT ,"+DAY +" TEXT ," + STATE + " TEXT "+ ");";
    private static final String TABLE_ALARM="Alarm";//table name
    private static final String ALARM_ID="alarmId";
    private static final String NAME="name";
    private static final String LOCATION="location";
    private static final String TRANSPORTATION="transportation";
    private static final String TIME="time";
    private static final String DAY="day";
    private static final String STATE="state";


    public DBhandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    //Create table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL(CREATE_Scedule_Table);
        sqLiteDatabase.execSQL(CREATE_ITEM_DETAILS);
        sqLiteDatabase.execSQL(CREATE_ALARM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE_SCEDULE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+CREATE_ITEM_DETAILS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+CREATE_ALARM_TABLE);
        onCreate(sqLiteDatabase);
    }


    public String databasetostringSedule(String day){
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE day=?";
        Cursor c =db.rawQuery(query,new String[]{day});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("id"))!=null) {
                dbString= c.getString(2);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    //update database
    public boolean updateDataSedule(String day, String items,String loc,String Tim,String Mode){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SCEDULE_COLUM_Location,loc);
        values.put(SCEDULE_COLUM_ITEMS,items);
        values.put(SCEDULE_COLUM_Time,Tim);
        values.put(SCEDULE_COLUM_TMode,Mode);
        sq.update(TABLE_SCEDULE,values,"day=?",new String[]{day});
        return true;
    }

    public void addProductSedule(String day)
    {
        ContentValues values= new ContentValues();
        values.put(SCEDULE_COLUM_DAY,day);
        //values.put(SCEDULE_COLUM_Location,loc);
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_SCEDULE,null,values);
        db.close();
    }




    public void addProductItem(String ItemName,String mac)
    {
        ContentValues values= new ContentValues();
        values.put(ITEM_NAME,ItemName);
        values.put(ITEM_MAC,mac);
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_ITEM,null,values);
        db.close();
    }

    public boolean updateDataItemLocation(String MAC, String gps){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(GPS,gps);
        sq.update(TABLE_ITEM,values,"itemMac=?",new String[]{MAC});
        return true;
    }

    public boolean updateDataItemName(String MAC, String Name){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(ITEM_NAME,Name);
        sq.update(TABLE_ITEM,values,"itemMac=?",new String[]{MAC});
        return true;
    }

    public String databasetostringItem(String Mac){
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEM + " WHERE itemMac=?";
        Cursor c =db.rawQuery(query,new String[]{Mac});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("itemId"))!=null) {
                dbString= c.getString(3);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    public String SelectAllItems(){
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ITEM ;
        Cursor c =db.rawQuery(query,null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("itemId"))!=null) {
                dbString= dbString+"#"+c.getString(2);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    public void removeSingleContact(String Mac) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ITEM + " WHERE " + ITEM_MAC + "= '" + Mac + "'");
        database.close();
    }


    //alrm methods
    public void addAlarm(String Name, String Location,String Transportation, String Time,String Day, String State)
    {
        ContentValues values= new ContentValues();
        values.put(NAME,Name);
        values.put(LOCATION,Location);
        values.put(TRANSPORTATION,Transportation);
        values.put(TIME,Time);
        values.put(DAY,Day);
        values.put(STATE,State);
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_ALARM,null,values);
        db.close();
    }

    public boolean updateAlarmState(String Alrmid, String Name){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(ALARM_ID,Alrmid);
        values.put(NAME,Name);
        sq.update(TABLE_ALARM,values,"alarmId=?",new String[]{ALARM_ID});
        return true;
    }


    public String databasetostringAlarm(String Day){
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_ALARM + " WHERE day=?";
        Cursor c =db.rawQuery(query,new String[]{Day});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("alarmId"))!=null) {
                dbString= c.getString(1)+"#"+c.getString(2)+"#"+c.getString(3);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }



    public void removeAlarm(String Name) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_ALARM + " WHERE " + NAME + "= '" + Name + "'");
        database.close();
    }
}
