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

public class MesajArrayAdapter extends ArrayAdapter<Mesaj> {

    public TextView tv1,tv2;
    public List<Mesaj> MesajListesi = new ArrayList();
    public RelativeLayout altlay;
    public LinearLayout tasiyicilay;

    public MesajArrayAdapter(Context applicationContext, int mesaj, List<Mesaj> mesajListesi) {
        super(applicationContext , mesaj , mesajListesi);
    }

    public void add(Mesaj mesaj){
        MesajListesi.add(mesaj);
        super.add(mesaj);
    }

    public int getCount(){
        return this.MesajListesi.size();
    }

    public Mesaj getItem(int index){
        return this.MesajListesi.get(index);
    }

    public View getView(int position , View convertView , ViewGroup parent){
        View v = convertView;
        if(v==null){
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.mesaj , parent, false);
        }

        tasiyicilay = (LinearLayout) v.findViewById(R.id.tasiyicilay);
        altlay = (RelativeLayout) v.findViewById(R.id.layu);
        Mesaj mesajobj = getItem(position);
        tv1=(TextView) v.findViewById(R.id.textView7);
        tv2 = (TextView) v.findViewById(R.id.textView19);
        tv1.setTextColor(mesajobj.side ? Color.BLACK : Color.WHITE);
        tv1.setText(mesajobj.mesac);
        tv2.setTextColor(mesajobj.side ? Color.GRAY : Color.WHITE);
        tv2.setText(mesajobj.date);
        altlay.setBackgroundResource(mesajobj.side ? R.drawable.beyazbalon : R.drawable.turkuaz);
        tasiyicilay.setGravity(mesajobj.side ? Gravity.LEFT : Gravity.RIGHT);
        return v;
    }
}

