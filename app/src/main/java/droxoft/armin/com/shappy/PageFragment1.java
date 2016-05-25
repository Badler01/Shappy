package droxoft.armin.com.shappy;


import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class PageFragment1 extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    String fileName;
    public static final String ARG_PAGE = "ARG_PAGE";
    private View view;
    SwipeRefreshLayout swipe;
    KanalAdapter kanalAdapter;
    GecmisKanalAdapter gecmisKanalAdapter;
    ArrayList<Kanal> channelbaba;
    ArrayList<Kanal> konusulankanallar = new ArrayList<>();
    ImageView kanalresmiimage;
    private Uri outputFileUri;
    private Uri selectedImageUri;
    String image = "default";
    boolean kanaleklemebitti;
    String nameofChannel;

    //Quick Return
    ViewGroup viewGroup;
    AbsListView absListView;
    ImageButton butoncevrendekigruplar, butonkatildigingruplar;
    RelativeLayout quickReturnBar;
    LinearLayout bottomTextView;
    QuickReturnTargetView topTargetView;
    int hangibolumdesin = 1;

    public static PageFragment1 newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment1 fragment = new PageFragment1();
        fragment.setArguments(args);
        return fragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public void onResume() {
        super.onResume();
        if (hangibolumdesin == 2) {
            konusulankanallar.clear();
            DatabaseClassKonusulanKanallar dCKK = new DatabaseClassKonusulanKanallar(getActivity());
            dCKK.open();
            List<String> kanallar = dCKK.databasedenkanalcek();
            List<String> modlar = dCKK.databasedenmodcek();
            List<String> likedurum = dCKK.databasedenlikecek();
            List<String> kanalurller = dCKK.databasedenkanalurlcek();
            List<String> yenimesajvarmilar = dCKK.databasedenyenimesajvarmicek();
            List<String> kacyenimesajlar = dCKK.databasedenkacyenimesajcek();
            dCKK.close();
            for (int i = kanallar.size() - 1; i > -1; i--) {
                if (likedurum.get(i).equals("yes")) {
                    if (modlar.get(i).equals("official")) {
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(kanallar.get(i));
                        kanal.setLikedurumu(1);
                        kanal.setKanalurl(kanalurller.get(i));
                        kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                        kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                        konusulankanallar.add(kanal);
                    } else {
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(kanallar.get(i));
                        kanal.setLikedurumu(1);
                        kanal.setKanalurl(kanalurller.get(i));
                        kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                        kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                        konusulankanallar.add(kanal);
                    }
                } else if (likedurum.get(i).equals("no")) {
                    if (modlar.get(i).equals("official")) {
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(kanallar.get(i));
                        kanal.setLikedurumu(0);
                        kanal.setKanalurl(kanalurller.get(i));
                        kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                        kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                        konusulankanallar.add(kanal);
                    } else {
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(kanallar.get(i));
                        kanal.setLikedurumu(0);
                        kanal.setKanalurl(kanalurller.get(i));
                        kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                        kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                        konusulankanallar.add(kanal);
                    }
                }
            }

            gecmisKanalAdapter = new GecmisKanalAdapter(getActivity(), konusulankanallar);
            if (viewGroup instanceof AbsListView) {
                absListView.setAdapter(new QuickReturnAdapter(gecmisKanalAdapter));
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
                attacher.addOnScrollListener(PageFragment1.this);
                attacher.setOnItemClickListener(PageFragment1.this);
                attacher.setOnItemLongClickListener(PageFragment1.this);
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        view = inflater.inflate(R.layout.kanallar, container, false);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh1);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                icerigiYenile();
            }
        });
        ImageButton butonkanalekleme = (ImageButton) view.findViewById(R.id.button53);
        butonkanalekleme.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                kanalEkleme();
            }
        });
        initializeQuickReturn();
        return view;
    }

    private void initializeQuickReturn() {
        viewGroup = (ViewGroup) view.findViewById(R.id.listView);
        absListView = (AbsListView) viewGroup;
        quickReturnBar = (RelativeLayout) view.findViewById(R.id.quickReturnBottomTarget);
        butoncevrendekigruplar = (ImageButton) view.findViewById(R.id.button);
        butonkatildigingruplar = (ImageButton) view.findViewById(R.id.button2);
        butoncevrendekigruplar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hangibolumdesin = 1;
                swipe.setEnabled(true);
                butoncevrendekigruplar.setImageResource(R.mipmap.cevrende_gruplar_iki);
                butonkatildigingruplar.setImageResource(R.mipmap.katil_gruplar);
                kanalAdapter = new KanalAdapter(getActivity(), channelbaba);
                if (viewGroup instanceof AbsListView) {
                    int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                    absListView.setAdapter(new QuickReturnAdapter(kanalAdapter, numColumns));
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
                    attacher.addOnScrollListener(PageFragment1.this);
                    attacher.setOnItemClickListener(PageFragment1.this);
                    attacher.setOnItemLongClickListener(PageFragment1.this);
                }
            }
        });
        butonkatildigingruplar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hangibolumdesin = 2;
                swipe.setEnabled(false);
                butoncevrendekigruplar.setImageResource(R.mipmap.cevrende_gruplar);
                butonkatildigingruplar.setImageResource(R.mipmap.katil_gruplar_iki);
                konusulankanallar.clear();
                DatabaseClassKonusulanKanallar dCKK = new DatabaseClassKonusulanKanallar(getActivity());
                dCKK.open();
                List<String> kanallar = dCKK.databasedenkanalcek();
                List<String> modlar = dCKK.databasedenmodcek();
                List<String> likedurum = dCKK.databasedenlikecek();
                List<String> kanalurller = dCKK.databasedenkanalurlcek();
                List<String> yenimesajvarmilar = dCKK.databasedenyenimesajvarmicek();
                List<String> kacyenimesajlar = dCKK.databasedenkacyenimesajcek();
                dCKK.close();
                for (int i = kanallar.size() - 1; i > -1; i--) {
                    if (likedurum.get(i).equals("yes")) {
                        if (modlar.get(i).equals("official")) {
                            Kanal kanal = new Kanal(true);
                            kanal.setKanaladi(kanallar.get(i));
                            kanal.setLikedurumu(1);
                            kanal.setKanalurl(kanalurller.get(i));
                            kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                            kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                            konusulankanallar.add(kanal);
                        } else {
                            Kanal kanal = new Kanal(false);
                            kanal.setKanaladi(kanallar.get(i));
                            kanal.setLikedurumu(1);
                            kanal.setKanalurl(kanalurller.get(i));
                            kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                            kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                            konusulankanallar.add(kanal);
                        }
                    } else if (likedurum.get(i).equals("no")) {
                        if (modlar.get(i).equals("official")) {
                            Kanal kanal = new Kanal(true);
                            kanal.setKanaladi(kanallar.get(i));
                            kanal.setLikedurumu(0);
                            kanal.setKanalurl(kanalurller.get(i));
                            kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                            kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                            konusulankanallar.add(kanal);
                        } else {
                            Kanal kanal = new Kanal(false);
                            kanal.setKanaladi(kanallar.get(i));
                            kanal.setLikedurumu(0);
                            kanal.setKanalurl(kanalurller.get(i));
                            kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                            kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                            konusulankanallar.add(kanal);
                        }
                    }
                }

                gecmisKanalAdapter = new GecmisKanalAdapter(getActivity(), konusulankanallar);
                if (viewGroup instanceof AbsListView) {
                    absListView.setAdapter(new QuickReturnAdapter(gecmisKanalAdapter));
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
                    attacher.addOnScrollListener(PageFragment1.this);
                    attacher.setOnItemClickListener(PageFragment1.this);
                    attacher.setOnItemLongClickListener(PageFragment1.this);
                }
            }
        });

        final String serverid = SharedPrefIdAl();
        if (!serverid.equals("defaultserverid")) {
            KanallariCek kC = new KanallariCek();
            kC.execute(serverid);
        } else {
            Thread a = new Thread() {
                public void run() {
                    try {
                        sleep(3000);
                        if (!serverid.equals("defaultid")) {
                            KanallariCek kC = new KanallariCek();
                            kC.execute(serverid);
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

    private String SharedPrefIdAl() {
        SharedPreferences sP = getActivity().getSharedPreferences("kullaniciverileri", Context.MODE_PRIVATE);
        return sP.getString("serverid", "defaultserverid");
    }

    public int dpToPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + 0.5f);
    }

    private void icerigiYenile() {
        KanallariYenidenCek kYC = new KanallariYenidenCek();
        kYC.execute(SharedPrefIdAl());
        swipe.setRefreshing(false);
    }

    private void kanalEkleme() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogyenikanal);
        dialog.getWindow().setDimAmount(0.7f);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        kanalresmiimage = (ImageView) dialog.findViewById(R.id.imageView14);
        final EditText etv1 = (EditText) dialog.findViewById(R.id.editText3);
        ImageButton buton1 = (ImageButton) dialog.findViewById(R.id.button10);
        ImageButton kanalresmibuton = (ImageButton) dialog.findViewById(R.id.button26);
        kanalresmibuton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openImageIntent();
            }
        });
        buton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (etv1.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Kanal adı verin", Toast.LENGTH_SHORT).show();
                } else {
                    String kurulacakkanaladi = etv1.getText().toString();
                    if (kanalresmiimage.getDrawable() == null) {
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.shappy_now);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] b = baos.toByteArray();
                        image = Base64.encodeToString(b, Base64.DEFAULT);
                    }
                    YeniKanalEkle yenikanalekle = new YeniKanalEkle();
                    yenikanalekle.execute(kurulacakkanaladi);
                    dialog.cancel();
                }
            }
        });
    }

    private void openImageIntent() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = "temphoto";
        final File sdImageMainDirectory = new File(root, fname);
        fileName = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Resmi Nerden Alacaksın");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, 100);
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

    public void aramaYap(String arananveri) {
        if (hangibolumdesin == 1) {
            kanalAdapter.getFilter().filter(arananveri);
        } else if (hangibolumdesin == 2) {
            gecmisKanalAdapter.getFilter().filter(arananveri);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bitmap bitmap = null;
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 100) {
                final boolean isCamera;
                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    ExifInterface ei = null;
                    try {
                        ei = new ExifInterface(fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
                    Bitmap bit;
                    switch(orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            bit=rotateImage(fileName, 90);
                            selectedImageUri = getImageUri(getContext(),bit);
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            bit=rotateImage(fileName, 180);
                            selectedImageUri=getImageUri(getContext(),bit);
                            break;
                    }
                    MyImageActivity(selectedImageUri);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    MyImageActivity(selectedImageUri);
                }
            }
            if (requestCode == 200) {
                Bundle extras = data.getExtras();
                // get the cropped bitmap
                Bitmap thePic = extras.getParcelable("data");
                Bitmap a = Bitmap.createScaledBitmap(thePic, 800, 525, false);
                kanalresmiimage.setImageBitmap(a);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thePic.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                image = Base64.encodeToString(b, Base64.DEFAULT);
            }
        }
    }
    private Bitmap rotateImage(String pathToImage,int angle) {

        int rotation = angle;
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);

        Bitmap sourceBitmap = BitmapFactory.decodeFile(pathToImage);
        return Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix, true);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void MyImageActivity(Uri urri) {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(urri, "image/*");
        cropIntent.putExtra("crop", "false");
        cropIntent.putExtra("aspectX", 3);
        cropIntent.putExtra("aspectY", 2);
        cropIntent.putExtra("scale", false);
        cropIntent.putExtra("outputX", 600);
        cropIntent.putExtra("outputY", 400);
        cropIntent.putExtra("return-data", true);
        startActivityForResult(cropIntent, 200);
    }

    public void konustugumkanallar() {
        Log.i("tago", "konustugumkanallar");
        swipe.setEnabled(false);
        konusulankanallar.clear();
        DatabaseClassKonusulanKanallar dCKK = new DatabaseClassKonusulanKanallar(getActivity());
        dCKK.open();
        List<String> kanallar = dCKK.databasedenkanalcek();
        List<String> modlar = dCKK.databasedenmodcek();
        List<String> likedurum = dCKK.databasedenlikecek();
        List<String> kanalurller = dCKK.databasedenkanalurlcek();
        List<String> yenimesajvarmilar = dCKK.databasedenyenimesajvarmicek();
        List<String> kacyenimesajlar = dCKK.databasedenkacyenimesajcek();
        dCKK.close();
        for (int i = kanallar.size() - 1; i > -1; i--) {
            if (likedurum.get(i).equals("yes")) {
                if (modlar.get(i).equals("official")) {
                    Kanal kanal = new Kanal(true);
                    kanal.setKanaladi(kanallar.get(i));
                    kanal.setLikedurumu(1);
                    kanal.setKanalurl(kanalurller.get(i));
                    kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                    konusulankanallar.add(kanal);
                } else {
                    Kanal kanal = new Kanal(false);
                    kanal.setKanaladi(kanallar.get(i));
                    kanal.setLikedurumu(1);
                    kanal.setKanalurl(kanalurller.get(i));
                    kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                    konusulankanallar.add(kanal);
                }
            } else if (likedurum.get(i).equals("no")) {
                if (modlar.get(i).equals("official")) {
                    Kanal kanal = new Kanal(true);
                    kanal.setKanaladi(kanallar.get(i));
                    kanal.setLikedurumu(0);
                    kanal.setKanalurl(kanalurller.get(i));
                    kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                    konusulankanallar.add(kanal);
                } else {
                    Kanal kanal = new Kanal(false);
                    kanal.setKanaladi(kanallar.get(i));
                    kanal.setLikedurumu(0);
                    kanal.setKanalurl(kanalurller.get(i));
                    kanal.setYenimesajvarmi(yenimesajvarmilar.get(i));
                    kanal.setKacyenimesaj(kacyenimesajlar.get(i));
                    konusulankanallar.add(kanal);
                }
            }
        }

        gecmisKanalAdapter = new GecmisKanalAdapter(getActivity(), konusulankanallar);
        if (viewGroup instanceof AbsListView) {
            absListView.setAdapter(new QuickReturnAdapter(gecmisKanalAdapter));
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
            attacher.addOnScrollListener(PageFragment1.this);
            attacher.setOnItemClickListener(PageFragment1.this);
            attacher.setOnItemLongClickListener(PageFragment1.this);
        }
    }

    private class KanallariCek extends AsyncTask<String, Void, String> {
        String charset;
        String query;

        protected String doInBackground(String... params) {
            charset = "utf-8";
            String param1 = "id";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return kanallarigor(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private String kanallarigor(String id) {
            HttpURLConnection sconnection = null;
            try {
                sconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/get_official_channels.php?id=" + id).openConnection();
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
                try {
                    int a = sconnection.getResponseCode();
                    String b = sconnection.getResponseMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channelbaba = new ArrayList<>();
                BufferedReader in;
                if (sconnection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(sconnection.getInputStream()));
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setDate("date");
                        kanal.setId(object.optString("id"));
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        channelbaba.add(kanal);
                    }
                } else {
                    in = new BufferedReader(new InputStreamReader(sconnection.getErrorStream()));
                    Log.i("tago", "Error Stream");
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setId(object.optString("id"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        channelbaba.add(kanal);
                    }
                }
                in.close();
                Log.i("tago", "Page Fragment official gor inputline yazd�m");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("tago", "Page Fragment official gor yazamad�m");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tago", "json Exception");
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/get_channels.php?id=" + id).openConnection();
                Log.i("tago", "Page Fragment1 kanalları gor bagı kuruldu");
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
                    Log.i("tago", "rerere" + a + " " + b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Log.i("tago", "InputStream");
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setKanalurl(object.optString("photo"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setId(object.optString("id"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        channelbaba.add(kanal);
                    }
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    Log.i("tago", "Error Stream");
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setKanalurl(object.optString("photo"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setId(object.optString("id"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        channelbaba.add(kanal);
                    }
                }
                in.close();
                Log.i("tago", "Page Fragment kanalları gor inputline yazd�m");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("tago", "Page Fragment kanalları gor yazamad�m");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tago", "json Exception");
            }

            return "inputline";
        }

        protected void onPostExecute(String s) {
            kanalAdapter = new KanalAdapter(getActivity(), channelbaba);
            if (viewGroup instanceof AbsListView) {
                int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                absListView.setAdapter(new QuickReturnAdapter(kanalAdapter, numColumns));
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
                attacher.addOnScrollListener(PageFragment1.this);
                attacher.setOnItemClickListener(PageFragment1.this);
                attacher.setOnItemLongClickListener(PageFragment1.this);
            }
        }
    }

    private class KanallariYenidenCek extends AsyncTask<String, Void, String> {
        String charset;
        String query;

        protected String doInBackground(String... params) {
            charset = "utf-8";
            String param1 = "id";
            try {
                query = String.format("param1=%s", URLEncoder.encode(param1, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return kanallarigor(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private String kanallarigor(String id) {
            HttpURLConnection sconnection = null;
            try {
                sconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy//get_official_channels.php?id=" + id).openConnection();
                Log.i("tago", "Page Fragment1 official kanalları gor bagı kuruldu");
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
                try {
                    int a = sconnection.getResponseCode();
                    String b = sconnection.getResponseMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                channelbaba = new ArrayList<>();
                BufferedReader in;
                if (sconnection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(sconnection.getInputStream()));
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setDate("date");
                        kanal.setId(object.optString("id"));
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        channelbaba.add(kanal);
                    }
                } else {
                    in = new BufferedReader(new InputStreamReader(sconnection.getErrorStream()));
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(true);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setId(object.optString("id"));
                        kanal.setLikedurumu(object.optInt("like_status"));
                        channelbaba.add(kanal);
                    }
                }
                in.close();
                Log.i("tago", "Page Fragment yeniden official gor inputline yazd�m");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("tago", "Page Fragment yeniden official gor yazamad�m");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tago", "json Exception");
            }

            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/get_channels.php?id=" + id).openConnection();
                Log.i("tago", "Page Fragment1 kanalları yeniden gor bagı kuruldu");
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
                    Log.i("tago", "rerere1" + a + " " + b);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader in;
                if (connection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setKanalurl(object.optString("photo"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setId(object.optString("id"));
                        channelbaba.add(kanal);
                    }
                } else {
                    in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                    String inputline = in.readLine();
                    JSONArray jsono = new JSONArray(inputline);
                    for (int i = 0; i < jsono.length(); i++) {
                        JSONObject object = jsono.getJSONObject(i);
                        Kanal kanal = new Kanal(false);
                        kanal.setKanaladi(object.optString("name"));
                        kanal.setKanalurl(object.optString("photo"));
                        kanal.setDate("date");
                        kanal.setDistance(object.optString("distance"));
                        kanal.setLikesayisi(object.optInt("like_count"));
                        kanal.setId(object.optString("id"));
                        channelbaba.add(kanal);
                    }
                }
                in.close();
                Log.i("tago", "Page Fragment yeniden kanalları gor inputline yazd�m");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("tago", "Page Fragment yeniden kanalları gor yazamad�m");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("tago", "json Exception");
            }

            return "inputline";
        }

        protected void onPostExecute(String s) {
            kanalAdapter = new KanalAdapter(getActivity(), channelbaba);
            if (viewGroup instanceof AbsListView) {
                int numColumns = (viewGroup instanceof GridView) ? 3 : 1;
                absListView.setAdapter(new QuickReturnAdapter(kanalAdapter, numColumns));
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
                attacher.addOnScrollListener(PageFragment1.this);
                attacher.setOnItemClickListener(PageFragment1.this);
                attacher.setOnItemLongClickListener(PageFragment1.this);
            }
        }
    }

    private class YeniKanalEkle extends AsyncTask<String, Void, String> {
        String charset;
        String query;
        boolean kanalikur;

        protected String doInBackground(String... params) {
            charset = "utf-8";
            String param1 = "id";
            String param2 = "name";
            try {
                query = String.format("param1=%s&param2=%s", URLEncoder.encode(param1, charset), URLEncoder.encode(param2, charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i("tago", "Page Fragment1 kanal ekleme ba�lat�ld�");
            try {
                nameofChannel = params[0];
                kanalvarmiyokmukontrol(params[0]);
                return kanaliekle(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return "olmadi";
            }
        }

        private void kanalvarmiyokmukontrol(String kanaladi) {
            Log.i("tago", "PageFragment1 kanalvarmiyokmukontrol");
            HttpURLConnection zconnection = null;
            try {
                zconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/kanalserbest.php?id=" + SharedPrefIdAl() + "&name=" + kanaladi)
                        .openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            zconnection.setDoInput(true);
            zconnection.setDoOutput(true);
            zconnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            zconnection.setRequestProperty("Accept", "* /*");
            zconnection.setRequestProperty("Accept-Charset", charset);
            zconnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
            try {
                OutputStream output = new BufferedOutputStream(zconnection.getOutputStream());
                output.write(query.getBytes(charset));
                output.close();
                int a = zconnection.getResponseCode();
                String b = zconnection.getResponseMessage();
                BufferedReader in;
                if (zconnection.getResponseCode() == 200) {
                    in = new BufferedReader(new InputStreamReader(zconnection.getInputStream()));
                    Log.i("tago", "InputStream");
                    String inputline = in.readLine();
                    if (inputline.equals("yok")) {
                        Log.i("tago", "PageFragment1 kanal yok");
                        kanalikur = true;
                    } else if (inputline.equals("var")) {
                        kanalikur = false;
                    } else {
                        kanalikur = true;
                        Log.e("tago", "inputline sıkıntılı:" + inputline);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String kanaliekle(String kanaladi) {
            if (kanalikur) {
                HttpURLConnection sconnection = null;
                try {
                    sconnection = (HttpURLConnection) new URL("http://185.22.184.15/shappy/add_channel.php?id=" + SharedPrefIdAl()
                            + "&name=" + URLEncoder.encode(kanaladi, charset)).openConnection();
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
                    int a = sconnection.getResponseCode();
                    String b = sconnection.getResponseMessage();
                    uploadFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "Caxobaxo";
        }

        public void uploadFile() throws IOException {
            Log.i("tago", "PageFragment1 resim gonderme islemi");
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("placeID", nameofChannel);
            postDataParams.put("img", image);
            postDataParams.put("id", SharedPrefIdAl());
            URL url;
            String response = "";
            try {
                url = new URL("http://185.22.184.15/shappy/place_photos/place_picture.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            return result.toString();
        }

        protected void onPostExecute(String s) {
            kanaleklemebitti = true;
            Log.i("tago", "PageFragment1 kanaleklemebitti" + String.valueOf(kanaleklemebitti));
        }
    }
}
