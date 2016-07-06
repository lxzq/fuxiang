package com.qcsh.fuxiang.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qcsh.fuxiang.widget.MyToast;

import java.util.Hashtable;

/**
 * Created by Administrator on 2015/8/11.
 */
public class UIHelper {

    /**
     * 显示提示消息
     *
     */
    public static void ToastMessage(Context cont, String msg) {
       // Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
        MyToast.makeText(cont,msg);
    }

    /**
     * 生成二维码
     * @param url 生成地址
     * @param QR_WIDTH 高度
     * @param QR_HEIGHT 宽度
     * @return
     */
    public static Bitmap createQRImage(String url,int QR_WIDTH,int QR_HEIGHT){
        Bitmap bitmap = null;
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return bitmap;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < QR_HEIGHT; y++)
            {
                for (int x = 0; x < QR_WIDTH; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

}
