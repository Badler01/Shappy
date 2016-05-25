package droxoft.armin.com.shappy;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class GrupSohbeti extends Activity {

    String kanaladi, kanalid, kanalmodu,kanalurl;
    int kanallikedurumu = 0;
    boolean taraf;
    ListView listviewMesaj;
    EditText edittextmesajalani;
    GrupMesajArrayAdapter mesajAdapter;
    ImageButton kanalilikebutonu;
    String yazaninmesaj;
    boolean grupnotificationbas;
    File a;
    ArrayList<GrupMesaj> mesajListesi = new ArrayList<>();
    BroadcastReceiver receiveralfa = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            String message = b.getString("mesaj");
            String yazaninnick = b.getString("karsinick");
            takeChatMessage(message, yazaninnick);
        }
    };

    private void sharedprefkanalsonidkaydet(String kanalid) {
        SharedPreferences sP = getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("kanalnotificationsonid", kanalid);
        Log.i("tago", "sonidkaydet " + kanalid);
        editor.apply();
    }

    private void sharedprefkanalnotificationkaydet() {
        SharedPreferences sP = getSharedPreferences("notification", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("kanalnotificationver", grupnotificationbas);
        editor.apply();
    }

    private boolean takeChatMessage(String message, String yazaninnick) {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);
        String date;
        if (minute < 10) {
            date = String.valueOf(hour) + ":0" + String.valueOf(minute);
        } else {
            date = String.valueOf(hour) + ":" + String.valueOf(minute);
        }
        mesajAdapter.add(new GrupMesaj(!taraf, message, date, yazaninnick, yazaninnick.length()));
        DatabaseClassGrupChat databaseClass = new DatabaseClassGrupChat(GrupSohbeti.this);
        databaseClass.open();
        databaseClass.olusturx(message, kanaladi, yazaninnick, date);
        databaseClass.close();
        return true;
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.grupsohbeti);
        Intent i = getIntent();
        if (i.getStringExtra("intentname").equals("kanaladapter")) {
            kanaladi = i.getExtras().getString("kanaladi");
            kanalid = i.getExtras().getString("kanalid");
            kanalmodu = i.getExtras().getString("kanalmodu");
            kanalurl = i.getExtras().getString("kanalurl");
            DatabaseClassKonusulanKanallar bbb = new DatabaseClassKonusulanKanallar(this);
            bbb.open();
            String likee = bbb.databasedenozellikecek(kanaladi);
            bbb.close();
            if(likee.equals("no")){
                kanallikedurumu=0;
            }else{
                kanallikedurumu=1;
            }
        } else if (i.getStringExtra("intentname").equals("gecmiskanaladapter")) {
            kanaladi = i.getExtras().getString("kanaladi");
            kanalid = i.getExtras().getString("kanalid");
            kanalmodu = i.getExtras().getString("kanalmodu");
            kanalurl = i.getExtras().getString("kanalurl");
            DatabaseClassKonusulanKanallar dwww = new DatabaseClassKonusulanKanallar(this);
            dwww.open();
            String likee = dwww.databasedenozellikecek(kanaladi);
            dwww.close();
            if(likee.equals("yes")){
                kanallikedurumu =1;
                Log.i("tago" , "likedurumuyes");
            }else{
                kanallikedurumu=0;
                Log.i("tago" , "likedurumuno");
            }
            DatabaseClassNotificationGrup dcng = new DatabaseClassNotificationGrup(this);
            dcng.open();
            dcng.deleteAll();
            dcng.close();
            DatabaseClassKonusulanKanallar dckq = new DatabaseClassKonusulanKanallar(this);
            dckq.open();
            String likedurumu = dckq.databasedenozellikecek(kanaladi);
            dckq.olustur(kanaladi, kanalmodu, likedurumu,kanalurl, "yok", "0");
            dckq.close();
        } else if (i.getStringExtra("intentname").equals("PushReceiver")) {
            kanaladi = i.getExtras().getString("kanaladi");
            kanalid = i.getExtras().getString("kanalid");
            kanalmodu = i.getExtras().getString("kanalmodu");
            kanalurl = i.getExtras().getString("kanalurl");
            DatabaseClassKonusulanKanallar dfff = new DatabaseClassKonusulanKanallar(this);
            dfff.open();
            String likee = dfff.databasedenozellikecek(kanaladi);
            dfff.close();
            if(likee.equals("yes")){
                kanallikedurumu=1;
            }else{
                kanallikedurumu=0;
            }
            DatabaseClassNotificationGrup dcng = new DatabaseClassNotificationGrup(this);
            dcng.open();
            dcng.deleteAll();
            dcng.close();
            DatabaseClassKonusulanKanallar dckq = new DatabaseClassKonusulanKanallar(this);
            dckq.open();
            String likedurumu = dckq.databasedenozellikecek(kanaladi);
            dckq.olustur(kanaladi, kanalmodu, likedurumu,kanalurl, "yok", "0");
            dckq.close();
        }
        tanimlar();
        registerReceiver(receiveralfa, new IntentFilter("broadcastGrup"));
        List<String> kayitlimesajlar = null;
        List<String> kayitlizamanlar = null;
        List<String> kayitlinickler = null;
        DatabaseClassGrupChat dCGC = new DatabaseClassGrupChat(this);
        dCGC.open();
        if (dCGC.databasedencek(kanaladi) != null) {
            kayitlimesajlar = dCGC.databasedencek(kanaladi);
            kayitlizamanlar = dCGC.databasedenzamancek(kanaladi);
            kayitlinickler = dCGC.databasedennickcek(kanaladi);
        }
        dCGC.close();
        for (int k = 0; k < kayitlimesajlar.size(); k++) {
            if (kayitlimesajlar.get(k).substring(0, 10).equals("badbadbado")) {
                mesajAdapter.add(new GrupMesaj(!taraf, kayitlimesajlar.get(k).substring(34), kayitlizamanlar.get(k), kayitlinickler.get(k), 3));
            } else {
                mesajAdapter.add(new GrupMesaj(taraf, kayitlimesajlar.get(k).substring(0,
                        kayitlimesajlar.get(k).indexOf("rumbararumbarumbarumruru")), kayitlizamanlar.get(k), kayitlinickler.get(k), 5));
            }
        }
    }

    protected void onNewIntent(Intent intent){
        super.onNewIntent(intent);
        if (receiveralfa != null) {
            unregisterReceiver(receiveralfa);
            registerReceiver(receiveralfa, new IntentFilter("broadcastmessage"));
        }
        setIntent(intent);
        Intent a = getIntent();
        Log.i("tago", "onNewIntent");
        if (a.getStringExtra("intentname").equals("PushReceiver")) {
            DatabaseClassNotificationGrup dCN = new DatabaseClassNotificationGrup(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            kanaladi = a.getStringExtra("kanaladi");
            kanalid = a.getStringExtra("kanalid");
            kanalmodu = a.getStringExtra("kanalmodu");
            kanalurl = a.getStringExtra("kanalurl");
            DatabaseClassKonusulanKanallar bbb = new DatabaseClassKonusulanKanallar(this);
            bbb.open();
            String likee = bbb.databasedenozellikecek(kanaladi);
            bbb.close();
            if(likee.equals("no")){
                kanallikedurumu=0;
            }else{
                kanallikedurumu=1;
            }
            DatabaseClassKonusulanKanallar dckk = new DatabaseClassKonusulanKanallar(this);
            dckk.open();
            String likedurumu = dckk.databasedenozellikecek(kanaladi);
            dckk.olustur(kanaladi,kanalmodu,likedurumu,kanalurl,"yok","0");
            dckk.close();
        }
    }

    protected void onResume() {
        super.onResume();
        grupnotificationbas = false;
        sharedprefkanalnotificationkaydet();
        sharedprefkanalsonidkaydet(kanalid);
    }

    protected void onPause() {
        super.onPause();
        grupnotificationbas = true;
        sharedprefkanalnotificationkaydet();
        sharedprefkanalsonidkaydet(kanalid);
    }

    private void tanimlar() {
        TextView textviewkanaladi = (TextView) findViewById(R.id.textViewisim);
        textviewkanaladi.setText(kanaladi);
        listviewMesaj = (ListView) findViewById(R.id.listViewmesaj);
        ImageButton buttonMesajigonder = (ImageButton) findViewById(R.id.buttongonder);
        ImageButton buttonGeridon = (ImageButton) findViewById(R.id.buttongeri);
        buttonMesajigonder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendChatMessage();
            }
        });
        buttonGeridon.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        edittextmesajalani = (EditText) findViewById(R.id.editTextyazi);
        edittextmesajalani.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });
        mesajAdapter = new GrupMesajArrayAdapter(this, R.layout.grupmesaj, mesajListesi);
        listviewMesaj.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listviewMesaj.setAdapter(mesajAdapter);
        mesajAdapter.registerDataSetObserver(new DataSetObserver() {
            public void onChanged() {
                super.onChanged();
                listviewMesaj.setSelection(mesajAdapter.getCount() - 1);
            }
        });
        ImageButton kanaldancikisbutonu = (ImageButton) findViewById(R.id.kanaldancikbutton);
        ImageButton kanalireportbutonu = (ImageButton) findViewById(R.id.imaj2);
        kanalilikebutonu = (ImageButton) findViewById(R.id.imaj1);
        if (kanallikedurumu==0) {
            kanalilikebutonu.setImageResource(R.mipmap.begenganal);
        } else if (kanallikedurumu==1) {
            kanalilikebutonu.setImageResource(R.mipmap.begenganal_k);
        }
        kanalilikebutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (kanallikedurumu==0) {
                    kanalilikelamaislemi();
                    kanallikedurumu = 1;
                } else if (kanallikedurumu==1) {
                    Toast.makeText(GrupSohbeti.this, "Zaten Like Attın", Toast.LENGTH_SHORT).show();
                }
            }
        });
        kanalireportbutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kanalireportetmeislemi();
            }
        });
        kanaldancikisbutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kanaldancikmaislemi();
            }
        });

    }

    private void kanaldancikmaislemi() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogkanaldancik);
        dialog.getWindow().setDimAmount(0.7f);
        dialog.show();
        ImageButton pozitif = (ImageButton) dialog.findViewById(R.id.button28);
        ImageButton negatif = (ImageButton) dialog.findViewById(R.id.button27);
        negatif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        pozitif.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ServerKanaldanCikis sKC = new ServerKanaldanCikis(kanaladi);
                sKC.execute(SharedPrefIdAl());
                finish();
            }
        });
    }

    private String SharedPrefIdAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("serverid", "defaultserverid");
    }

    private void kanalilikelamaislemi() {
        kanalilikebutonu.setImageResource(R.mipmap.begenganal_k);
        if (kanalmodu.equals("o")) {
            OfficialKanaliLike oKL = new OfficialKanaliLike();
            oKL.execute(kanalid);
        } else if (kanalmodu.equals("n")) {
            NormalKanaliLike nKL = new NormalKanaliLike();
            nKL.execute(kanalid);
        }
        DatabaseClassKonusulanKanallar dCKK = new DatabaseClassKonusulanKanallar(this);
        dCKK.open();
        dCKK.olustur(kanaladi, kanalmodu, "yes",kanalurl, "yok", "0");
        dCKK.close();
    }

    private void kanalireportetmeislemi() {
        final String knaladi = kanaladi;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialognormalkanalireportet);
        dialog.getWindow().setDimAmount(0.7f);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        ImageButton buton1 = (ImageButton) dialog.findViewById(R.id.button25);
        //final EditText etv1 = (EditText) dialog.findViewById(R.id.editText11);
        buton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //String sikayetnedeni = etv1.getText().toString();
                String sikayetnedeni = "kazanan dort esnaftan biri oldu";
                ServerKanalReport sKR = new ServerKanalReport(sikayetnedeni, knaladi);
                String veritabani_id = SharedPrefIdAl();
                sKR.execute(veritabani_id);
                dialog.dismiss();
            }
        });
    }

    private boolean sendChatMessage() {
        if (edittextmesajalani.getText().toString() != "") {
            yazaninmesaj = edittextmesajalani.getText().toString();
            String yazaninid = SharedPrefIdAl();
            String yazaninnicki = SharedPrefNickAl();
            ServerGrupSohbetiMesaj sGSM = new ServerGrupSohbetiMesaj(yazaninid, yazaninnicki, kanaladi, yazaninmesaj);
            sGSM.execute("sikim");
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            String date = String.valueOf(hour) + ":" + String.valueOf(minute);
            mesajAdapter.add(new GrupMesaj(taraf, yazaninmesaj, date, yazaninnicki, yazaninnicki.length()));
            edittextmesajalani.setText("");
            konusulankanalikaydet();
            mesajiexternalkaydet(yazaninmesaj, yazaninnicki, date);
            return true;
        } else {
            return false;
        }
    }

    private void mesajiexternalkaydet(String yazaninmesaj, String yazaninnicki, String date) {
        DatabaseClassGrupChat dCGC = new DatabaseClassGrupChat(this);
        dCGC.open();
        dCGC.olustur(yazaninmesaj, kanaladi, yazaninnicki, date);
        dCGC.close();
    }

    private void konusulankanalikaydet() {
        DatabaseClassKonusulanKanallar dCKK = new DatabaseClassKonusulanKanallar(this);
        dCKK.open();
        String likedurumu = dCKK.databasedenozellikecek(kanaladi);
        dCKK.olustur(kanaladi, kanalmodu,likedurumu,kanalurl, "yok", "0");
        dCKK.close();
    }

    private String SharedPrefNickAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("nick", "defaultnick");
    }

    private class ServerKanaldanCikis extends AsyncTask<String, Void, String> {
        String kanaladi;
        String query, charset;

        public ServerKanaldanCikis(String kanaladi) {
            this.kanaladi = kanaladi;
            charset = "UTF-8";
            String param1, param2;
            param1 = "_id";
            param2 = "kanal";
            try {
                query = String.format(("param1=%s&param2=%s"), URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.184.15/shappy/leave_channel.php?id=" + params[0] +
                        "&name=" + kanaladi).openConnection();
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
            return "qalabama";
        }
    }

    private class OfficialKanaliLike extends AsyncTask<String, Void, String> {

        String charset, query;

        protected String doInBackground(String... strings) {
            charset = "utf-8";
            String param1 = "kanalid";
            String param2 = "ekleyenid";
            String param3 = "type";
            try {
                query = String.format("param1=%s&param2=%s&param3=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset)
                        , URLEncoder.encode(param3, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("tago", "KanalAdapter official kanali like etme başlatıldı");
            try {
                return officiallike(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private String officiallike(String string) {
            HttpURLConnection vconnection = null;
            String kullaniciid = SharedPrefIdAl();
            Log.i("tago", "kanalid= " + string);
            Log.i("tago", "kullaniciid= " + kullaniciid);
            try {
                vconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/like_channel.php?id=" + string + "&userid="
                        + kullaniciid + "&type=2").openConnection();
                Log.i("tago", "KanalAdapter official like etme bağı kuruldu");
            } catch (IOException e) {
                e.printStackTrace();
            }
            vconnection.setDoOutput(true);
            vconnection.setRequestProperty("Accept-Charset", charset);
            vconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try {
                OutputStream output = new BufferedOutputStream(vconnection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream response = vconnection.getInputStream();
                Log.i("tago", "olması lazımm");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "alabama";
        }
    }

    private class NormalKanaliLike extends AsyncTask<String, Void, String> {
        String charset, query;

        protected String doInBackground(String... strings) {
            charset = "utf-8";
            String param1 = "kanalid";
            String param2 = "likelayanid";
            String param3 = "type";
            try {
                query = String.format("param1=%s&param2=%s&param3=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset)
                        , URLEncoder.encode(param3, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("tago", "GrupSohbeti normal kanali like etme başlatıldı");
            try {
                return officiallike(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private String officiallike(String string) {
            HttpURLConnection vconnection = null;
            String kullaniciid = SharedPrefIdAl();
            Log.i("tago", "kanalid= " + string);
            Log.i("tago", "kullaniciid= " + kullaniciid);
            try {
                vconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/like_channel.php?id=" + string + "&userid="
                        + kullaniciid + "&type=1").openConnection();
                Log.i("tago", "GrupSohbeti normal like etme bağı kuruldu");
            } catch (IOException e) {
                e.printStackTrace();
            }
            vconnection.setDoOutput(true);
            vconnection.setRequestProperty("Accept-Charset", charset);
            vconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try {
                OutputStream output = new BufferedOutputStream(vconnection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream response = vconnection.getInputStream();
                Log.i("tago", "olması lazımm");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "alabama";
        }
    }

    private class ServerKanalReport extends AsyncTask<String, Void, String> {

        String sikayetnedeni, kanaladi;
        String query, charset;

        public ServerKanalReport(String sikayetnedeni, String kanaladi) {
            this.sikayetnedeni = sikayetnedeni;
            this.kanaladi = kanaladi;
            charset = "UTF-8";
            String param1, param2, param3;
            param1 = "_id";
            param2 = "kanal";
            param3 = "sikayetnedeni";
            try {
                query = String.format(("param1=%s&param2=%s&param3=%s"), URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset),
                        URLEncoder.encode(param3, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.184.15/shappy/report_channel.php?id=" +
                        params[0] + "&kanalid=" + kanalid + "&reason=" + sikayetnedeni).openConnection();
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

    private class ServerGrupSohbetiMesaj extends AsyncTask<String, Void, String> {
        String yazaninid;
        String yazaninmesaj;
        String yazaninnicki;
        String kanaladi;
        String charset;
        String query;

        public ServerGrupSohbetiMesaj(String yazaninid, String yazaninnicki, String kanaladi, String yazaninmesaj) {
            this.yazaninid = yazaninid;
            this.yazaninmesaj = yazaninmesaj;
            this.yazaninnicki = yazaninnicki;
            this.kanaladi = kanaladi;
            charset = "UTF-8";
            String param1 = "id";
            String param2 = "nick";
            String param3 = "kanal";
            String param4 = "mesaj";
            try {
                query = String.format("param1=%s&param2=%s&param3=%s&param4=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset),
                        URLEncoder.encode(param3, charset), URLEncoder.encode(param4, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {

            Log.i("tago", "Grup mesajı veritabanına gonderme işlemi başlatıldı");
            try {
                return mesajıgonder();
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadı";
            }
        }

        private String mesajıgonder() {
            Log.i("tago" , "kanalidg" + kanalid);
            URLConnection connection = null;
            Log.i("tago", "nick" + yazaninnicki);
            try {
                connection = new URL("http://185.22.184.15/shappy/group_chat.php?" +
                        "id=" + yazaninid + "&plcid=" + kanalid +
                        "&name=" + yazaninnicki + "&placename=" + kanaladi +
                        "&msg=" + URLEncoder.encode(yazaninmesaj, charset) + "&type=" + kanalmodu).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream response = connection.getInputStream();
                Log.i("tago", "GrupSohbeti mesajı gruba gonderme yazdım");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("tago", "GrupSohbeti Arkadan vurdurma yazamadım");
            }
            return "alabama";
        }
    }
}
