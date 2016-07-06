package com.qcsh.fuxiang.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qcsh.fuxiang.R;

/**
 * Created by Administrator on 2015/10/16.
 */
public class MyToast extends Toast {


    private MyToast(Context context){
        super(context);
    }

    public static void makeText(Context context,String text){
        MyToast result = new MyToast(context);
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast, null);
        TextView tv = (TextView)v.findViewById(R.id.content);
        tv.setText(text);
        result.setDuration(Toast.LENGTH_SHORT);
        result.setView(v);
        result.show();
    }

}
