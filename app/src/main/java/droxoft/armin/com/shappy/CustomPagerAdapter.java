package droxoft.armin.com.shappy;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class CustomPagerAdapter extends PagerAdapter {

    Context context;
    int[] resimlerres;
    LayoutInflater layoutInflater;

    public CustomPagerAdapter(Context context ,int[] resimlerres ) {
        this.context = context;
        this.resimlerres = resimlerres;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return resimlerres.length;
    }

    public boolean isViewFromObject(View view, Object object) {
        return view ==object;
    }
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = layoutInflater.inflate(R.layout.taniticiresim, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
        imageView.setImageResource(resimlerres[position]);

        container.addView(itemView);

        return itemView;
    }
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
