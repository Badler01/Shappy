package droxoft.armin.com.shappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class GecmisInsanAdapter extends ArrayAdapter<Insan> {

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


    private String sharedPrefBilinmeyenPathAl() {
        SharedPreferences sP = context.getSharedPreferences("programisleyis", Context.MODE_PRIVATE);
        return sP.getString("bilinmeyenresimpath", "defaultbilinmeyenresimpath");
    }

    public GecmisInsanAdapter(Context context, int resource, ArrayList<Insan> objects) {
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

    public int getCount(){
        return objects.size();
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
        if(objects.get(position).getResmiacik().equals("degil")){
            Log.i("tago" , "resmiacikdegil");
            String bilinmeyenpath = sharedPrefBilinmeyenPathAl();
            Bitmap b = BitmapFactory.decodeFile(bilinmeyenpath);
            if(b==null){
                Log.i("tago" , "resmiaciknull");
            }else{
                insanholder.insanresmi.setImageBitmap(b);
            }
            insanholder.insanadi.setText("??????");
            insanholder.insandurumu.setText("?????????????");
            if(objects.get(position).getYenimesajvarmi().equals("var")){
                insanholder.yenimesajvarmi.setVisibility(View.VISIBLE);
                insanholder.yenimesajsayisi.setText(objects.get(position).getKacyenimesaj());
            }

        }else if(objects.get(position).getResmiacik().equals("acik")) {
            String decodedImgUri = Uri.fromFile(new File(objects.get(position).getResimpath())).toString();
            ImageLoader.getInstance().displayImage(decodedImgUri, insanholder.insanresmi, options);
            insanholder.insanadi.setText(objects.get(position).getName());
            insanholder.insandurumu.setText(objects.get(position).getDurum());
            if(objects.get(position).getYenimesajvarmi().equals("var")){
                insanholder.yenimesajvarmi.setVisibility(View.VISIBLE);
                insanholder.yenimesajsayisi.setText(objects.get(position).getKacyenimesaj());
            }
        }
        insanholder.esasbolge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = objects.get(pozisyon).getName();
                String id = objects.get(pozisyon).getId();
                String durum = objects.get(pozisyon).getDurum();
                String resimpath = objects.get(pozisyon).getResimpath();
                String bandurumu = objects.get(pozisyon).getBandurumu();
                String faceprofilurl = objects.get(pozisyon).getFaceprofilur();
                String cinsiyet = objects.get(pozisyon).getCinsiyet();
                String burc = objects.get(pozisyon).getBurc();
                String yas = objects.get(pozisyon).getYas();
                String okul = objects.get(pozisyon).getOkul();
                String coverfotourl = objects.get(pozisyon).getCoverphotourl();
                Intent intent = new Intent(context, Mesajlasma.class);
                intent.putExtra("faceprofilurl" , faceprofilurl);
                intent.putExtra("bandurumu" , bandurumu);
                intent.putExtra("karsiserverid", id);
                intent.putExtra("karsiisim", name);
                intent.putExtra("karsidurum" ,durum);
                intent.putExtra("karsiresimpath" , resimpath);
                intent.putExtra("cinsiyet" , cinsiyet);
                intent.putExtra("burc" , burc);
                intent.putExtra("yas" , yas);
                intent.putExtra("okul" , okul);
                intent.putExtra("coverfotourl" , coverfotourl);
                intent.putExtra("intentname", "GecmisInsanAdapter");
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    public Filter getFilter() {
        if (insanfilter==null){
            Log.i("tago" , "gecmis insanfilter bos");
        }
        return insanfilter;
    }

    static class InsanHolder {
        public ImageView insanresmi ,banlananinsanarkaplan, banlananinsanicon;
        public TextView insanadi, insandurumu , yenimesajsayisi;
        public LinearLayout esasbolge;
        public FrameLayout yenimesajvarmi;
    }

    public class ServerGuncelDurumuAl extends AsyncTask<String, Void, String> {
        String query, charset;

        public ServerGuncelDurumuAl() {
            charset = "UTF-8";
            String param1 = "id";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            String durum = "basibos";
            try {
                connection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/get_stats.php?id=" + params[0]).openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            connection.setRequestProperty("Accept", "* /*");
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);


            try {
                OutputStream output = new BufferedOutputStream(connection.getOutputStream());
                output.write(query.getBytes(charset));
                output.close();
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Log.i("tago", "Kullanici Durumu yenileme");
                    String inputline = null;
                    inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    JSONObject jsonobj = jsono.getJSONObject(0);
                    durum = jsonobj.optString("status");
                    Log.i("tago", "gunceldurum=" + durum);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return durum;
        }
    }
}
