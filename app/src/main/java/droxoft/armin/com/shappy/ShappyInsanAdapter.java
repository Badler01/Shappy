package droxoft.armin.com.shappy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;

import java.io.File;
import java.util.ArrayList;

public class ShappyInsanAdapter extends ArrayAdapter<Insan> {
    ArrayList<Insan> objects;
    ArrayList<Insan> kokobjects;
    int resource;
    Context context;
    LayoutInflater lala;
    private DisplayImageOptions options;
    Filter insanfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = kokobjects;
                results.count = kokobjects.size();
            } else {
                ArrayList<Insan> sonuclistesi = new ArrayList<>();
                for (Insan i : kokobjects) {
                    if (i.getName().toLowerCase().contains(constraint.toString())) {
                        sonuclistesi.add(i);
                    }
                }
                results.values = sonuclistesi;
                results.count = sonuclistesi.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                objects = (ArrayList<Insan>) results.values;
                notifyDataSetChanged();
            }
        }
    };

    public ShappyInsanAdapter(Context context, int resource, ArrayList<Insan> objects) {
        super(context, resource, objects);
        this.objects = objects;
        kokobjects = objects;
        this.resource = resource;
        this.context = context;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ins)
                .showImageForEmptyUri(R.mipmap.soruisareti)
                .showImageOnFail(R.mipmap.soruisareti)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new CircleBitmapDisplayer(Color.WHITE, 0))
                .build();
        lala = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final InsanHolder insanholder;
        final int pozisyon = position;
        if (convertView == null) {
            convertView = lala.inflate(resource, null);
            insanholder = new InsanHolder();
            insanholder.insanresmi = (ImageView) convertView.findViewById(R.id.imgIcon);
            insanholder.insanadi = (TextView) convertView.findViewById(R.id.txtTitle);
            insanholder.insandurumu = (TextView) convertView.findViewById(R.id.textView20);
            insanholder.esasbolge = (LinearLayout) convertView.findViewById(R.id.esasbolge);
            insanholder.yenimesajvarmi = (FrameLayout) convertView.findViewById(R.id.yenimesajvarmi);
            insanholder.yenimesajsayisi = (TextView) convertView.findViewById(R.id.kacyenimesaj);
            insanholder.banlananinsanarkaplan = (ImageView) convertView.findViewById(R.id.imgIcon3);
            insanholder.banlananinsanicon = (ImageView) convertView.findViewById(R.id.imageView3);
            convertView.setTag(insanholder);
        } else {
            insanholder = (InsanHolder) convertView.getTag();
        }
        if(objects.get(position).getBandurumu().equals("evet")){
            insanholder.banlananinsanarkaplan.setVisibility(View.VISIBLE);
            insanholder.banlananinsanicon.setVisibility(View.VISIBLE);
        }
        if(objects.get(position).getYenimesajvarmi().equals("var")){
            insanholder.yenimesajvarmi.setVisibility(View.VISIBLE);
            insanholder.yenimesajsayisi.setText(objects.get(position).getKacyenimesaj());
        }
        String decodedImgUri = Uri.fromFile(new File(objects.get(position).getResimpath())).toString();
        ImageLoader.getInstance().displayImage(decodedImgUri, insanholder.insanresmi, options);
        insanholder.insanadi.setText(objects.get(position).getName());
        insanholder.insandurumu.setText(objects.get(position).getDurum());
        insanholder.esasbolge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = objects.get(pozisyon).getName();
                String id = objects.get(pozisyon).getId();
                String durum = objects.get(pozisyon).getDurum();
                String bandurumu = objects.get(pozisyon).getBandurumu();
                String resimpath = objects.get(pozisyon).getResimpath();
                String faceprofilurl = objects.get(pozisyon).getFaceprofilur();
                String cinsiyet = objects.get(pozisyon).getCinsiyet();
                String burc = objects.get(pozisyon).getBurc();
                Intent intent = new Intent(context, Mesajlasma.class);
                intent.putExtra("faceprofilurl", faceprofilurl);
                intent.putExtra("karsiserverid", id);
                intent.putExtra("karsiisim", name);
                intent.putExtra("karsidurum", durum);
                intent.putExtra("bandurumu", bandurumu);
                intent.putExtra("karsiresimpath", resimpath);
                intent.putExtra("cinsiyet" , cinsiyet);
                intent.putExtra("burc" , burc);
                intent.putExtra("intentname", "ShappyInsanAdapter");
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public int getCount() {
        return objects.size();
    }

    public Filter getFilter() {
        if (insanfilter==null){

        }
        return insanfilter;
    }

    static class InsanHolder {
        public ImageView insanresmi ,banlananinsanarkaplan, banlananinsanicon;
        public TextView insanadi, insandurumu , yenimesajsayisi;
        public LinearLayout esasbolge;
        public FrameLayout yenimesajvarmi;
    }
}

