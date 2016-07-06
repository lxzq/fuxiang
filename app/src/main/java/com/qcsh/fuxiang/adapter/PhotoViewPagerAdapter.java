package com.qcsh.fuxiang.adapter;

import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by WWW on 2015/8/21.
 */
public class PhotoViewPagerAdapter extends PagerAdapter {
    //private List<Bitmap> list;
    private List<View> list;

    public PhotoViewPagerAdapter(List<View> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0) {
            return list.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
//        PhotoView photoView = new PhotoView(container.getContext());
//        photoView.setImageBitmap(list.get(position));
//        // Now just add PhotoView to ViewPager and return it
//        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        return photoView;
        container.addView(list.get(position));
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
