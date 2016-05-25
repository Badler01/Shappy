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

public class DatabaseClassKimleriActirdin {

    private static final String DATABASENAME = "Slxfshwu.db";
    private static final String TABLENAME = "ActirdiklarinTablosu";
    private static final int DATABASEVERSION = 6;

    private static final String ROWID = "_id";
    private static final String KARSIID = "karsiid";
    private static final String KARSIISIM = "karsiisim";
    private static final String KARSIRESIMPATH = "karsiresimpath";
    private static final String KARSIDURUM = "karsidurum";
    private static final String BANDURUMU = "bandurumu";
    private static final String KARSIFACEPROFILURL = "karsifaceprofilurl";
    private static final String CINSIYET = "cinsiyet";
    private static final String BURC = "burc";
    private static final String YENIMESAJVARMI = "yenimesajvarmi";
    private static final String KACYENIMESAJ = "kacyenimesaj";

    Context context;
    private static File kayityeri;
    private static SQLiteDatabase sqlitedatabaseobjesi;
    int hangisatir = 0;
    boolean oncedenvar = false;
    String varolanid = null;

    public DatabaseClassKimleriActirdin(Context context) {
        this.context = context;
        File path = Environment.getExternalStorageDirectory();
        String shappy = "Shappy";
        File f = new File(path, shappy);
        if (!f.exists()) {
            f.mkdirs();
        }
        File mesajlar = new File(f, "Shawho");
        if (!mesajlar.exists()) {
            mesajlar.mkdirs();
        }
        kayityeri = mesajlar;
    }

    public DatabaseClassKimleriActirdin open() {
        DbHelper dbHelper = new DbHelper(context);
        sqlitedatabaseobjesi = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        sqlitedatabaseobjesi.close();
    }

