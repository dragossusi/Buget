package com.rachierudragos.buget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dragos on 27.01.2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "venituri.db";
    public static final String VENIT_TABLE_NAME = "venituri_table";
    public static final String COST_TABLE_NAME = "costuri_table";
    public static final String WISHLIST_TABLE_NAME = "wishlist_table";
    public static final String CUMPARATE_TABLE_NAME = "cumparate_table";
    public static final String COL_1 = "_id";
    public static final String COL_2 = "nume";
    public static final String COL_3 = "valoare";
    public static final String COL_4 = "tip";
    public static final String COL_44 = "bucati";
    public static final String COL_5 = "data";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_Stringv = "CREATE TABLE " + VENIT_TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT UNIQUE, "
                + COL_3 + " INTEGER, "
                + COL_4 + " TEXT, "
                + COL_5 + " TEXT" + ")";
        String SQL_Stringc = "CREATE TABLE " + COST_TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT UNIQUE, "
                + COL_3 + " INTEGER, "
                + COL_4 + " TEXT, "
                + COL_5 + " TEXT" + ")";
        String SQL_Stringw = "CREATE TABLE " + WISHLIST_TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, "
                + COL_3 + " INTEGER, "
                + COL_44 + " INTEGER, "
                + COL_5 + " TEXT" + ")";
        String SQL_Stringcumparate = "CREATE TABLE " + CUMPARATE_TABLE_NAME + "("
                + COL_1 + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_2 + " TEXT, "
                + COL_3 + " INTEGER, "
                + COL_44 + " INTEGER, "
                + COL_5 + " TEXT" + ")";
        db.execSQL(SQL_Stringv);
        db.execSQL(SQL_Stringc);
        db.execSQL(SQL_Stringw);
        db.execSQL(SQL_Stringcumparate);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + VENIT_TABLE_NAME);
        onCreate(db);
    }

    ///               Adaugari              ///
    public boolean insertDataV(String nume, String valoare, String tip, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, nume);
        values.put(COL_3, valoare);
        values.put(COL_4, tip);
        values.put(COL_5, data);
        long result = db.insert(VENIT_TABLE_NAME, null, values);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataC(String nume, String valoare, String tip, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, nume);
        values.put(COL_3, valoare);
        values.put(COL_4, tip);
        values.put(COL_5, data);
        long result = db.insert(COST_TABLE_NAME, null, values);
        if (result == -1)
            return false;
        else
            return true;
    }

    public boolean insertDataW(String nume, String valoare, String bucati, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, nume);
        values.put(COL_3, valoare);
        values.put(COL_44, bucati);
        values.put(COL_5, data);
        long result = db.insert(WISHLIST_TABLE_NAME, null, values);
        if (result == -1)
            return false;
        else
            return true;
    }
    //cumparate~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>
    public void insertDataCump(String nume, String valoare, String bucati, String data){
        SQLiteDatabase db = this.getWritableDatabase();
        String Query = "SELECT * FROM " + CUMPARATE_TABLE_NAME
                + " WHERE " + COL_2 + " = '" + nume+"' AND "
                            + COL_5 + " = '" + data+"' AND "
                            + COL_3 + " = " + valoare
                + " LIMIT 1";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.moveToFirst()){
            updateCumparate(nume, valoare, bucati, data);
        }
        else {
            insertC(nume,valoare,bucati,data);
        }
    }

    public void insertC(String nume, String valoare, String bucati, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_2, nume);
        values.put(COL_3, valoare);
        values.put(COL_44, bucati);
        values.put(COL_5, data);
        db.insert(CUMPARATE_TABLE_NAME,null,values);
    }

    public void updateCumparate(String nume, String valoare,String bucati, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + CUMPARATE_TABLE_NAME + " SET " + COL_44 + " = "+COL_44+ " + " + bucati + " "
                + " WHERE " + COL_2 + " = '" + nume + "' AND "
                            + COL_5 + " = '" + data + "' AND "
                            + COL_3 + " = " + valoare);
    }
    //cumparate~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~>

    ///               Cautarile              ///

    public Cursor cautarevenituri() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(VENIT_TABLE_NAME, new String[]{COL_1,
                        COL_2, COL_3, COL_4, COL_5},
                null, null, null, null, COL_3 + " ASC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor cautarecosturi() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(COST_TABLE_NAME, new String[]{COL_1,
                        COL_2, COL_3, COL_4, COL_5},
                null, null, null, null, COL_3 + " ASC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor cautarewishlist() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(WISHLIST_TABLE_NAME, new String[]{COL_1,
                        COL_2, COL_3, COL_44, COL_5},
                null, null, null, null, COL_3 + " * " + COL_44 + " ASC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor cautarecumparat(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(CUMPARATE_TABLE_NAME, new String[]{COL_1,
                        COL_2, COL_3, COL_44, COL_5},
                COL_5 + "='" + data+"'", null, null, null, COL_3 + " * " + COL_44 + " ASC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor cautarewishlistlowerthan(String valoare) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(WISHLIST_TABLE_NAME, new String[]{COL_1,
                        COL_2, COL_3, COL_44},
                COL_3 + "<= " + valoare, null, null, null, COL_3 + " * " + COL_44 + " ASC");

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    ///               Delete-uri              ///

    public void deleteNumeV(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + VENIT_TABLE_NAME + " WHERE " + COL_2 + "='" + name + "'");
        db.close();
    }

    public void deleteNumeC(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + COST_TABLE_NAME + " WHERE " + COL_2 + "='" + name + "'");
        db.close();
    }

    public void deleteNumeW(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + WISHLIST_TABLE_NAME + " WHERE " + COL_1 + "= " + id);
        db.close();
    }

    public void scadebucatiW(String id, String bucati){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE " + WISHLIST_TABLE_NAME + " SET " + COL_44 + " = " + COL_44 + " - "+ bucati
                + " WHERE " + COL_1 + "= " + id);
        db.execSQL("DELETE FROM " + WISHLIST_TABLE_NAME + " WHERE " + COL_44 + "= 0");
    }

    ///               Selectare              ///
    public String getval(String nume, int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        int valoare;
        String val = null;
        String table;
        if (i == 1) table = VENIT_TABLE_NAME;
        else table = COST_TABLE_NAME;
        Cursor mCursor;
        mCursor = db.rawQuery("SELECT " + COL_3 + " FROM " + table + " WHERE " + COL_2 + "= '" + nume + "'", null);

        if (mCursor != null) {
            mCursor.moveToFirst();
            valoare = mCursor.getInt(0);
            val = String.valueOf(valoare);
        }
        return val;
    }

    public void updatedetails(String nume, String valoare, String tip, int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table;
        if (i == 1) table = VENIT_TABLE_NAME;
        else table = COST_TABLE_NAME;
        db.execSQL("UPDATE " + table + " SET " + COL_3 + " = " + valoare + ", "
                + COL_4 + " = '" + tip + "'"
                + " WHERE " + COL_2 + "= '" + nume + "'");
    }

    public void updatedata(String nume, String data, int i) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table;
        if (i == 1) table = VENIT_TABLE_NAME;
        else table = COST_TABLE_NAME;
        db.execSQL("UPDATE " + table + " SET " + COL_5 + " = '" + data + "' "
                + " WHERE " + COL_2 + " = '" + nume + "'");
    }

    public void updatedetailsW(String id, String valoare, String bucati) {
        SQLiteDatabase db = this.getWritableDatabase();
        String table = WISHLIST_TABLE_NAME;
        db.execSQL("UPDATE " + table + " SET " + COL_3 + " = " + valoare + ", "
                + COL_44 + " = "+ bucati
                +" WHERE " + COL_1 + " = " + id );
    }
}
