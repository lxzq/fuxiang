package com.qcsh.fuxiang.bean;


/**
 * Created by liu on 13-12-20.
 * 版本更新实体类
 */
public class VersionCheckListEntity extends BaseEntity {
   
    private String content;
    private String upload;
    private String version;
    private String id;
    
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
