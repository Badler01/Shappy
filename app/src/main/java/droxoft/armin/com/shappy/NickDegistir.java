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

public class NickDegistir extends Activity {

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.nickdegistir);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        final TextView tv1 = (TextView) findViewById(R.id.textView10);
        final EditText et = (EditText) findViewById(R.id.editText4);
        ImageButton btn = (ImageButton) findViewById(R.id.button3);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nick = et.getText().toString();
                sharedPrefNickKaydet(nick);
                String id = sharedPrefIdAl();
                ServerNickKaydet sNK = new ServerNickKaydet(nick);
                sNK.execute(id);
                finish();
            }
        });
        et.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String nick = et.getText().toString();
                    sharedPrefNickKaydet(nick);
                    String id = sharedPrefIdAl();
                    ServerNickKaydet sNK = new ServerNickKaydet(nick);
                    sNK.execute(id);
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
                tv1.setText(String.valueOf(20-count));
            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    private String sharedPrefIdAl() {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        return sP.getString("serverid" , "defaultserverid");
    }

    private void sharedPrefNickKaydet(String nick) {
        SharedPreferences sP = getSharedPreferences("kullaniciverileri" , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sP.edit();
        editor.putString("nick" , nick);
        editor.apply();
    }

    public class ServerNickKaydet extends AsyncTask<String,Void,String>{
        String nick;
        String query, charset;

        public ServerNickKaydet(String nick) {
            this.nick = nick;
            charset = "UTF-8";
            String param1 = "id";
            String param2 = "nick";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            URLConnection connection = null;
            try {
                connection = new URL("http://185.22.184.15/shappy/update_status.php?id="+params[0]+"&nick="+nick+"&bildir=0&placename=ture")
                        .openConnection();
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
            return "palaba";
        }
    }
}
