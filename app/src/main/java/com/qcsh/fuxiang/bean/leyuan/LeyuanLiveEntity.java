package com.qcsh.fuxiang.bean.leyuan;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by Administrator on 2015/11/4.
 */
public class LeyuanLiveEntity extends BaseEntity {

    private String id;
    private String name;
    private String pic;
    private String path;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
