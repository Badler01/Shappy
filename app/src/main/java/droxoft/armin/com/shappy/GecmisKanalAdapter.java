package droxoft.armin.com.shappy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class GecmisKanalAdapter extends BaseAdapter implements Filterable {
    Context context;
    String kanaladi;
    String veritabani_id;
    ArrayList<Kanal> channelbaba;
    ArrayList<Kanal> kokchannelbaba;
    int OFFICIAL_KANAL = 0;
    int NORMAL_KANAL = 1;
    LayoutInflater lala;
    private DisplayImageOptions options;
    Filter kanalfilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = kokchannelbaba;
                results.count = kokchannelbaba.size();
            } else {
                ArrayList<Kanal> sonuclistesi = new ArrayList<>();
                for (Kanal i : kokchannelbaba) {
                    if (i.getKanaladi().toLowerCase().contains(constraint.toString())) {
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
                channelbaba = (ArrayList<Kanal>) results.values;
                notifyDataSetChanged();
            }
        }
    };

    public GecmisKanalAdapter(Context context, ArrayList<Kanal> channelbaba) {
        this.context = context;
        this.channelbaba = channelbaba;
        kokchannelbaba = channelbaba;
        veritabani_id = SharedIdCek();
        kanaldakikisisayilari();
        lala = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.badooooo)
                .showImageForEmptyUri(R.mipmap.badooooo)
                .showImageOnFail(R.mipmap.badooooo)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 970, 260, false);
                    }
                })
                .build();
    }

    private void kanaldakikisisayilari() {
        for (int i = 0; i < channelbaba.size(); i++) {
            ServerKanaldakiKisiSayisi sKKS = new ServerKanaldakiKisiSayisi(i);
            sKKS.execute(channelbaba.get(i).getKanaladi());
        }
    }

    private String SharedIdCek() {
        SharedPreferences sp = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String veritabani_id = sp.getString("serverid", "defaultserverid");
        return veritabani_id;
    }

    public int getCount() {
        return (channelbaba.size());
    }

    public Object getItem(int i) {
        return channelbaba.get(i);

    }

    public long getItemId(int i) {
        return i;
    }

    public int getItemViewType(int position) {
        Object item = getItem(position - 1);
        Kanal kanal = (Kanal) item;
        if (kanal.official) {
            return OFFICIAL_KANAL;
        } else {
            return NORMAL_KANAL;
        }
    }

    public int getViewTypeCount() {
        return 2;
    }

    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        KanalHolder holder = null;
        final int pozisyon = position;
        Object currentKanal = getItem(position);
        Kanal kanal = (Kanal) currentKanal;
        if (convertView == null) {
            holder = new KanalHolder();
            if (kanal.official) {
                convertView = lala.inflate(R.layout.officialkanal, null);
                holder.officialgenel = (ImageView) convertView.findViewById(R.id.imageView5);
                holder.officialkanaladitext = (TextView) convertView.findViewById(R.id.textView4);
                holder.menubutonu = (ImageButton) convertView.findViewById(R.id.button8);
                holder.officialkisisayisi = (TextView) convertView.findViewById(R.id.textView26);
                holder.officiallikesayisi = (TextView) convertView.findViewById(R.id.textView27);
                holder.officialyenimesajfon = (ImageView) convertView.findViewById(R.id.imageView7);
                holder.officialyenimesajsayisi = (TextView) convertView.findViewById(R.id.kanaldakikisi);
            }
            if (!kanal.official) {
                convertView = lala.inflate(R.layout.normalkanal, null);
                holder.normalgenel = (ImageView) convertView.findViewById(R.id.imageView5);
                holder.normalkanaladi = (TextView) convertView.findViewById(R.id.textView4);
                holder.normalkisisayisi = (TextView) convertView.findViewById(R.id.textView8);
                holder.normallikesayisi = (TextView) convertView.findViewById(R.id.textView28);
                holder.normalyenimesajfon = (ImageView) convertView.findViewById(R.id.imageView8);
                holder.normalyenimesajsayisi = (TextView) convertView.findViewById(R.id.kanaldakikisinormal);
            }

            convertView.setTag(holder);
        } else {
            holder = (KanalHolder) convertView.getTag();
        }
        if (kanal.official) {
            if(channelbaba.get(position).getYenimesajvarmi().equals("var")){
                holder.officialyenimesajfon.setVisibility(View.VISIBLE);
                holder.officialyenimesajsayisi.setVisibility(View.VISIBLE);
                holder.officialyenimesajsayisi.setText(channelbaba.get(position).getKacyenimesaj());
            }
            holder.officialgenel.setBackgroundResource(R.mipmap.cropped);
            holder.officialkanaladitext.setText(channelbaba.get(position).getKanaladi());
            holder.officiallikesayisi.setText(String.valueOf(channelbaba.get(position).getLikesayisi()));
            holder.officialgenel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    kanaladi = channelbaba.get(position).getKanaladi();
                    String kanalid = channelbaba.get(position).getId();
                    int likedurumu = channelbaba.get(position).getLikedurumu();
                    Log.i("tago", kanaladi);
                    KanalaElemanEkle kEE = new KanalaElemanEkle();
                    kEE.execute(kanaladi);
                    Intent intent = new Intent(context, GrupSohbeti.class);
                    intent.putExtra("intentname", "kanaladapter");
                    intent.putExtra("kanalmodu", "o");
                    intent.putExtra("kanaladi", kanaladi);
                    intent.putExtra("likedurumu", likedurumu);
                    intent.putExtra("kanalid", kanalid);
                    context.startActivity(intent);
                }
            });
        }
        if (!kanal.official) {
            if(channelbaba.get(position).getYenimesajvarmi().equals("var")){
                holder.normalyenimesajfon.setVisibility(View.VISIBLE);
                holder.normalyenimesajsayisi.setVisibility(View.VISIBLE);
                holder.normalyenimesajsayisi.setText(channelbaba.get(position).getKacyenimesaj());
            }
            Log.i("tago" , "kanalurl" + channelbaba.get(position).getKanalurl());
            Kanal objectt = channelbaba.get(position);
            if(holder.normalgenel.getTag()==null ||!holder.normalgenel.getTag().equals(objectt.getKanalurl())){
                ImageAware imageAvare = new ImageViewAware(holder.normalgenel,false);
                ImageLoader.getInstance().displayImage(channelbaba.get(position).getKanalurl(), imageAvare, options);
                holder.normalgenel.setTag(objectt.getKanalurl());
            }
            holder.normalkisisayisi.setText(channelbaba.get(position).getKisisayisi());
            holder.normalkanaladi.setText(channelbaba.get(position).getKanaladi());
            holder.normallikesayisi.setText(String.valueOf(channelbaba.get(position).getLikesayisi()));
            Log.i("tago" , "getview channel position" + position + "likesayisi" + channelbaba.get(position).getLikesayisi());
            holder.normalgenel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    kanaladi = channelbaba.get(pozisyon).getKanaladi();
                    String kanalid = channelbaba.get(position).getId();
                    String kanalurl = channelbaba.get(pozisyon).getKanalurl();
                    int likedurumu = channelbaba.get(pozisyon).getLikedurumu();
                    Log.i("tago" , "like durumu adapter" + likedurumu);
                    Intent intent = new Intent(context, GrupSohbeti.class);
                    intent.putExtra("intentname", "gecmiskanaladapter");
                    intent.putExtra("kanalurl" ,kanalurl);
                    intent.putExtra("kanalmodu", "n");
                    intent.putExtra("kanaladi", kanaladi);
                    intent.putExtra("kanalid", kanalid);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public Filter getFilter() {
        if (kanalfilter == null) {
            Log.i("tago", "filter bos");
        }
        return kanalfilter;
    }


    static class KanalHolder {
        public ImageView normalgenel, officialgenel , officialyenimesajfon , normalyenimesajfon;
        public TextView normalkanaladi, officialkanaladitext, normalkisisayisi, normallikesayisi, officialkisisayisi, officiallikesayisi;
        public TextView officialyenimesajsayisi, normalyenimesajsayisi;
        public ImageButton menubutonu;
    }

    private class KanalaElemanEkle extends AsyncTask<String, Void, String> {
        String charset;
        String query;

        public KanalaElemanEkle() {

        }

        protected String doInBackground(String... params) {
            charset = "utf-8";
            String param1 = "id";
            String param2 = "name";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("tago", "KanalAdapter kanala eleman ekleme ba�lat�ld�");
            try {
                return kanaliekle(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private String kanaliekle(String kanaladi) {
            HttpURLConnection sconnection = null;
            try {
                sconnection = (HttpURLConnection) new URL("http://185.22.187.60/shappy/join_us.php?id=" + veritabani_id + "&name=" + kanaladi).openConnection();
                Log.i("tago", "KanalAdapter kanala ekleme bağı kuruldu");
            } catch (IOException e) {
                e.printStackTrace();
            }
            sconnection.setDoOutput(true);
            sconnection.setDoInput(true);
            sconnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            sconnection.setRequestProperty("Accept", "* /*");
            sconnection.setRequestProperty("Accept-Charset", charset);
            sconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);

            try {
                OutputStream output = new BufferedOutputStream(sconnection.getOutputStream());
                output.write(query.getBytes(charset));
                output.close();
                InputStream response = sconnection.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "alabama";
        }
    }

    private class ServerKanaldakiKisiSayisi extends AsyncTask<String, Void, List<String>> {
        String charset, query;
        String kisisayisi;
        String likesayisi;
        int i;

        public ServerKanaldakiKisiSayisi(int i){
            this.i = i;
        }

        protected List<String> doInBackground(String... params) {
            charset = "UTF-8";
            String param1 = "name";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.187.60/shappy/population.php?placename=" +
                        URLEncoder.encode(params[0],charset)).openConnection();
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
                    String inputline= in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    JSONObject jsonObject = jsono.getJSONObject(0);
                    JSONObject obje = jsono.getJSONObject(1);
                    kisisayisi = obje.optString("cnt");
                    likesayisi = jsonObject.optString("like_count");
                    Log.i("tago" , "gecmis kanal likesayisi" + likesayisi);
                    Log.i("tago", "kisisayisi kanaladapter" + kisisayisi);
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            List<String> sonuclar = new ArrayList<>();
            sonuclar.add(kisisayisi);
            sonuclar.add(likesayisi);
            return sonuclar;
        }

        protected void onPostExecute(List<String> results) {
            channelbaba.get(i).setKisisayisi(results.get(0));
            Integer a = Integer.valueOf(results.get(1));
            channelbaba.get(i).setLikesayisi(a);
            notifyDataSetChanged();
        }
    }
}