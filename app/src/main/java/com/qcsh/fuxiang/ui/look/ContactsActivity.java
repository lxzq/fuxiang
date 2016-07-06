package com.qcsh.fuxiang.ui.look;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.TextWatcher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.text.Editable;

import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.ui.BaseActivity;
import com.qcsh.fuxiang.widget.CustomLetterListView;
import com.qcsh.fuxiang.widget.MyConfirmDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 通讯录界面
 * Created by Administrator on 2015/9/10.
 */
public class ContactsActivity extends BaseActivity {

    private ListView listview;
    private CustomLetterListView letterListView;
    private TextView titleView;
    private ImageButton backB;
    private Button actionB;
    private EditText search;

    private ArrayList<ContentValues> dataList;
    private ContactAdapter adapter;
    private HashSet<ContentValues> checkDataList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        dataList = new ArrayList<ContentValues>();
        checkDataList = new HashSet<ContentValues>();

        initView();
        initData();
    }

    private void initView(){
        listview = (ListView)findViewById(R.id.list_view);
        letterListView = (CustomLetterListView)findViewById(R.id.letter_list_view);
        titleView = (TextView)findViewById(R.id.action_bar_title);
        backB = (ImageButton)findViewById(R.id.action_bar_back);
        actionB = (Button)findViewById(R.id.action_bar_action);
        search = (EditText)findViewById(R.id.search_text);

        adapter = new ContactAdapter(this);
        listview.setAdapter(adapter);
        letterListView.setOnTouchingLetterChangedListener(new LetterListViewListener());

        titleView.setText("通讯录");
        actionB.setText("发送");
        backB.setVisibility(View.VISIBLE);
        backB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMS();
            }
        });

      /*  search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String key = s.toString();
                if (TextUtils.isEmpty(key)) {
                  // initData();
                }else {onSearch();
                }

            }
        })*/;
    }

    private void initData(){
        MyAsyncQueryHandler asyncQuery = new MyAsyncQueryHandler(
                getContentResolver());
        Uri uri = Uri.parse("content://com.android.contacts/data/phones");
        String[] projection = { "_id", "display_name", "data1", "sort_key" };
        asyncQuery.startQuery(0, null, uri, projection, null, null,
                "sort_key COLLATE LOCALIZED asc");
    }

    private void sendSMS(){
        if(!checkDataList.isEmpty()){
            StringBuilder numbersBuff = new StringBuilder();
            for (ContentValues cv : checkDataList) {
                numbersBuff.append(cv.getAsString("number")).append(";");
            }
            String smsphone = numbersBuff.toString().substring(0,numbersBuff.lastIndexOf(";"));
            Uri uri = Uri.parse("smsto:" + smsphone);
            Intent it = new Intent(Intent.ACTION_SENDTO,uri);
            it.putExtra("sms_body", "测试");
            startActivity(it);
        }else{
            UIHelper.ToastMessage(this,"请选择联系人");
        }
    }

    private void onSearch() {
        EditText searchT = (EditText)findViewById(R.id.search_text);
        Editable searchEditable = searchT.getText();
        assert searchEditable != null;
        String searchContent = searchEditable.toString().trim();
        if (TextUtils.isEmpty(searchContent)) {
            // 隐藏软键盘
            View focusView = getCurrentFocus();
            if (focusView != null) {
                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(focusView.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
            }
            return;
        }
        if (dataList.size() == 0)
            return;

        List<ContentValues> tempList = new ArrayList<ContentValues>();// 存放从通讯录中获取的数据
        String phone;
        String name;
        for (ContentValues contentValues : dataList) {
            phone = contentValues.getAsString("number");
            name = contentValues.getAsString("name");
            assert phone != null;
            assert name != null;
            if (phone.contains(searchContent) || name.contains(searchContent)) {
                tempList.add(contentValues);
            }
        }
        if(!tempList.isEmpty()){
        dataList.clear();
        dataList.addAll(tempList);
        adapter.updateAlphaIndexer();
        adapter.notifyDataSetChanged();
        tempList.clear();
        }
        tempList = null;
    }

    private class ContactAdapter extends BaseAdapter{

        private LayoutInflater inflater;
        private HashMap<String, Integer> alphaIndexer;

        private ContactAdapter(Context context) {
            this.inflater = LayoutInflater.from(context);
            alphaIndexer = new HashMap<String, Integer>();
            updateAlphaIndexer();
        }

        private void updateAlphaIndexer() {
            alphaIndexer.clear();
            letterListView.letters.clear();
            for (int i = 0; i < dataList.size(); i++) {
                // 当前汉语拼音首字母
                String currentStr = dataList.get(i).getAsString("sortKey");// 获取值并将它转化为字符串
                // 上一个汉语拼音首字母，如果不存在为“ ”
                String previewStr = (i - 1) >= 0 ? dataList.get(i - 1).getAsString("sortKey") : " ";
                assert previewStr != null;
                if (!previewStr.equals(currentStr)) {
                    alphaIndexer.put(currentStr, i);
                    letterListView.letters.add(currentStr);
                }
            }
            letterListView.invalidate();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HolderView holderView;
            if(null == convertView){
                holderView = new HolderView();
                convertView = inflater.inflate(R.layout.activity_contacts_item,null);
                holderView.alphaView = (TextView)convertView.findViewById(R.id.alpha);
                holderView.nameView = (TextView)convertView.findViewById(R.id.name);
                holderView.phoneView = (TextView)convertView.findViewById(R.id.phone);
                holderView.checkBox = (CheckBox)convertView.findViewById(R.id.checkbox);
                convertView.setTag(holderView);
            }else {
                holderView = (HolderView) convertView.getTag();
            }

            final ContentValues contentValues = dataList.get(position);
            holderView.nameView.setText(contentValues.getAsString("name"));
            holderView.phoneView.setText(contentValues.getAsString("number"));

            // 设置字母状态
            String currentStr = contentValues.getAsString("sortKey");
            String previewStr = (position - 1) >= 0 ? dataList.get(
                    position - 1).getAsString("sortKey") : " ";
            String nextStr = (position + 1) <= (dataList.size() - 1) ? dataList
                    .get(position + 1).getAsString("sortKey") : "";

            assert previewStr != null;
            if (!previewStr.equals(currentStr)) {
                holderView.alphaView.setVisibility(View.VISIBLE);
                holderView.alphaView.setText(currentStr);
            } else {
                holderView.alphaView.setVisibility(View.GONE);
            }

            holderView.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        checkDataList.add(contentValues);
                    }else{
                        checkDataList.remove(contentValues);
                    }
                }
            });

            if(checkDataList.contains(contentValues)){
                holderView.checkBox.setChecked(true);
            }else{
                holderView.checkBox.setChecked(false);
            }

            return convertView;
        }
    }

    private static class HolderView {
        TextView alphaView;
        TextView nameView;
        TextView phoneView;
        CheckBox checkBox;

    }

    private class LetterListViewListener implements CustomLetterListView.OnTouchingLetterChangedListener{

        @Override
        public void onTouchingLetterChanged(String s) {
            if (adapter != null && adapter.alphaIndexer.get(s) != null) {
                int position = adapter.alphaIndexer.get(s);
                listview.setSelection(position);
            }
        }
    }

    private class MyAsyncQueryHandler extends AsyncQueryHandler {

        private MyAsyncQueryHandler(ContentResolver cr){
           super(cr);
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            super.onQueryComplete(token, cookie, cursor);
            // 1.查询出来的结果中不会包括只有名字，没有手机号的
            // 2.只有手机号的，名字和Key值都会是手机号
            // 所以取出的字段都会有值
            dataList.clear();
            if(null != cursor && cursor.getCount() >  0){
                cursor.moveToFirst();
                ArrayList<ContentValues> tempList = new ArrayList<ContentValues>();// 存放从通讯录中获取到的#号数据
                for(int i = 0 ; i < cursor.getCount() ; i++){
                    ContentValues contentValues = new ContentValues();
                    cursor.moveToPosition(i);
                    String name = cursor.getString(1);
                    String number = cursor.getString(2);
                    String sortKey = cursor.getString(3);
                    sortKey = getAlpha(sortKey);
                    if(number.startsWith("+86")){
                        number = number.substring(3);
                    }
                    contentValues.put("name",name);
                    contentValues.put("number",number);
                    contentValues.put("sortKey",sortKey);

                    if("#".equals(sortKey)){
                        tempList.add(contentValues);
                    }else{
                        dataList.add(contentValues);
                    }

                }

                if(!tempList.isEmpty()){
                    dataList.addAll(tempList);
                }

                tempList.clear();
                tempList = null;
                cursor.close();
                adapter.updateAlphaIndexer();
                adapter.notifyDataSetChanged();
            }else {
                MyConfirmDialog myConfirmDialog = new MyConfirmDialog(ContactsActivity.this
                ,"提示","请在权限管理中授予允许读取联系人权限，然后关闭家家帮重新进入应用",
                        new MyConfirmDialog.OnCancelDialogListener(){
                            @Override
                            public void onCancel() {
                            }
                        },
                        new MyConfirmDialog.OnConfirmDialogListener(){
                            @Override
                            public void onConfirm() {
                                Uri packageURI = Uri.parse("package:" + getApplication().getPackageName());
                                Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
                myConfirmDialog.show();
            }

        }
    }

    // 获得汉语拼音首字母,取到的值为空时或者不是26个字母时返回#
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else {
            return "#";
        }
    }
}
