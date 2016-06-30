package droxoft.armin.com.shappy;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

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

public class AnaAkim extends AppCompatActivity {

    boolean ilkgiris;
    ViewPager viewPager;
    RelativeLayout ustbarlogolay;
    RelativeLayout ustbararamalay;
    ImageButton minikbaykussimge;
    ImageView shappylogo;
    ImageButton ilkaramabutonu;
    ImageButton geridonmebutonu;
    EditText aramaalani;
    ImageButton ikinciaramabutonu;
    ImageView imageviewnickvarmi;



    private void sharedPrefNickKaydet(String nick) {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("nick", nick);
        editor.apply();
    }

    private void sharedPrefOkulKaydet(String okul) {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("okul", okul);
        editor.apply();
    }

    private void SharedPrefBosResimPathKaydet() {
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
        if (okunabilir && yazilabilir) {
            File root = Environment.getExternalStorageDirectory();
            File shappy = new File(root, "Shappy");
            File konusulanresimler = new File(shappy, "Pictures");
            if (!konusulanresimler.exists()) {
                konusulanresimler.mkdirs();
            }
            File a = new File(konusulanresimler, "unknown.jpeg");
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.mipmap.bilinmeyen);
            FileOutputStream fOS;
            try {
                fOS = new FileOutputStream(a);
                b.compress(Bitmap.CompressFormat.JPEG, 100, fOS);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            SharedPreferences sP = getSharedPreferences("programisleyis", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sP.edit();
            editor.putString("bilinmeyenresimpath", a.getAbsolutePath());
            editor.apply();
        }
    }

    private String SharedPrefIdAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("serverid", "defaultserverid");
    }

