package droxoft.armin.com.shappy;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.plattysoft.leonids.ParticleSystem;
import com.timqi.sectorprogressview.ColorfulRingProgressView;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Mesajlasma extends AppCompatActivity {

    String kendiserverid;
    String karsiserverid, karsiisim, karsidurum, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl;
    Bitmap karsiresim;
    String resmiacikmi;
    MesajArrayAdapter mesajAdapter;
    ArrayList<Mesaj> mesajListesi = new ArrayList<>();
    File a;
    String banlanmadurumu = "hayir";
    boolean notificationbas;
    ColorfulRingProgressView crpv;
    ImageButton buttonbomba;

    TextView textviewisim;
    ImageButton imagebuttonkarsiprofil;
    EditText edittextyazmaalani;
    ImageView banlanmaperdesi;
    Toolbar toolbar;


    //Orjinal Kıza Yurume
    ClipDrawable horizontalClipD, diagonalClipD;
    int YukselmeLeveli = 0;
    ParticleSystem ps_red, ps_yellow, ps_orange, ps_white;
    double i, j, k, t;
    private Runnable yuru;
    boolean benyazdim = false;
    boolean senyazdin = false;
    Handler handler = new Handler();
    public static final int DELAY = 100;

    BroadcastReceiver receiveralfa = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String message = b.getString("mesaj");
            takeChatMessage(message);
        }
    };

    BroadcastReceiver receiverbeta = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Karsidaki sohbetten cikmistir", Toast.LENGTH_LONG).show();
            sohbettencikmaislemi();
        }
    };
    BroadcastReceiver receiverteta = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Karsidakisenibanladi", Toast.LENGTH_LONG).show();
            banlanmadurumu = "evet";
            DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(Mesajlasma.this);
            dCKK.open();
            dCKK.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, resmiacikmi, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
            DatabaseClassKimleriActirdin dCKA = new DatabaseClassKimleriActirdin(Mesajlasma.this);
            dCKA.open();
            List<String> varolanlar = dCKA.databasedenidcek();
            for (String f : varolanlar) {
                if (f.equals(karsiserverid)) {
                    dCKA.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
                    dCKK.kisiyisil(karsiserverid);
                }
            }
            dCKK.close();
            dCKA.close();
            edittextyazmaalani.setHint("Banladığınız insana yazamazsınız");
            edittextyazmaalani.setEnabled(false);
            banlanmaperdesi.setVisibility(View.VISIBLE);
        }
    };

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (receiveralfa != null) {
            unregisterReceiver(receiveralfa);
            registerReceiver(receiveralfa, new IntentFilter("broadcastmessage"));
        }
        if (receiverbeta != null) {
            unregisterReceiver(receiverbeta);
            registerReceiver(receiverbeta, new IntentFilter("broadcastLeave"));
        }
        if (receiverteta != null) {
            unregisterReceiver(receiverteta);
            registerReceiver(receiverteta, new IntentFilter("broadcastBan"));
        }

        setIntent(intent);
        Intent a = getIntent();
        Log.i("tago", "onNewIntent");
        if (a.getStringExtra("intentname").equals("PushReceiverGecmis")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = a.getStringExtra("karsiid");
            karsiisim = a.getStringExtra("karsiname");
            karsidurum = a.getStringExtra("karsidurum");
            karsifaceprofilurl = a.getStringExtra("karsifaceprofilurl");
            cinsiyet = a.getStringExtra("cinsiyet");
            burc = a.getStringExtra("burc");
            String karsiresmpath = a.getStringExtra("karsiresimpath");
            String karsibandurumu = a.getStringExtra("karsibandurumu");
            try {
                karsiresim = new urldenResimm().execute(karsifaceprofilurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(this);
            dCKK.open();
            resmiacikmi = dCKK.databasedenresmiacikmicek(karsiserverid);
            dCKK.close();
            DatabaseClassKiminleKonustun dckk = new DatabaseClassKiminleKonustun(this);
            dckk.open();
            dckk.olustur(karsiserverid, karsiisim, karsiresmpath, karsidurum, resmiacikmi, karsibandurumu, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
            dckk.close();
        } else if (a.getStringExtra("intentname").equals("PushReceiverShappy")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = a.getStringExtra("karsiid");
            karsiisim = a.getStringExtra("karsiname");
            karsidurum = a.getStringExtra("karsidurum");
            karsifaceprofilurl = a.getStringExtra("karsifaceprofilurl");
            cinsiyet = a.getStringExtra("cinsiyet");
            burc = a.getStringExtra("burc");
            String karsiresmpath = a.getStringExtra("karsiresimpath");
            String karsibandurumu = a.getStringExtra("karsibandurumu");
            try {
                karsiresim = new urldenResimm().execute(karsifaceprofilurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            resmiacikmi = "acik";
            DatabaseClassKimleriActirdin poo = new DatabaseClassKimleriActirdin(this);
            poo.open();
            poo.olustur(karsiserverid, karsiisim, karsiresmpath, karsidurum, karsibandurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
            poo.close();
        }
    }

    private String SharedPrefIdAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String kendiserverid = sP.getString("serverid", "defaultid");
        return kendiserverid;
    }

    private void SharedPrefNotificationKaydet(boolean notificationbas) {
        SharedPreferences sharedPreferences = getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("notificationbas", notificationbas);
        editor.apply();
    }

    private void SharedPrefNotificationSonIdKaydet(String karsiserverid) {
        SharedPreferences sharedPreferences = getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notificationsonid", karsiserverid);
        editor.apply();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.mesajlasma);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        yuru = new Runnable() {
            @Override
            public void run() {
                if (YukselmeLeveli <= 20) {
                    KaymaAnimasyonuYatay();
                } else if (YukselmeLeveli < 23) {
                    KaymaAnimasyonuCapraz();
                } else if (YukselmeLeveli >= 23) {
                    ps_red.cancel();
                    ps_yellow.cancel();
                    ps_orange.cancel();
                    ps_white.cancel();
                }
            }
        };
        toolbar = (Toolbar) findViewById(R.id.toolbarmesajlasma);
        setSupportActionBar(toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        Drawable d = ContextCompat.getDrawable(this, R.drawable.ucnokta);
        toolbar.setOverflowIcon(d);
        banlanmaperdesi = (ImageView) findViewById(R.id.imageView6);
        Intent i = getIntent();
        if (i.getStringExtra("intentname").equals("CevrendekiInsanAdapter")) {
            karsiserverid = i.getStringExtra("karsiserverid");
            karsiisim = i.getStringExtra("karsiname");
            karsidurum = i.getStringExtra("karsidurum");
            karsiresim = i.getBundleExtra("karsiresimbundle").getParcelable("karsiresim");
            karsifaceprofilurl = i.getStringExtra("faceprofilurl");
            cinsiyet = i.getStringExtra("cinsiyet");
            burc = i.getStringExtra("burc");
            yas = i.getStringExtra("yas");
            okul = i.getStringExtra("okul");
            coverfotourl = i.getStringExtra("coverfotourl");
            resmiacikmi = "acik";

        } else if (i.getStringExtra("intentname").equals("GecmisInsanAdapter")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = i.getStringExtra("karsiserverid");
            karsiisim = i.getStringExtra("karsiisim");
            karsidurum = i.getStringExtra("karsidurum");
            banlanmadurumu = i.getStringExtra("bandurumu");
            karsifaceprofilurl = i.getStringExtra("faceprofilurl");
            cinsiyet = i.getStringExtra("cinsiyet");
            burc = i.getStringExtra("burc");
            yas = i.getStringExtra("yas");
            okul = i.getStringExtra("okul");
            coverfotourl = i.getStringExtra("coverfotourl");
            Log.i("tago", " burc" + burc);
            Log.i("tago", " cinsiyet" + cinsiyet);
            try {
                karsiresim = new urldenResimm().execute(karsifaceprofilurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(this);
            dCKK.open();
            resmiacikmi = dCKK.databasedenresmiacikmicek(karsiserverid);
            String resimpath = dCKK.databasedenozelresimpathcek(karsiserverid);
            String bandur = dCKK.databasedenozelbanlanmadurumucek(karsiserverid);
            dCKK.close();
            DatabaseClassKiminleKonustun dckx = new DatabaseClassKiminleKonustun(this);
            dckx.open();
            dckx.olustur(karsiserverid, karsiisim, resimpath, karsidurum, resmiacikmi, bandur, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
            dckx.close();
        } else if (i.getStringExtra("intentname").equals("ShappyInsanAdapter")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = i.getStringExtra("karsiserverid");
            karsiisim = i.getStringExtra("karsiisim");
            karsidurum = i.getStringExtra("karsidurum");
            banlanmadurumu = i.getStringExtra("bandurumu");
            karsifaceprofilurl = i.getStringExtra("faceprofilurl");
            cinsiyet = i.getStringExtra("cinsiyet");
            burc = i.getStringExtra("burc");
            Log.i("tago", "cinsiyet ShappyInsanAdapter" + cinsiyet);
            Log.i("tago", " burc ShappyInsanAdapter" + burc);
            String karsiresimpath = i.getStringExtra("karsiresimpath");
            karsiresim = BitmapFactory.decodeFile(karsiresimpath);
            resmiacikmi = "acik";
            DatabaseClassKimleriActirdin uuu = new DatabaseClassKimleriActirdin(this);
            uuu.open();
            uuu.olustur(karsiserverid, karsiisim, karsiresimpath, karsidurum, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
            uuu.close();
        } else if (i.getStringExtra("intentname").equals("PushReceiverShappy")) {
            Log.i("tago", "notiden gelindi");
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = i.getStringExtra("karsiid");
            karsiisim = i.getStringExtra("karsiname");
            karsidurum = i.getStringExtra("karsidurum");
            karsifaceprofilurl = i.getStringExtra("karsifaceprofilurl");
            cinsiyet = i.getStringExtra("cinsiyet");
            burc = i.getStringExtra("burc");
            Log.i("tago", "cinsiyet PushReceiverShappy" + cinsiyet);
            Log.i("tago", " burc PushReceiverShappy" + burc);
            String karsiresmpath = i.getStringExtra("karsiresimpath");
            String karsibandurumu = i.getStringExtra("karsibandurumu");
            try {
                karsiresim = new urldenResimm().execute(karsifaceprofilurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            resmiacikmi = "acik";
            DatabaseClassKimleriActirdin ooo = new DatabaseClassKimleriActirdin(this);
            ooo.open();
            ooo.olustur(karsiserverid, karsiisim, karsiresmpath, karsidurum, karsibandurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
            ooo.close();
        } else if (i.getStringExtra("intentname").equals("PushReceiverGecmis")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            karsiserverid = i.getStringExtra("karsiid");
            karsiisim = i.getStringExtra("karsiname");
            karsidurum = i.getStringExtra("karsidurum");
            karsifaceprofilurl = i.getStringExtra("karsifaceprofilurl");
            cinsiyet = i.getStringExtra("cinsiyet");
            burc = i.getStringExtra("burc");
            yas = i.getStringExtra("yas");
            okul = i.getStringExtra("okul");
            coverfotourl = i.getStringExtra("coverfotourl");
            String karsiresmpath = i.getStringExtra("karsiresimpath");
            String karsibandurumu = i.getStringExtra("karsibandurumu");
            try {
                karsiresim = new urldenResimm().execute(karsifaceprofilurl).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(this);
            dCKK.open();
            resmiacikmi = dCKK.databasedenresmiacikmicek(karsiserverid);
            dCKK.close();
            DatabaseClassKiminleKonustun dckk = new DatabaseClassKiminleKonustun(this);
            dckk.open();
            dckk.olustur(karsiserverid, karsiisim, karsiresmpath, karsidurum, resmiacikmi, karsibandurumu, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
            dckk.close();
        }
        tanimlar();
        DatabaseClassMesajlar dB = new DatabaseClassMesajlar(Mesajlasma.this);
        dB.open();
        List<String> kayitlimesajlar;
        List<String> kayitlizamanlar;
        int kayitliilerleme;
        kayitlimesajlar = dB.databasedencek(karsiserverid);
        kayitlizamanlar = dB.databasedenzamanlaricek(karsiserverid);
        kayitliilerleme = dB.databasedenilerlemecek(karsiserverid);
        dB.close();
        for (int k = 0; k < kayitlimesajlar.size(); k++) {
            Log.i("tago", kayitlimesajlar.get(k).substring(0, 10));
            if (kayitlimesajlar.get(k).substring(0, 10).equals("badbadbado")) {
                mesajAdapter.add(new Mesaj(true, kayitlimesajlar.get(k).substring(34), kayitlizamanlar.get(k)));
            } else {
                mesajAdapter.add(new Mesaj(false, kayitlimesajlar.get(k).substring(0, kayitlimesajlar.get(k).indexOf("rumbararumbarumbarumruru")), kayitlizamanlar.get(k)));
            }
        }
        YukselmeLeveli = kayitliilerleme;
        if (resmiacikmi.equals("acik")) {
            buttonbomba = (ImageButton) findViewById(R.id.imageButton11);
            buttonbomba.setVisibility(View.GONE);
            crpv = (ColorfulRingProgressView) findViewById(R.id.crpv);
            crpv.setPercent(YukselmeLeveli * (100 / 23));
            crpv.setVisibility(View.VISIBLE);
        }
        yurumesistemi();
        if (resmiacikmi.equals("acik")) {
            horizontalClipD.setLevel(0);
            diagonalClipD.setLevel(0);
            ps_red.cancel();
            ps_yellow.cancel();
            ps_orange.cancel();
            ps_white.cancel();

        }
        if (YukselmeLeveli > 23) {
            ps_red.cancel();
            ps_yellow.cancel();
            ps_orange.cancel();
            ps_white.cancel();
            horizontalClipD.setLevel(0);
            diagonalClipD.setLevel(0);
        }
    }

    private void KaymaAnimasyonuCapraz() {
        if (k < (YukselmeLeveli - 20) * 7.6667 + 7.6667) {
            double x = 120 - k;
            double y = 616 - (0.0567107 * x * x - 13.610586 * x + 1351.63516);
            Log.i("tagooo", "x and y =" + x + "and " + y);
            float XinDpi = (float) x;
            float YinDpi = (float) y;
            int level = (int) Math.round((y - 52) * 333.3);
            diagonalClipD.setLevel(level);
            Resources r = getResources();
            int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
            int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));

            ps_red.updateEmitPoint(XinPixel, YinPixel);
            ps_yellow.updateEmitPoint(XinPixel, YinPixel);
            ps_orange.updateEmitPoint(XinPixel, YinPixel);
            ps_white.updateEmitPoint(XinPixel, YinPixel);
            k += 1.533;
            handler.postDelayed(yuru, DELAY);

        }
    }

    private void KaymaAnimasyonuYatay() {
        if (j > 2445 - (YukselmeLeveli + 1) * 77.25) {
            j -= 12.875;
            t = j * 3.14159 / 180;
            double y = 71 - (10 * (Math.sin(0.7 * t)));
            double x = -5 + t * 8;
            float XinDpi = (float) x;
            float YinDpi = (float) y;
            int level = (int) Math.round((x - 121) / 20 * 927.12);
            horizontalClipD.setLevel(level);
            Log.i("tagooo", "x and y =" + x + "and " + y);
            Log.i("tago", "level =" + level);
            Resources r = getResources();
            int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
            int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));

            ps_red.updateEmitPoint(XinPixel, YinPixel);
            ps_yellow.updateEmitPoint(XinPixel, YinPixel);
            ps_orange.updateEmitPoint(XinPixel, YinPixel);
            ps_white.updateEmitPoint(XinPixel, YinPixel);
            handler.postDelayed(yuru, DELAY);
        } else if (YukselmeLeveli >= 20) {
            k = (YukselmeLeveli - 19) * 7.6667;
            StaticAnimCapraz();
        }
    }

    private void yurumesistemi() {
        ImageView horizontalrope = (ImageView) findViewById(R.id.ivYatay);
        ImageView diagonalrope = (ImageView) findViewById(R.id.ivCapraz);
        horizontalClipD = (ClipDrawable) horizontalrope.getDrawable();
        diagonalClipD = (ClipDrawable) diagonalrope.getDrawable();
        if (YukselmeLeveli < 20) {
            horizontalClipD.setLevel(10000);
            diagonalClipD.setLevel(10000);
        } else if (YukselmeLeveli < 23) {
            horizontalClipD.setLevel(0);
            diagonalClipD.setLevel(10000);
        } else if (YukselmeLeveli >= 23) {
            horizontalClipD.setLevel(0);
            diagonalClipD.setLevel(0);
        }
        j = 2445 - (YukselmeLeveli) * 77.25;
        k = (YukselmeLeveli - 19) * 7.6667;
        t = j * 3.14159 / 180;
        double x = 320 + ((10 * Math.sin(0.7 * t)));
        double y = 390.76 - t * 10;
        float XinDpi = (float) x;
        float YinDpi = (float) y;
        Resources r = getResources();
        int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
        int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));
        Log.i("tagooo", " static x and y =" + XinPixel + "and " + YinPixel);
        ps_red = new ParticleSystem(Mesajlasma.this, 300, R.mipmap.yeni_kirmizi, 600);
        ps_red.setScaleRange(0.7f, 1.3f);
        ps_red.setSpeedModuleAndAngleRange(0.01f, 0.03f, 0, 360);
        ps_red.setRotationSpeedRange(90, 180);
        ps_red.setAcceleration(0.00001f, 90);
        ps_red.setFadeOut(200, new AccelerateInterpolator());
        ps_red.emit(XinPixel, YinPixel, 150);
        ps_orange = new ParticleSystem(Mesajlasma.this, 300, R.mipmap.turuncu_yildiz, 500);
        ps_orange.setScaleRange(0.7f, 1.3f);
        ps_orange.setSpeedModuleAndAngleRange(0.01f, 0.03f, 0, 360);
        ps_orange.setRotationSpeedRange(90, 180);
        ps_orange.setAcceleration(0.00001f, 90);
        ps_orange.setFadeOut(200, new AccelerateInterpolator());
        ps_orange.emit(XinPixel, YinPixel, 100);
        ps_yellow = new ParticleSystem(Mesajlasma.this, 300, R.mipmap.sari_yildiz1, 300);
        ps_yellow.setScaleRange(0.7f, 1.3f);
        ps_yellow.setSpeedModuleAndAngleRange(0.01f, 0.03f, 0, 360);
        ps_yellow.setRotationSpeedRange(90, 180);
        ps_yellow.setAcceleration(0.00001f, 90);
        ps_yellow.setFadeOut(200, new AccelerateInterpolator());
        ps_yellow.emit(XinPixel, YinPixel, 100);

        ps_white = new ParticleSystem(Mesajlasma.this, 300, R.mipmap.beyaz_yildiz, 200);
        ps_white.setScaleRange(0.7f, 1.3f);
        ps_white.setSpeedModuleAndAngleRange(0.01f, 0.03f, 0, 360);
        ps_white.setRotationSpeedRange(90, 180);
        ps_white.setAcceleration(0.00001f, 90);
        ps_white.setFadeOut(200, new AccelerateInterpolator());
        ps_white.emit(XinPixel, YinPixel, 100);
        if (YukselmeLeveli < 20) {
            StaticAnimYatay();
        } else if (YukselmeLeveli < 23) {
            StaticAnimCapraz();
        }
    }

    private void StaticAnimCapraz() {
        double x = 120 - k;
        double y = 616 - (0.0567107 * x * x - 13.610586 * x + 1351.63516);
        int level = (int) Math.round((y - 51) * 333.3);
        diagonalClipD.setLevel(level);
        float XinDpi = (float) x;
        float YinDpi = (float) y;
        Resources r = getResources();
        int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
        int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));
        ps_red.updateEmitPoint(XinPixel, YinPixel);
        ps_yellow.updateEmitPoint(XinPixel, YinPixel);
        ps_orange.updateEmitPoint(XinPixel, YinPixel);
        ps_white.updateEmitPoint(XinPixel, YinPixel);
    }

    private void StaticAnimYatay() {
        t = j * 3.14159 / 180;
        double y = 71 - (10 * Math.sin(0.7 * t));
        double x = -5 + t * 8;
        float XinDpi = (float) x;
        float YinDpi = (float) y;
        int level = (int) Math.round((x - 121) / 20 * 927.12);
        horizontalClipD.setLevel(level);
        Log.i("tagooo", " static x and y =" + XinDpi + "and " + YinDpi);
        Resources r = getResources();
        int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
        int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));
        ps_red.updateEmitPoint(XinPixel, YinPixel);
        ps_yellow.updateEmitPoint(XinPixel, YinPixel);
        ps_orange.updateEmitPoint(XinPixel, YinPixel);
        ps_white.updateEmitPoint(XinPixel, YinPixel);
    }

    protected void onStart() {
        super.onStart();
        registerReceiver(receiveralfa, new IntentFilter("broadcastmessage"));
        registerReceiver(receiverbeta, new IntentFilter("broadcastLeave"));
        registerReceiver(receiverteta, new IntentFilter("broadcastBan"));
    }

    protected void onResume() {
        super.onResume();
        notificationbas = false;
        SharedPrefNotificationKaydet(notificationbas);
        SharedPrefNotificationSonIdKaydet(karsiserverid);
    }

    protected void onPause() {
        super.onPause();
        notificationbas = true;
        SharedPrefNotificationKaydet(notificationbas);
    }

    protected void onStop() {
        unregisterReceiver(receiveralfa);
        unregisterReceiver(receiverbeta);
        unregisterReceiver(receiverteta);
        super.onStop();

    }

    private void tanimlar() {
        kendiserverid = SharedPrefIdAl();
        textviewisim = (TextView) findViewById(R.id.textView2);
        buttonbomba = (ImageButton) findViewById(R.id.imageButton11);
        imagebuttonkarsiprofil = (ImageButton) findViewById(R.id.imageButton);
        ImageView imageviewcinsiyet = (ImageView) findViewById(R.id.cinsiyet);
        if (cinsiyet.equals("m") || cinsiyet.equals("M")) {
            imageviewcinsiyet.setImageResource(R.mipmap.erkek);
        } else {
            imageviewcinsiyet.setImageResource(R.mipmap.kari);
        }
        final ImageView imageviewburc = (ImageView) findViewById(R.id.burclar);
        if (burc.equals("oglak")) {
            imageviewburc.setImageResource(R.drawable.koglak);
            imageviewburc.setContentDescription("Oğlak");
        } else if (burc.equals("kova")) {
            imageviewburc.setImageResource(R.drawable.kkova);
            imageviewburc.setContentDescription("Kova");
        } else if (burc.equals("balik")) {
            imageviewburc.setImageResource(R.drawable.kbalik);
            imageviewburc.setContentDescription("Balık");
        } else if (burc.equals("koc")) {
            imageviewburc.setImageResource(R.drawable.kkoc);
            imageviewburc.setContentDescription("Koç");
        } else if (burc.equals("boga")) {
            imageviewburc.setImageResource(R.drawable.kboga);
            imageviewburc.setContentDescription("Boğa");
        } else if (burc.equals("ikizler")) {
            imageviewburc.setImageResource(R.drawable.kikizler);
            imageviewburc.setContentDescription("İkizler");
        } else if (burc.equals("yengec")) {
            imageviewburc.setImageResource(R.drawable.kyengec);
            imageviewburc.setContentDescription("Yengeç");
        } else if (burc.equals("Aslan")) {
            imageviewburc.setContentDescription("Aslan");
            imageviewburc.setImageResource(R.drawable.kaslan);
        } else if (burc.equals("basak")) {
            imageviewburc.setImageResource(R.drawable.kbasak);
            imageviewburc.setContentDescription("Başak");
        } else if (burc.equals("terazi")) {
            imageviewburc.setImageResource(R.drawable.kterazi);
            imageviewburc.setContentDescription("Terazi");
        } else if (burc.equals("akrep")) {
            imageviewburc.setImageResource(R.drawable.kakrep);
            imageviewburc.setContentDescription("Akrep");
        } else if (burc.equals("yay")) {
            imageviewburc.setImageResource(R.drawable.kyay);
            imageviewburc.setContentDescription("Yay");
        }
        imageviewburc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.toastburc,
                        (ViewGroup) findViewById(R.id.toastburc));
                TextView text = (TextView) layout.findViewById(R.id.textburc);
                text.setText(imageviewburc.getContentDescription());
                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
                return false;
            }
        });
        if (resmiacikmi.equals("acik")) {
            textviewisim.setText(karsiisim);
            if (karsiresim != null) {
                Bitmap a = getCircleBitmap(karsiresim);
                Bitmap sa = Bitmap.createScaledBitmap(a, 130, 130, false);
                imagebuttonkarsiprofil.setImageBitmap(sa);
            } else if (karsiresim == null) {
                urldenResimm uR = new urldenResimm();
                try {
                    karsiresim = uR.execute(karsifaceprofilurl).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                Bitmap a = getCircleBitmap(karsiresim);
                Bitmap sa = Bitmap.createScaledBitmap(a, 130, 130, false);
                imagebuttonkarsiprofil.setImageBitmap(sa);
            }
        } else {
            textviewisim.setText("??????");
            Bitmap a = getCircleBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.soruisareti));
            Bitmap sa = Bitmap.createScaledBitmap(a, 130, 130, false);
            imagebuttonkarsiprofil.setImageBitmap(sa);
        }
        ImageButton butongeridon = (ImageButton) findViewById(R.id.button6);
        ImageButton butonmesajigonder = (ImageButton) findViewById(R.id.button4);
        butongeridon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        butonmesajigonder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendChatMessage();
            }
        });
        edittextyazmaalani = (EditText) findViewById(R.id.editText2);
        if (banlanmadurumu.equals("evet")) {
            edittextyazmaalani.setHint("Banladığınız insana yazamazsınız");
            edittextyazmaalani.setEnabled(false);
            banlanmaperdesi.setVisibility(View.VISIBLE);
        }
        edittextyazmaalani.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        final ListView mesajlistviewi = (ListView) findViewById(R.id.listView2);
        mesajAdapter = new MesajArrayAdapter(this, R.layout.mesaj, mesajListesi);
        mesajlistviewi.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mesajlistviewi.setAdapter(mesajAdapter);
        mesajAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                mesajlistviewi.setSelection(mesajAdapter.getCount() - 1);
            }
        });
        boolean okunabilir, yazilabilir;
        String durum = Environment.getExternalStorageState();
        if (durum.equals(Environment.MEDIA_MOUNTED)) {
            okunabilir = true;
            yazilabilir = true;
        } else if (durum.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            okunabilir = true;
            yazilabilir = false;
        } else {
            okunabilir = false;
            yazilabilir = false;
        }
        if (okunabilir && yazilabilir) {
            File root = Environment.getExternalStorageDirectory();
            File shappy = new File(root, "Shappy");
            File konusulanresimler = new File(shappy, "Pictures");
            if (!konusulanresimler.exists()) {
                konusulanresimler.mkdirs();
            }
            a = new File(konusulanresimler, karsiserverid + "pic.jpeg");
        }
    }

    private boolean sendChatMessage() {
        if (!edittextyazmaalani.getText().toString().equals("")) {
            String yazilanmesaj = edittextyazmaalani.getText().toString();
            ServerBirebirMesajGonder sBMG = new ServerBirebirMesajGonder(kendiserverid, yazilanmesaj, karsiserverid);
            sBMG.execute("nedir");
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            String date;
            if (minute < 10) {
                date = String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                date = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
            mesajAdapter.add(new Mesaj(false, yazilanmesaj, date));
            edittextyazmaalani.setText("");
            kiminleMesajlasiyorsun();
            benyazdim = true;
            if (senyazdin) {
                kızailerle();
                benyazdim = false;
                senyazdin = false;
            }
            mesajiexternalkaydet(yazilanmesaj, date);
            return true;
        }
        return false;
    }

    private void kızailerle() {
        if (YukselmeLeveli < 23) {
            YukselmeLeveli++;
            if (resmiacikmi.equals("acik")) {
                crpv.setPercent(YukselmeLeveli * (100 / 23));
            }
            j = 2445 - (YukselmeLeveli) * 77.25;
            k = (YukselmeLeveli - 20) * 7.667;
            if (resmiacikmi.equals("degil")) {
                Log.i("tago", "Yukselme leveli" + String.valueOf(YukselmeLeveli));
                handler.postDelayed(yuru, DELAY);
            }
        }
        if (YukselmeLeveli == 23) {
            if (resmiacikmi.equals("acik")) {
                crpv.setPercent(100);
            }
            View view = getCurrentFocus();
            InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
            YukselmeLeveli++;
            paravanaciliyor();
        } else {

        }
    }

    private void kiminleMesajlasiyorsun() {
        boolean okunabilir, yazilabilir;
        String durum = Environment.getExternalStorageState();
        if (durum.equals(Environment.MEDIA_MOUNTED)) {
            okunabilir = true;
            yazilabilir = true;
        } else if (durum.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            okunabilir = true;
            yazilabilir = false;
        } else {
            okunabilir = false;
            yazilabilir = false;
        }
        if (okunabilir && yazilabilir) {
            File root = Environment.getExternalStorageDirectory();
            File shappy = new File(root, "Shappy");
            File konusulanresimler = new File(shappy, "Pictures");
            if (!konusulanresimler.exists()) {
                konusulanresimler.mkdirs();
            }
            a = new File(konusulanresimler, karsiserverid + "pic.jpeg");
            Bitmap b = karsiresim;
            FileOutputStream fOS;
            try {
                fOS = new FileOutputStream(a);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fOS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            DatabaseClassKiminleKonustun dB = new DatabaseClassKiminleKonustun(Mesajlasma.this);
            dB.open();
            dB.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, resmiacikmi, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
            dB.close();
            DatabaseClassKimleriActirdin www = new DatabaseClassKimleriActirdin(this);
            www.open();
            List<String> acilanidler = www.databasedenidcek();
            for (String i : acilanidler) {
                if (i.equals(karsiserverid)) {
                    www.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
                    DatabaseClassKiminleKonustun eee = new DatabaseClassKiminleKonustun(Mesajlasma.this);
                    eee.open();
                    eee.kisiyisil(karsiserverid);
                    eee.close();
                }
            }
            www.close();

        }
    }

    private void mesajiexternalkaydet(String yazilanmesaj, String date) {
        DatabaseClassMesajlar dB = new DatabaseClassMesajlar(Mesajlasma.this);
        dB.open();
        dB.olustur(yazilanmesaj, karsiserverid, date, String.valueOf(YukselmeLeveli));
        dB.close();
    }

    private void paravanaciliyor() {
        if (resmiacikmi.equals("degil")) {
            final RelativeLayout Lay = (RelativeLayout) findViewById(R.id.layout);
            TextView textviewpatlayanisim = (TextView) findViewById(R.id.textView11);
            final ImageView patlayanprofil = (ImageView) findViewById(R.id.ivPatlayanprofil);
            ImageButton profilegit = (ImageButton) findViewById(R.id.bProfileGit);
            textviewpatlayanisim.setText(karsiisim);
            Thread ww = new Thread() {
                public void run() {
                    try {
                        sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Lay.setVisibility(View.VISIBLE);
                                Animation arkaplanikarart = AnimationUtils.loadAnimation(Mesajlasma.this, R.anim.patlama_layoutu);
                                final Animation patlamaprofilibuyut = AnimationUtils.loadAnimation(Mesajlasma.this, R.anim.patlamaprofili_scaling);
                                Lay.startAnimation(arkaplanikarart);
                                patlayanprofil.setImageBitmap(getScaledBitmapinDpi(karsiresim, 120, 120));
                                patlayanprofil.startAnimation(patlamaprofilibuyut);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            ww.start();
            Lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Animation arkaplanikarart = AnimationUtils.loadAnimation(Mesajlasma.this, R.anim.dark_background_kaldir);
                    Lay.startAnimation(arkaplanikarart);
                    Thread a = new Thread() {
                        public void run() {
                            try {
                                sleep(500);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Lay.setVisibility(View.GONE);
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    a.start();
                }
            });
            buttonbomba.setVisibility(View.GONE);
            profilegit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Mesajlasma.this, KarsiProfil.class);
                    intent.putExtra("isim", karsiisim);
                    intent.putExtra("durum", karsidurum);
                    intent.putExtra("cinsiyet", cinsiyet);
                    intent.putExtra("karsifaceprofilurl", karsifaceprofilurl);
                    intent.putExtra("coverfotourl", coverfotourl);
                    intent.putExtra("yas", yas);
                    intent.putExtra("burc", burc);
                    intent.putExtra("okul", okul);
                    startActivity(intent);
                }
            });
            Animation ismikarart = AnimationUtils.loadAnimation(this, R.anim.ismikarart);
            textviewisim.startAnimation(ismikarart);
            textviewisim.setText(karsiisim);
            Animation ismigoster = AnimationUtils.loadAnimation(this, R.anim.ismigoster);
            textviewisim.startAnimation(ismigoster);
            Bitmap ax = getCircleBitmap(karsiresim);
            final Bitmap sa = Bitmap.createScaledBitmap(ax, 130, 130, false);
            imagebuttonkarsiprofil.setImageBitmap(getCircleBitmap(sa));
        }
        resmiacikmi = "acik";
        kiminleMesajlasiyorsun();
        patlayanlarKonusunuAyarla();
    }

    private Bitmap getScaledBitmapinDpi(Bitmap b, int XinDpi, int YinDpi) {
        Bitmap a = getCircleBitmap(b);
        Resources r = getResources();
        int XinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, XinDpi, r.getDisplayMetrics()));
        int YinPixel = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, YinDpi, r.getDisplayMetrics()));

        Bitmap sa = Bitmap.createScaledBitmap(a, XinPixel, YinPixel, false);
        return sa;
    }

    private void patlayanlarKonusunuAyarla() {
        ServerFavla sF = new ServerFavla(karsiserverid);
        sF.execute(kendiserverid);
        DatabaseClassKiminleKonustun swsw = new DatabaseClassKiminleKonustun(Mesajlasma.this);
        swsw.open();
        swsw.kisiyisil(karsiserverid);
        swsw.close();
        DatabaseClassKimleriActirdin dbA = new DatabaseClassKimleriActirdin(this);
        dbA.open();
        dbA.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
        dbA.close();
    }

    private Bitmap getCircleBitmap(Bitmap b) {
        final Bitmap output = Bitmap.createBitmap(b.getWidth(), b.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);
        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, b.getWidth(), b.getHeight());
        final RectF rectf = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setColor(color);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectf, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(b, rect, rect, paint);
        return output;
    }

    private boolean takeChatMessage(String mesaj) {
        MediaPlayer mP = MediaPlayer.create(this, R.raw.alleyesonme);
        mP.start();
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        String date;
        if (minute < 10) {
            date = String.valueOf(hour) + ":0" + String.valueOf(minute);
        } else {
            date = String.valueOf(hour) + ":" + String.valueOf(minute);
        }
        mesajAdapter.add(new Mesaj(true, mesaj, date));
        kiminleMesajlasiyorsun();
        senyazdin = true;
        if (benyazdim) {
            kızailerle();
            benyazdim = false;
            senyazdin = false;
        }
        alinanMesajiExternalKaydet(mesaj, date);
        return true;
    }

    private void alinanMesajiExternalKaydet(String mesaj, String date) {
        DatabaseClassMesajlar dB = new DatabaseClassMesajlar(Mesajlasma.this);
        dB.open();
        dB.olusturx(mesaj, karsiserverid, date, String.valueOf(YukselmeLeveli));
        dB.close();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mesajlasmamenusu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.sohbettencik:
                if (banlanmadurumu.equals("evet")) {
                    Toast.makeText(this, "Banlanılan konuşmada etkileşimde bulunamazsın", Toast.LENGTH_SHORT).show();
                } else {
                    sohbettencikmadialog();
                }
                break;
            case R.id.kullanicibanla:
                if (banlanmadurumu.equals("evet")) {
                    Toast.makeText(this, "Banlanılan konuşmada etkileşimde bulunamazsın", Toast.LENGTH_SHORT).show();
                } else {
                    kullaniciyibanlamadialog();
                }
                break;
        }
        return false;
    }

    private void kullaniciyibanlamadialog() {
        final Dialog banlamadialog = new Dialog(this);
        banlamadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        banlamadialog.getWindow().setDimAmount(0.7f);
        banlamadialog.setContentView(R.layout.dialogbanlama);
        banlamadialog.show();
        ImageButton buton1, buton2;
        buton1 = (ImageButton) banlamadialog.findViewById(R.id.button18);
        buton2 = (ImageButton) banlamadialog.findViewById(R.id.button19);
        buton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                banlamaislemi();
                banlamadialog.dismiss();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                banlamadialog.dismiss();
            }
        });
    }

    private void banlamaislemi() {
        banlanmadurumu = "evet";
        DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(this);
        dCKK.open();
        dCKK.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, resmiacikmi, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, yas, okul, coverfotourl, "yok", "0");
        DatabaseClassKimleriActirdin dCKA = new DatabaseClassKimleriActirdin(this);
        dCKA.open();
        List<String> varolanlar = dCKA.databasedenidcek();
        for (String f : varolanlar) {
            if (f.equals(karsiserverid)) {
                dCKA.olustur(karsiserverid, karsiisim, a.getAbsolutePath(), karsidurum, banlanmadurumu, karsifaceprofilurl, cinsiyet, burc, "yok", "0");
                dCKK.kisiyisil(karsiserverid);
            }
        }
        dCKK.close();
        dCKA.close();
        Animation barisikanimation = AnimationUtils.loadAnimation(this, R.anim.barisik);
        //ilerleyenimagetutucu.startAnimation(barisikanimation);
        edittextyazmaalani.setHint("Banladığınız insana yazamazsınız");
        edittextyazmaalani.setEnabled(false);
        banlanmaperdesi.setVisibility(View.VISIBLE);
        ServerBanla sB = new ServerBanla(kendiserverid, karsiserverid);
        sB.execute();
    }

    private void sohbettencikmadialog() {
        final Dialog sohbettenayrilmadialog = new Dialog(this);
        sohbettenayrilmadialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        sohbettenayrilmadialog.getWindow().setDimAmount(0.7f);
        sohbettenayrilmadialog.setContentView(R.layout.dialogsohbettenayrilma);
        sohbettenayrilmadialog.show();
        ImageButton buton1, buton2;
        buton1 = (ImageButton) sohbettenayrilmadialog.findViewById(R.id.button20);
        buton2 = (ImageButton) sohbettenayrilmadialog.findViewById(R.id.button21);
        buton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sohbettencikmaislemi();
            }
        });
        buton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sohbettenayrilmadialog.dismiss();
            }
        });
    }

    private void sohbettencikmaislemi() {
        ServerSohbettenCikis sSC = new ServerSohbettenCikis(kendiserverid, karsiserverid);
        sSC.execute();
        DatabaseClassKiminleKonustun dcfv = new DatabaseClassKiminleKonustun(this);
        dcfv.open();
        dcfv.kisiyisil(karsiserverid);
        dcfv.close();
        DatabaseClassMesajlar dGM = new DatabaseClassMesajlar(this);
        dGM.open();
        dGM.ozelmesajlarisil(karsiserverid);
        dGM.close();
        DatabaseClassKimleriActirdin ooo = new DatabaseClassKimleriActirdin(this);
        ooo.open();
        ooo.kisiyisil(karsiserverid);
        ooo.close();
        finish();
    }

    public class urldenResimm extends AsyncTask<String, Void, Bitmap> {


        public urldenResimm() {
        }

        protected Bitmap doInBackground(String... params) {
            URL url;
            Bitmap icon = null;
            try {
                url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                icon = BitmapFactory.decodeStream(input);
                return icon;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap bitmap) {

        }
    }

    public class ServerBirebirMesajGonder extends AsyncTask<String, Void, String> {
        String yazanid;
        String yazanmesaj;
        String karsiid;
        String charset;
        String query;

        public ServerBirebirMesajGonder(String yazanid, String yazanmesaj, String karsiid) {
            this.yazanid = yazanid;
            this.yazanmesaj = yazanmesaj;
            this.karsiid = karsiid;
            charset = "UTF-8";
            String param1 = "id";
            String param2 = "mesage";
            String param3 = "karsiid";
            try {
                query = String.format("param1=%s&param2=%s&param3=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset),
                        URLEncoder.encode(param3, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                Log.i("tago", "mesaj" + URLEncoder.encode(yazanmesaj, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                connection = (HttpURLConnection) new URL("http://185.22.187.60/shappy/sendmsg.php?" +
                        "id=" + yazanid + "&id2=" + karsiid + "&msg=" + URLEncoder.encode(yazanmesaj, charset)).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "* /*");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                output.close();
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputline = null;
                    for (int i = 0; i < 3; i++) {
                        inputline = in.readLine();
                        Log.i("tago", "" + i + "mesaj for inputline= " + inputline);
                    }
                    if (inputline.equals("gitmedi")) {
                        Mesajlasma.this.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(Mesajlasma.this, "5 km den uzakta artık mesajlarını göremez", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "alabama";
        }
    }

    public class ServerBanla extends AsyncTask<String, Void, String> {

        String yazaninid, karsiid;
        String charset, query;

        public ServerBanla(String yazaninid, String karsiid) {
            this.yazaninid = yazaninid;
            this.karsiid = karsiid;
            charset = "UTF-8";
            String param1, param2;
            param1 = "_id";
            param2 = "karsiid";
            try {
                query = String.format(("param1=%s&param2=%s"), URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/ban.php?id=" + yazaninid +
                        "&id2=" + karsiid).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "salabama";
        }
    }

    public class ServerSohbettenCikis extends AsyncTask<String, Void, String> {
        String yazaninid, karsiid;
        String charset, query;

        public ServerSohbettenCikis(String yazaninid, String karsiid) {
            this.yazaninid = yazaninid;
            this.karsiid = karsiid;
            charset = "UTF-8";
            String param1 = "_id";
            String param2 = "karsiid";
            try {
                query = String.format(("param1=%s&param2=%s"), URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/leave.php?id=" + yazaninid +
                        "&id2=" + karsiid).openConnection();
                connection.setDoOutput(true);
                connection.setRequestProperty("Accept-Charset", charset);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "valabama";
        }
    }

    public class ServerFavla extends AsyncTask<String, Void, String> {
        String karsidakiid, charset, query;

        public ServerFavla(String karsidakiid) {
            this.karsidakiid = karsidakiid;
            String param1 = "id";
            String param2 = "karsiid";
            charset = "UTF-8";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/favs.php?gonderen=" + params[0] + "&alici=" + karsidakiid).openConnection();
                Log.i("tago", "Mesajlasma Favlama islemi baslatildi");
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream is = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "calabama";
        }
    }

}
