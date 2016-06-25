package droxoft.armin.com.shappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.facebook.FacebookSdk;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity {

    private void sharedPrefMainDurumKaydet(boolean a){
        SharedPreferences sP = getSharedPreferences("programisleyis" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("mainiyasat" ,a );
        editor.apply();
    }

    private String SharedPrefBurcAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("burc" , "defaultburc");
    }

    private boolean sharedPrefMainDurumAl(){
        SharedPreferences sP = getSharedPreferences("programisleyis" , Context.MODE_PRIVATE);
        return sP.getBoolean("mainiyasat", true);
    }

    private String SharedPrefEmailAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("email", "defaultemail");
    }

    private String SharedPrefCinsiyetAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("cinsiyet", "defaultcinsiyet");
    }

    private String SharedPrefFaceProfilUrlAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("faceprofilurl", "defaultfaceprofilurl");
    }

    private String SharedPrefIsımAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("firstname", "defaultfirstname");
    }

    private String SharedPrefYasAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("yas", "defaultyas");
    }

    private String SharedPrefTumIsimAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("fullname", "defaulttumisim");
    }

    private void SharedPreferenceRegidKaydet(String registrationId) {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("pushyid", registrationId);
        editor.apply();
    }

    private String SharedPrefIdAl() {
        SharedPreferences sharedPreferences = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sharedPreferences.getString("serverid", "defaultserverid");
    }

    private boolean SharedPrefKullaniciCiktiAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getBoolean("kullanicicikti", false);
    }

    private String SharedPrefFacebookIDAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("facebookID", "defaultfacebookID");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefMainDurumKaydet(true);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        boolean a = checkinternet();
        if(!a){
            setContentView(R.layout.splashscreen);
            Toast.makeText(getApplicationContext(), "Programı kullanmak için interneti açın", Toast.LENGTH_SHORT).show();
            Thread w = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            w.start();
        }else{
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            FacebookSdk.sdkInitialize(getApplicationContext());
            Pushy.listen(this);
            new registerForPushNotificationsAsync().execute();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);
            String serverid = SharedPrefIdAl();
            boolean kullanicicikti = SharedPrefKullaniciCiktiAl();
            if (serverid.equals("defaultserverid")||kullanicicikti) {
                setContentView(R.layout.activity_main);
            } else {
                setContentView(R.layout.splashscreen);
                ImageView mavigoz = (ImageView) findViewById(R.id.mavi_goz);
                ImageView siyahgoz = (ImageView) findViewById(R.id.siyah_goz);
                Animation Animsiyah_goz = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_siyah);
                Animation Animmavi_goz = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_mavi);
                mavigoz.startAnimation(Animmavi_goz);
                siyahgoz.startAnimation(Animsiyah_goz);
                final String tumisim = SharedPrefTumIsimAl();
                final String yas = SharedPrefYasAl();
                final String isim = SharedPrefIsımAl();
                final String faceprofilurl = SharedPrefFaceProfilUrlAl();
                final String cinsiyet = SharedPrefCinsiyetAl();
                final String email = SharedPrefEmailAl();
                final String facebookID = SharedPrefFacebookIDAl();
                final String burc = SharedPrefBurcAl();
                Thread p = new Thread(){
                    public void run(){
                        try {
                            sleep(3000);
                            Intent i = new Intent(MainActivity.this, TakipServisi.class);
                            i.putExtra("isim", isim);
                            i.putExtra("resimurl", faceprofilurl);
                            i.putExtra("gender", cinsiyet);
                            i.putExtra("email", email);
                            i.putExtra("facebookID", facebookID);
                            i.putExtra("burc" , burc);
                            i.putExtra("tumisim" , tumisim);
                            i.putExtra("yas" , yas);
                            i.putExtra("ilkgiris", false);
                            startService(i);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                p.start();
            }
        }
    }

    private boolean checkinternet() {
        ConnectivityManager conMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null) {
            return false;
        }
        if (!i.isConnected()){
            return false;
        }
        if (!i.isAvailable()) {
            return false;
        }
        return true;
    }

    protected void onResume() {
        super.onResume();
        //AppEventsLogger.activateApp(this);
        boolean a = sharedPrefMainDurumAl();
        if(!a){
            finish();
        }
    }

    protected void onPause() {
        super.onPause();
        //AppEventsLogger.deactivateApp(this);
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onStart() {
        super.onStart();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }

    private class registerForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                String registrationId = Pushy.register(getApplicationContext());
                SharedPreferenceRegidKaydet(registrationId);
            } catch (Exception exc) {
                return exc;
            }

            return null;
        }

        protected void onPostExecute(Exception exc) {
            if (exc != null) {
                Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

}