package droxoft.armin.com.shappy;

import android.app.Activity;
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
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class KarsiProfil extends Activity {

    String karsifaceprofil,isim,durum,okul,cinsiyet,yas,coverfotourl;

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
        ImageView imageprofil = (ImageView) findViewById(R.id.imageView11);
        textviewisim.setText(isim + ", " + yas);
        textviewdurum.setText(durum);
        textviewokul.setText(okul);
        if(cinsiyet.equals("m")){
            textviewcinsiyet.setText("Erkek");
        }else if(cinsiyet.equals("f")){
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
        new urldenResimm(imageprofil).execute(karsifaceprofil);
        ImageView imageviewkapak = (ImageView) findViewById(R.id.imageView10);
        new urldenCover(imageviewkapak).execute(coverfotourl);
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
                return icon;
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                return icon;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return icon;
        }

        protected void onPostExecute(Bitmap bitmap) {
            Drawable d = new BitmapDrawable(getResources(),bitmap);
            imageview.setBackground(d);
        }
    }
}
