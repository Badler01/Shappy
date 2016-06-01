package droxoft.armin.com.shappy;


import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GrupMesajArrayAdapter extends ArrayAdapter<GrupMesaj> {

    public TextView tv1, tv2, tv3;
    public List<GrupMesaj> MesajListesi = new ArrayList<>();
    public RelativeLayout layout1;
    public LinearLayout tasiyicilay;

    public GrupMesajArrayAdapter(Context applicationContext, int mesaj, List<GrupMesaj> mesajListesi) {
        super(applicationContext, mesaj, mesajListesi);
    }

    public void add(GrupMesaj grupMesaj) {
        MesajListesi.add(grupMesaj);
        super.add(grupMesaj);
    }

    public int getCount() {
        return this.MesajListesi.size();
    }

    public GrupMesaj getItem(int index) {
        return this.MesajListesi.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.grupmesaj, parent, false);
        }

        layout1 = (RelativeLayout) v.findViewById(R.id.mesajlayout);
        tasiyicilay = (LinearLayout) v.findViewById(R.id.tasiyicilay);
        GrupMesaj mesajobj = getItem(position);
        tv1 = (TextView) v.findViewById(R.id.textView7);
        tv2 = (TextView) v.findViewById(R.id.textView19);
        tv3 = (TextView) v.findViewById(R.id.textView24);
        tv1.setTextColor(mesajobj.side ? Color.BLACK : Color.WHITE);
        tv1.setText(mesajobj.mesac);
        tv2.setTextColor(mesajobj.side ? Color.GRAY : Color.WHITE);
        tv2.setText(mesajobj.date);
        tv3.setTextColor(mesajobj.side ? Color.BLACK : Color.WHITE);
        tv3.setText(mesajobj.nick);
        if(mesajobj.renkkatalogu=='a' || mesajobj.renkkatalogu=='b'){
            tv3.setTextColor(Color.argb(255,86,17,34));
        }else if(mesajobj.renkkatalogu=='c' || mesajobj.renkkatalogu=='d'){
            tv3.setTextColor(Color.argb(255,214,58,23));
        }else if(mesajobj.renkkatalogu =='e' || mesajobj.renkkatalogu =='f'){
            tv3.setTextColor(Color.argb(255,123,42,134));
        }else if(mesajobj.renkkatalogu == 'g' || mesajobj.renkkatalogu == 'h'){
            tv3.setTextColor(Color.argb(255,68,172,52));
        }else if(mesajobj.renkkatalogu == 'k' || mesajobj.renkkatalogu == 'l'){
            tv3.setTextColor(Color.argb(255,43,182,188));
        }else if(mesajobj.renkkatalogu == 'i' || mesajobj.renkkatalogu =='ı'){
            tv3.setTextColor(Color.argb(255,96,41,22));
        }else if(mesajobj.renkkatalogu == 'm' || mesajobj.renkkatalogu == 'n'){
            tv3.setTextColor(Color.argb(255,228,31,33));
        }else if(mesajobj.renkkatalogu == 'o' || mesajobj.renkkatalogu == 'ö'){
            tv3.setTextColor(Color.argb(255,42,75,155));
        }else if(mesajobj.renkkatalogu == 'p' || mesajobj.renkkatalogu == 'r'){
            tv3.setTextColor(Color.argb(255,15,105,89));
        }else if(mesajobj.renkkatalogu == 's' || mesajobj.renkkatalogu == 'ş'){
            tv3.setTextColor(Color.argb(255,24,58,29));
        }else{
            tv3.setTextColor(Color.CYAN);
        }
        layout1.setBackgroundResource(mesajobj.side ? R.drawable.yenibeyazbalon : R.drawable.turkuaz);
        tasiyicilay.setGravity(mesajobj.side ? Gravity.LEFT : Gravity.RIGHT);
        return v;
    }
}
