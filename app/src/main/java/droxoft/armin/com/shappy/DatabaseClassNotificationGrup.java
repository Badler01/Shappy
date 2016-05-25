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

public class DatabaseClassNotificationGrup  {

    private static final String DATABASENAME = "Slxfnotigr.db";
    private static final String TABLENAME = "notificationtablose";
    private static final int DATABASEVERSION = 1 ;

    private static final String ROWID = "_id";
    private static final String KANALID = "kanalid";
    private static final String KANALNAME = "kanalname";
    private static final String YAZANINNICK = "yazaninnick";
    private static final String MESAJ = "mesaj";

    Context context;
    private static File kayityeri;
    private static SQLiteDatabase sqlitedatabaseobjesi;
    boolean oncedenvar = false;
    String varolanid;

    public DatabaseClassNotificationGrup(Context context){
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


    public DatabaseClassNotificationGrup open(){
        DbHelper dbhelper = new DbHelper(context);
        sqlitedatabaseobjesi = dbhelper.getWritableDatabase();
        return this;
    }

    public void close(){
        sqlitedatabaseobjesi.close();
    }

    public long olustur(String kanalid , String kanalname , String yazaninnick , String mesaj) {
        ContentValues cV = new ContentValues();
        cV.put(KANALID, kanalid);
        cV.put(KANALNAME, kanalname);
        cV.put(YAZANINNICK , yazaninnick);
        cV.put(MESAJ, mesaj);
        Log.i("tago" , "olusturuldu" + kanalid);
        return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
    }
    public void deleteAll(){
        sqlitedatabaseobjesi.delete(TABLENAME, null, null);
    }

    public List<String> varolankanallaricek() {
        String[] kolonlar = new String[]{ROWID , KANALID , KANALNAME, YAZANINNICK, MESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikanallar = new ArrayList<>();
        int kanalnameindexi = c.getColumnIndex(KANALNAME);
        for(c.moveToFirst() ; !c.isAfterLast() ; c.moveToNext()){
            kayitlikanallar.add(c.getString(kanalnameindexi));
        }
        c.close();
        return kayitlikanallar ;
    }
    public int varolankanalsayisinicek() {
        String[] kolonlar = new String[]{ROWID , KANALID , KANALNAME, YAZANINNICK, MESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikanalidler = new ArrayList<>();
        int karsiidindexi = c.getColumnIndex(KANALID);
        for(c.moveToFirst() ; !c.isAfterLast() ; c.moveToNext()){
            if(!kayitlikanalidler.contains(c.getString(karsiidindexi))){
                kayitlikanalidler.add(c.getString(karsiidindexi));
                Log.i("tago" , "bulunmuyor" + c.getString(karsiidindexi));
            }else{
                kayitlikanalidler.remove(c.getString(karsiidindexi));
                kayitlikanalidler.add(c.getString(karsiidindexi));
                Log.i("tago", "bulunuyor" + c.getString(karsiidindexi));
            }
        }
        c.close();
        return kayitlikanalidler.size();
    }


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, kayityeri + File.separator + DATABASENAME, null, DATABASEVERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KANALID +" TEXT NOT NULL, "+ KANALNAME + " TEXT NOT NULL, " + YAZANINNICK +" TEXT NOT NULL, "
                    + MESAJ+ " TEXT NOT NULL);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}
