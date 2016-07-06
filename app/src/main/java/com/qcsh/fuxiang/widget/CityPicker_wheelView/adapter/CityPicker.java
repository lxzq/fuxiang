package com.qcsh.fuxiang.widget.CityPicker_wheelView.adapter;



import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.widget.CityPicker_wheelView.AreaDataUtil;
import com.qcsh.fuxiang.widget.CityPicker_wheelView.MWheelView;

import java.util.ArrayList;

/**
 * Created by wo on 15/9/21.
 */
public class CityPicker extends LinearLayout{

    private static final int REFRESH_VIEW = 0x001;

    private MWheelView mProvincePicker;
    private MWheelView mCityPicker;

    private int mCurrProvinceIndex = -1;
    private int mTemCityIndex = -1;

    private AreaDataUtil mAreaDataUtil;
    private ArrayList<String> mProvinceList = new ArrayList<String>();

    public CityPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAreaInfo();
    }

    public CityPicker(Context context) {
        this(context, null);
    }

    private void getAreaInfo() {
        mAreaDataUtil = new AreaDataUtil();
        mProvinceList = mAreaDataUtil.getProvinces();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.city_picker, this);

        mProvincePicker = (MWheelView) findViewById(R.id.province);
        mCityPicker = (MWheelView) findViewById(R.id.city);

        mProvincePicker.setData(mProvinceList);
        mProvincePicker.setDefault(0);

        String defaultProvince = mProvinceList.get(0);
        mCityPicker.setData(mAreaDataUtil.getCitysByProvince(defaultProvince));
        mCityPicker.setDefault(1);

        mProvincePicker.setOnSelectListener(new MWheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                if (text.equals("") || text == null)
                    return;
                if (mCurrProvinceIndex != id) {
                    mCurrProvinceIndex = id;
                    String selectProvince = mProvincePicker.getSelectedText();
                    if (selectProvince == null || selectProvince.equals(""))
                        return;
                    // get city names by province
                    ArrayList<String> citys = mAreaDataUtil
                            .getCitysByProvince(mProvinceList.get(id));
                    if (citys.size() == 0) {
                        return;
                    }

                    mCityPicker.setData(citys);

                    if (citys.size() > 1) {
                        //if city is more than one,show start index == 1
                        mCityPicker.setDefault(1);
                    } else {
                        mCityPicker.setDefault(0);
                    }
                }

            }

            @Override
            public void selecting(int id, String text) {
            }
        });

        mCityPicker.setOnSelectListener(new MWheelView.OnSelectListener() {

            @Override
            public void endSelect(int id, String text) {
                if (text.equals("") || text == null)
                    return;
                if (mTemCityIndex != id) {
                    mTemCityIndex = id;
                    String selectCity = mCityPicker.getSelectedText();
                    if (selectCity == null || selectCity.equals(""))
                        return;
                    int lastIndex = Integer.valueOf(mCityPicker.getListSize());
                    if (id > lastIndex) {
                        mCityPicker.setDefault(lastIndex - 1);
                    }
                }
            }

            @Override
            public void selecting(int id, String text) {

            }
        });

    }
}
