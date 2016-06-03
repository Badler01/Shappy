package droxoft.armin.com.shappy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class PageFragment2 extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private View view;
    private ImageView imageviewkapak, imageviewprofil, ivkapak_onu;
    private String isim;
    private boolean kullanicicikti = false;
    EditText editTextaciklama;
    static TextView textviewisim;
    static TextView textviewnick;
    static TextView textviewokul;
    static ImageView imageviewburc;
    Switch switch1;
    TextView textviewaciklama;
    Bitmap karsiresim;
    Bitmap karsicover;
    Bitmap bluredcover, halfbluredcover, scaledcover, scaledhalfcover;


    ProfileTracker protracker;
    AccessTokenTracker tracker;
    String yenicoverfotourl;


    private String sharedPrefNickAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("nick", "ddefaultnick");
    }

    private String sharedPrefIdAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("serverid", "defaultserverid");
    }

    private String sharedPrefCinsiyetAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("cinsiyet", "defaultcinsiyet");
    }

    private String sharedPrefDurumAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("durum", "defaultdurum");
    }

    private void sharedBildirimlerKaydet(boolean bildirimler) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("bildirimler", bildirimler);
        editor.apply();
    }

    private void sharedPrefDurumKaydet(String durum) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("durum", durum);
        editor.apply();
    }

    public void onResume() {
        super.onResume();
        String nick = sharedPrefNickAl();
        textviewnick.setText(nick);
        String durum = sharedPrefDurumAl();
        if (!durum.equals("defaultdurum")) {
            textviewaciklama.setText(durum);
        }
    }

    private void SharedPrefKullaniciCiktiKaydet(boolean kullanicicikti) {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("kullanicicikti", kullanicicikti);
        editor.apply();
    }

    public static void SharedPrefNickYerlestir(Context context) {
        SharedPreferences sP = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        textviewnick.setText(sP.getString("nick", "defaultnick"));
    }

    public static void SharedPrefOkulYerlestir(Context context) {
        SharedPreferences sP = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        textviewokul.setText(sP.getString("okul", "defaultokul"));
    }

    public static void SharedPrefYasYerlestir(Context context) {
        SharedPreferences sP = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String a = textviewisim.getText().toString();
        String b = a + ", " + sP.getString("yas", "defaultyas");
        textviewisim.setText(b);
    }

    public static void SharedPrefBurcYerlestir(Context context) {
        SharedPreferences sP = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String burc = sP.getString("burc", "defaultburc");
        Log.i("tago", "burc frag" + burc);
        if (burc.equals("oglak")) {
            imageviewburc.setImageResource(R.drawable.ktoglak);
            imageviewburc.setContentDescription("Oğlak");
        } else if (burc.equals("kova")) {
            imageviewburc.setImageResource(R.drawable.ktkova);
            imageviewburc.setContentDescription("Kova");
        } else if (burc.equals("balik")) {
            imageviewburc.setImageResource(R.drawable.ktbalik);
            imageviewburc.setContentDescription("Balık");
        } else if (burc.equals("koc")) {
            imageviewburc.setImageResource(R.drawable.ktkoc);
            imageviewburc.setContentDescription("Koç");
        } else if (burc.equals("boga")) {
            imageviewburc.setImageResource(R.drawable.ktboga);
            imageviewburc.setContentDescription("Boğa");
        } else if (burc.equals("ikizler")) {
            imageviewburc.setImageResource(R.drawable.ktikizler);
            imageviewburc.setContentDescription("İkizler");
        } else if (burc.equals("yengec")) {
            imageviewburc.setImageResource(R.drawable.ktyengec);
            imageviewburc.setContentDescription("Yengeç");
        } else if (burc.equals("Aslan")) {
            imageviewburc.setContentDescription("Aslan");
            imageviewburc.setImageResource(R.drawable.ktaslan);
        } else if (burc.equals("basak")) {
            imageviewburc.setImageResource(R.drawable.ktbasak);
            imageviewburc.setContentDescription("Başak");
        } else if (burc.equals("terazi")) {
            imageviewburc.setImageResource(R.drawable.ktterazi);
            imageviewburc.setContentDescription("Terazi");
        } else if (burc.equals("akrep")) {
            imageviewburc.setImageResource(R.drawable.ktakrep);
            imageviewburc.setContentDescription("Akrep");
        } else if (burc.equals("yay")) {
            imageviewburc.setImageResource(R.drawable.ktyay);
            imageviewburc.setContentDescription("Yay");
        }
    }

    public static PageFragment2 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment2 fragment = new PageFragment2();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        SharedPrefKullaniciCiktiKaydet(false);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.profil, container, false);
        Bundle b = getActivity().getIntent().getExtras();
        isim = b.getString("isim");
        String faceprofilurl = b.getString("faceprofilurl");
        UrldenResim uR = new UrldenResim();
        uR.execute(faceprofilurl);
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tanimlar(view);
        coverphotocek();
    }

    private void tanimlar(View view) {
        imageviewburc = (ImageView) view.findViewById(R.id.burc);
        SharedPrefBurcYerlestir(getActivity());
//        imageviewburc.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                LayoutInflater inflater = getActivity().getLayoutInflater();
//                View layout = inflater.inflate(R.layout.toastburc,
//                        (ViewGroup) getActivity().findViewById(R.id.toastburc));
//                TextView text = (TextView) layout.findViewById(R.id.textburc);
//                text.setText(imageviewburc.getContentDescription());
//                Toast toast = new Toast(getActivity().getApplicationContext());
//                toast.setGravity(Gravity.TOP | Gravity.LEFT, 0, 0);
//                toast.setDuration(Toast.LENGTH_SHORT);
//                toast.setView(layout);
//                toast.show();
//                return false;
//            }
//        });
        imageviewkapak = (ImageView) view.findViewById(R.id.imageviewkapak);
        ivkapak_onu = (ImageView) view.findViewById(R.id.imageView42);
        final RelativeLayout laba = (RelativeLayout) view.findViewById(R.id.laba);
        final boolean[] pressed = {false};
        imageviewkapak.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                switch (arg1.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i("tago", "basiliyo");
                        laba.bringToFront();
                        laba.invalidate();
                        pressed[0] = true;
                        imageviewkapak.setImageBitmap(halfbluredcover);
                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        Log.i("tago", "basiliyo move");
//                        laba.bringToFront();
//                        laba.invalidate();
//                        pressed[0] = true;
//                        imageviewkapak.setImageBitmap(halfbluredcover);
//                        pressed[0] = true;
                    case MotionEvent.ACTION_UP:
                            Log.i("tago", "basiliyo kaldirildi");

                        ivkapak_onu.bringToFront();
                        ivkapak_onu.invalidate();
                        imageviewprofil.bringToFront();
                        imageviewprofil.invalidate();
                        imageviewkapak.setImageBitmap(bluredcover);
                        pressed[0] = false;
                        break;
                }
                return pressed[0];
            }
        });

        imageviewprofil = (ImageView) view.findViewById(R.id.imageviewprofil);
        imageviewprofil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setDimAmount(0.7f);
                dialog.setContentView(R.layout.profilresmi);
                ImageView profilimagee = (ImageView) dialog.findViewById(R.id.fataa);
                profilimagee.setImageBitmap(karsiresim);
                dialog.show();
            }
        });
        textviewokul = (TextView) view.findViewById(R.id.textView18);
        textviewisim = (TextView) view.findViewById(R.id.textView);
        textviewisim.setText(isim);
        textviewnick = (TextView) view.findViewById(R.id.textView3);
        textviewnick.setText(sharedPrefNickAl());
        ImageButton nickdegistir = (ImageButton) view.findViewById(R.id.imageButton7);
        nickdegistir.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), NickDegistir.class);
                startActivity(i);
            }
        });
        TextView textviewcinsiyet = (TextView) view.findViewById(R.id.textView22);
        String cinso = sharedPrefCinsiyetAl();
        if(cinso.equals("female")||cinso.equals("Female")){
            textviewcinsiyet.setText("Kadın");
        }else if(cinso.equals("male")||cinso.equals("Male")){
            textviewcinsiyet.setText("Erkek");
        }else{
            textviewcinsiyet.setText("Others");
        }
        textviewaciklama = (TextView) view.findViewById(R.id.textviewaciklama);
        String durum = sharedPrefDurumAl();
        if (!durum.equals("defaultdurum")) {
            textviewaciklama.setText(durum);
        }
        ImageButton aciklamadegistir = (ImageButton) view.findViewById(R.id.imageButton8);
        aciklamadegistir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AciklamaDegistir.class);
                startActivity(i);
            }
        });
        /*editTextaciklama.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return yaziyiyerlestir();
                }
                return false;
            }
        });
        */
        switch1 = (Switch) view.findViewById(R.id.switch1);
        boolean acikmi = sharedBildirimlerAl();
        if (acikmi) {
            switch1.setChecked(true);
        } else {
            switch1.setChecked(false);
        }
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedBildirimlerKaydet(true);
                } else {
                    sharedBildirimlerKaydet(false);
                }
            }
        });


        ImageView imageviewcikis = (ImageView) view.findViewById(R.id.imageView4);
        imageviewcikis.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                kullanicicikti = true;
                SharedPrefKullaniciCiktiKaydet(kullanicicikti);
                sharedPrefMainDurumKaydet(false);
                getActivity().finish();
            }
        });
        ImageView imageviewayril = (ImageView) view.findViewById(R.id.imageButton2);
        imageviewayril.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("tago", "basildi");
                new AlertDialog.Builder(getActivity())
                        .setTitle("Hesabı Sil")
                        .setMessage("Hesabınızı silmek istediğinize eminmisiniz ?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ServerHesabiSil sHS = new ServerHesabiSil(sharedPrefIdAl());
                                sHS.execute();
                                LoginManager.getInstance().logOut();
                                kullanicicikti = true;
                                SharedPrefKullaniciCiktiKaydet(kullanicicikti);
                                SharedPrefIdSıfırla();
                                sharedfacebookIDsıfırla();
                                sharedPrefMainDurumKaydet(false);
                                getActivity().finish();

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void sharedPrefMainDurumKaydet(boolean b) {
        SharedPreferences sP = getActivity().getSharedPreferences("programisleyis", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putBoolean("mainiyasat", b);
        editor.apply();
    }

    private void SharedPrefIdSıfırla() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("serverid", "defaultserverid");
        editor.apply();
    }

    private void sharedfacebookIDsıfırla() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("facebookID", "defaultfacebookID");
        editor.apply();
    }

    private boolean sharedBildirimlerAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getBoolean("bildirimler", true);
    }

    private boolean yaziyiyerlestir() {
        editTextaciklama.setText(editTextaciklama.getText());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        editTextaciklama.clearFocus();
        sharedPrefDurumKaydet(editTextaciklama.getText().toString());
        ServerKullaniciDurum sKD = new ServerKullaniciDurum(editTextaciklama.getText().toString());
        String veritabaniid = sharedPrefIdAl();
        sKD.execute(veritabaniid);
        return true;
    }

    private void coverphotocek() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    if (object != null) {
                        yenicoverfotourl = object.getJSONObject("cover").getString("source");
                        ServerCoverPhotoUrlGonder sCPUG = new ServerCoverPhotoUrlGonder(sharedPrefIdAl(), yenicoverfotourl);
                        sCPUG.execute();
                        Log.i("tago", "yenicover1" + yenicoverfotourl);
                        UrldenCoverPhoto uCP = new UrldenCoverPhoto(yenicoverfotourl);
                        uCP.execute();
                    } else {
                        imageviewkapak.setBackgroundResource(R.mipmap.bos_kapak);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "cover");
        request.setParameters(parameters);
        request.executeAsync();
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

    public Bitmap blur(Bitmap image) {
        final float BLUR_RADIUS = 20f;
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(getActivity());
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public Bitmap antiblur(Bitmap image, float radius) {
        final float BLUR_RADIUS = radius;
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(getActivity());
        Allocation tmpIn = Allocation.createFromBitmap(renderScript, image);
        Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);
        //Intrinsic Gausian blur filter
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);
        return outputBitmap;
    }

    public class UrldenResim extends AsyncTask<String, Void, Bitmap> {

        Bitmap bitmap;

        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                if (bitmap == null) {
                    Log.i("tago", "PageFragment3 bitmap yok");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            karsiresim = bitmap;
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            Bitmap bitmape = getCircleBitmap(bitmap);
            imageviewprofil.setImageBitmap(bitmape);
        }
    }

    public class UrldenCoverPhoto extends AsyncTask<String, Void, Bitmap> {

        Bitmap bitmap;
        String yenicoverfotourl;

        public UrldenCoverPhoto(String yenicoverfotourl) {
            this.yenicoverfotourl = yenicoverfotourl;
            Log.i("tago", "yenicover" + yenicoverfotourl);
        }

        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(yenicoverfotourl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                if (bitmap == null) {
                    Log.i("tago", "PageFragment3 bitmap yok");
                } else {
                    Log.i("tago", "PageFragment 2 bitmap var");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            karsicover = bitmap;
            Bitmap karsicover1 = karsicover;
            Bitmap karsicover2 = karsicover;

            scaledcover = Bitmap.createScaledBitmap(karsicover1, 1080, 660, false);
            bluredcover = blur(scaledcover);
            scaledhalfcover = Bitmap.createScaledBitmap(karsicover2, 1080, 660, false);
            halfbluredcover = antiblur(scaledhalfcover, 2f);

            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.i("tago", "bitmap bos degil");
                Bitmap bitmape = Bitmap.createScaledBitmap(bitmap, 1080, 660, false);
                Bitmap blurbitmap = blur(bitmape);
                imageviewkapak.setImageBitmap(blurbitmap);
            }
        }
    }

    public class ServerKullaniciDurum extends AsyncTask<String, Void, String> {
        String durum;
        String query, charset;

        public ServerKullaniciDurum(String durum) {
            this.durum = durum;
            charset = "UTF-8";
            String param1 = "id";
            String param2 = "status";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                Log.i("tago", "durum" + durum);
                Log.i("tago", "durum encode " + URLEncoder.encode(durum, charset));
                connection = new URL("http://185.22.187.60/shappy/my_status?id="
                        + params[0] + "&status=" + URLEncoder.encode(durum, charset)).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output;
            try {
                output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream inputstream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "calaba";
        }
    }

    public class ServerHesabiSil extends AsyncTask<String, Void, String> {

        String serverid;
        String query, charset;

        public ServerHesabiSil(String serverid) {
            this.serverid = serverid;
            charset = "UTF-8";
            String param1 = "id";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/delete_user.php?id=" + serverid).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output;
            try {
                output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream inputstream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "talaba";
        }
    }

    public class ServerCoverPhotoUrlGonder extends AsyncTask<String, Void, String> {

        String serverid, coverurl;
        String query, charset;

        public ServerCoverPhotoUrlGonder(String serverid, String coverurl) {
            this.serverid = serverid;
            this.coverurl = coverurl;
            charset = "UTF-8";
            String param1 = "id";
            String param2 = "coverurl";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/add_cover.php?id=" + serverid + "&coverURL=" + coverurl).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            OutputStream output;
            try {
                output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                InputStream inputstream = connection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "talaba";
        }
    }
}
