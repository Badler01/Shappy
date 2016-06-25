package droxoft.armin.com.shappy;


import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ugurtekbas.fadingindicatorlibrary.FadingIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

public class FacebookFragment extends Fragment {
    String email;
    String cinsiyet;
    String tumisim;
    String firstname;
    String lastname;
    String facebookID;
    String coverphotourl;
    String dogumgunu;
    String  day , month , year , burc , yass;

    CallbackManager callbackManager;
    Profile profile;



    private void sharedcoverphotourlkaydet(String coverphotourl) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("coverphotourl" , coverphotourl);
        editor.apply();
    }

    private void sharedfacebookIDkaydet(String facebookID) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("facebookID" , facebookID);
        editor.apply();
    }

    private void sharedPrefYearKaydet() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("year" , year);
        editor.apply();
    }

    private void sharedPrefMonthKaydet() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("month" , month);
        editor.apply();
    }

    private void sharedPrefDayKaydet() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("day" , day);
        editor.apply();
    }

    private String sharedFacebookIDAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        return sP.getString("facebookID" , "defaultfacebookID");
    }

    private void sharedilkgiriskaydet(boolean b) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("ilkgiris" , b);
        editor.apply();
    }

    private boolean sharedilkgirisal(){
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getBoolean("ilkgiris" , true);
    }

    private void sharedcinsiyetkaydet(String cinsiyet) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("cinsiyet", cinsiyet);
        prefEditor.apply();
    }

    private void sharedemailkaydet(String email) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("email", email);
        prefEditor.apply();
    }

    private void sharedlastnamekaydet(String lastname) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("lastname", lastname);
        prefEditor.apply();
    }

    private void sharedfirstnamekaydet(String firstname) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("firstname", firstname);
        prefEditor.apply();
    }

    private void sharedtumisimkaydet(String tumisim) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("tumisim", tumisim);
        prefEditor.apply();
    }

    private void sharedPrefYasKaydet(String yas) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("yas", yas);
        editor.apply();
    }

    private void sharedPrefBurcKaydet(String burc) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("burc", burc);
        editor.apply();
    }

    private void sharedresimurlkaydet(String profilfotourl) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = sP.edit();
        prefEditor.putString("faceprofilurl", profilfotourl);
        prefEditor.apply();
    }

    private String bitmapiinternalkaydet(Bitmap bitmap) {
        ContextWrapper cw = new ContextWrapper(getActivity());
        File directory = cw.getDir("userpro", Context.MODE_PRIVATE);
        File mypath=new File(directory,"kullaniciresmi.jpg");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    public void onResume() {
        Log.i("tago" , "onResume");
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            tumisim = profile.getName();
            firstname = profile.getFirstName();
            lastname = profile.getLastName();
            sharedtumisimkaydet(tumisim);
            sharedfirstnamekaydet(firstname);
            sharedlastnamekaydet(lastname);
        }
    }

    public void onStop() {
        super.onStop();
        getActivity().finish();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.facebookfragment, container, false);
        int[] taniticiresimler = {R.mipmap.birbir,R.mipmap.ikiiki,R.mipmap.dort};
        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getActivity(),taniticiresimler);
        FadingIndicator indicator = (FadingIndicator)view.findViewById(R.id.circleIndicator);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        indicator.setViewPager(viewPager);
        indicator.setFillColor(Color.argb(255,255,103,0));
        indicator.setStrokeColor(Color.argb(255,0,0,0));
        indicator.setRadius(15f);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "public_profile", "email" ,"user_birthday"));
        loginButton.setFragment(this);
        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheckk = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i("tago", "permission alinmis");
        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},131);
        }
        if (permissionCheckk == PackageManager.PERMISSION_GRANTED) {
            Log.i("tago", "permission alinmiss");
        }else{
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},132);
        }
        Log.i("tago" , "burdan gecti");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("tago" , "buraya geldi");
                profile = Profile.getCurrentProfile();
                if (profile.getId() != null) {
                    facebookID = profile.getId();
                    String a = sharedFacebookIDAl();
                    if (a.equals("defaultfacebookID")) {
                        sharedilkgiriskaydet(true);
                    }else if(!a.equals(facebookID)){
                        sharedilkgiriskaydet(true);
                    }else{
                        sharedilkgiriskaydet(false);
                    }
                    sharedfacebookIDkaydet(facebookID);
                    KullaniciProfilCek kPC = new KullaniciProfilCek();
                    kPC.execute(profile.getId());
                }
                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    email = object.getString("email");
                                    sharedemailkaydet(email);
                                    cinsiyet = object.getString("gender");
                                    Log.i("tago" , "cinsiyetxx "+ cinsiyet);
                                    sharedcinsiyetkaydet(cinsiyet);
                                    coverphotourl = object.getJSONObject("cover").getString("source");
                                    sharedcoverphotourlkaydet(coverphotourl);
                                    dogumgunu = object.getString("birthday");
                                    day = dogumgunu.substring(0,2);
                                    month = dogumgunu.substring(3,5);
                                    year = dogumgunu.substring(6,10);
                                    int yas = getAge(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day));
                                    yass = String.valueOf(yas);
                                    burc = getBurc(Integer.valueOf(month),Integer.valueOf(day));
                                    sharedPrefYasKaydet(String.valueOf(yas));
                                    sharedPrefBurcKaydet(burc);
                                    sharedPrefDayKaydet();
                                    sharedPrefMonthKaydet();
                                    sharedPrefYearKaydet();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "email,gender,cover,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i("tago" , "face login onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("tago" , "face login onError");
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private int getAge(int year, int month, int day) {
        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if (month > currentMonth) {
            --age;
        } else if (month == currentMonth) {
            if (day > todayDay) {
                --age;
            }
        }
        return age;

    }

    private String getBurc(int month, int day) {
        if (month == 1) {
            if (day < 21) {
                return "oglak";
            } else {
                return "kova";
            }
        } else if (month == 2) {
            if (day < 20) {
                return "kova";
            } else {
                return "balik";
            }
        } else if (month == 3) {
            if (day < 22) {
                return "balik";
            } else {
                return "koc";
            }
        } else if (month == 4) {
            if (day < 21) {
                return "koc";
            } else {
                return "boga";
            }
        } else if (month == 5) {
            if (day < 22) {
                return "boga";
            } else {
                return "ikizler";
            }
        } else if (month == 6) {
            if (day < 22) {
                return "ikizler";
            } else {
                return "yengec";
            }
        } else if (month == 7) {
            if (day < 24) {
                return "yengec";
            } else {
                return "aslan";
            }
        } else if (month == 8) {
            if (day < 23) {
                return "aslan";
            } else {
                return "basak";
            }
        } else if (month == 9) {
            if (day < 23) {
                return "basak";
            } else {
                return "terazi";
            }
        } else if (month == 10) {
            if (day < 23) {
                return "terazi";
            } else {
                return "akrep";
            }
        } else if (month == 11) {
            if (day < 23) {
                return "akrep";
            } else {
                return "yay";
            }
        } else if (month == 12) {
            if (day < 22) {
                return "yay";
            } else {
                return "oglak";
            }
        }

        return "aslan";
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 131: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i("tago" , "permission alindi");
                } else {
                    Log.i("tago" , "permission alinamadi");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 132:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("tago" , "permission alindii");
                } else {
                    Log.i("tago" , "permission alinamadii");
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class KullaniciProfilCek extends AsyncTask<String, Void, Bitmap> {

        String urll;

        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL("https://graph.facebook.com/" + params[0] + "/picture?type=large&redirect=true&width=900&height=900");
                urll = "https://graph.facebook.com/" + params[0] + "/picture?type=large&redirect=true&width=900&height=900";
                sharedresimurlkaydet(urll);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                bitmapiinternalkaydet(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (tumisim != null) {
                boolean ilkgiris = sharedilkgirisal();
                Intent i = new Intent(getActivity(), TakipServisi.class);
                i.putExtra("isim", firstname);
                i.putExtra("resimurl", urll);
                i.putExtra("email", email);
                i.putExtra("gender", cinsiyet);
                i.putExtra("facebookID", facebookID);
                i.putExtra("day" , day );
                i.putExtra("month" , month);
                i.putExtra("year" , year);
                i.putExtra("burc", burc);
                i.putExtra("tumisim", tumisim);
                i.putExtra("yas", yass);
                i.putExtra("ilkgiris" , ilkgiris);
                getActivity().startService(i);
            }
        }

    }

}
