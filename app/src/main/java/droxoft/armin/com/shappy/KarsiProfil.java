package droxoft.armin.com.shappy;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class KarsiProfil extends Activity {

    String karsifaceprofil,isim,durum,okul,cinsiyet,yas,burc,coverfotourl;
    ImageView ivkapak_onu;
    ImageView imageprofil;
    ImageView imageviewkapak;
    Bitmap scaledcover;
    Bitmap bluredcover;
    Bitmap halfbluredcover;
    Bitmap scaledhalfcover;
    Bitmap karsiresim;

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.altprofil);
        Intent i = getIntent();
        karsifaceprofil = i.getStringExtra("karsifaceprofilurl");
        isim = i.getStringExtra("isim");
        durum = i.getStringExtra("durum");
        cinsiyet = i.getStringExtra("cinsiyet");
        okul = i.getStringExtra("okul");
        yas = i.getStringExtra("yas");
        burc = i.getStringExtra("burc");
        coverfotourl = i.getStringExtra("coverfotourl");
        tanimlar();
    }

    private void tanimlar() {
        TextView textviewisim = (TextView) findViewById(R.id.textView);
        TextView textviewdurum = (TextView) findViewById(R.id.editText10);
        TextView textviewokul = (TextView) findViewById(R.id.textView18);
        TextView textviewcinsiyet = (TextView) findViewById(R.id.textView22);
        TextView textviewustbarisim = (TextView) findViewById(R.id.textView5);
        ImageButton imagebutongeri = (ImageButton) findViewById(R.id.imageButton9);
        imageprofil = (ImageView) findViewById(R.id.imageView11);
        new urldenResimm(imageprofil).execute(karsifaceprofil);
        imageprofil.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Dialog dialog = new Dialog(KarsiProfil.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setDimAmount(0.7f);
                dialog.setContentView(R.layout.profilresmi);
                ImageView profilimagee = (ImageView) dialog.findViewById(R.id.fataa);
                if(karsiresim != null){
                   Log.i("tago", "karsı resim var");
                }
                profilimagee.setImageBitmap(karsiresim);
                dialog.show();
            }
        });
        textviewisim.setText(isim + ", " + yas);
        textviewdurum.setText(durum);
        textviewokul.setText(okul);
        if(cinsiyet.equals("m") || cinsiyet.equals("M") || cinsiyet.equals("Male") ||cinsiyet.equals("male") ){
            textviewcinsiyet.setText("Erkek");
        }else if(cinsiyet.equals("f")||cinsiyet.equals("F")||cinsiyet.equals("Female")||cinsiyet.equals("female")){
            textviewcinsiyet.setText("Kadın");
        }else{
            textviewcinsiyet.setText("Others");
        }
        textviewustbarisim.setText(isim + ", " + yas);
        imagebutongeri.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        imageviewkapak = (ImageView) findViewById(R.id.imageView10);
        new urldenCover(imageviewkapak).execute(coverfotourl);
        ivkapak_onu = (ImageView) findViewById(R.id.imageView12);
        final RelativeLayout CoverFotoLay = (RelativeLayout) findViewById(R.id.relativeLayout4);
        imageviewkapak.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {

                int action = arg1.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    CoverFotoLay.bringToFront();
                    CoverFotoLay.invalidate();
                    imageviewkapak.setImageBitmap(halfbluredcover);
                    return true;
                }

                if (action == MotionEvent.ACTION_MOVE) {

                }

                if (action == MotionEvent.ACTION_CANCEL) {
                    ivkapak_onu.bringToFront();
                    ivkapak_onu.invalidate();
                    imageprofil.bringToFront();
                    imageprofil.invalidate();
                    imageviewkapak.setImageBitmap(bluredcover);
                }

                if (action == MotionEvent.ACTION_UP) {
                    ivkapak_onu.bringToFront();
                    ivkapak_onu.invalidate();
                    imageprofil.bringToFront();
                    imageprofil.invalidate();
                    imageviewkapak.setImageBitmap(bluredcover);
                    return true;
                }


                return false;

            }});
        ImageView imageviewburc = (ImageView) findViewById(R.id.imageView28);
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

    public class urldenResimm extends AsyncTask<String, Void, Bitmap> {

        ImageView imageview;
        public urldenResimm(ImageView imageview) {
            this.imageview = imageview;
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

            } catch (IOException e) {
                e.printStackTrace();
            }
            karsiresim = icon;
            return icon;
        }

        protected void onPostExecute(Bitmap bitmap) {
            Bitmap bs = getCircleBitmap(bitmap);
            imageview.setImageBitmap(bs);
        }
    }

    public class urldenCover extends AsyncTask<String,Void,Bitmap>{
        ImageView imageview;
        public urldenCover(ImageView imageview)  {
            this.imageview = imageview;
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

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (icon != null) {
                scaledcover = Bitmap.createScaledBitmap(icon, 1080, 600, false);
                scaledhalfcover = Bitmap.createScaledBitmap(icon, 1080, 600, false);
                bluredcover = blur(scaledcover);
                halfbluredcover = antiblur(scaledhalfcover, 2f);
            }
            return icon;
        }

        protected void onPostExecute(Bitmap bitmap) {
            Bitmap a = blur(bitmap);
            Drawable d = new BitmapDrawable(getResources(),a);
            imageview.setBackground(d);


        }
    }

    public Bitmap blur(Bitmap image) {
        final float BLUR_RADIUS = 20f;
        if (null == image) return null;
        Bitmap outputBitmap = Bitmap.createBitmap(image);
        final RenderScript renderScript = RenderScript.create(KarsiProfil.this);
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
        final RenderScript renderScript = RenderScript.create(this);
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

}
