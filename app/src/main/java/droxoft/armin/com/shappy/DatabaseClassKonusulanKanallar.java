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

public class DatabaseClassKonusulanKanallar {
    private static final String DATABASENAME = "SlxfChan.db";
    private static final String TABLENAME = "KonusulanKanalTablosu";
    private static final int DATABASEVERSION = 4;

    private static final String ROWID = "_id";
    private static final String KANALADI = "kanaladi";
    private static final String KANALMODU = "kanalmodu";
    private static final String KANALBEGENME = "kanalbegenme";
    private static final String KANALRESIMURL = "kanalresimpath";
    private static final String YENIMESAJVARMI = "yenimesajvarmi";
    private static final String KACYENIMESAJ = "kacyenimesaj";

    Context context;
    private static File kayityeri;
    private static SQLiteDatabase sqlitedatabaseobjesi;
    String varolankanal = null;

    public DatabaseClassKonusulanKanallar(Context context) {
        this.context = context;
        File path = Environment.getExternalStorageDirectory();
        String shappy = "Shappy";
        File f = new File(path, shappy);
        if (!f.exists()) {
            f.mkdirs();
        }
        File mesajlar = new File(f, "WhCha");
        if (!mesajlar.exists()) {
            mesajlar.mkdirs();
        }
        kayityeri = mesajlar;
    }

    public DatabaseClassKonusulanKanallar open() {
        boolean okunabilir, yazilabilir;
        String durum = Environment.getExternalStorageState();
        if (durum.equals(Environment.MEDIA_MOUNTED)) {
            okunabilir = true;
            yazilabilir = true;
            Log.i("tago", "okunabilir ve yazılabilir");
        } else if (durum.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            okunabilir = true;
            yazilabilir = false;
            Log.i("tago", "okunabilir fakat yazılamaz");
        } else {
            okunabilir = false;
            yazilabilir = false;
            Log.i("tago", "okunamaz ve yazılamaz");
        }
        DbHelper dbhelper = new DbHelper(context);
        sqlitedatabaseobjesi = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqlitedatabaseobjesi.close();
    }

    public long olustur(String kanaladi, String kanalmodu, String like, String kanalresimpath, String yenimesajvarmi, String kacyenimesaj) {
        boolean var = false;
        boolean begenmevar = false;
        if (like.equals("yes")) {
            begenmevar = true;
        }
        List<String> a = databasedenkanalcek();
        for (int i = 0; i < a.size(); i++) {
            Log.i("tago", "kanaladi =" + kanaladi);
            Log.i("tago", "databasekanal = " + a.get(i));
            if (kanaladi.equals(a.get(i))) {
                var = true;
                varolankanal = a.get(i);
                Log.i("tago", String.valueOf(var));
            }
        }

        if (var == true && begenmevar == false) {
            sqlitedatabaseobjesi.delete(TABLENAME, KANALADI + "='" + varolankanal + "'", null);
            ContentValues cV = new ContentValues();
            cV.put(KANALADI, kanaladi);
            cV.put(KANALMODU, kanalmodu);
            cV.put(KANALBEGENME, "no");
            cV.put(KANALRESIMURL, kanalresimpath);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        if (var == true && begenmevar == true) {
            sqlitedatabaseobjesi.delete(TABLENAME, KANALADI + "='" + varolankanal + "'", null);
            ContentValues cV = new ContentValues();
            cV.put(KANALADI, kanaladi);
            cV.put(KANALMODU, kanalmodu);
            cV.put(KANALBEGENME, "yes");
            cV.put(KANALRESIMURL, kanalresimpath);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        if (var == false && begenmevar == false) {
            ContentValues cV = new ContentValues();
            cV.put(KANALADI, kanaladi);
            cV.put(KANALMODU, kanalmodu);
            cV.put(KANALBEGENME, "no");
            cV.put(KANALRESIMURL, kanalresimpath);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        if (var == false && begenmevar == true) {
            ContentValues cV = new ContentValues();
            cV.put(KANALADI, kanaladi);
            cV.put(KANALMODU, kanalmodu);
            cV.put(KANALBEGENME, "yes");
            cV.put(KANALRESIMURL, kanalresimpath);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        long b = 23;
        return b;
    }

    public List<String> databasedenkanalcek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikanallar = new ArrayList<>();
        int kanaladiindexi = c.getColumnIndex(KANALADI);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlikanallar.add(c.getString(kanaladiindexi));
        }
        c.close();
        return kayitlikanallar;
    }

    public List<String> databasedenmodcek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlimodlar = new ArrayList<>();
        int kanalmoduindexi = c.getColumnIndex(KANALMODU);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlimodlar.add(c.getString(kanalmoduindexi));
        }
        c.close();
        return kayitlimodlar;
    }

