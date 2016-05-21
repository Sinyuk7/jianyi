package com.sinyuk.jianyimaterial.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.NameGenerator;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
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

    public final static String[] avatarUrls = new String[]{
            "http://i4.piimg.com/bfe33c321472a8e1.jpg",

            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/7493872.jpg",
            "http://i2.piimg.com/a3128205876036a0.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/65272015.jpg",

            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28409200.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28774205.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/75678264.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/85429884.jpg",

            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/95644103.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/48260140.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/34259100.jpg",
            "http://i2.piimg.com/8740ab34b90ce823.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/21059124.jpg",

            "http://i2.piimg.com/3aa7ddf4fe26c039.jpg",
            "http://i2.piimg.com/3fcd5c6b292f12d6.jpg",
            "http://i2.piimg.com/c67b8af9e3b8faa4.jpg",
            "http://i2.piimg.com/11d5f7736b03274c.jpg",
            "http://i4.piimg.com/2e25613d5ecc5892.jpg",
            "http://i2.piimg.com/9095a3df4918db70.jpg",
            "http://i2.piimg.com/0916f1757efb77a1.jpg",
            "http://i2.piimg.com/d7cdf687a94f0387.jpg",
            "http://i2.piimg.com/0916f1757efb77a1.jpg",
            "http://i2.piimg.com/f23dcb0af8064150.jpg",
            "http://i2.piimg.com/29c465301b6d7d99.jpg",
            "http://i2.piimg.com/8b619e96a3a4809f.jpg",
            "http://i2.piimg.com/f0e6bf048b3b2aaa.jpg",
            "http://i1.piimg.com/98005a2ef5def8b6.jpg",
            "http://i4.piimg.com/2e25613d5ecc5892.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/82385112.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/2617784.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/26331572.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/64088077.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/55141845.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/60177585.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/64546251.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/86513563.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/89918438.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/61545022.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/78252022.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/26940366.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/43930374.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/43930374.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/98363213.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/9408043.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/4783808.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/93786492.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/28693559.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/13943362.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/90545840.jpg",
            "http://7xrn7f.com1.z0.glb.clouddn.com/16-4-27/85353049.jpg",
    };
    private static final float ANIM_SCALE_FACTOR = 1f;
    private static String[] fakeComments = new String[]{
            "哇晒，好看！(⊙ˍ⊙)",
            "╥﹏╥...好贵",
    };
    private final DrawableRequestBuilder<String> avatarRequest;
    private boolean enterAnimationLocked = false;
    private boolean delayEnterAnimation = true;
    private int lastAnimatedPosition = -1;
    private boolean useFakeComments = false;

    public CommentsAdapter(Context context) {
        super(context);
        avatarRequest = Glide.with(mContext).fromString()
                .priority(Priority.IMMEDIATE)
                .bitmapTransform(new CropCircleTransformation(mContext))
                .crossFade(400)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .error(R.drawable.ic_avatar_placeholder);
    }

    @Override
    public void footerOnVisibleItem() {

    }

    private void runEnterAnimation(final View view, int position) {
        if (enterAnimationLocked) { return; }

        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(ScreenUtils.dpToPx(mContext, 30));
            view.setPivotX(0);
            view.setPivotY(0);
            view.setRotation(5);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f).rotation(0)
                    .setStartDelay(delayEnterAnimation ? (long) (ANIM_SCALE_FACTOR * 50 * position) : 0)
                    .setInterpolator(new EaseSineOutInterpolator())
                    .setDuration(150)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ViewCompat.setTranslationY(view, 0);
                            ViewCompat.setAlpha(view, 1.f);
                            ViewCompat.setRotation(view, 0);
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

        holder.userNameTv.setText(NameGenerator.generateName());

        holder.pubDateTv.setText(FuzzyDateFormater.getTimeAgo(mContext, new Date(System.currentTimeMillis() - 600000 * new Random().nextInt(position + 1) - position * 6000000)));


//

        if (position < fakeComments.length && useFakeComments) {
            holder.contentTv.setText(fakeComments[position]);
        } else {
            holder.contentTv.setText(getData().get(position));
            holder.contentTv.setMaxLines(new Random().nextInt(5) + 1);
        }

        int index = position % avatarUrls.length;
        if (index > avatarUrls.length || index < 0) {
            index = new Random().nextInt(20);
        }

        avatarRequest.load(avatarUrls[index]).into(holder.avatar);

        runEnterAnimation(holder.itemView, position);

    }

    public void useFakeComment(boolean bool) {
        useFakeComments = bool;
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
        @Bind(R.id.comment_view)
        RelativeLayout commentView;

        public CommentItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
