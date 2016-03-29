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
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.ProfileActivity;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.details.DetailsView;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.RatioImageView;
import com.sinyuk.jianyimaterial.widgets.TextDrawable;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.1.20.
 */
public class CardListAdapter extends ExtendedRecyclerViewAdapter<YihuoProfile, CardListAdapter.CardViewHolder> {


    private DrawableRequestBuilder<String> avatarRequest;

    private BitmapRequestBuilder<String, Bitmap> shotRequest;

    public CardListAdapter(Context context) {
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
                .inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(v);

    }

    @Override
    public void onBindDataItemViewHolder(final CardViewHolder holder, final int position) {
        YihuoProfile itemData = null;
        if (!getData().isEmpty() && getData().get(position) != null)
            itemData = getData().get(position);

        if (itemData == null)
            return;
        // TODO: initialize cardView
        if (holder.cardView != null) {
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
        }

        // TODO: initialize username;
        holder.userNameTv.setText(String.format(StringUtils.getRes(mContext, R.string.details_username),
                StringUtils.check(mContext, itemData.getUsername(), R.string.untable)));
        // TODO: initialize title;
        holder.titleTv.setText(StringUtils.check(mContext, itemData.getName(), R.string.unknown_title));
        // TODO: initialize newPrice;
        holder.newPriceLabelView.setText(FormatUtils.formatPrice(itemData.getPrice()));
        // TODO: initialize pubDate;
    /*    try {
            holder.pubDateTv.setText(FuzzyDateFormater.getParsedDate(mContext, itemData.getTime()));
        } catch (ParseException e) {
            holder.pubDateTv.setText(StringUtils.getRes(mContext, R.string.unknown_date));
            e.printStackTrace();
        }*/
        // TODO: initialize location;
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


        // initialize shot
        final YihuoProfile finalItemData1 = itemData;

        holder.avatar.setOnClickListener(avatar -> {
            Intent intent = new Intent(mContext, ProfileActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("user_name", finalItemData1.getUsername());
            bundle.putString("location", finalItemData1.getSchoolname());
            bundle.putString("tel", finalItemData1.getTel());
            bundle.putString("avatar", finalItemData1.getHeadImg());
            intent.putExtras(bundle);
            mContext.startActivity(intent);
        });

        shotRequest.load(JianyiApi.shotUrl(itemData.getPic()))
                .listener(new RequestListener<String, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(final Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.shotIv);

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