    public long olustur(String karsiid, String karsiisim, String karsiresimpath, String karsidurum, String bandurumu,
                        String karsifaceprofilurl,String cinsiyet, String burc, String yenimesajvarmi, String kacyenimesaj) {

        List<String> a = databasedenidcek();
        for (int i = 0; i < a.size(); i++) {
            if (karsiid.equals(a.get(i))) {
                oncedenvar = true;
                varolanid = a.get(i);
            }
        }
        if (oncedenvar) {
            int c = sqlitedatabaseobjesi.delete(TABLENAME, KARSIID + "=" + varolanid, null);
            ContentValues cV = new ContentValues();
            cV.put(KARSIID, karsiid);
            cV.put(KARSIISIM, karsiisim);
            cV.put(KARSIRESIMPATH, karsiresimpath);
            cV.put(KARSIDURUM, karsidurum);
            cV.put(BANDURUMU, bandurumu);
            cV.put(KARSIFACEPROFILURL, karsifaceprofilurl);
            cV.put(CINSIYET, cinsiyet);
            cV.put(BURC, burc);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        if (!oncedenvar) {
            ContentValues cV = new ContentValues();
            cV.put(KARSIID, karsiid);
            cV.put(KARSIISIM, karsiisim);
            cV.put(KARSIRESIMPATH, karsiresimpath);
            cV.put(KARSIDURUM, karsidurum);
            cV.put(BANDURUMU, bandurumu);
            cV.put(KARSIFACEPROFILURL, karsifaceprofilurl);
            cV.put(CINSIYET, cinsiyet);
            cV.put(BURC, burc);
            cV.put(YENIMESAJVARMI, yenimesajvarmi);
            cV.put(KACYENIMESAJ, kacyenimesaj);
            return sqlitedatabaseobjesi.insert(TABLENAME, null, cV);
        }
        return 23;
    }

    public List<String> databasedenidcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliidler = new ArrayList<>();
        int karsiidindexi = c.getColumnIndex(KARSIID);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliidler.add(c.getString(karsiidindexi));
            Log.i("tago", "hangi id" + c.getString(karsiidindexi));
        }
        c.close();
        return kayitliidler;
    }

    public List<String> databasedenisimcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliisimler = new ArrayList<>();
        int karsiisimindexi = c.getColumnIndex(KARSIISIM);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliisimler.add(c.getString(karsiisimindexi));
        }
        c.close();
        return kayitliisimler;
    }

    public List<String> databasedenresimpathcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliresimpathler = new ArrayList<>();
        int karsiresimpathindexi = c.getColumnIndex(KARSIRESIMPATH);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliresimpathler.add(c.getString(karsiresimpathindexi));
        }
        c.close();
        return kayitliresimpathler;
    }

    public List<String> databasedendurumcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlidurumlar = new ArrayList<>();
        int karsidurumindexi = c.getColumnIndex(KARSIDURUM);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlidurumlar.add(c.getString(karsidurumindexi));
        }
        c.close();
        return kayitlidurumlar;
    }

    public List<String> databasedenfaceprofilurlcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlifaceprofilurller = new ArrayList<>();
        int karsifaceprofilindexi = c.getColumnIndex(KARSIFACEPROFILURL);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlifaceprofilurller.add(c.getString(karsifaceprofilindexi));
        }
        c.close();
        return kayitlifaceprofilurller;

    }

    public String databasedenbanlanmadurumucek(String karsiid) {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KARSIID + "='" + karsiid + "'", null, null, null, null);
        List<String> kayitlibandurumu = new ArrayList<>();
        int bandurumuindexi = c.getColumnIndex(BANDURUMU);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlibandurumu.add(c.getString(bandurumuindexi));
        }
        c.close();
        for (String s : kayitlibandurumu) {
            if (s.equals("evet")) {
                return "evet";
            }
        }
        return "hayir";
    }

    public List<String> databasedenyenimesajvarmicek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliyenimesajvarmilar = new ArrayList<>();
        int karsiyenimesajvarmiindexi = c.getColumnIndex(YENIMESAJVARMI);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliyenimesajvarmilar.add(c.getString(karsiyenimesajvarmiindexi));
        }
        c.close();
        return kayitliyenimesajvarmilar;

    }

    public List<String> databasedenkacyenimesajcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlikacyenimesajlar = new ArrayList<>();
        int karsikacyenimesajindexi = c.getColumnIndex(KACYENIMESAJ);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlikacyenimesajlar.add(c.getString(karsikacyenimesajindexi));
        }
        c.close();
        return kayitlikacyenimesajlar;
    }

    public String databasedenozelresimpathcek(String karsiid) {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KARSIID + "='" + karsiid + "'", null, null, null, null);
        List<String> kayitliresimpathler = new ArrayList<>();
        int karsiresimpathindexi = c.getColumnIndex(KARSIRESIMPATH);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliresimpathler.add(c.getString(karsiresimpathindexi));
        }
        c.close();
        return kayitliresimpathler.get(0);
    }

    public String databasedenyenimesajsayisicek(String karsiid) {
        List<String> kacyenimesajlar = new ArrayList<>();
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, KARSIID + "='" + karsiid + "'", null, null, null, null);
        int kacyenimesajindexi = c.getColumnIndex(KACYENIMESAJ);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kacyenimesajlar.add(c.getString(kacyenimesajindexi));
        }
        c.close();
        if(kacyenimesajlar!=null){
            return kacyenimesajlar.get(0);
        }else{
            return "0";
        }
    }

    public List<String> databasedencinsiyetcek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitlicinsiyetler = new ArrayList<>();
        int cinsiyetindexi = c.getColumnIndex(CINSIYET);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitlicinsiyetler.add(c.getString(cinsiyetindexi));
        }
        c.close();
        return kayitlicinsiyetler;
    }

    public List<String> databasedenburccek() {
        String[] kolonlar = new String[]{ROWID, KARSIID, KARSIISIM, KARSIRESIMPATH, KARSIDURUM, BANDURUMU, KARSIFACEPROFILURL,CINSIYET,BURC,
                YENIMESAJVARMI, KACYENIMESAJ};
        Cursor c = sqlitedatabaseobjesi.query(TABLENAME, kolonlar, null, null, null, null, null);
        List<String> kayitliburclar = new ArrayList<>();
        int burcindexi = c.getColumnIndex(BURC);
        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
            kayitliburclar.add(c.getString(burcindexi));
        }
        c.close();
        return kayitliburclar;
    }

    public void kisiyisil(String karsiserverid) {
        sqlitedatabaseobjesi.delete(TABLENAME, KARSIID + "=" + karsiserverid, null);
    }


    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(Context context) {
            super(context, kayityeri + File.separator + DATABASENAME, null, DATABASEVERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLENAME + "(" + ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KARSIID + " TEXT NOT NULL, " + KARSIISIM + " TEXT NOT NULL, " + KARSIRESIMPATH + " TEXT NOT NULL, " + KARSIDURUM +
                    " TEXT NOT NULL, " + BANDURUMU + " TEXT NOT NULL, " + KARSIFACEPROFILURL + " TEXT NOT NULL, " + CINSIYET + " TEXT NOT NULL, "
                    + BURC + " TEXT NOT NULL, " + YENIMESAJVARMI + " TEXT NOT NULL, " + KACYENIMESAJ + " TEXT NOT NULL);");
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLENAME);
            onCreate(db);
        }
    }
}

