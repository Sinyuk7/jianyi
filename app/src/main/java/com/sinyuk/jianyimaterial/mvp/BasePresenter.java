package com.sinyuk.jianyimaterial.mvp;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
/**
 * Created by Sinyuk on 16.3.16.
 */

/**
 * MVP中的Presenter基类，因为Activity，Fragment可能会回收或者变化
 * <p>
 * 所以要持有这些View的弱引用，一旦View被回收了（比如Activity被系统收回），就不再保留引用
 * <p>
 * 如果你要实现自己的Presenter，最好继承这个类
 *
 * @param <V> View的接口的类型【你的Activity肯定会实现View的接口的】
 * @param <M> Model的接口的类型【Model一定会实现Model接口】
 */

public abstract class BasePresenter<V, M extends BaseModel> {

    protected Reference<V> mViewRef;

    protected M mModel;

    public void attachView(V view) {
        detachView();
        mViewRef = new WeakReference<>(view);

    }

    public V getView() {
        return mViewRef != null ? mViewRef.get() : null;
    }

    public boolean isViewAttached() {
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView() {
        if (mViewRef != null) {
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public void setModel(M model) {
        mModel = model;
    }

    public M getModel() {
        return mModel;
    }
}
