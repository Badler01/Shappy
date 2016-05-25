package droxoft.armin.com.shappy;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public class PushReceiver extends BroadcastReceiver {

    private static final int mesajuniqueID = 31313;
    private static final int grupuniqueID = 31323;
    private static final int banuniqueID = 31333;
    private static final int leaveuniqueID = 31343;

    boolean notificationver = true;
    boolean bildirimleracikmi = true;
    String notificationsonid = null;
    boolean kanalnotificationver = true;
    String kanalnotificationsonid = null;

    private void SharedPrefNotificationAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        notificationver = sP.getBoolean("notificationbas", true);
    }

    private void SharedPrefNotificationsonidAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        notificationsonid = sP.getString("notificationsonid", "defaultnotificationsonid");
    }

    private void SharedPrefNotificationKanalAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        kanalnotificationver = sP.getBoolean("kanalnotificationver", true);
    }

    private void SharedPrefNotificationKanalSonIdAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("notification", Context.MODE_PRIVATE);
        kanalnotificationsonid = sP.getString("kanalnotificationsonid", "defaultkanalnotificationsonid");
        Log.i("tago", "noti sonid" + kanalnotificationsonid);
    }

    private void SharedPrefBildirimlerAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        bildirimleracikmi = sP.getBoolean("bildirimler", true);
    }

    private String SharedPrefBosResimPathAl(Context context) {
        SharedPreferences sP = context.getSharedPreferences("programisleyis", Context.MODE_PRIVATE);
        return sP.getString("bilinmeyenresimpath", "defaultbilinmeyenresimpath");
    }

    public void onReceive(Context context, Intent intent) {
        SharedPrefNotificationAl(context);
        SharedPrefNotificationsonidAl(context);
        SharedPrefBildirimlerAl(context);
        SharedPrefNotificationKanalAl(context);
        SharedPrefNotificationKanalSonIdAl(context);
        String notificationTitle = "Shappy";
        String notificationDesc;

        if (intent.getStringExtra("mode").equals("leave")) {
            Bundle bundle = intent.getExtras();
            String konusmadancikanid = bundle.getString("id");
            String konusmadancikanisim = bundle.getString("name");
            if (!notificationver && notificationsonid.equals(konusmadancikanid)) {
                Intent i = new Intent("broadcastLeave");
                context.sendBroadcast(i);
            } else {
                DatabaseClassKiminleKonustun dcfv = new DatabaseClassKiminleKonustun(context);
                dcfv.open();
                dcfv.kisiyisil(konusmadancikanid);
                dcfv.close();
                DatabaseClassMesajlar dGM = new DatabaseClassMesajlar(context);
                dGM.open();
                dGM.ozelmesajlarisil(konusmadancikanid);
                dGM.close();
                DatabaseClassKiminleKonustun gggg = new DatabaseClassKiminleKonustun(context);
                gggg.open();
                String admackmi = gggg.databasedenresmiacikmicek(konusmadancikanid);
                String nottiname;
                if(admackmi.equals("acik")){
                    nottiname = konusmadancikanisim;
                }else{
                    nottiname = "default";
                }


                if(bildirimleracikmi) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setAutoCancel(true);
                    notification.setContentText(nottiname + " konuşmadan ayrıldı");
                    notification.setContentTitle(notificationTitle);
                    Bitmap ww = BitmapFactory.decodeResource(context.getResources(),R.mipmap.left);
                    notification.setLargeIcon(ww);
                    notification.setSmallIcon(R.mipmap.galin_shappy);
                    notification.setWhen(System.currentTimeMillis());
                    notification.setVibrate(new long[]{1000, 1000, 1000});
                    notification.setLights(Color.YELLOW, 2000, 2000);
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(alarmSound);
                    Intent i = new Intent(context, AnaAkim.class);
                    i.putExtra("intentname", "PushReceiverLeave");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(leaveuniqueID, notification.build());
                }
            }
        } else if (intent.getStringExtra("mode").equals("ban")) {
            Bundle bundle = intent.getExtras();
            String banlayanisim = bundle.getString("name");
            String banlayanid = bundle.getString("id");
            String banlayandurum = bundle.getString("status");
            String banlayanfaceprofilurl = bundle.getString("url");
            String cinsiyet = bundle.getString("gender");
            String burc = bundle.getString("burc");
            String borndate = bundle.getString("borndate");
            String okul = bundle.getString("school");
            String coverfotourl = bundle.getString("cover_photo");
            Log.i("tago" , "pusho" + borndate);
            Log.i("tago" , "pusho" + okul);
            Log.i("tago" , "pusho" + coverfotourl);
            String year = borndate.substring(0, 4);
            String month = borndate.substring(5, 7);
            String day = borndate.substring(8, 10);
            Integer yaso = getAge(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            String yas = String.valueOf(yaso);
            if (!notificationver && notificationsonid.equals(banlayanid)) {
                Intent i = new Intent("broadcastBan");
                context.sendBroadcast(i);
            } else {
                String adamacikmi;
                DatabaseClassKiminleKonustun swsw = new DatabaseClassKiminleKonustun(context);
                swsw.open();
                adamacikmi = swsw.databasedenresmiacikmicek(banlayanid);
                swsw.close();
                String notaname;
                if (adamacikmi.equals("degil")) {
                    notaname = "default";
                    DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(context);
                    dCKK.open();
                    dCKK.olustur(banlayanid, banlayanisim, SharedPrefBosResimPathAl(context), banlayandurum, "degil", "evet", banlayanfaceprofilurl,cinsiyet,burc,yas,okul,coverfotourl,"yok", "0");
                    DatabaseClassKimleriActirdin fff = new DatabaseClassKimleriActirdin(context);
                    fff.open();
                    List<String> shaps = fff.databasedenidcek();
                    for(String g :shaps){
                        if(g.equals(banlayanid)){
                            dCKK.kisiyisil(banlayanid);
                        }
                    }
                    dCKK.close();
                    fff.close();
                } else {
                    notaname = banlayanisim;
                    File root = Environment.getExternalStorageDirectory();
                    File shappy = new File(root, "Shappy");
                    File konusulanresimler = new File(shappy, "Pictures");
                    if (!konusulanresimler.exists()) {
                        konusulanresimler.mkdirs();
                    }
                    File a = new File(konusulanresimler, banlayanid + "pic.jpeg");
                    DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(context);
                    dCKK.open();
                    dCKK.olustur(banlayanid, banlayanisim, a.getAbsolutePath(), banlayandurum, "acik", "evet", banlayanfaceprofilurl,cinsiyet,burc,yas,okul,coverfotourl, "yok", "0");
                    DatabaseClassKimleriActirdin kkk = new DatabaseClassKimleriActirdin(context);
                    kkk.open();
                    List<String> shpl = kkk.databasedenidcek();
                    for(String h : shpl){
                        if(h.equals(banlayanid)){
                            dCKK.kisiyisil(banlayanid);
                        }
                    }
                    dCKK.close();
                    kkk.close();
                }
                String adamshapmi = "yok";
                DatabaseClassKimleriActirdin sss = new DatabaseClassKimleriActirdin(context);
                sss.open();
                List<String> shapde = sss.databasedenidcek();
                for(String p : shapde){
                    if(p.equals(banlayanid)){
                        adamshapmi = "var";
                    }
                }
                sss.close();
                if(adamshapmi.equals("var")){
                    File root = Environment.getExternalStorageDirectory();
                    File shappy = new File(root, "Shappy");
                    File konusulanresimler = new File(shappy, "Pictures");
                    if (!konusulanresimler.exists()) {
                        konusulanresimler.mkdirs();
                    }
                    File a = new File(konusulanresimler, banlayanid + "pic.jpeg");
                    DatabaseClassKimleriActirdin dCKA = new DatabaseClassKimleriActirdin(context);
                    dCKA.open();
                    dCKA.olustur(banlayanid, banlayanisim, a.getAbsolutePath(), banlayandurum, "evet", banlayanfaceprofilurl,cinsiyet,burc,"yok" , "0");
                    dCKA.close();
                }
                if(bildirimleracikmi) {
                    NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                    notification.setAutoCancel(true);
                    notification.setContentText(notaname + " seni banladı");
                    notification.setContentTitle(notificationTitle);
                    Bitmap ee = BitmapFactory.decodeResource(context.getResources(),R.mipmap.circle);
                    notification.setLargeIcon(ee);
                    notification.setSmallIcon(R.mipmap.fire_tr);
                    notification.setWhen(System.currentTimeMillis());
                    notification.setVibrate(new long[]{1000, 1000, 1000});
                    notification.setLights(Color.YELLOW, 2000, 2000);
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    notification.setSound(alarmSound);
                    Intent i = new Intent(context, AnaAkim.class);
                    i.putExtra("intentname", "PushReceiverBan");
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(pendingIntent);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(banuniqueID, notification.build());
                }
            }
        } else if (intent.getStringExtra("mode").equals("chat")) {
            Bundle bundle = intent.getExtras();
            String mesaj = bundle.getString("message");
            String karsiid = bundle.getString("id");
            String karsiname = bundle.getString("name");
            String karsidurum = bundle.getString("status");
            String karsifaceprofilurl = bundle.getString("url");
            String cinsiyet = bundle.getString("gender");
            String burc = bundle.getString("burc");
            String borndate = bundle.getString("borndate");
            String okul = bundle.getString("school");
            String coverfotourl = bundle.getString("cover_photo");
            Log.i("tago" , "pushor" + borndate);
            Log.i("tago" , "pushor" + okul);
            Log.i("tago" , "pushor" + coverfotourl);
            String year = borndate.substring(0, 4);
            String month = borndate.substring(5, 7);
            String day = borndate.substring(8, 10);
            Integer yaso = getAge(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day));
            String yas = String.valueOf(yaso);
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            String date;
            if (minute < 10) {
                date = String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                date = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
            if (!notificationver && notificationsonid.equals(karsiid)) {
                Intent i = new Intent("broadcastmessage");
                i.putExtra("mesaj", mesaj);
                context.sendBroadcast(i);

            } else {
                String notiname= "default";
                ///////////////////////////////
                String adamvarmi = "yok";
                String adamshapmi = "hayir";
                String adamacikmi;
                DatabaseClassKimleriActirdin hhh = new DatabaseClassKimleriActirdin(context);
                hhh.open();
                List<String> shappyler = hhh.databasedenidcek();
                for(String s : shappyler){
                    if(s.equals(karsiid)){
                        adamshapmi = "evet";
                    }
                }
                DatabaseClassKiminleKonustun dcKK = new DatabaseClassKiminleKonustun(context);
                dcKK.open();
                List<String> kayitliidler = dcKK.databasedenidcek();
                dcKK.close();
                for (String id : kayitliidler) {
                    if (karsiid.equals(id)) {
                        adamvarmi = "var";
                    }
                }
                DatabaseClassKiminleKonustun xxxx = new DatabaseClassKiminleKonustun(context);
                xxxx.open();
                adamacikmi = xxxx.databasedenresmiacikmicek(karsiid);
                xxxx.close();
                ///////////////////
                File a=null;
                if (adamvarmi.equals("yok")&&adamshapmi.equals("hayir")) {
                    notiname = "Shapps";
                    String bosresimpath = SharedPrefBosResimPathAl(context);
                    DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(context);
                    dCKK.open();
                    dCKK.olustur(karsiid, karsiname, bosresimpath, karsidurum, "degil", "hayir", karsifaceprofilurl, cinsiyet,burc,yas,okul,coverfotourl,"var", "1");
                    dCKK.close();
                }else if (adamvarmi.equals("yok") &&adamshapmi.equals("evet")) {
                    notiname = karsiname;
                    File root = Environment.getExternalStorageDirectory();
                    File shappy = new File(root, "Shappy");
                    File konusulanresimler = new File(shappy, "Pictures");
                    if (!konusulanresimler.exists()) {
                        konusulanresimler.mkdirs();
                    }
                    a = new File(konusulanresimler, karsiid + "pic.jpeg");
                    DatabaseClassKimleriActirdin lll = new DatabaseClassKimleriActirdin(context);
                    lll.open();
                    String yenimesajsayisi = lll.databasedenyenimesajsayisicek(karsiid);
                    int q = Integer.valueOf(yenimesajsayisi) + 1;
                    String yepismesajsayisi = String.valueOf(q);
                    String bandurumu = lll.databasedenbanlanmadurumucek(karsiid);
                    lll.olustur(karsiid,karsiname,a.getAbsolutePath(),karsidurum,bandurumu,karsifaceprofilurl,cinsiyet,burc,"var",yepismesajsayisi);
                    lll.close();
                    adamacikmi = "acik";
                } else if (adamvarmi.equals("var") && adamacikmi.equals("degil")) {
                    notiname = "Shapps";
                    String bosresimpath = SharedPrefBosResimPathAl(context);
                    DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(context);
                    dCKK.open();
                    String yenimesajsayisi = dCKK.databasedenyenimesajsayisicek(karsiid);
                    int q = Integer.valueOf(yenimesajsayisi) + 1;
                    String yepismesajsayisi = String.valueOf(q);
                    dCKK.olustur(karsiid, karsiname, bosresimpath, karsidurum, "degil", "hayir", karsifaceprofilurl,cinsiyet,burc,yas,okul,coverfotourl,"var", yepismesajsayisi);
                    dCKK.close();
                } else if (adamvarmi.equals("var") && adamacikmi.equals("acik")) {
                    notiname = karsiname;
                    File root = Environment.getExternalStorageDirectory();
                    File shappy = new File(root, "Shappy");
                    File konusulanresimler = new File(shappy, "Pictures");
                    if (!konusulanresimler.exists()) {
                        konusulanresimler.mkdirs();
                    }
                    a = new File(konusulanresimler, karsiid + "pic.jpeg");
                    DatabaseClassKiminleKonustun dckkk = new DatabaseClassKiminleKonustun(context);
                    dckkk.open();
                    String yenimesajsayisi = dckkk.databasedenyenimesajsayisicek(karsiid);
                    int x = Integer.valueOf(yenimesajsayisi) + 1;
                    String yepismesajsayisi = String.valueOf(x);
                    dckkk.olustur(karsiid, karsiname, a.getAbsolutePath(), karsidurum, "acik", "hayir", karsifaceprofilurl,cinsiyet,burc,yas,okul,coverfotourl, "var", yepismesajsayisi);
                    dckkk.close();
                }
                ////////////////////////////
                DatabaseClassMesajlar dCM = new DatabaseClassMesajlar(context);
                dCM.open();
                dCM.olusturx(mesaj, karsiid, date, String.valueOf(0));
                dCM.close();
                //////////////////////////////
                DatabaseClassNotification dCN = new DatabaseClassNotification(context);
                dCN.open();
                dCN.olustur(karsiid, karsiname, mesaj);
                dCN.close();
                dCN.open();
                int kisisayisi = dCN.varolankisisayisinicek();
                List<String> kisiler = dCN.varolankisilericek();
                Log.i("tago", "kisisayisi" + String.valueOf(kisisayisi));
                dCN.close();
                if (bildirimleracikmi) {
                    if (kisisayisi == 1) {
                        if(adamshapmi.equals("evet")){
                            notificationDesc = mesaj;
                            DatabaseClassKimleriActirdin pppp = new DatabaseClassKimleriActirdin(context);
                            pppp.open();
                            String karsiresimpath = pppp.databasedenozelresimpathcek(karsiid);
                            String bandurumu = pppp.databasedenbanlanmadurumucek(karsiid);
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notiname);
                            Bitmap b = BitmapFactory.decodeFile(a.getAbsolutePath());
                            Bitmap l = getCircleBitmap(b);
                            notification.setLargeIcon(l);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, Mesajlasma.class);
                            i.putExtra("karsiid", karsiid);
                            i.putExtra("karsiname", karsiname);
                            i.putExtra("karsifaceprofilurl", karsifaceprofilurl);
                            i.putExtra("karsidurum", bundle.getString("status"));
                            i.putExtra("karsiresimpath", karsiresimpath);
                            i.putExtra("karsibandurumu", bandurumu);
                            i.putExtra("cinsiyet" , cinsiyet);
                            i.putExtra("burc" , burc);
                            i.putExtra("intentname", "PushReceiverShappy");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1000, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }
                        if(adamacikmi.equals("acik")&&adamshapmi.equals("hayir")){
                            notificationDesc = mesaj;
                            DatabaseClassKiminleKonustun dckk = new DatabaseClassKiminleKonustun(context);
                            dckk.open();
                            String karsiresimpath = dckk.databasedenozelresimpathcek(karsiid);
                            String bandurumu = dckk.databasedenbanlanmadurumucek(karsiid);
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notiname);
                            Bitmap b = BitmapFactory.decodeFile(a.getAbsolutePath());
                            Bitmap l = getCircleBitmap(b);
                            notification.setLargeIcon(l);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, Mesajlasma.class);
                            i.putExtra("karsiid", karsiid);
                            i.putExtra("karsiname", karsiname);
                            i.putExtra("karsifaceprofilurl", karsifaceprofilurl);
                            i.putExtra("karsidurum", bundle.getString("status"));
                            i.putExtra("karsiresimpath", karsiresimpath);
                            i.putExtra("karsibandurumu", bandurumu);
                            i.putExtra("cinsiyet", cinsiyet);
                            i.putExtra("burc" , burc);
                            i.putExtra("yas" , yas);
                            i.putExtra("okul" , okul);
                            i.putExtra("coverfotourl" , coverfotourl);
                            i.putExtra("intentname", "PushReceiverGecmis");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1000, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }else if(adamacikmi.equals("degil")&&adamshapmi.equals("hayir")){
                            notificationDesc = mesaj;
                            DatabaseClassKiminleKonustun dckk = new DatabaseClassKiminleKonustun(context);
                            dckk.open();
                            String karsiresimpath = dckk.databasedenozelresimpathcek(karsiid);
                            String bandurumu = dckk.databasedenbanlanmadurumucek(karsiid);
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notiname);
                            Bitmap r = BitmapFactory.decodeFile(SharedPrefBosResimPathAl(context));
                            Bitmap p = getCircleBitmap(r);
                            notification.setLargeIcon(p);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, Mesajlasma.class);
                            i.putExtra("karsiid", karsiid);
                            i.putExtra("karsiname", karsiname);
                            i.putExtra("karsifaceprofilurl", karsifaceprofilurl);
                            i.putExtra("karsidurum", bundle.getString("status"));
                            i.putExtra("karsiresimpath", karsiresimpath);
                            i.putExtra("karsibandurumu", bandurumu);
                            i.putExtra("cinsiyet" , cinsiyet);
                            i.putExtra("burc" , burc);
                            i.putExtra("yas" , yas);
                            i.putExtra("okul" , okul);
                            i.putExtra("coverfotourl" , coverfotourl);
                            i.putExtra("intentname", "PushReceiverGecmis");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 1000, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }
                    } else if (kisisayisi == 2) {
                        if(adamacikmi.equals("acik")){
                            String notiilkkisi = "default";
                            String notikincikisi = "default";
                            String ilkkisi = kisiler.get(0);
                            String ikincikisi = kisiler.get(1);
                            DatabaseClassKiminleKonustun mmm = new DatabaseClassKiminleKonustun(context);
                            mmm.open();
                            String ilkkisiacikmi = mmm.databasedenisimliresmiacikmicek(ilkkisi);
                            String ikincikisiacikmi = mmm.databasedenisimliresmiacikmicek(ikincikisi);
                            mmm.close();
                            if(ilkkisiacikmi.equals("acik")){
                                notiilkkisi = ilkkisi;
                            }
                            if(ikincikisiacikmi.equals("acik")){
                                notikincikisi = ikincikisi;
                            }
                            DatabaseClassKimleriActirdin eee = new DatabaseClassKimleriActirdin(context);
                            eee.open();
                            List<String> varlar = eee.databasedenisimcek();
                            eee.close();
                            for(String aa : varlar){
                                if(ilkkisi.equals(aa)){
                                    notiilkkisi = ilkkisi;
                                }if(ikincikisi.equals(aa)){
                                    notikincikisi = ikincikisi;
                                }
                            }
                            notificationDesc = notikincikisi + " ve " + notiilkkisi + " sana mesaj attı";
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notificationTitle);
                            Bitmap b = BitmapFactory.decodeFile(a.getAbsolutePath());
                            Bitmap l = getCircleBitmap(b);
                            notification.setLargeIcon(l);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, AnaAkim.class);
                            i.putExtra("intentname", "PushReceiverNoti");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }else if(adamacikmi.equals("degil")){
                            String notiilkkisi = "default";
                            String notikincikisi = "default";
                            String ilkkisi = kisiler.get(0);
                            String ikincikisi = kisiler.get(1);
                            DatabaseClassKiminleKonustun mmm = new DatabaseClassKiminleKonustun(context);
                            mmm.open();
                            String ilkkisiacikmi = mmm.databasedenisimliresmiacikmicek(ilkkisi);
                            String ikincikisiacikmi = mmm.databasedenisimliresmiacikmicek(ikincikisi);
                            mmm.close();
                            if(ilkkisiacikmi.equals("acik")){
                                notiilkkisi = ilkkisi;
                            }
                            if(ikincikisiacikmi.equals("acik")){
                                notikincikisi = ikincikisi;
                            }
                            DatabaseClassKimleriActirdin dckx = new DatabaseClassKimleriActirdin(context);
                            dckx.open();
                            List<String> mevcutisimlerr = dckx.databasedenisimcek();
                            for (String isim : mevcutisimlerr) {
                                if (ilkkisi.equals(isim)) {
                                    notiilkkisi = ilkkisi;
                                }
                                if (ikincikisi.equals(isim)) {
                                    notikincikisi = ikincikisi;
                                }
                            }
                            dckx.close();
                            notificationDesc = notikincikisi + " ve " + notiilkkisi + " sana mesaj attı";
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notificationTitle);
                            Bitmap r = BitmapFactory.decodeFile(SharedPrefBosResimPathAl(context));
                            Bitmap p = getCircleBitmap(r);
                            notification.setLargeIcon(p);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, AnaAkim.class);
                            i.putExtra("intentname", "PushReceiverNoti");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }

                    } else {
                        if(adamacikmi.equals("acik")){
                            notificationDesc = kisisayisi + " kişiden yeni mesaj var";
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notificationTitle);
                            Bitmap b = BitmapFactory.decodeFile(a.getAbsolutePath());
                            Bitmap l = getCircleBitmap(b);
                            notification.setLargeIcon(l);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, AnaAkim.class);
                            i.putExtra("intentname", "PushReceiverNoti");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }else if(adamacikmi.equals("degil")){
                            notificationDesc = kisisayisi + " kişiden yeni mesaj var";
                            NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                            notification.setAutoCancel(true);
                            notification.setContentText(notificationDesc);
                            notification.setContentTitle(notificationTitle);
                            Bitmap r = BitmapFactory.decodeFile(SharedPrefBosResimPathAl(context));
                            Bitmap p = getCircleBitmap(r);
                            notification.setLargeIcon(p);
                            notification.setSmallIcon(R.mipmap.galin_shappy);
                            notification.setWhen(System.currentTimeMillis());
                            notification.setVibrate(new long[]{1000, 1000, 1000});
                            notification.setLights(Color.YELLOW, 2000, 2000);
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            notification.setSound(alarmSound);
                            Intent i = new Intent(context, AnaAkim.class);
                            i.putExtra("intentname", "PushReceiverNoti");
                            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                            notification.setContentIntent(pendingIntent);
                            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.notify(mesajuniqueID, notification.build());
                        }
                    }
                }
            }
        } else if (intent.getStringExtra("mode").equals("groupChat")) {
            Log.i("tago" , "grupchatnoti");
            Bundle bundle = intent.getExtras();
            String mesaj = bundle.getString("message");
            String karsinick = bundle.getString("name");
            String kanaladi = bundle.getString("placename");
            String kanalid = bundle.getString("placeID");
            String kanalmodu = bundle.getString("type");
            String kanalurl = bundle.getString("photo");
            String grupNotificationTitle = kanaladi;
            String grupNotificationDesc = karsinick + ": " + mesaj;
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            String date;
            if (minute < 10) {
                date = String.valueOf(hour) + ":0" + String.valueOf(minute);
            } else {
                date = String.valueOf(hour) + ":" + String.valueOf(minute);
            }
            if (!kanalnotificationver && kanalnotificationsonid.equals(kanalid)) {
                Intent i = new Intent("broadcastGrup");
                i.putExtra("mesaj", mesaj);
                i.putExtra("karsinick", karsinick);
                context.sendBroadcast(i);
            } else {
                Log.i("tago" , "kanalidp" + kanalid);
                DatabaseClassNotificationGrup dcng = new DatabaseClassNotificationGrup(context);
                dcng.open();
                dcng.olustur(kanalid, kanaladi, karsinick, mesaj);
                dcng.close();
                ///////////////////////////
                String kanalvarmi;
                DatabaseClassKonusulanKanallar dcss = new DatabaseClassKonusulanKanallar(context);
                dcss.open();
                kanalvarmi = dcss.kanalvarmicek(kanaladi);
                dcss.close();
                if (kanalvarmi.equals("yok")) {
                    DatabaseClassKonusulanKanallar dckf = new DatabaseClassKonusulanKanallar(context);
                    dckf.open();
                    dckf.olustur(kanaladi, kanalmodu, "no",kanalurl, "var", "1");
                    dckf.close();
                } else {
                    DatabaseClassKonusulanKanallar dckf = new DatabaseClassKonusulanKanallar(context);
                    dckf.open();
                    int yenimesaj = Integer.valueOf(dckf.databasedenozelkacyenimesajcek(kanaladi));
                    yenimesaj = yenimesaj + 1;
                    String a = String.valueOf(yenimesaj);
                    String likedurumu = dckf.databasedenozellikecek(kanaladi);
                    dckf.olustur(kanaladi, kanalmodu,likedurumu,kanalurl, "var", a);
                    dckf.close();
                }
                ////////////////////////
                DatabaseClassGrupChat dCGC = new DatabaseClassGrupChat(context);
                dCGC.open();
                dCGC.olusturx(mesaj, kanaladi, karsinick, date);
                dCGC.close();
                DatabaseClassNotificationGrup dcnq = new DatabaseClassNotificationGrup(context);
                dcnq.open();
                int kanalsayisi = dcnq.varolankanalsayisinicek();
                Log.i("tago" , "kanalsayisi" + kanalsayisi);
                dcnq.close();
                if (bildirimleracikmi) {
                    if (kanalsayisi == 1) {
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                        notification.setAutoCancel(true);
                        notification.setContentText(grupNotificationDesc);
                        notification.setContentTitle(grupNotificationTitle);
                        Bitmap z = BitmapFactory.decodeResource(context.getResources(),R.mipmap.genelsoh_kr);
                        notification.setLargeIcon(z);
                        notification.setSmallIcon(R.mipmap.galin_shappy);
                        notification.setWhen(System.currentTimeMillis());
                        Intent i = new Intent(context, GrupSohbeti.class);
                        i.putExtra("intentname", "PushReceiver");
                        i.putExtra("kanaladi", kanaladi);
                        i.putExtra("kanalmodu", kanalmodu);
                        i.putExtra("kanalid", kanalid);
                        i.putExtra("kanalurl" , kanalurl);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(grupuniqueID, notification.build());
                    } else {
                        NotificationCompat.Builder notification = new NotificationCompat.Builder(context);
                        notification.setAutoCancel(true);
                        notification.setContentText(grupNotificationDesc);
                        notification.setContentTitle(grupNotificationTitle);
                        Bitmap z = BitmapFactory.decodeResource(context.getResources(),R.mipmap.genelsoh_tr);
                        notification.setLargeIcon(z);
                        notification.setSmallIcon(R.mipmap.galin_shappy);
                        notification.setWhen(System.currentTimeMillis());
                        Intent i = new Intent(context, AnaAkim.class);
                        i.putExtra("intentname", "PushReceiverKanal");
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
                        notification.setContentIntent(pendingIntent);
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(grupuniqueID, notification.build());
                    }
                }
            }
        }
    }
    private int getAge(int year, int month, int day) {
        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if(month > currentMonth){
            --age;
        }
        else if(month == currentMonth){
            if(day > todayDay){
                --age;
            }
        }
        return age;

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
}