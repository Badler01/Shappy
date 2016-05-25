package droxoft.armin.com.shappy;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class AciklamaDegistir extends Activity {


    private void sharedPrefDurumKaydet(String nick) {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("durum", nick);
        editor.apply();
    }

    private String sharedPrefIdAl(){
        SharedPreferences sP = getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        return sP.getString("serverid" , "defaultserverid");
    }

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.aciklamadegistir);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final TextView tv1 = (TextView) findViewById(R.id.textView10);
        final EditText et = (EditText) findViewById(R.id.editText4);
        ImageButton btn = (ImageButton) findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String durum = et.getText().toString();
                sharedPrefDurumKaydet(durum);
                String id = sharedPrefIdAl();
                ServerDurumKaydet sDK = new ServerDurumKaydet(durum);
                sDK.execute(id);
                finish();
            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String durum = et.getText().toString();
                    sharedPrefDurumKaydet(durum);
                    String id = sharedPrefIdAl();
                    ServerDurumKaydet sDK = new ServerDurumKaydet(durum);
                    sDK.execute(id);
                    finish();
                    return true;
                }
                return false;
            }
        });
        et.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv1.setText(String.valueOf(150-count));
            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    public class ServerDurumKaydet extends AsyncTask<String,Void,String>{
        String durum;
        String query, charset;

        public ServerDurumKaydet(String durum) {
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
                connection = new URL("http://185.22.184.15/shappy/my_status?id="
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
}
