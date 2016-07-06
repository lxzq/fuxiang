package com.qcsh.fuxiang.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;


import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSBucket;

import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.storage.TaskHandler;


public class OssUtils {
	private static TaskHandler ossHandler;
	public static OSSBucket jjbBucket;
	
	static {
		jjbBucket = new OSSBucket("jiajiabang");
		jjbBucket.setBucketACL(AccessControlList.PUBLIC_READ_WRITE); // 如果这个Bucket跟全局默认的访问权限不一致，就需要单独设置
	}
	
   public static void upload(String strlocalPath,String subfolder,final OssHandler handler){
	   
	 String name=String.valueOf(System.currentTimeMillis());
	 String ext=strlocalPath.substring(strlocalPath.lastIndexOf("."));
	 
	 Calendar calendar=Calendar.getInstance();  
     calendar.setTime(new Date());  
     String year=String.valueOf(calendar.get(Calendar.YEAR));  
     String month=String.valueOf(calendar.get(Calendar.MONTH)+1); 
	 
	 final String strPath= String.format("/%s/%s/%s/%s%s",subfolder, year,month,name,ext);

	 final OSSFile ossFile = new OSSFile(jjbBucket, strPath);

   	 // 指明需要上传的文件的路径， 和文件内 容类型
   	  ossFile. setUploadFilePath(strlocalPath, ext) ; 
   	 
      ossHandler = ossFile.uploadInBackground(new SaveCallback() {

           @Override
           public void onSuccess(String objectKey) {
        	   
        	  
           	     handler.onSuccess( strPath);
           }

           @Override
           public void onProgress(String objectKey, int byteCount, int totalSize) {
               handler.onProgress( objectKey,  byteCount,  totalSize);
           }

           @Override
           public void onFailure(String objectKey, OSSException ossException) {
            	     handler.onFailure(objectKey,ossException);
           }
       });
	 
   }
  
     
      public static void cancel(){
    	if ( ossHandler != null) {
    		ossHandler.cancel(); // 取消上传任务
    	}
    		   
      }
}
