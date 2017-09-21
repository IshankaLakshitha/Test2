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
    private static final String TABLE_SCEDULE="scedule";//table name
    private static final String SCEDULE_DAY_ID="id";
    private static final String SCEDULE_COLUM_DAY="day";
    private static final String SCEDULE_COLUM_ITEMS="items";

    private static final String TABLE_ITEM="scedule";//table name
    private static final String ITEM_ID="iteaId";
    private static final String ITEM_NAME="itemName";
    private static final String ITEM_MAC="itemMac";


    public DBhandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    //Create table
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_QUARY = "CREATE TABLE " + TABLE_SCEDULE + "(" + SCEDULE_DAY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + SCEDULE_COLUM_DAY + " TEXT ,"+SCEDULE_COLUM_ITEMS +" TEXT "+ ");";
        sqLiteDatabase.execSQL(CREATE_QUARY);
        /*String CREATE_QUARY_Iteam = "CREATE TABLE " + TABLE_ITEM + "(" + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT ,"+ITEM_MAC +" TEXT "+ ");";
        sqLiteDatabase.execSQL(CREATE_QUARY_Iteam);*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE_SCEDULE);
        onCreate(sqLiteDatabase);
       /* sqLiteDatabase.execSQL("DROP TABLE IF EXISTS"+TABLE_ITEM);
        onCreate(sqLiteDatabase);*/
    }

    //get values from database MON=1
   /* public String databasetostringSedule(int id){
        String ID= String.valueOf(id);
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        //String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE" + SCEDULE_DAY_ID + "=\"" + id + "\";";
        String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE id=?";
        Cursor c =db.rawQuery(query,new String[]{ID});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("id"))!=null) {
                dbString= c.getString(2);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }*/

    public String databasetostringSedule(String day){
        //String ID= String.valueOf(id);
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        //String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE" + SCEDULE_DAY_ID + "=\"" + id + "\";";
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
    public boolean updateDataSedule(String day, String items){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SCEDULE_COLUM_ITEMS,items);
        sq.update(TABLE_SCEDULE,values,"day=?",new String[]{day});
        return true;
    }

    public void addProductSedule(String day)
    {
        ContentValues values= new ContentValues();
        values.put(SCEDULE_COLUM_DAY,day);
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_SCEDULE,null,values);
        db.close();
    }

/*
    public String databasetostringItem(int id){
        String ID= String.valueOf(id);
        String dbString="";
        SQLiteDatabase db= getWritableDatabase();
        //String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE" + SCEDULE_DAY_ID + "=\"" + id + "\";";
        String query = "SELECT * FROM " + TABLE_SCEDULE + " WHERE id=?";
        Cursor c =db.rawQuery(query,new String[]{ID});
        c.moveToFirst();
        while (!c.isAfterLast()) {
            if(c.getString(c.getColumnIndex("id"))!=null) {
                dbString= c.getString(1);
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    //update database
    public boolean updateDataItem(String day, String items){
        SQLiteDatabase sq=this.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(SCEDULE_COLUM_ITEMS,items);
        sq.update(TABLE_ITEM,values,"day=?",new String[]{day});
        return true;
    }

    public void addProductItem(String Item)
    {
        ContentValues values= new ContentValues();
        values.put(ITEM_NAME,Item);
        SQLiteDatabase db= getWritableDatabase();
        db.insert(TABLE_ITEM,null,values);
        db.close();
    }*/
}
