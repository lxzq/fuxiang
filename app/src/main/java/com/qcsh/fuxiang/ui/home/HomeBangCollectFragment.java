package com.qcsh.fuxiang.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qcsh.fuxiang.AppConfig;
import com.qcsh.fuxiang.AppIntentManager;
import com.qcsh.fuxiang.R;
import com.qcsh.fuxiang.api.ApiClient;
import com.qcsh.fuxiang.api.ApiConfig;
import com.qcsh.fuxiang.api.ApiResponseHandler;
import com.qcsh.fuxiang.api.DataEntity;
import com.qcsh.fuxiang.api.ErrorEntity;
import com.qcsh.fuxiang.bean.bang.BangInfo;
import com.qcsh.fuxiang.common.StringUtils;
import com.qcsh.fuxiang.common.UIHelper;
import com.qcsh.fuxiang.generalAdapter.CommonAdapter;
import com.qcsh.fuxiang.generalAdapter.ViewHolder;
import com.qcsh.fuxiang.ui.BaseFragment;
import com.qcsh.fuxiang.widget.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WWW on 2015/10/28.
 */
public class HomeBangCollectFragment extends BaseFragment implements XListView.IXListViewListener {
    private View root;
    private XListView xListView;
    private List<Object> dataList;
    private int pageCount = 1;
    private int currentPage = 1;
    private CommonAdapter<BangInfo> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataList = new ArrayList<Object>();

        adapter = new CommonAdapter<BangInfo>(getActivity(), dataList, R.layout.bang_list_item) {
            @Override
            public void convert(ViewHolder holder, final BangInfo bangInfo) {
                holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AppIntentManager.startBangDetailActivity(getActivity(), bangInfo);
                    }
                });

                String user = bangInfo.getUser();
                if (!StringUtils.isEmpty(user)) {
                    try {
                        JSONObject userObj = new JSONObject(user);
                        String nickname = userObj.getString("nickname");
                        String face = userObj.getString("userface");

                        holder.setText(R.id.nickname, nickname);
                        ImageView faceView = holder.getView(R.id.face);
                        if (!StringUtils.isEmpty(face)) {
                            ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_x(face), faceView);
                        }else {
                            holder.setImageResource(R.id.face,R.mipmap.defalut_user_face);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                holder.setText(R.id.date, StringUtils.friendly_time(bangInfo.getCreatTime()));

                holder.setText(R.id.title, bangInfo.getTitle());
                holder.setVisible(R.id.image_1, false);
                holder.setVisible(R.id.image_2, false);
                holder.setVisible(R.id.image_3, false);
                String images = bangInfo.getImages();
                if (!StringUtils.isEmpty(images)) {
                    try {
                        JSONArray imgArray = new JSONArray(images);
                        if (imgArray.length() > 0) {
                            holder.setVisible(R.id.image_1, View.INVISIBLE);
                            holder.setVisible(R.id.image_2, View.INVISIBLE);
                            holder.setVisible(R.id.image_3, View.INVISIBLE);
                            for (int i = 0; i < imgArray.length(); i++) {
                                String imgUrl = imgArray.getJSONObject(i).getString("imageName");
                                ImageView imgView = null;
                                switch (i) {
                                    case 0:
                                        holder.setVisible(R.id.image_1, true);
                                        imgView = holder.getView(R.id.image_1);
                                        break;
                                    case 1:
                                        holder.setVisible(R.id.image_2, true);
                                        imgView = holder.getView(R.id.image_2);
                                        break;
                                    case 2:
                                        holder.setVisible(R.id.image_3, true);
                                        imgView = holder.getView(R.id.image_3);
                                        break;
                                }
                                ImageLoader.getInstance().displayImage(AppConfig.getUserPhoto_xx(imgUrl), imgView);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String collect = bangInfo.getCollect();
                if (!StringUtils.isEmpty(collect)) {
                    try {
                        JSONArray collArray = new JSONArray(collect);
                        if (collArray != null && collArray.length() > 0) {
                            holder.setText(R.id.share_num, collArray.length() + "");
                        } else {
                            holder.setText(R.id.share_num, "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String praise = bangInfo.getPraise();
                if (!StringUtils.isEmpty(praise)) {
                    try {
                        JSONArray collArray = new JSONArray(praise);
                        if (collArray != null && collArray.length() > 0) {
                            holder.setText(R.id.hit_num, collArray.length() + "");
                        } else {
                            holder.setText(R.id.hit_num, "0");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                String commentCount = bangInfo.getCommentCount();
                if (!StringUtils.isEmpty(commentCount)) {
                    holder.setText(R.id.comment_num, commentCount);
                } else {
                    holder.setText(R.id.comment_num, "0");
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.layout_listview,container,false);
        initView();
        loadData();
        return root;
    }

    private void initView(){
        xListView = (XListView) root.findViewById(R.id.listView);
        xListView.setXListViewListener(this);
        xListView.setPullRefreshEnable(true);
        xListView.setPullLoadEnable(true);
        xListView.setAdapter(adapter);
    }

    private void loadData(){

        if(pageCount > 0 && currentPage > pageCount){
            stopLoading(xListView);
            return;
        }

        showProgress();
        ApiClient.get(this.getActivity(), ApiConfig.HOME_MY_COLLECT, new ApiResponseHandler<BangInfo>() {
            @Override
            public void onSuccess(DataEntity entity) {
                closeProgress();
                stopLoading(xListView);
                List<Object> objectList = entity.data;
                if (null != objectList && !objectList.isEmpty()) {
                    dataList.addAll(objectList);
                    adapter.notifyDataSetChanged();
                    pageCount = entity.pageCount;
                    currentPage++;
                }
            }

            @Override
            public void onFailure(ErrorEntity errorInfo) {
                closeProgress();
                stopLoading(xListView);
                UIHelper.ToastMessage(getActivity(), errorInfo.getMessage());
            }
        },"userId", getCurrentUser().getId(), "type", "3", "page", currentPage + "");

    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        dataList.clear();
        loadData();
    }

    @Override
    public void onLoadMore() {
        loadData();
    }
}
