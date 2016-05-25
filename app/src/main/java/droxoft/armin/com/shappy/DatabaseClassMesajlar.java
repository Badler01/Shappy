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
import java.util.Collections;
import java.util.List;

public class DatabaseClassMesajlar {

    private static final String DATABASENAME= "Slxfmes.db";
    private static final String TABLENAME = "MesajTablosu";
    private static final int DATABASEVERSION = 1;

    private static final String ROWID = "_id";
    private static final String KENDIMESAJ = "kendimesaj";
    private static final String KARSIMESAJ = "karsimesaj";
    private static final String KARSIID = "karsiid";
    private static final String DATE="date";
    private static final String ILERLEMEDURUMU = "ilerlemedurumu";

    Context context;
    private static File kayityeri;
    private static SQLiteDatabase sqlitedatabaseobjesi;

    public DatabaseClassMesajlar(Context context) {
        this.context = context;
        File path = Environment.getExternalStorageDirectory();
        String shappy = "Shappy";
        File f = new File(path , shappy);
        if(!f.exists()){
            f.mkdirs();
        }
        File mesajlar =new File(f,"TalkW");
        if(!mesajlar.exists()){
            mesajlar.mkdirs();
        }
        kayityeri = mesajlar ;
    }

    public DatabaseClassMesajlar open(){
        DbHelper dbhelper = new DbHelper(context);
        checkexternal();
        sqlitedatabaseobjesi = dbhelper.getWritableDatabase();
        return this;
    }
    private void checkexternal() {
        String durum = Environment.getExternalStorageState();
        boolean okunabilir , yazilabilir;
        if(durum.equals(Environment.MEDIA_MOUNTED)){
            okunabilir = true;
            yazilabilir=true;
            Log.i("tago", "okunabilir ve yazılabilir");
        }else if(durum.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            okunabilir = true;
            yazilabilir = false;
            Log.i("tago" , "okunabilir fakat yazılamaz");
        }else{
            okunabilir = false;
            yazilabilir = false;
            Log.i("tago" , "okunamaz ve yazılamaz");
        }
    }
    public long olustur(String mesaj , String karsiid, String mesajdate,String ilerlemedurumu){
        ContentValues cV = new ContentValues();
        cV.put(KENDIMESAJ , mesaj);
        cV.put(KARSIMESAJ , "badbadbado");
        cV.put(KARSIID, karsiid);
        cV.put(DATE,mesajdate);
        cV.put(ILERLEMEDURUMU, ilerlemedurumu);
        return sqlitedatabaseobjesi.insert(TABLENAME , null , cV);
    }
    public void close(){
        sqlitedatabaseobjesi.close();
    }
    public List<String> databasedencek(String karsidakiid) {
        String[] kolonlar = new String[]{ROWID , KENDIMESAJ , KARSIMESAJ, KARSIID,DATE,ILERLEMEDURUMU};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KARSIID+"="+karsidakiid, null, null, null, null);
        List<String> kayitlimesajlar = new ArrayList<>();
        int kendimesajindexi = c.getColumnIndex(KENDIMESAJ);
        int karsimesajindexi = c.getColumnIndex(KARSIMESAJ);
        for(c.moveToFirst() ; !c.isAfterLast() ; c.moveToNext()){
            kayitlimesajlar.add(c.getString(kendimesajindexi) + "rumbararumbarumbarumruru" + c.getString(karsimesajindexi));
        }
        c.close();
        return kayitlimesajlar ;
    }
    public List<String> databasedenzamanlaricek(String karsidakiid){
        String[] kolonlar = new String[]{ROWID,KENDIMESAJ,KARSIMESAJ,KARSIID,DATE,ILERLEMEDURUMU};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME,kolonlar,KARSIID+"="+karsidakiid,null,null,null,null);
        List<String> kayitlizamanlar = new ArrayList<>();
        int dateindexi = c.getColumnIndex(DATE);
        for(c.moveToFirst();!c.isAfterLast() ;c.moveToNext()){
            kayitlizamanlar.add(c.getString(dateindexi));
        }
        c.close();
        return kayitlizamanlar;
    }
    public int databasedenilerlemecek(String karsidakiid){
        String[] kolonlar = new String[]{ROWID,KENDIMESAJ,KARSIMESAJ,KARSIID,DATE,ILERLEMEDURUMU};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME,kolonlar,KARSIID+"="+karsidakiid,null,null,null,null);
        List<String> kayitliilerleme = new ArrayList<>();
        List<Integer> kayitliiler = new ArrayList<>();
        int ilerleme =0;
        int ilerlemeindexi = c.getColumnIndex(ILERLEMEDURUMU);
        for(c.moveToFirst();!c.isAfterLast() ;c.moveToNext()){
            kayitliilerleme.add(c.getString(ilerlemeindexi));
        }
        for(String z : kayitliilerleme){
            kayitliiler.add(Integer.valueOf(z));
        }
        if(kayitliiler.size()>0){
            Log.i("tago" ,"size= " + String.valueOf(kayitliiler.size()));
            ilerleme = Collections.max(kayitliiler);
        }
        c.close();
        return ilerleme;
    }
    public long olusturx(String mesaj,String karsidakiid,String date,String ilerlemedurumu) {
        ContentValues cV = new ContentValues();
        cV.put(KENDIMESAJ , "badbadbado");
        cV.put(KARSIMESAJ , mesaj);
        cV.put(KARSIID , karsidakiid);
        cV.put(DATE,date);
        cV.put(ILERLEMEDURUMU, ilerlemedurumu);
        return sqlitedatabaseobjesi.insert(TABLENAME , null , cV);
    }

    public void ozelmesajlarisil(String karsiserverid) {
        sqlitedatabaseobjesi.delete(TABLENAME,KARSIID+"="+karsiserverid,null);
    }

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, kayityeri + File.separator + DATABASENAME, null, DATABASEVERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KENDIMESAJ +" TEXT NOT NULL, "+ KARSIMESAJ + " TEXT NOT NULL, " + KARSIID + " TEXT NOT NULL, "
                    +ILERLEMEDURUMU + " TEXT NOT NULL, "+ DATE + " TEXT NOT NULL);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}
