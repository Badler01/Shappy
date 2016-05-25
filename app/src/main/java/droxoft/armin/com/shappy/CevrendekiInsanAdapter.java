package droxoft.armin.com.shappy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class CevrendekiInsanAdapter extends ArrayAdapter implements Filterable {

    LayoutInflater layoutInflater;
    Context context;
    ArrayList<Insan> cevrendekiInsanListesi;
    ArrayList<Insan> kokcevrendekiInsanListesi;
    ProgressBar progressBar;
    int resource;
    private DisplayImageOptions options;
    Filter insanfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = kokcevrendekiInsanListesi;
                results.count = kokcevrendekiInsanListesi.size();
            } else {
                ArrayList<Insan> sonuclistesi = new ArrayList<>();
                for (Insan i : kokcevrendekiInsanListesi) {
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
                cevrendekiInsanListesi = (ArrayList<Insan>) results.values;
                notifyDataSetChanged();
            }
        }
    };

    public CevrendekiInsanAdapter(Context context, int resource, ArrayList<Insan> cevrendekiinsanListesi, ProgressBar progressBar) {
        super(context, resource, cevrendekiinsanListesi);
        this.context = context;
        this.progressBar = progressBar;
        this.resource = resource;
        this.cevrendekiInsanListesi = cevrendekiinsanListesi;
        this.kokcevrendekiInsanListesi = cevrendekiinsanListesi;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ins)
                .showImageForEmptyUri(R.mipmap.soruisareti)
                .showImageOnFail(R.mipmap.soruisareti)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return cevrendekiInsanListesi.size();
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final InsanHolder insanholder;
        final int pozisyon = position;
        if (convertView == null) {
            convertView = layoutInflater.inflate(resource, null);
            insanholder = new InsanHolder();
            insanholder.insanresmi = (ImageView) convertView.findViewById(R.id.imgIcon);
            insanholder.insanadi = (TextView) convertView.findViewById(R.id.txtTitle);
            insanholder.insandurumu = (TextView) convertView.findViewById(R.id.textView20);
            insanholder.insanuzakligi = (TextView) convertView.findViewById(R.id.textView25);
            insanholder.esasbolge = (LinearLayout) convertView.findViewById(R.id.esasbolge);
            convertView.setTag(insanholder);
        } else {
            insanholder = (InsanHolder) convertView.getTag();
        }
        insanholder.insanadi.setText(cevrendekiInsanListesi.get(position).getName());
        insanholder.insandurumu.setText(cevrendekiInsanListesi.get(position).getDurum());
        insanholder.insanuzakligi.setText(UzaklikDonusturme(position));
        ImageLoader.getInstance().displayImage(cevrendekiInsanListesi.get(position).getFaceprofilur(), insanholder.insanresmi, options);
        insanholder.esasbolge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = cevrendekiInsanListesi.get(pozisyon).getName();
                String serverid = cevrendekiInsanListesi.get(pozisyon).getId();
                String durum = cevrendekiInsanListesi.get(pozisyon).getDurum();
                String cinsiyet = cevrendekiInsanListesi.get(pozisyon).getCinsiyet();
                String burc = cevrendekiInsanListesi.get(pozisyon).getBurc();
                String okul = cevrendekiInsanListesi.get(pozisyon).getOkul();
                String yas = cevrendekiInsanListesi.get(pozisyon).getYas();
                String coverfotourl = cevrendekiInsanListesi.get(pozisyon).getCoverphotourl();
                Bitmap a = ((BitmapDrawable) insanholder.insanresmi.getDrawable()).getBitmap();
                String faceprofilurl = cevrendekiInsanListesi.get(pozisyon).getFaceprofilur();
                Intent intent = new Intent(context, Mesajlasma.class);
                intent.putExtra("intentname", "CevrendekiInsanAdapter");
                intent.putExtra("karsiname", name);
                intent.putExtra("karsiserverid", serverid);
                intent.putExtra("karsidurum", durum);
                intent.putExtra("faceprofilurl", faceprofilurl);
                intent.putExtra("cinsiyet", cinsiyet);
                intent.putExtra("burc", burc);
                intent.putExtra("okul" , okul);
                intent.putExtra("yas" , yas);
                intent.putExtra("coverfotourl" , coverfotourl);
                Bundle bundle = new Bundle();
                bundle.putParcelable("karsiresim", a);
                intent.putExtra("karsiresimbundle", bundle);
                context.startActivity(intent);
            }
        });

        insanholder.insanresmi.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String faceprofilurl = cevrendekiInsanListesi.get(pozisyon).getFaceprofilur();
                String isim = cevrendekiInsanListesi.get(pozisyon).getName();
                String cinsiyet = cevrendekiInsanListesi.get(pozisyon).getCinsiyet();
                String okul = cevrendekiInsanListesi.get(pozisyon).getOkul();
                String durum = cevrendekiInsanListesi.get(pozisyon).getDurum();
                String yas = cevrendekiInsanListesi.get(pozisyon).getYas();
                String coverfotourl = cevrendekiInsanListesi.get(pozisyon).getCoverphotourl();
                Intent i = new Intent(context, KarsiProfil.class);
                i.putExtra("karsifaceprofilurl", faceprofilurl);
                i.putExtra("isim", isim);
                i.putExtra("cinsiyet", cinsiyet);
                i.putExtra("okul", okul);
                i.putExtra("durum", durum);
                i.putExtra("yas", yas);
                i.putExtra("coverfotourl", coverfotourl);
                context.startActivity(i);
            }
        });
        return convertView;
    }

    public Filter getFilter() {
        if (insanfilter == null) {

        }
        return insanfilter;
    }

    public String UzaklikDonusturme(int position) {
        String uzaklikS;
        double uzaklik = (Double.parseDouble(cevrendekiInsanListesi.get(position).getUzaklik()));
        if (uzaklik <= 0.5) {
            uzaklik = uzaklik * 1000;
            uzaklikS = String.format("%.0f", uzaklik) + " m";
        } else {
            uzaklikS = String.format("%.1f", uzaklik) + " km";
        }
        return uzaklikS;

    }

    static class InsanHolder {
        public ImageView insanresmi;
        public TextView insanadi, insandurumu, insanuzakligi;
        public LinearLayout esasbolge;
    }
}