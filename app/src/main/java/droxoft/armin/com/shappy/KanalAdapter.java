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

public class KanalAdapter extends BaseAdapter implements Filterable {

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

    public KanalAdapter(Context context, ArrayList<Kanal> channelbaba) {
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

    private String SharedIdCek() {
        SharedPreferences sp = context.getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String veritabani_id = sp.getString("serverid", "defaultserverid");
        return veritabani_id;
    }

    private void kanaldakikisisayilari() {
        for (int i = 0; i < channelbaba.size(); i++) {
            ServerKanaldakiKisiSayisi sKKS = new ServerKanaldakiKisiSayisi(i);
            sKKS.execute(channelbaba.get(i).getKanaladi());
        }
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
        Object item = getItem(position-1);
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
            }
            if (!kanal.official) {
                convertView = lala.inflate(R.layout.normalkanal, null);
                holder.normalgenel = (ImageView) convertView.findViewById(R.id.imageView5);
                holder.normalkanaladi = (TextView) convertView.findViewById(R.id.textView4);
                holder.normalkisisayisi = (TextView) convertView.findViewById(R.id.textView8);
                holder.normallikesayisi = (TextView) convertView.findViewById(R.id.textView28);
            }

            convertView.setTag(holder);
        } else {
            holder = (KanalHolder) convertView.getTag();
        }
        if (kanal.official) {
            holder.officialgenel.setBackgroundResource(R.mipmap.cropped);
            holder.officialkanaladitext.setText(channelbaba.get(position).getKanaladi());
            holder.officiallikesayisi.setText(String.valueOf(channelbaba.get(position).getLikesayisi()));
            holder.officialgenel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    kanaladi = channelbaba.get(position).getKanaladi();
                    String kanalid = channelbaba.get(position).getId();
                    int likedurumu = channelbaba.get(position).getLikedurumu();
                    Log.i("tago", "adapter like durumu" + likedurumu);
                    KanalaElemanEkle kEE = new KanalaElemanEkle();
                    kEE.execute(kanaladi);
                    Intent intent = new Intent(context, GrupSohbeti.class);
                    intent.putExtra("intentname", "kanaladapter");
                    intent.putExtra("kanalmodu", "o");
                    intent.putExtra("kanaladi", kanaladi);
                    intent.putExtra("kanallikedurumu", likedurumu);
                    intent.putExtra("kanalid", kanalid);
                    context.startActivity(intent);
                }
            });
        }
        if (!kanal.official) {
            Kanal objectt = channelbaba.get(position);
            if(holder.normalgenel.getTag()==null ||!holder.normalgenel.getTag().equals(objectt.getKanalurl())){
                ImageAware imageAvare = new ImageViewAware(holder.normalgenel,false);
                ImageLoader.getInstance().displayImage(channelbaba.get(position).getKanalurl(), imageAvare, options);
                holder.normalgenel.setTag(objectt.getKanalurl());
            }
            holder.normalkisisayisi.setText(channelbaba.get(position).getKisisayisi());
            Log.i("tago" , "getview kisisayisi kanaladapterposition" + position + "kisisayisi" + channelbaba.get(position).getKisisayisi());
            holder.normalkanaladi.setText(channelbaba.get(position).getKanaladi());
            holder.normallikesayisi.setText(String.valueOf(channelbaba.get(position).getLikesayisi()));
            holder.normalgenel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    kanaladi = channelbaba.get(pozisyon).getKanaladi();
                    String kanalid = channelbaba.get(position).getId();
                    String kanalurl = channelbaba.get(pozisyon).getKanalurl();
                    int likedurumu = channelbaba.get(pozisyon).getLikedurumu();
                    KanalaElemanEkle kEE = new KanalaElemanEkle();
                    kEE.execute(kanaladi);
                    Intent intent = new Intent(context, GrupSohbeti.class);
                    intent.putExtra("intentname", "kanaladapter");
                    intent.putExtra("kanalmodu", "n");
                    intent.putExtra("kanaladi", kanaladi);
                    intent.putExtra("kanalid", kanalid);
                    intent.putExtra("kanalurl", kanalurl);
                    intent.putExtra("kanallikedurumu", likedurumu);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    public Filter getFilter() {
        if (kanalfilter == null) {

        }
        return kanalfilter;
    }


    static class KanalHolder {
        public ImageView normalgenel, officialgenel;
        public TextView normalkanaladi, officialkanaladitext, normalkisisayisi, normallikesayisi, officialkisisayisi, officiallikesayisi;
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
                sconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/join_us.php?id=" + veritabani_id + "&name=" + kanaladi).openConnection();
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

    private class ServerKanaldakiKisiSayisi extends AsyncTask<String, Void, String> {
        String charset, query;
        String kisisayisi;
        int i;

        public ServerKanaldakiKisiSayisi(int i) {
            this.i = i;
        }

        protected String doInBackground(String... params) {
            charset = "UTF-8";
            String param1 = "name";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpURLConnection connection = null;
            try {
                Log.i("tago", "kanaladapter " + params[0]);
                connection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/population.php?placename=" + params[0])
                        .openConnection();
                Log.i("tago", "KanalAdapter kisi sayisini cek bagı kuruldu");
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
                try {
                    int a = connection.getResponseCode();
                    String b = connection.getResponseMessage();
                    Log.i("tago", "rerere kanaladapter" + a + " " + b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Log.i("tago", "InputStream kanaladapter");
                    String inputline = null;
                    inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    JSONObject jsonObject = jsono.getJSONObject(1);
                    kisisayisi = jsonObject.optString("cnt");
                    Log.i("tago", "kisisayisi kanaladapter" + kisisayisi);
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("tago", "kisi sayisi bitti");
            return kisisayisi;
        }

        protected void onPostExecute(String s) {
            channelbaba.get(i).setKisisayisi(s);
            Log.i("tago" , "KanalAdapter kisisayisi get i" + i+ "sayi" + s );
            notifyDataSetChanged();
        }
    }

}