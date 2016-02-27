package com.github.znacloud.galleraydemo.model;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by Stephan on 2016/1/24.
 */
public class AsyncDrawable extends BitmapDrawable {
//弱引用，保存异步任务类
private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

public AsyncDrawable(Bitmap bitmap,
        BitmapWorkerTask bitmapWorkerTask) {
        super(bitmap);
        bitmapWorkerTaskReference =
        new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
        }
        }