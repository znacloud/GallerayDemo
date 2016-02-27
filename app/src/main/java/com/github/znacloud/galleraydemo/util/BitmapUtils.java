package com.github.znacloud.galleraydemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.ImageView;

import com.github.znacloud.galleraydemo.model.BitmapWorkerTask;

import java.io.File;


/**
 * Created by Stephan on 2016/1/24.
 */
public class BitmapUtils {

    /**
     * 从资源id加载图片，转换成目标宽高
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // 首先将inJustDecodeBounds 设置为true,去读取尺寸信息
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // 计算压缩比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 将inJustDecodeBounds设置为false,加载压缩后的图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 从外部文件记载图片
     * @param filePath 图片文件路径
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @return Bitmap
     */
    public static Bitmap decodeSampledBitmapFromFile(String filePath,
                                                         int reqWidth, int reqHeight){
        // 首先将inJustDecodeBounds 设置为true,去读取尺寸信息
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // 计算压缩比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 将inJustDecodeBounds设置为false,加载压缩后的图片
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }


    /**
     * 计算压缩比例
     * @param options 包含原始宽高信息
     * @param reqWidth 目标宽度
     * @param reqHeight 目标高度
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;//压缩比例，默认为不压缩

        if (height > reqHeight || width > reqWidth) { //比较宽高

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // 反复比较计算出最接近目标尺寸但是要保证不小于目标尺寸的压缩比例
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * 异步加载资源图片
     * @param res 资源类
     * @param pImageView ImageView
     * @param resId 图片资源id
     */
    public static void loadBitmap(ImageView pImageView,Resources res,int resId){
        BitmapWorkerTask task = new BitmapWorkerTask(pImageView);
        task.execute(res,resId);
    }

    public static void loadBitmap(ImageView pImageView,String filePath){
        BitmapWorkerTask task = new BitmapWorkerTask(pImageView);
        task.execute(filePath);
    }

    public static File getDiskCacheDir(Context context, String uniqueName) {
        // 如果有外存则使用外部缓存空间
        //否则使用内部缓存空间
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                        context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

}
