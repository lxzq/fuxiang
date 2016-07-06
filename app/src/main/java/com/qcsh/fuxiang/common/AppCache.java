package com.qcsh.fuxiang.common;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * 作者：Owen on 2015-06-19 08:49
 * 邮箱：xwuyuan521@163.com
 */
public class AppCache {

    private static final int CACHE_TIME = 60*60000;//缓存失效时间
    /**
     * 保存对象
     * @param ser
     * @param file
     * @throws java.io.IOException
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean saveObject(Context context, Serializable ser, String file)  {
        ObjectOutputStream oos = null;
        try (FileOutputStream fos = context.openFileOutput(file, Context.MODE_PRIVATE)) {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
          try {
              assert oos != null;
              oos.close();
          }
          catch (Exception e){}


        }
    }

    /**
     * 读取对象
     * @param file
     * @return
     * @throws java.io.IOException
     */
    public static Serializable readObject(Context context, String file){
        if(!isExistDataCache(context,file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try{
            fis = context.openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable)ois.readObject();
        }catch(FileNotFoundException e){
        }catch(Exception e){
            e.printStackTrace();
            //反序列化失败 - 删除缓存文件
            if(e instanceof InvalidClassException){
                File data = context.getFileStreamPath(file);
                data.delete();
            }
        }finally{
            try {
                assert ois!=null;
                ois.close();
            } catch (Exception e) {}
            try {
                assert fis!=null;
                fis.close();
            } catch (Exception e) {}
        }
        return null;
    }

    //删除对象
    public static boolean removeObject(Context context, String file)
    {
        if(!isExistDataCache(context,file))
            return true;
        try {
            File data =context.getFileStreamPath(file);
            data.delete();
            return true;
        }
        catch (Exception e) { return false;}

    }

    /**
     * 判断缓存是否存在
     * @param cachefile
     * @return
     */
    public static boolean isExistDataCache(Context context, String cachefile)
    {
        boolean exist = false;
        File data = context.getFileStreamPath(cachefile);
        if(data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存数据是否可读
     * @param cachefile
     * @return
     */
    public static boolean isReadDataCache(Context context,String cachefile)
    {
        return readObject(context,cachefile) != null;
    }



    /**
     * 判断缓存是否失效
     * @param cachefile
     * @return
     */
    public static boolean isCacheDataFailure(Context context, String cachefile)
    {
        boolean failure = false;
        File data = context.getFileStreamPath(cachefile);
        if(data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if(!data.exists())
            failure = true;
        return failure;
    }


}
