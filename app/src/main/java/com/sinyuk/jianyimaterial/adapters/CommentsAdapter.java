package com.sinyuk.jianyimaterial.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;

import java.util.Date;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import cimi.com.easeinterpolator.EaseSineOutInterpolator;


/**
 * Created by Sinyuk on 15.12.19.
 */
public class CommentsAdapter extends ExtendedRecyclerViewAdapter<String, CommentsAdapter.CommentItemViewHolder> {

    private final DrawableRequestBuilder<String> avatarRequest;
    private boolean enterAnimationLocked = false;
    private boolean delayEnterAnimation = true;
    private int lastAnimatedPosition = -1;

    private final static String[] avatarUrls = new String[]{
            "http://ww2.sinaimg.cn/square/b29e155agw1eylork6523j20zk0npjvg.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eylorl860fj21hc0u078o.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eylork32y7j211y0lcju0.jpg",
            "http://ww1.sinaimg.cn/square/b29e155agw1eylore72c5j20c80c8jst.jpg",
            "http://ww3.sinaimg.cn/square/b29e155agw1eylordr8lzj20dl0h6dh9.jpg",


            "http://ww2.sinaimg.cn/mw690/69352c30gw1ev3w6ewp08j21hi0zh1kx.jpg",
            "http://ww3.sinaimg.cn/mw690/b29e155agw1eyfew5ncdoj209j0b4t93.jpg",
            "http://ww1.sinaimg.cn/mw690/b29e155agw1eyfew4gxwej20e60amgow.jpg",
            "http://ww4.sinaimg.cn/mw690/b29e155agw1eyfew3f622j20e60amae5.jpg",
            "http://ww4.sinaimg.cn/square/b29e155agw1eyfew1ez6jj20dw0dwjuv.jpg"
    };

    public CommentsAdapter(Context context) {
        super(context);
        avatarRequest = Glide.with(mContext).fromString()
                .priority(Priority.HIGH)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.drawable.ic_avatar_placeholder)
                .placeholder(R.drawable.ic_avatar_placeholder);
    }

    @Override
    public void footerOnVisibleItem() {

    }

    private void runEnterAnimation(final View view, int position) {
        if (enterAnimationLocked)
            return;

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(100);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f)
                    .setStartDelay(delayEnterAnimation ? 30 * (position) : 0)
                    .setInterpolator(new EaseSineOutInterpolator())
                    .setDuration(AnimUtils.ANIMATION_TIME_SHORT_EXTRA)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            enterAnimationLocked = true;
                            ViewCompat.setTranslationY(view, 0);
                            ViewCompat.setAlpha(view, 1.f);
                        }
                    })
                    .start();
        }
    }


    @Override
    public CommentItemViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentItemViewHolder(itemView);
    }

    @Override
    public void onBindDataItemViewHolder(CommentItemViewHolder holder, int position) {
        runEnterAnimation(holder.itemView, position);
        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_100));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_100));
        }

        holder.userNameTv.setText(mContext.getResources().getStringArray(R.array.user_names)[position]);

        holder.pubDateTv.setText(FuzzyDateFormater.getTimeAgo(mContext, new Date(System.currentTimeMillis() - 6000000 * new Random().nextInt(100))));

        holder.contentTv.setText(StringUtils.getRes(mContext, R.string.lorem));

        holder.contentTv.setMaxLines(new Random().nextInt(5) + 1);

        avatarRequest.load(avatarUrls[position]).into(holder.avatar);
    }


    public void lockEnterAnimation(boolean enterAnimationLocked) {
        this.enterAnimationLocked = enterAnimationLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public class CommentItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.user_name_tv)
        TextView userNameTv;
        @Bind(R.id.pub_date_tv)
        TextView pubDateTv;
        @Bind(R.id.content_tv)
        TextView contentTv;
        @Bind(R.id.avatar)
        MyCircleImageView avatar;

        public CommentItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
