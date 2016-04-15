package com.sinyuk.jianyimaterial.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XShotDropEvent;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.tumblr.backboard.imitator.ToggleImitator;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.2.18.
 */
public class ShotsGalleryAdapter extends ExtendedRecyclerViewAdapter<Uri, ShotsGalleryAdapter.ShotViewHolder> {


    private final DrawableRequestBuilder<Uri> loadRequest;


    public ShotsGalleryAdapter(Context context) {
        super(context);
        loadRequest = Glide.with(mContext).fromMediaStore().thumbnail(0.2f).diskCacheStrategy(DiskCacheStrategy.RESULT);
    }

    @Override
    public void footerOnVisibleItem() {

    }

    @Override
    public ShotViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed_cell, parent, false);
        return new ShotViewHolder(v);

    }

    @Override
    public void onBindDataItemViewHolder(final ShotViewHolder holder, final int position) {

        Uri uri = getData().get(position);

        holder.deleteIv.setOnClickListener(v -> EventBus.getDefault().post(new XShotDropEvent(position)));

        if (position == 0) {
            holder.coverLabelView.setVisibility(View.VISIBLE);
        } else {
            holder.coverLabelView.setVisibility(View.GONE);
        }

        holder.deleteIv.setVisibility(View.GONE);
        holder.shotIv.setOnLongClickListener(v -> {
            if (holder.deleteIv.getVisibility() != View.VISIBLE) {
                holder.deleteIv.setVisibility(View.VISIBLE);
            } else {
                holder.deleteIv.setVisibility(View.GONE);
            }
            return true;
        });

        loadRequest.load(uri).into(holder.shotIv);
    }


    public class ShotViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.shot_iv)
        ImageView shotIv;
        @Bind(R.id.delete_iv)
        ImageView deleteIv;
        @Bind(R.id.cover_label_view)
        LabelView coverLabelView;
        @Bind(R.id.wrapper)
        FrameLayout wrapper;


        public ShotViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
