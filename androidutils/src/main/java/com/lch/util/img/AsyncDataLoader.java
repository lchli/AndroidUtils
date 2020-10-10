package com.lch.util.img;

import android.support.annotation.NonNull;

import com.lch.util.executor.BgTask;

import java.lang.ref.WeakReference;

public abstract class AsyncDataLoader<T,P> {

    public void load(@NonNull final P param,@NonNull AsyncDataTargetView<T> targetView, final AsyncDataTransformer<T> transform) {

        final Object viewUid = param;
        targetView.setViewUid(viewUid);
        final WeakReference<AsyncDataTargetView<T>> ref = new WeakReference<>(targetView);

        new BgTask<T>() {
            @Override
            protected T doInBackground() {
                T cache = getCache(param);
                if (cache != null) {
                    return transform!=null?transform.transform(cache):cache;
                }

                T res = loadImpl(param);
                if (res != null) {
                    putCache(param, res);
                }

                return transform!=null?transform.transform(res):res;
            }

            @Override
            protected void doPost(T result) {
                if (result == null) {
                    return;
                }
                AsyncDataTargetView<T> iv = ref.get();
                if (iv == null) {
                    return;
                }
                Object vid = iv.getViewUid();
                if (!viewUid.equals(vid)) {
                    return;
                }

                iv.bindResult(result);

            }
        }.execute();

    }

    protected abstract T getCache(P param);

    protected abstract void putCache(P param, T res);

    protected abstract T loadImpl(P param);

   public interface AsyncDataTargetView<R> {

        void bindResult(R res);

        void setViewUid(Object uid);

        Object getViewUid();

    }

    public interface AsyncDataTransformer<R> {

        R transform(R res);

    }
}
