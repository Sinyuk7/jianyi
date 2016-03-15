package com.sinyuk.jianyimaterial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sinyuk on 16.1.4.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<T> mList;
    public Context mContext;


    public BaseRecyclerViewAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public void setData(ArrayList<T> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    public ArrayList<T> getData() {
        return mList;
    }

    public void setData(T[] list) {
        ArrayList<T> arrayList = new ArrayList<>(list.length);
        for (T t : list) {
            arrayList.add(t);
        }
        setData(arrayList);
    }

    public void setData(List<T> list) {
        ArrayList<T> arrayList = new ArrayList<>(list.size());
        for (T t : list) {
            arrayList.add(t);
        }
        setData(arrayList);
    }


    public void addData(int position, T item) {
        if (mList != null && position < mList.size()) {
            mList.add(position, item);
            notifyItemInserted(position);
        }
    }

    public void removeData(int position) {
        if (mList != null && position < mList.size()) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    protected void notifyMyItemInserted(int position) {
        notifyItemInserted(position);
    }

    protected void notifyMyItemRemoved(int position) {
        notifyItemRemoved(position);
    }

    protected void notifyMyItemChanged(int position) {
        notifyItemChanged(position);
    }

    protected void notifyMyItemListChanged(List<T> list) {

        ArrayList<T> arrayList = new ArrayList<>(list.size());
        for (T t : list) {
            arrayList.add(t);
        }
            setData(arrayList);
    }

}
