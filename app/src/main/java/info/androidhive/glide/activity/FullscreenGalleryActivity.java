package info.androidhive.glide.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;
import com.liuguangqiang.swipeback.SwipeBackLayout;
import info.androidhive.glide.R;
import info.androidhive.glide.helper.ExtendedViewPager;
import info.androidhive.glide.model.Image;
import org.w3c.dom.Text;


public class FullscreenGalleryActivity extends AppCompatActivity {
    private String TAG = FullscreenGalleryActivity.class.getSimpleName();
    private ArrayList<Image> images;
    private ExtendedViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private TextView lblCount;
    private int selectedPosition = 0;

    static FullscreenGalleryActivity newInstance() {
        FullscreenGalleryActivity f = new FullscreenGalleryActivity();
        return f;
    }

    //@Override
    //public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.fragment_image_slider);

        //setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        //View v = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = findViewById(R.id.viewpager);
        lblCount = findViewById(R.id.lbl_count);

        if(this.getIntent().getExtras() != null) {
            Bundle bundle = this.getIntent().getExtras().getBundle("bundle");

            Log.i("test", String.valueOf(this.getIntent().getExtras()));

            images = (ArrayList<Image>) bundle.getSerializable("images");
            selectedPosition = bundle.getInt("position");
        }

        Log.e(TAG, "position: " + selectedPosition);
        Log.e(TAG, "images size: " + images);

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        //return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewPager.getBackground().setAlpha(255);
    }

    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    private void displayMetaInfo(int position) {
        lblCount.setText((position + 1) + " / " + images.size());
    }

    //	adapter
    public class MyViewPagerAdapter extends PagerAdapter{

        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            final ProgressBar progressBar = view.findViewById(R.id.progressBar);
            SwipeBackLayout swipeBackLayout = view.findViewById(R.id.swipeBackLayout);
            viewPager.getBackground().setAlpha(255);

            swipeBackLayout.setOnSwipeBackListener(new SwipeBackLayout.SwipeBackListener() {
                @Override
                public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
                    int scale = (int) -(255 * fractionAnchor);
                    int alpha =  scale != 0 ? scale : 255;
                    viewPager.getBackground().setAlpha(alpha);
                }

            });


            PhotoView imageViewPreview = view.findViewById(R.id.image_preview);

            Image image = images.get(position);


            Glide.with(getApplicationContext())
                .asBitmap()
                .load(image.getLarge())
                .apply(new RequestOptions()
                        .fitCenter()
                )

                .into(new BitmapImageViewTarget(imageViewPreview) {
                    public void onResourceReady(Bitmap bitmap, Transition transition) {
                        super.onResourceReady(bitmap, transition);
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

            TextView lblTitle = view.findViewById(R.id.title);
            TextView lblDate = view.findViewById(R.id.date);

            if(image.getName() != "") {
                lblTitle.setText(image.getName());
            }else{
                lblTitle.setVisibility(View.GONE);
            }

            if(image.getTimestamp() != "") {
                lblDate.setText(image.getTimestamp());
            }else{
                lblDate.setVisibility(View.GONE);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == ((View) obj);
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
