package droxoft.armin.com.shappy;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.felipecsl.quickreturn.library.AbsListViewQuickReturnAttacher;
import com.felipecsl.quickreturn.library.QuickReturnAttacher;
import com.felipecsl.quickreturn.library.widget.AbsListViewScrollTarget;
import com.felipecsl.quickreturn.library.widget.QuickReturnAdapter;
import com.felipecsl.quickreturn.library.widget.QuickReturnTargetView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PageFragment0 extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    public static final String ARG_PAGE = "ARG_PAGE";
    private View view;
    static ProgressBar progressBar;
    SwipeRefreshLayout swipe;
    ArrayList<Insan> cevrendekiinsanListesi;
    ArrayList<Insan> gecmisinsanlistesi = new ArrayList<>();
    ArrayList<Insan> shappyinsanlistesi = new ArrayList<>();
    CevrendekiInsanAdapter cevrendekiInsanAdapter;
    GecmisInsanAdapter gecmisInsanAdapter;
    ShappyInsanAdapter shappyInsanAdapter;
    ImageView kisiyokkonusmalar;
    //Quick Return
    ViewGroup viewGroup;
    AbsListView absListView;
    RelativeLayout quickReturnBar;
    ImageButton cevrendekilerbutonu, shappybutonu, konusmalarimbutonu;
    LinearLayout bottomTextView;
    QuickReturnTargetView topTargetView;

    int hangibolumdesin = 1;


    private String SharedPrefIdAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        String serverid = sP.getString("serverid", "defaultid");
        return serverid;
    }

    public static PageFragment0 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment0 fragment = new PageFragment0();
        fragment.setArguments(args);
        return fragment;
    }

    public static void spinnerkapat(){
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
        if(hangibolumdesin==2){
            gecmisinsanlistesi.clear();
            DatabaseClassKiminleKonustun dB = new DatabaseClassKiminleKonustun(getActivity());
            dB.open();
            List<String> idler = dB.databasedenidcek();
            List<String> isimler = dB.databasedenisimcek();
            List<String> resimpathler = dB.databasedenresimpathcek();
            List<String> durumlar = dB.databasedendurumcek();
            List<String> faceprofilurller = dB.databasedenfaceprofilurlcek();
            List<String> yenimesajvarmilar = dB.databasedenyenimesajvarmilarcek();
            List<String> kacyenimesajlar = dB.databasedenkacyenimesajcek();
            List<String> cinsiyetler = dB.databasedencinsiyetcek();
            List<String> burclar = dB.databasedenburccek();
            List<String> yaslar = dB.databasedenyascek();
            List<String> okullar = dB.databasedenokulcek();
            List<String> coverfotourller = dB.databasedencoverfotourlcek();
            dB.close();
            for (int i = idler.size() - 1; i > -1; i--) {
                Insan insann = new Insan();
                DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(getActivity());
                dCKK.open();
                String resmiacikmi = dCKK.databasedenresmiacikmicek(idler.get(i));
                String bandurumu = dCKK.databasedenbanlanmadurumucek(idler.get(i));
                dCKK.close();
                insann.setBandurumu(bandurumu);
                insann.setResmiacik(resmiacikmi);
                insann.setId(idler.get(i));
                insann.setName(isimler.get(i));
                insann.setResimpath(resimpathler.get(i));
                insann.setDurum(durumlar.get(i));
                insann.setFaceprofilur(faceprofilurller.get(i));
                insann.setYenimesajvarmi(yenimesajvarmilar.get(i));
                insann.setKacyenimesaj(kacyenimesajlar.get(i));
                insann.setCinsiyet(cinsiyetler.get(i));
                insann.setBurc(burclar.get(i));
                insann.setYas(yaslar.get(i));
                insann.setOkul(okullar.get(i));
                insann.setCoverphotourl(coverfotourller.get(i));
                gecmisinsanlistesi.add(insann);
            }

            gecmisInsanAdapter = new GecmisInsanAdapter(getActivity(), R.layout.gecmisinsan, gecmisinsanlistesi);
            if (viewGroup instanceof AbsListView) {
                int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                absListView.setAdapter(new QuickReturnAdapter(gecmisInsanAdapter, numColumns));
            }

            QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
            quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
            topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                    AbsListViewScrollTarget.POSITION_BOTTOM,
                    dpToPx(getActivity(), 50));

            if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                AbsListViewQuickReturnAttacher
                        attacher =
                        (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                attacher.addOnScrollListener(PageFragment0.this);
                attacher.setOnItemClickListener(PageFragment0.this);
                attacher.setOnItemLongClickListener(PageFragment0.this);
            }
        }else if(hangibolumdesin==3){
            shappyinsanlistesi.clear();
            DatabaseClassKimleriActirdin dbA = new DatabaseClassKimleriActirdin(getActivity());
            dbA.open();
            List<String> idler = dbA.databasedenidcek();
            List<String> isimler = dbA.databasedenisimcek();
            List<String> resimpathler = dbA.databasedenresimpathcek();
            List<String> durumlar = dbA.databasedendurumcek();
            List<String> faceprofilurller = dbA.databasedenfaceprofilurlcek();
            List<String> yenimesajvarmilar = dbA.databasedenyenimesajvarmicek();
            List<String> kacyenimesajlar = dbA.databasedenkacyenimesajcek();
            List<String> cinsiyetler = dbA.databasedencinsiyetcek();
            List<String> burclar = dbA.databasedenburccek();
            dbA.close();
            for (int i = idler.size() - 1; i > -1; i--) {
                Insan insann = new Insan();
                DatabaseClassKimleriActirdin dCKA = new DatabaseClassKimleriActirdin(getActivity());
                dCKA.open();
                String bandurumu = dCKA.databasedenbanlanmadurumucek(idler.get(i));
                dCKA.close();
                insann.setId(idler.get(i));
                insann.setDurum(durumlar.get(i));
                insann.setResimpath(resimpathler.get(i));
                insann.setName(isimler.get(i));
                insann.setFaceprofilur(faceprofilurller.get(i));
                insann.setYenimesajvarmi(yenimesajvarmilar.get(i));
                insann.setKacyenimesaj(kacyenimesajlar.get(i));
                insann.setBandurumu(bandurumu);
                insann.setCinsiyet(cinsiyetler.get(i));
                insann.setBurc(burclar.get(i));
                shappyinsanlistesi.add(insann);
            }
            shappyInsanAdapter = new ShappyInsanAdapter(getActivity(), R.layout.gecmisinsan, shappyinsanlistesi);
            if (viewGroup instanceof AbsListView) {
                int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                absListView.setAdapter(new QuickReturnAdapter(shappyInsanAdapter, numColumns));
            }

            QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
            quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
            topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                    AbsListViewScrollTarget.POSITION_BOTTOM,
                    dpToPx(getActivity(), 50));

            if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                AbsListViewQuickReturnAttacher
                        attacher =
                        (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                attacher.addOnScrollListener(PageFragment0.this);
                attacher.setOnItemClickListener(PageFragment0.this);
                attacher.setOnItemLongClickListener(PageFragment0.this);
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onStop() {
        super.onStop();
    }

    public void konusmalarimButonuIkili(){
        hangibolumdesin = 2;
        swipe.setEnabled(false);
        cevrendekilerbutonu.setImageResource(R.mipmap.cevremdekiler);
        shappybutonu.setImageResource(R.mipmap.bomba);
        konusmalarimbutonu.setImageResource(R.mipmap.konusmalarim);
        gecmisinsanlistesi.clear();
        DatabaseClassKiminleKonustun dB = new DatabaseClassKiminleKonustun(getActivity());
        dB.open();
        List<String> idler = dB.databasedenidcek();
        List<String> isimler = dB.databasedenisimcek();
        List<String> resimpathler = dB.databasedenresimpathcek();
        List<String> durumlar = dB.databasedendurumcek();
        List<String> faceprofilurller = dB.databasedenfaceprofilurlcek();
        List<String> yenimesajvarmilar = dB.databasedenyenimesajvarmilarcek();
        List<String> kacyenimesajlar = dB.databasedenkacyenimesajcek();
        dB.close();
        for (int i = idler.size() - 1; i > -1; i--) {
            Insan insann = new Insan();
            DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(getActivity());
            dCKK.open();
            String resmiacikmi = dCKK.databasedenresmiacikmicek(idler.get(i));
            String bandurumu = dCKK.databasedenbanlanmadurumucek(idler.get(i));
            dCKK.close();
            insann.setBandurumu(bandurumu);
            insann.setResmiacik(resmiacikmi);
            insann.setId(idler.get(i));
            insann.setName(isimler.get(i));
            insann.setResimpath(resimpathler.get(i));
            insann.setDurum(durumlar.get(i));
            insann.setFaceprofilur(faceprofilurller.get(i));
            insann.setYenimesajvarmi(yenimesajvarmilar.get(i));
            insann.setKacyenimesaj(kacyenimesajlar.get(i));
            gecmisinsanlistesi.add(insann);
        }

        gecmisInsanAdapter = new GecmisInsanAdapter(getActivity(), R.layout.gecmisinsan, gecmisinsanlistesi);
        if (viewGroup instanceof AbsListView) {
            int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
            absListView.setAdapter(new QuickReturnAdapter(gecmisInsanAdapter, numColumns));
        }

        QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
        quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
        topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                AbsListViewScrollTarget.POSITION_BOTTOM,
                dpToPx(getActivity(), 50));

        if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
            AbsListViewQuickReturnAttacher
                    attacher =
                    (AbsListViewQuickReturnAttacher) quickReturnAttacher;
            attacher.addOnScrollListener(PageFragment0.this);
            attacher.setOnItemClickListener(PageFragment0.this);
            attacher.setOnItemLongClickListener(PageFragment0.this);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bambam) {
        view = inflater.inflate(R.layout.cevrendekiler, container, false);
        kisiyokkonusmalar = (ImageView) view.findViewById(R.id.kisiyokkonusmalar);
        progressBar = (ProgressBar) view.findViewById(R.id.pbLoading);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                icerigiyenile();
            }
        });
        initializeQuickReturn();
        return view;
    }

    private void initializeQuickReturn() {
        viewGroup = (ViewGroup) view.findViewById(R.id.listView);
        absListView = (AbsListView) viewGroup;
        quickReturnBar = (RelativeLayout) view.findViewById(R.id.quickReturnBottomTarget);
        cevrendekilerbutonu = (ImageButton) view.findViewById(R.id.button22);
        shappybutonu = (ImageButton) view.findViewById(R.id.button23);
        konusmalarimbutonu = (ImageButton) view.findViewById(R.id.button24);
        cevrendekilerbutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kisiyokkonusmalar.setVisibility(View.INVISIBLE);
                hangibolumdesin = 1;
                swipe.setEnabled(true);
                cevrendekilerbutonu.setImageResource(R.mipmap.cevremdekiler_iki);
                shappybutonu.setImageResource(R.mipmap.ana_bomba);
                konusmalarimbutonu.setImageResource(R.mipmap.konusmalarim);
                CevrendekiInsanAdapter cevrendekiInsanAdapter = new CevrendekiInsanAdapter(getActivity(), R.layout.insan, cevrendekiinsanListesi,progressBar);
                if (viewGroup instanceof AbsListView) {
                    int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                    absListView.setAdapter(new QuickReturnAdapter(cevrendekiInsanAdapter, numColumns));
                }

                QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
                quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
                topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                        AbsListViewScrollTarget.POSITION_BOTTOM,
                        dpToPx(getActivity(), 50));

                if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                    AbsListViewQuickReturnAttacher
                            attacher =
                            (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                    attacher.addOnScrollListener(PageFragment0.this);
                    attacher.setOnItemClickListener(PageFragment0.this);
                    attacher.setOnItemLongClickListener(PageFragment0.this);
                }
            }
        });
        shappybutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hangibolumdesin = 2;
                swipe.setEnabled(false);
                kisiyokkonusmalar.setVisibility(View.INVISIBLE);
                cevrendekilerbutonu.setImageResource(R.mipmap.cevremdekiler);
                shappybutonu.setImageResource(R.mipmap.ana_bomba_iki);
                konusmalarimbutonu.setImageResource(R.mipmap.konusmalarim);
                gecmisinsanlistesi.clear();
                DatabaseClassKiminleKonustun dB = new DatabaseClassKiminleKonustun(getActivity());
                dB.open();
                List<String> idler = dB.databasedenidcek();
                List<String> isimler = dB.databasedenisimcek();
                List<String> resimpathler = dB.databasedenresimpathcek();
                List<String> durumlar = dB.databasedendurumcek();
                List<String> faceprofilurller = dB.databasedenfaceprofilurlcek();
                List<String> yenimesajvarmilar = dB.databasedenyenimesajvarmilarcek();
                List<String> kacyenimesajlar = dB.databasedenkacyenimesajcek();
                List<String> cinsiyetler = dB.databasedencinsiyetcek();
                List<String> burclar = dB.databasedenburccek();
                List<String> yaslar = dB.databasedenyascek();
                List<String> okullar = dB.databasedenokulcek();
                List<String> coverfotourller = dB.databasedencoverfotourlcek();
                dB.close();
                for (int i = idler.size() - 1; i > -1; i--) {
                    Insan insann = new Insan();
                    DatabaseClassKiminleKonustun dCKK = new DatabaseClassKiminleKonustun(getActivity());
                    dCKK.open();
                    String resmiacikmi = dCKK.databasedenresmiacikmicek(idler.get(i));
                    String bandurumu = dCKK.databasedenbanlanmadurumucek(idler.get(i));
                    dCKK.close();
                    insann.setBandurumu(bandurumu);
                    insann.setResmiacik(resmiacikmi);
                    insann.setId(idler.get(i));
                    insann.setName(isimler.get(i));
                    insann.setResimpath(resimpathler.get(i));
                    insann.setDurum(durumlar.get(i));
                    insann.setFaceprofilur(faceprofilurller.get(i));
                    insann.setCinsiyet(cinsiyetler.get(i));
                    insann.setBurc(burclar.get(i));
                    insann.setYas(yaslar.get(i));
                    insann.setOkul(okullar.get(i));
                    insann.setCoverphotourl(coverfotourller.get(i));
                    insann.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    insann.setKacyenimesaj(kacyenimesajlar.get(i));
                    gecmisinsanlistesi.add(insann);
                }

                gecmisInsanAdapter = new GecmisInsanAdapter(getActivity(), R.layout.gecmisinsan, gecmisinsanlistesi);
                if (viewGroup instanceof AbsListView) {
                    int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                    absListView.setAdapter(new QuickReturnAdapter(gecmisInsanAdapter, numColumns));
                }

                QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
                quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
                topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                        AbsListViewScrollTarget.POSITION_BOTTOM,
                        dpToPx(getActivity(), 50));

                if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                    AbsListViewQuickReturnAttacher
                            attacher =
                            (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                    attacher.addOnScrollListener(PageFragment0.this);
                    attacher.setOnItemClickListener(PageFragment0.this);
                    attacher.setOnItemLongClickListener(PageFragment0.this);
                }
            }
        });
        konusmalarimbutonu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hangibolumdesin = 3;
                swipe.setEnabled(false);
                kisiyokkonusmalar.setVisibility(View.INVISIBLE);
                cevrendekilerbutonu.setImageResource(R.mipmap.cevremdekiler);
                shappybutonu.setImageResource(R.mipmap.ana_bomba);
                konusmalarimbutonu.setImageResource(R.mipmap.konusmalarim_iki);
                shappyinsanlistesi.clear();
                DatabaseClassKimleriActirdin dbA = new DatabaseClassKimleriActirdin(getActivity());
                dbA.open();
                List<String> idler = dbA.databasedenidcek();
                List<String> isimler = dbA.databasedenisimcek();
                List<String> resimpathler = dbA.databasedenresimpathcek();
                List<String> durumlar = dbA.databasedendurumcek();
                List<String> faceprofilurller = dbA.databasedenfaceprofilurlcek();
                List<String> yenimesajvarmilar = dbA.databasedenyenimesajvarmicek();
                List<String> kacyenimesajlar = dbA.databasedenkacyenimesajcek();
                List<String> cinsiyetler = dbA.databasedencinsiyetcek();
                List<String> burclar = dbA.databasedenburccek();
                dbA.close();
                for (int i = idler.size() - 1; i > -1; i--) {
                    Insan insann = new Insan();
                    DatabaseClassKimleriActirdin dCKA = new DatabaseClassKimleriActirdin(getActivity());
                    dCKA.open();
                    String bandurumu = dCKA.databasedenbanlanmadurumucek(idler.get(i));
                    dCKA.close();
                    insann.setId(idler.get(i));
                    insann.setDurum(durumlar.get(i));
                    insann.setResimpath(resimpathler.get(i));
                    insann.setName(isimler.get(i));
                    insann.setFaceprofilur(faceprofilurller.get(i));
                    insann.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    insann.setKacyenimesaj(kacyenimesajlar.get(i));
                    insann.setBandurumu(bandurumu);
                    insann.setCinsiyet(cinsiyetler.get(i));
                    Log.i("tago", "cinsiyetr" + cinsiyetler.get(i));
                    Log.i("tago", "burclarr" + burclar.get(i));
                    insann.setBurc(burclar.get(i));
                    shappyinsanlistesi.add(insann);
                }
                if (shappyinsanlistesi.size() == 0) {
                    kisiyokkonusmalar.setVisibility(View.VISIBLE);
                }
                shappyInsanAdapter = new ShappyInsanAdapter(getActivity(), R.layout.gecmisinsan, shappyinsanlistesi);
                if (viewGroup instanceof AbsListView) {
                    int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                    absListView.setAdapter(new QuickReturnAdapter(shappyInsanAdapter, numColumns));
                }

                QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
                quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
                topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                        AbsListViewScrollTarget.POSITION_BOTTOM,
                        dpToPx(getActivity(), 50));

                if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                    AbsListViewQuickReturnAttacher
                            attacher =
                            (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                    attacher.addOnScrollListener(PageFragment0.this);
                    attacher.setOnItemClickListener(PageFragment0.this);
                    attacher.setOnItemLongClickListener(PageFragment0.this);
                }

            }
        });
        final String serverid = SharedPrefIdAl();
        if (!serverid.equals("defaultid")) {
            ServerCevredekileriCek sCC = new ServerCevredekileriCek(serverid);
            sCC.execute();
        } else {
            Thread a = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                        if (!serverid.equals("defaultid")) {
                            ServerCevredekileriCek sCC = new ServerCevredekileriCek(serverid);
                            sCC.execute();
                        } else {
                            Toast.makeText(getActivity(), "Connection Error With Server", Toast.LENGTH_LONG);
                            getActivity().finish();
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            a.start();
        }
    }

    private void icerigiyenile() {
        ServerCevredekileriCek sCC = new ServerCevredekileriCek(SharedPrefIdAl());
        sCC.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        swipe.setRefreshing(false);
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    private int dpToPx(Context context, int i) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((i * scale) + 0.5f);
    }

    public void aramaYap(String arananyazi) {
        Log.i("tago" , "arananyazi " + arananyazi);
        if(hangibolumdesin==1){
            cevrendekiInsanAdapter.getFilter().filter(arananyazi);
        }
        if(hangibolumdesin==2&&gecmisInsanAdapter!=null) {
            gecmisInsanAdapter.getFilter().filter(arananyazi);
        }
        if(hangibolumdesin==3&&shappyInsanAdapter!=null) {
            shappyInsanAdapter.getFilter().filter(arananyazi);
        }
    }

    private int getAge(int year, int month, int day) {
        int age;

        final Calendar calenderToday = Calendar.getInstance();
        int currentYear = calenderToday.get(Calendar.YEAR);
        int currentMonth = 1 + calenderToday.get(Calendar.MONTH);
        int todayDay = calenderToday.get(Calendar.DAY_OF_MONTH);

        age = currentYear - year;

        if(month > currentMonth){
            --age;
        }
        else if(month == currentMonth){
            if(day > todayDay){
                --age;
            }
        }
        return age;

    }

    private class ServerCevredekileriCek extends AsyncTask<String, Void, String> {

        String charset, query, param1, serverid;

        public ServerCevredekileriCek(String serverid) {
            this.serverid = serverid;
            charset = "UTF-8";
            param1 = "id";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
                Log.i("tago" , "calisti");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.187.60/shappy/near_users.php?id=" + serverid)
                        .openConnection();
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
                    String inputline = null;

                    for (int i = 0; i < 3; i++) {
                        inputline = in.readLine();
                        Log.i("tago", "" + i + " for inputline= " + inputline);
                    }
                    cevrendekiinsanListesi = new ArrayList<>();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Insan insann = new Insan();
                        insann.setId(object.optString("id"));
                        insann.setName(object.optString("name"));
                        insann.setFaceprofilur(object.optString("profile_picture"));
                        insann.setCoverphotourl(object.optString("cover_photo"));
                        insann.setUzaklik(object.optString("distance"));
                        insann.setOkul(object.optString("school"));
                        insann.setBorndate(object.optString("borndate"));
                        insann.setCinsiyet(object.optString("gender"));
                        insann.setBurc(object.optString("burc"));
                        insann.setDurum(object.optString("status"));
                        String borndate = object.optString("borndate");
                        String year = borndate.substring(0, 4);
                        String month = borndate.substring(5, 7);
                        String day = borndate.substring(8,10);
                        Integer yas = getAge(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day));
                        insann.setYas(String.valueOf(yas));
                        cevrendekiinsanListesi.add(insann);
                    }
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    Log.i("tago", "Error Stream");
                    String inputline = null;
                    for (int i = 0; i < 3; i++) {
                        inputline = in.readLine();
                        Log.i("tago", "" + i + " for inputline= " + inputline);
                    }
                    cevrendekiinsanListesi = new ArrayList<>();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Insan insann = new Insan();
                        insann.setId(object.optString("id"));
                        insann.setName(object.optString("name"));
                        insann.setFaceprofilur(object.optString("profile_picture"));
                        insann.setCoverphotourl(object.optString("cover_photo"));
                        insann.setUzaklik(object.optString("distance"));
                        insann.setOkul(object.optString("school"));
                        insann.setBorndate(object.optString("borndate"));
                        insann.setCinsiyet(object.optString("gender"));
                        insann.setBurc(object.optString("burc"));
                        insann.setDurum(object.optString("status"));
                        String borndate = object.optString("borndate");
                        String year = borndate.substring(0, 4);
                        String month = borndate.substring(5, 7);
                        String day = borndate.substring(8,10);
                        Integer yas = getAge(Integer.valueOf(year),Integer.valueOf(month),Integer.valueOf(day));
                        insann.setYas(String.valueOf(yas));
                        cevrendekiinsanListesi.add(insann);
                    }
                }
                in.close();
                Log.i("tago", "Page Fragment cevredekileri gor inputline yazdim");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tago", "json Exception");
            }
            return "inputline";
        }

        protected void onPostExecute(String s) {
            List<String> varolankonusulanlar;
            List<String> varolanacilanlar;
            DatabaseClassKiminleKonustun sss = new DatabaseClassKiminleKonustun(getActivity());
            sss.open();
            varolankonusulanlar = sss.databasedenidcek();
            sss.close();
            DatabaseClassKimleriActirdin aaa = new DatabaseClassKimleriActirdin(getActivity());
            aaa.open();
            varolanacilanlar = aaa.databasedenidcek();
            aaa.close();
            ArrayList<Integer> silineceklerListesi = new ArrayList<>();
            for(int q=0 ; q<cevrendekiinsanListesi.size(); q++){
                for(String a:varolankonusulanlar){
                    if(cevrendekiinsanListesi.get(q).getId().equals(a)){
                        silineceklerListesi.add(q);
                    }
                }
            }
            for(int q=0 ; q<cevrendekiinsanListesi.size(); q++){
                for(String a:varolanacilanlar){
                    if(cevrendekiinsanListesi.get(q).getId().equals(a)){
                        silineceklerListesi.add(q);
                    }
                }
            }
            for(int i =0 ; i<silineceklerListesi.size();i++){
                int a = silineceklerListesi.get(i);
                Log.i("tago" , "siline "+ a);
                cevrendekiinsanListesi.remove(a);
            }
            cevrendekiInsanAdapter = new CevrendekiInsanAdapter(getActivity(), R.layout.insan, cevrendekiinsanListesi,progressBar);
            if (viewGroup instanceof AbsListView) {
                int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                absListView.setAdapter(new QuickReturnAdapter(cevrendekiInsanAdapter, numColumns));
            }
            QuickReturnAttacher quickReturnAttacher = QuickReturnAttacher.forView(viewGroup);
            quickReturnAttacher.addTargetView(bottomTextView, AbsListViewScrollTarget.POSITION_BOTTOM);
            topTargetView = quickReturnAttacher.addTargetView(quickReturnBar,
                    AbsListViewScrollTarget.POSITION_BOTTOM,
                    dpToPx(getActivity(), 50));

            if (quickReturnAttacher instanceof AbsListViewQuickReturnAttacher) {
                AbsListViewQuickReturnAttacher
                        attacher =
                        (AbsListViewQuickReturnAttacher) quickReturnAttacher;
                attacher.addOnScrollListener(PageFragment0.this);
                attacher.setOnItemClickListener(PageFragment0.this);
                attacher.setOnItemLongClickListener(PageFragment0.this);
            }

        }
    }

}
