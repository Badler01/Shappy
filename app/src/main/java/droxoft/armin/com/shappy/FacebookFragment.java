package droxoft.armin.com.shappy;


import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

public class FacebookFragment extends Fragment {
    String email;
    String cinsiyet;
    String tumisim;
    String firstname;
    String lastname;
    String facebookID;
    String coverphotourl;

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
        int[] taniticiresimler = {R.mipmap.birbir,R.mipmap.ikiiki,R.mipmap.ucuc,R.mipmap.dort};
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
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
                                    sharedcinsiyetkaydet(cinsiyet);
                                    coverphotourl = object.getJSONObject("cover").getString("source");
                                    sharedcoverphotourlkaydet(coverphotourl);
                                    String dogumgunu = object.getString("birthday");
                                    Log.i("tago" , "dogumgunu " + dogumgunu);
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
                Toast.makeText(getActivity(), "Facebook Login iptal edildi", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getActivity(), "Facebook Login hata oluşturdu", Toast.LENGTH_LONG).show();
            }
        });
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                i.putExtra("ilkgiris" , ilkgiris);
                getActivity().startService(i);
            }
        }

    }

}
