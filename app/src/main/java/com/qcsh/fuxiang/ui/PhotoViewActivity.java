package com.qcsh.fuxiang.ui;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.PhotoViewPagerAdapter;
import com.qcsh.fuxiang.widget.HackyViewPager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * 图片查看器，支持单张和多张图片
 */
public class PhotoViewActivity extends BaseActivity {
    private ViewPager viewPage;
    private TextView imageSize;
    //    List<Bitmap> listViews = new ArrayList<Bitmap>();
    List<View> listViews = new ArrayList<View>();
    private PhotoViewPagerAdapter adapter;
    private MyOnPageChangeListener changeListener;
    /**
     * 图片地址集合
     */
    public static final String IMAGES_LIST = "iamgeList";
    private ArrayList<String> imagesList;
    /**
     * 当前图片路径
     */
    public static final String IMAGE_PATG = "imagePath";
    private String imagePath;
    /**
     * 图片类型
     */
    public static final String IMAGE_TYPE = "imageType";
    public static final int LOCALIMG = 0;
    public static final int HTTPIMG = 1;
    private int imageType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);
        imagesList = getIntent().getStringArrayListExtra(IMAGES_LIST);
        imagePath = getIntent().getStringExtra(IMAGE_PATG);
        imageType = getIntent().getIntExtra(IMAGE_TYPE, 0);
        Log.i("imagesList", imagesList.toString());
        Log.i("imagePath", imagePath);
        initView();
        initData();
    }

    private void initView() {
        viewPage = (HackyViewPager) findViewById(R.id.view_page);
        imageSize = (TextView) findViewById(R.id.image_size);
        adapter = new PhotoViewPagerAdapter(listViews);
        viewPage.setAdapter(adapter);
        changeListener = new MyOnPageChangeListener();
        viewPage.setOnPageChangeListener(changeListener);
    }

    private void initData() {

        if (null != imagesList && !imagesList.isEmpty()) {
            for (int i = 0; i < imagesList.size(); i++) {
                View view = LayoutInflater.from(getApplicationContext()).inflate(
                        R.layout.layout_photoview, null);
                PhotoView iv = (PhotoView) view.findViewById(R.id.view_image);
                switch (imageType) {
                    case LOCALIMG:
                        File file = new File(imagesList.get(i));
                        if (file.exists()) {
                            iv.setImageBitmap(BitmapFactory.decodeFile(imagesList.get(i)));
                        }
                        break;
                    case HTTPIMG:
                        ImageLoader.getInstance().displayImage(AppConfig.getOriginalImage(imagesList.get(i)), iv);
                        break;
                }
                listViews.add(view);
//                ImageLoader.getInstance().loadImage(/*AppConfig.getOriginalImage(*/imagesList.get(i), new SimpleImageLoadingListener()
//                {
//                    @Override
//                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                        super.onLoadingComplete(imageUri, view, loadedImage);
//                        listViews.add(loadedImage);
//                    }
//                });

            }
            imageSize.setText("1/" + imagesList.size());
            if (!TextUtils.isEmpty(imagePath)) {
                int item = imagesList.indexOf(imagePath);
                viewPage.setCurrentItem(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            int intem = arg0 + 1;
            imageSize.setText(intem + "/" + imagesList.size());
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
