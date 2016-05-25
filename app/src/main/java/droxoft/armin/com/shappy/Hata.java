package droxoft.armin.com.shappy;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by bahadır on 6.5.2016.
 */
public class Hata extends Activity {

    protected void onCreate(Bundle bundle){
        super.onCreate(bundle);
        Log.i("tago" , "hataya geldi");
        setContentView(R.layout.hata);
    }
}
