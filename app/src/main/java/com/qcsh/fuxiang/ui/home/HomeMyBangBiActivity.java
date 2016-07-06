package com.qcsh.fuxiang.ui.home;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.adapter.InviteFriendAdapter;
import com.qcsh.fuxiang.adapter.MyBangBiAdapter;
import com.qcsh.fuxiang.common.NetworkHelper;
import com.qcsh.fuxiang.ui.BaseActivity;

/**
 * Created by wo on 15/9/20.
 */
public class HomeMyBangBiActivity extends BaseActivity {

    private ImageButton leftBtn;
    private TextView title;
    private Button rightBtn;

    private ListView listView;
    private MyBangBiAdapter adapter;

    private TextView bangbiCountText;
    private TextView bangBiDetailText;

    public static HomeMyBangBiActivity newInstance() {
        return new HomeMyBangBiActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_mybangbi);
        initToolBar();
        initView();
    }

    private void initToolBar() {
        leftBtn = (ImageButton) findViewById(R.id.action_bar_back);
        title = (TextView) findViewById(R.id.action_bar_title);
        rightBtn = (Button) findViewById(R.id.action_bar_action);
        leftBtn.setVisibility(View.VISIBLE);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rightBtn.setVisibility(View.INVISIBLE);
        title.setText("我的帮币");
    }

    private void initView() {

        bangbiCountText = (TextView)findViewById(R.id.ic_bangbi);

        bangBiDetailText = (TextView)findViewById(R.id.ic_bangbicount);
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_wojia_magnifier);
        float swidthf = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
        int swidth = Math.round(swidthf);
        drawable.setBounds(0, 0, swidth, swidth);
        bangBiDetailText.setCompoundDrawables(drawable,null,null,null);
        bangBiDetailText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppIntentManager.startHomeBangBiMoreActivity(HomeMyBangBiActivity.this);
            }
        });
        adapter = new MyBangBiAdapter(HomeMyBangBiActivity.this);
        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);
    }
}
