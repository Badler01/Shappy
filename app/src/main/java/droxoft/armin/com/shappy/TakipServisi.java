package droxoft.armin.com.shappy;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class TakipServisi extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    String isim, faceprofilurl, cinsiyet, email, facebookID;
    String cinsiyett;
    GoogleApiClient googleclient;
    String serverid;
    boolean ilkgiris;
    boolean ilkgirisyapildi = false;

    private void SharedPreferenceIdKaydet(String id) {
        SharedPreferences sp = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("serverid", id);
        editor.apply();
    }

    private String SharedPreferencePushyIdAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String pushyid = sP.getString("pushyid", "defaultpushyid");
        return pushyid;
    }

    public void onCreate() {
        super.onCreate();
        if (googleclient == null) {
            googleclient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Runnable r = new Runnable() {
            public void run() {
                googleclient = new GoogleApiClient.Builder(TakipServisi.this).addApi(LocationServices.API).addConnectionCallbacks(TakipServisi.this)
                        .addOnConnectionFailedListener(TakipServisi.this).build();
                if (googleclient != null) {
                    googleclient.connect();
                }
            }
        };
        Thread islem = new Thread(r);
        islem.start();
        isim = intent.getStringExtra("isim");
        faceprofilurl = intent.getStringExtra("resimurl");
        cinsiyet = intent.getStringExtra("gender");
        cinsiyett = String.valueOf(cinsiyet.toUpperCase().charAt(0));
        email = intent.getStringExtra("email");
        facebookID = intent.getStringExtra("facebookID");
        ilkgiris = intent.getBooleanExtra("ilkgiris" ,true);
        return Service.START_NOT_STICKY;
    }

    public void onDestroy() {
        googleclient.disconnect();
        stopSelf();
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onConnected(Bundle bundle) {
        Location firstlocation = LocationServices.FusedLocationApi.getLastLocation(googleclient);
        LocationRequest locrequest = new LocationRequest();
        locrequest.setInterval(1000000);
        locrequest.setFastestInterval(500000);
        locrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleclient, locrequest, this);
        String pushyid = SharedPreferencePushyIdAl();
        if (firstlocation == null) {
            Toast.makeText(getApplicationContext(), "Programı kullanmak için yüksek doğruluklu konumu aktifleştiriniz", Toast.LENGTH_SHORT).show();
            Thread a = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            a.start();
        } else {
            ServerIlkGiris sIG = new ServerIlkGiris(pushyid, isim, faceprofilurl, String.valueOf(firstlocation.getLongitude()),
                    String.valueOf(firstlocation.getLatitude()));
            sIG.execute();
        }
    }

    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Google Location onConnectionSuspended", Toast.LENGTH_LONG).show();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Location onConnectionFailed", Toast.LENGTH_LONG).show();
    }

    public void onLocationChanged(final Location location) {
        Thread a = new Thread() {
            public void run() {
                while (!ilkgirisyapildi) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ilkgirisyapildi) {
                    ServerKullaniciKoordinatYenile sKKY = new ServerKullaniciKoordinatYenile(serverid, String.valueOf
                            (location.getLongitude()), String.valueOf(location.getLatitude()));
                    sKKY.execute();
                }
            }
        };
        a.start();

    }


    private class ServerIlkGiris extends AsyncTask<String, Void, String> {

        String regid, isim, faceprofilurl, longitude, latitude;
        String charset = "UTF-8";
        String query;

        public ServerIlkGiris(String regid, String isim, String faceprofilurl, String longitude, String latitude) {
            String param1 = "isim";
            String param2 = "resimurrl";
            String param3 = "longi";
            String param4 = "lat";
            String param5 = "regid";
            this.regid = regid;
            this.isim = isim;
            this.faceprofilurl = faceprofilurl;
            this.longitude = longitude;
            this.latitude = latitude;
            try {
                query = String.format("param1=%s&param2=%s&param3=%s&param4=%s&param5=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset),
                        URLEncoder.encode(param3, charset), URLEncoder.encode(param4, charset), URLEncoder.encode(param5, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/connection.php?name=" +
                        URLEncoder.encode(isim, charset) + "&url=" + URLEncoder.encode(faceprofilurl, charset) +
                        "&long=" + URLEncoder.encode(longitude, charset) + "&lat=" + URLEncoder.encode(latitude, charset)
                        + "&regid=" + URLEncoder.encode(regid, charset) +
                        "&email=" + URLEncoder.encode(email, charset)
                        + "&gender=" + URLEncoder.encode(cinsiyett, charset) +
                        "&facebookID=" + URLEncoder.encode(facebookID, charset)).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "*/*");
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String inputline;
            BufferedReader bufferedReader;
            try {
                if (connection.getResponseCode() == 200) {
                    bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((inputline = bufferedReader.readLine()) != null) {
                        SharedPreferenceIdKaydet(inputline);
                        serverid = inputline;
                        bufferedReader.close();
                    }
                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "cop";
        }

        protected void onPostExecute(String s) {
            Intent i = new Intent(TakipServisi.this, AnaAkim.class);
            ilkgirisyapildi = true;
            i.putExtra("isim", isim);
            i.putExtra("intentname", "TakipServisi");
            i.putExtra("faceprofilurl", faceprofilurl);
            i.putExtra("ilkgiris" , ilkgiris);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    private class ServerKullaniciKoordinatYenile extends AsyncTask<String, Void, String> {

        String serverid, longitude, latitude;
        String charset = "UTF-8";
        String query;

        public ServerKullaniciKoordinatYenile(String serverid, String longitude, String latitude) {
            this.serverid = serverid;
            this.longitude = longitude;
            this.latitude = latitude;
            String param1 = "id";
            String param2 = "longitude";
            String param3 = "latitude";
            try {
                query = String.format("param1=%s&params=%s&param3=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset)
                        , URLEncoder.encode(param3, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.184.15/shappy/update_location.php?id=" + serverid +
                        "&long=" + longitude + "&lat=" + latitude).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output = null;
            try {
                output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream response = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
