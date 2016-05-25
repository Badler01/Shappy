package droxoft.armin.com.shappy;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseClassNotification  {

    private static final String DATABASENAME = "Slxfnoti.db";
    private static final String TABLENAME = "notificationtablose";
    private static final int DATABASEVERSION = 1 ;

    private static final String ROWID = "_id";
    private static final String KARSIID = "karsiid";
    private static final String KARSINAME = "karsiname";
    private static final String MESAJ = "mesaj";

    Context context;
    private static File kayityeri;
    private static SQLiteDatabase sqlitedatabaseobjesi;
    boolean oncedenvar = false;
    String varolanid;

    public DatabaseClassNotification(Context context){
        this.context = context;
        File path = Environment.getExternalStorageDirectory();
        String shappy = "Shappy";
        File f = new File(path , shappy);
        if(!f.exists()){
            f.mkdirs();
        }
        File mesajlar =new File(f,"TalkWho");
        if(!mesajlar.exists()){
            mesajlar.mkdirs();
        }
        kayityeri = mesajlar ;
    }


    public DatabaseClassNotification open(){
        DbHelper dbhelper = new DbHelper(context);
        sqlitedatabaseobjesi = dbhelper.getWritableDatabase();
        return this;
    }

    public void close(){
        sqlitedatabaseobjesi.close();
    }

    public long olustur(String karsiid , String karsiname , String mesaj) {
            ContentValues cV = new ContentValues();
            cV.put(KARSIID, karsiid);
            cV.put(KARSINAME, karsiname);
            cV.put(MESAJ, mesaj);
            Log.i("tago" , "eklendi");
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
    }
    public void deleteAll(){
        sqlitedatabaseobjesi.delete(TABLENAME, null, null);
    }

    public List<String> varolankisilericek() {
        String[] kolonlar = new String[]{ROWID , KARSIID , KARSINAME, MESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikisiler = new ArrayList<>();
        int karsinameindexi = c.getColumnIndex(KARSINAME);
        for(c.moveToFirst() ; !c.isAfterLast() ; c.moveToNext()){
            kayitlikisiler.add(c.getString(karsinameindexi));
        }
        c.close();
        return kayitlikisiler ;
    }
    public int varolankisisayisinicek() {
        Log.i("tago" , "eklee");
        String[] kolonlar = new String[]{ROWID , KARSIID , KARSINAME, MESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliidler = new ArrayList<>();
        int karsiidindexi = c.getColumnIndex(KARSIID);
        for(c.moveToFirst() ; !c.isAfterLast() ; c.moveToNext()){
            if(!kayitliidler.contains(c.getString(karsiidindexi))){
             kayitliidler.add(c.getString(karsiidindexi));
                Log.i("tago" , "ekle");
            }else{
                kayitliidler.remove(c.getString(karsiidindexi));
                kayitliidler.add(c.getString(karsiidindexi));
                Log.i("tago" , "sil ekle");
            }
        }
        c.close();
        return kayitliidler.size();
    }


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, kayityeri + File.separator + DATABASENAME, null, DATABASEVERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KARSIID +" TEXT NOT NULL, "+ KARSINAME + " TEXT NOT NULL, " + MESAJ+ " TEXT NOT NULL);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}
