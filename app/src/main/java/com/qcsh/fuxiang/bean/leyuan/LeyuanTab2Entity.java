package com.qcsh.fuxiang.bean.leyuan;

import com.qcsh.fuxiang.bean.BaseEntity;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LeyuanTab2Entity extends BaseEntity {

    private String id;
    private String pic;
    private String name;
    private String notes;
    private String price;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