    public List<String> databasedenlikecek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlilikeler = new ArrayList<>();
        int likeindexi = c.getColumnIndex(KANALBEGENME);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlilikeler.add(c.getString(likeindexi));
        }
        c.close();
        return kayitlilikeler;
    }

    public String databasedenozellikecek(String kanaladi) {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KANALADI + "='" + kanaladi + "'", null, null, null, null);
        String likedurumu = "no";
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            likedurumu = c.getString(3);
        }
        c.close();
        return likedurumu;
    }

    public String databasedenozelkacyenimesajcek(String kanaladi) {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KANALADI + "='" + kanaladi + "'", null, null, null, null);
        List<String> kacyenimesajlar = new ArrayList<>();
        int kacyenimesajindexi = c.getColumnIndex(KACYENIMESAJ);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kacyenimesajlar.add(c.getString(kacyenimesajindexi));
        }
        c.close();
        return kacyenimesajlar.get(0);
    }

    public List<String> databasedenyenimesajvarmicek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliyenimesajvarmilar = new ArrayList<>();
        int yenimesajvarmiindexi = c.getColumnIndex(YENIMESAJVARMI);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliyenimesajvarmilar.add(c.getString(yenimesajvarmiindexi));
        }
        c.close();
        return kayitliyenimesajvarmilar;
    }

    public List<String> databasedenkacyenimesajcek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikacyenimesajlar = new ArrayList<>();
        int kacyenimesajindexi = c.getColumnIndex(KACYENIMESAJ);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlikacyenimesajlar.add(c.getString(kacyenimesajindexi));
        }
        c.close();
        return kayitlikacyenimesajlar;
    }

    public String kanalvarmicek(String kanaladi) {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KANALADI + "='" + kanaladi + "'", null, null, null, null);
        List<String> kanalvarmi = new ArrayList<>();
        int kanalvarmiindexi = c.getColumnIndex(KANALADI);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kanalvarmi.add(c.getString(kanalvarmiindexi));
        }
        c.close();
        if (kanalvarmi.size() == 0) {
            Log.i("tago", "kanalyok");
            return "yok";
        } else {
            Log.i("tago", "kanalvar");
            return "var";
        }
    }

    public List<String> databasedenkanalurlcek() {
        String[] kolonlar = new String[]{ROWID, KANALADI, KANALMODU, KANALBEGENME, KANALRESIMURL, YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliresimurller = new ArrayList<>();
        int resimurlindexi = c.getColumnIndex(KANALRESIMURL);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliresimurller.add(c.getString(resimurlindexi));
        }
        c.close();
        return kayitliresimurller;
    }


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, kayityeri + File.separator + DATABASENAME, null, DATABASEVERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KANALADI + " TEXT NOT NULL, " + KANALMODU + " TEXT NOT NULL, " + KANALBEGENME + " TEXT NOT NULL, "
                    + KANALRESIMURL + " TEXT NOT NULL, " + YENIMESAJVARMI + " TEXT NOT NULL, " + KACYENIMESAJ + " TEXT NOT NULL);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}

