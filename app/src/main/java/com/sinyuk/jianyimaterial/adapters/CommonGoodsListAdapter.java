package com.sinyuk.jianyimaterial.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.details.DetailsView;
import com.sinyuk.jianyimaterial.feature.profile.ProfileView;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.RatioImageView;
import com.sinyuk.jianyimaterial.widgets.TextDrawable;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.1.20.
 */
public class CommonGoodsListAdapter extends ExtendedRecyclerViewAdapter<YihuoProfile, CommonGoodsListAdapter.CardViewHolder> {


    private DrawableRequestBuilder<String> avatarRequest;

    private BitmapRequestBuilder<String, Bitmap> shotRequest;

    public CommonGoodsListAdapter(Context context) {
        super(context);
        avatarRequest = Glide.with(mContext).fromString()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.HIGH)
                .bitmapTransform(new CropCircleTransformation(mContext));

        shotRequest = Glide.with(mContext).fromString()
                .asBitmap()
                .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_icon))
                .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .thumbnail(0.2f);
    }

    @Override
    public void footerOnVisibleItem() {

    }


    @Override
    public CardViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goods_common, parent, false);
        return new CardViewHolder(v);

    }

    @Override
    public void onBindDataItemViewHolder(final CardViewHolder holder, final int position) {
        YihuoProfile itemData = null;
        if (!getData().isEmpty() && getData().get(position) != null) {
            itemData = getData().get(position);
        }
        if (itemData == null) { return; }

        LogUtils.simpleLog(CommonGoodsListAdapter.class, "UID -> " + itemData.getUid());
        final YihuoProfile finalItemData = itemData;
        holder.cardView.setOnClickListener(v -> {
            holder.cardView.setClickable(false); // prevent fast double tap
            Intent intent = new Intent(mContext, DetailsView.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable(YihuoProfile.TAG, finalItemData);
            intent.putExtras(bundle);
            mContext.startActivity(intent);
            holder.cardView.postDelayed(() -> holder.cardView.setClickable(true), 300);
        });

        holder.userNameTv.setText(String.format(StringUtils.getRes(mContext, R.string.details_username),
                StringUtils.check(mContext, itemData.getUsername(), R.string.untable)));
        holder.titleTv.setText(StringUtils.check(mContext, itemData.getName(), R.string.unknown_title));
        holder.newPriceLabelView.setText(FormatUtils.formatPrice(itemData.getPrice()));
    /*    try {
            holder.pubDateTv.setText(FuzzyDateFormater.getParsedDate(mContext, itemData.getTime()));
        } catch (ParseException e) {
            holder.pubDateTv.setText(StringUtils.getRes(mContext, R.string.unknown_date));
            e.printStackTrace();
        }*/
//        holder.locationTv.setText(StringUtils.check(mContext, itemData.getSchoolname(), R.string.unknown_location));


        // use a TextDrawable as a placeholder
        final char firstLetter;
        if (null == itemData.getUsername()) {
            firstLetter = ' ';

        } else {
            firstLetter = itemData.getUsername().charAt(0);
        }
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(firstLetter + "", mContext.getResources().getColor(R.color.grey_300));

        // initialize avatar
        avatarRequest.load(itemData.getHeadImg())
                .placeholder(textDrawable)
                .error(textDrawable)
                .into(holder.avatar);
        holder.avatar.setTag(R.id.avatar_tag, position + "_24dp");


        holder.avatar.setOnClickListener(avatar -> {
            Intent intent = new Intent(mContext, ProfileView.class);
            Bundle bundle = new Bundle();
            bundle.putFloat(ProfileView.PROFILE_TYPE, ProfileView.OTHER);
            bundle.putString("uid", finalItemData.getUid());
            bundle.putString("user_name", finalItemData.getUsername());
            bundle.putString("location", finalItemData.getSchoolname());
            bundle.putString("tel", finalItemData.getTel());
            bundle.putString("avatar", finalItemData.getHeadImg());
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });

        shotRequest.load(JianyiApi.shotUrl(itemData.getPic())).into(holder.shotIv);
        holder.shotIv.setTag(R.id.shots_cover_tag, position);

    }


    public class CardViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.shot_iv)
        RatioImageView shotIv;
        @Bind(R.id.new_price_label_view)
        LabelView newPriceLabelView;
        @Bind(R.id.title_tv)
        TextView titleTv;
        @Bind(R.id.avatar)
        ImageView avatar;
        @Bind(R.id.user_name_tv)
        TextView userNameTv;
        @Bind(R.id.card_view)
        CardView cardView;

        public CardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