    protected void onCreate(Bundle bambam) {
        super.onCreate(bambam);
        Intent i = getIntent();
        ilkgiris = i.getBooleanExtra("ilkgiris", true);
        if (ilkgiris) {
            final Dialog dialog = new Dialog(AnaAkim.this, R.style.DialogThemeFull);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.bilgi);
            dialog.getWindow().setDimAmount(0.7f);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            imageviewnickvarmi = (ImageView) dialog.findViewById(R.id.imageView27);
            final EditText edittextnick = (EditText) dialog.findViewById(R.id.editText5);
            final EditText edittextokul = (EditText) dialog.findViewById(R.id.editText6);
            edittextnick.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        if (edittextnick.getText().toString().length() > 2) {
                            ServerNickKontrol sNK = new ServerNickKontrol(edittextnick.getText().toString());
                            sNK.execute();
                        }
                    }
                }
            });
            ImageButton butonhemenbasla = (ImageButton) dialog.findViewById(R.id.imageButton10);
            butonhemenbasla.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!edittextnick.getText().toString().equals("") && !edittextokul.getText().toString().equals("")) {
                        if (edittextnick.getText().toString().length() < 3) {
                            Toast.makeText(AnaAkim.this, "En az 3 haneli bir nickname giriniz", Toast.LENGTH_SHORT).show();
                        } else {
                            sharedPrefNickKaydet(edittextnick.getText().toString());
                            sharedPrefOkulKaydet(edittextokul.getText().toString());
                            PageFragment2.SharedPrefNickYerlestir(AnaAkim.this);
                            PageFragment2.SharedPrefOkulYerlestir(AnaAkim.this);
                            ServerButunculBilgileriGonder sBBG = new ServerButunculBilgileriGonder(edittextnick.getText().toString(),
                                    edittextokul.getText().toString());
                            sBBG.execute();
                            dialog.cancel();
                        }
                    }
                }
            });
            dialog.show();
        }
        setContentView(R.layout.anaakim);
        sayfaduzeni();
        aramaislemi();
        SharedPrefBosResimPathKaydet();
    }

    protected void onStop() {
        super.onStop();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Intent a = getIntent();
        if (a.getStringExtra("intentname").equals("PushReceiverNoti")) {
            DatabaseClassNotification dCN = new DatabaseClassNotification(this);
            dCN.open();
            dCN.deleteAll();
            dCN.close();
            viewPager.setCurrentItem(0);
            PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            frag1.konusmalarimButonuIkili();
        } else if (a.getStringExtra("intentname").equals("PushReceiverKanal")) {
            DatabaseClassNotificationGrup dcng = new DatabaseClassNotificationGrup(this);
            dcng.open();
            dcng.deleteAll();
            dcng.close();
            viewPager.setCurrentItem(1);
            PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            frag2.konustugumkanallar();
        } else if (a.getStringExtra("intentname").equals("PushReceiverLeave")) {

        } else if (a.getStringExtra("intentname").equals("PushReceiverBan")) {

        }
    }

    private void sayfaduzeni() {
        ustbarlogolay = (RelativeLayout) findViewById(R.id.ustbarlogolay);
        ustbararamalay = (RelativeLayout) findViewById(R.id.ustbararamalay);
        minikbaykussimge = (ImageButton) findViewById(R.id.minikbaykussimge);
        shappylogo = (ImageView) findViewById(R.id.shappylogo);
        ilkaramabutonu = (ImageButton) findViewById(R.id.aramabutonu);
        geridonmebutonu = (ImageButton) findViewById(R.id.geridonmebutonu);
        aramaalani = (EditText) findViewById(R.id.aramaalani);
        ikinciaramabutonu = (ImageButton) findViewById(R.id.ikinciaramabutonu);
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager()));
        tabStrip.setViewPager(viewPager);
        final LinearLayout view = (LinearLayout) tabStrip.getChildAt(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                if (position == 0) {
                    ImageButton imageButton = (ImageButton) view.getChildAt(position);
                    imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fire_tr));
                    ImageButton imageButton1 = (ImageButton) view.getChildAt(1);
                    imageButton1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.genelsoh_gr));
                    ImageButton imageButton2 = (ImageButton) view.getChildAt(2);
                    imageButton2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.profile_gr));
                } else if (position == 1) {
                    ImageButton imageButton = (ImageButton) view.getChildAt(position);
                    imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.genelsoh_tr));
                    ImageButton imageButton1 = (ImageButton) view.getChildAt(0);
                    imageButton1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fire_gr));
                    ImageButton imageButton2 = (ImageButton) view.getChildAt(2);
                    imageButton2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.profile_gr));
                } else if (position == 2) {
                    ImageButton imageButton = (ImageButton) view.getChildAt(position);
                    imageButton.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.profile_tr));
                    ImageButton imageButton1 = (ImageButton) view.getChildAt(0);
                    imageButton1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.fire_gr));
                    ImageButton imageButton2 = (ImageButton) view.getChildAt(1);
                    imageButton2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.genelsoh_gr));
                }
            }

            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void aramaislemi() {
        ilkaramabutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                aramaalani.setText("");
                Thread a = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Animation animationCevirA = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevira);
                                ustbarlogolay.startAnimation(animationCevirA);
                            }
                        });
                        try {
                            sleep(220);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ustbarlogolay.setVisibility(View.GONE);
                                minikbaykussimge.setVisibility(View.GONE);
                                shappylogo.setVisibility(View.GONE);
                                ilkaramabutonu.setVisibility(View.GONE);
                                ustbararamalay.setVisibility(View.VISIBLE);
                                geridonmebutonu.setVisibility(View.VISIBLE);
                                aramaalani.setVisibility(View.VISIBLE);
                                ikinciaramabutonu.setVisibility(View.VISIBLE);
                                Animation animationCevirB = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevirb);
                                ustbararamalay.startAnimation(animationCevirB);
                            }
                        });
                    }
                };
                a.start();
                InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                aramaalani.requestFocus();
                iMM.showSoftInput(aramaalani, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        geridonmebutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread b = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Animation gericevira = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevira);
                                ustbararamalay.startAnimation(gericevira);
                            }
                        });
                        try {
                            sleep(220);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ustbararamalay.setVisibility(View.GONE);
                                geridonmebutonu.setVisibility(View.GONE);
                                ikinciaramabutonu.setVisibility(View.GONE);
                                aramaalani.setVisibility(View.GONE);
                                ustbarlogolay.setVisibility(View.VISIBLE);
                                minikbaykussimge.setVisibility(View.VISIBLE);
                                shappylogo.setVisibility(View.VISIBLE);
                                ilkaramabutonu.setVisibility(View.VISIBLE);
                                Animation gericevirb = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevirb);
                                ustbarlogolay.startAnimation(gericevirb);
                            }
                        });
                    }
                };
                b.start();
                View view = getCurrentFocus();
                InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
                view.clearFocus();
                aramaalani.setText("");
                if (viewPager.getCurrentItem() == 0) {
                    PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag1.aramaYap(null);
                } else if (viewPager.getCurrentItem() == 1) {
                    PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag2.aramaYap(null);
                }
            }
        });
        ikinciaramabutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread b = new Thread() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Animation gericevira = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevira);
                                ustbararamalay.startAnimation(gericevira);
                            }
                        });
                        try {
                            sleep(220);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                ustbararamalay.setVisibility(View.GONE);
                                geridonmebutonu.setVisibility(View.GONE);
                                ikinciaramabutonu.setVisibility(View.GONE);
                                aramaalani.setVisibility(View.GONE);
                                ustbarlogolay.setVisibility(View.VISIBLE);
                                minikbaykussimge.setVisibility(View.VISIBLE);
                                shappylogo.setVisibility(View.VISIBLE);
                                ilkaramabutonu.setVisibility(View.VISIBLE);
                                Animation gericevirb = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevirb);
                                ustbarlogolay.startAnimation(gericevirb);
                            }
                        });
                    }
                };
                b.start();
                View view = getCurrentFocus();
                InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
                view.clearFocus();
                String a = aramaalani.getText().toString();
                if (viewPager.getCurrentItem() == 0) {
                    PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag1.aramaYap(a);
                } else if (viewPager.getCurrentItem() == 1) {
                    PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag2.aramaYap(a);
                }
            }
        });
        aramaalani.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (viewPager.getCurrentItem() == 0) {
                    PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag1.aramaYap(String.valueOf(s));
                } else if (viewPager.getCurrentItem() == 1) {
                    PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                    frag2.aramaYap(String.valueOf(s));
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });
        aramaalani.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String arananyazi = aramaalani.getText().toString();
                    if (viewPager.getCurrentItem() == 0) {
                        PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        frag1.aramaYap(String.valueOf(arananyazi));
                    } else if (viewPager.getCurrentItem() == 1) {
                        PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                        frag2.aramaYap(String.valueOf(arananyazi));
                    }
                    Thread b = new Thread() {
                        public void run() {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Animation gericevira = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevira);
                                    ustbararamalay.startAnimation(gericevira);
                                }
                            });
                            try {
                                sleep(370);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ustbararamalay.setVisibility(View.GONE);
                                    geridonmebutonu.setVisibility(View.GONE);
                                    ikinciaramabutonu.setVisibility(View.GONE);
                                    aramaalani.setVisibility(View.GONE);
                                    ustbarlogolay.setVisibility(View.VISIBLE);
                                    minikbaykussimge.setVisibility(View.VISIBLE);
                                    shappylogo.setVisibility(View.VISIBLE);
                                    ilkaramabutonu.setVisibility(View.VISIBLE);
                                    Animation gericevirb = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevirb);
                                    ustbarlogolay.startAnimation(gericevirb);
                                }
                            });
                        }
                    };
                    b.start();
                    View view = getCurrentFocus();
                    InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                }
                return false;
            }
        });
    }

    public void onBackPressed() {
        if (shappylogo.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        } else {
            Thread b = new Thread() {
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Animation gericevira = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevira);
                            ustbararamalay.startAnimation(gericevira);
                        }
                    });
                    try {
                        sleep(220);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ustbararamalay.setVisibility(View.GONE);
                            geridonmebutonu.setVisibility(View.GONE);
                            ikinciaramabutonu.setVisibility(View.GONE);
                            aramaalani.setVisibility(View.GONE);
                            ustbarlogolay.setVisibility(View.VISIBLE);
                            minikbaykussimge.setVisibility(View.VISIBLE);
                            shappylogo.setVisibility(View.VISIBLE);
                            ilkaramabutonu.setVisibility(View.VISIBLE);
                            Animation gericevirb = AnimationUtils.loadAnimation(AnaAkim.this, R.anim.ustbaricevirb);
                            ustbarlogolay.startAnimation(gericevirb);
                        }
                    });
                }
            };
            b.start();
            View view = getCurrentFocus();
            InputMethodManager iMM = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            iMM.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
            aramaalani.setText("");
            if (viewPager.getCurrentItem() == 0) {
                PageFragment0 frag1 = (PageFragment0) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                frag1.aramaYap(null);
            } else if (viewPager.getCurrentItem() == 1) {
                PageFragment1 frag2 = (PageFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                frag2.aramaYap(null);
            }
        }
    }

    protected void onDestroy() {
        DatabaseClassNotification dCN = new DatabaseClassNotification(this);
        dCN.open();
        dCN.deleteAll();
        dCN.close();
        super.onDestroy();
    }

    private class ServerButunculBilgileriGonder extends AsyncTask<String, Void, String> {

        String nick, okul;
        String query, charset;

        public ServerButunculBilgileriGonder(String nick, String okul) {
            charset = "UTF-8";
            String param1 = "nick";
            String param2 = "okul";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.nick = nick;
            this.okul = okul;
        }

        protected String doInBackground(String... params) {
            String serverid = SharedPrefIdAl();
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.187.60/shappy/update_born.php?id=" + serverid +
                        "&nick=" + nick + "&okul=" + okul).openConnection();
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
            return "qalaba";
        }
    }

    private class ServerNickKontrol extends AsyncTask<String, Void, String> {

        String nickname;
        String charset, query;

        public ServerNickKontrol(String nickname) {
            charset = "UTF-8";
            String param1 = "nickname";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            this.nickname = nickname;
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.187.60/shappy/nickserbest.php?nick=" + nickname).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "* /*");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            OutputStream output;
            try {
                output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputline=in.readLine();
                    if(inputline.equals("var")){
                        AnaAkim.this.runOnUiThread(new Runnable() {
                            public void run() {
                                imageviewnickvarmi.setVisibility(View.VISIBLE);
                                imageviewnickvarmi.setImageResource(R.mipmap.carpi);
                            }
                        });
                    }else if(inputline.equals("yok")){
                        AnaAkim.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if(imageviewnickvarmi.getVisibility()==View.INVISIBLE){

                                }else{
                                    imageviewnickvarmi.setImageResource(R.mipmap.tik);
                                }
                            }
                        });
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "qalaba";
        }
    }
}
