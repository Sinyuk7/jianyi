package com.sinyuk.jianyimaterial.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JNeed;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.utils.SpringUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.TextDrawable;

import java.text.ParseException;

import butterknife.Bind;
import butterknife.ButterKnife;
import cimi.com.easeinterpolator.EaseSineInInterpolator;
import cimi.com.easeinterpolator.EaseSineOutInterpolator;

/**
 * Created by Sinyuk on 16.1.4.
 */
public class NeedsListAdapter extends ExtendedRecyclerViewAdapter<JNeed.Data.Need, NeedsListAdapter.MyViewHolder> {


    private static long ANIMATION_SCALE;

    private DrawableRequestBuilder<String> avatarRequest;

    public NeedsListAdapter(Context context) {
        super(context);

        ANIMATION_SCALE = AnimUtils.ANIMATION_TIME_SHORT;
        avatarRequest = Glide.with(mContext).fromString()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.IMMEDIATE)
                .bitmapTransform(new CropCircleTransformation(mContext));
    }

    @Override
    public void footerOnVisibleItem() {

    }


    @Override
    public MyViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_needs, parent, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindDataItemViewHolder(final MyViewHolder holder, final int position) {
        final JNeed.Data.Need data = getData().get(position);

        if (position % 2 == 0) {
            holder.wrapper.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey_50));
        } else {
            holder.wrapper.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }

        holder.mainBodyTv.setText(
                StringUtils.check(mContext, data.getDetail(), R.string.needs_hint_no_description));


        if (TextUtils.isEmpty(data.getPrice())) {
            holder.priceTv.setVisibility(View.GONE);
        } else {
            holder.priceTv.setText(FormatUtils.formatPrice(data.getPrice()));

        }


        holder.telTv.setText(
                StringUtils.check(mContext,
                        FormatUtils.formatPhoneNum(data.getTel()),
                        R.string.unknown_tel));


        try {
            holder.pubDateTv.setText(
                    StringUtils.check(mContext, FuzzyDateFormater.getParsedDate(mContext, data.getTime()), R.string.unknown_date));
        } catch (ParseException e) {
            holder.pubDateTv.setText(StringUtils.getRes(mContext, R.string.unknown_date));
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            holder.pubDateTv.setText(StringUtils.getRes(mContext, R.string.unknown_date));
        }

        holder.userNameTv.setText(StringUtils.check(mContext, data.getUsername(), R.string.unknown_user_name));

        holder.locationIv.setText(
                StringUtils.check(mContext, data.getSchoolName(), R.string.unknown_location));


        // a little tricky to deal with the scroll bug
        if (holder.expandView.getVisibility() == View.VISIBLE) {
            holder.expandView.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.wrapper.setTranslationZ(0);
            }
        }

        // TODO: initialize avatar

        // use a TextDrawable as a placeholder
        final char firstLetter;
        if (null == data.getUsername()) {
            firstLetter = ' ';

        } else {
            firstLetter = data.getUsername().charAt(0);
        }
        TextDrawable textDrawable = TextDrawable.builder()
                .buildRound(firstLetter + "", mContext.getResources().getColor(R.color.grey_300));
        // TODO: initialize avatar
        avatarRequest.load(data.getAvatarUrl())
                .placeholder(textDrawable)
                .error(textDrawable)
                .into(holder.avatar);


        holder.avatar.setTag(R.id.avatar_tag, position + "_36dp");


        holder.chatIv.setOnClickListener(v -> {});

        holder.phoneCallIv.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(data.getTel())) {
                Uri uri = Uri.parse("tel:" + data.getTel());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                mContext.startActivity(intent);
            }
        });

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.avatar)
        ImageView avatar;
        @Bind(R.id.user_name_tv)
        TextView userNameTv;
        @Bind(R.id.pub_date_tv)
        TextView pubDateTv;
        @Bind(R.id.price_tv)
        TextView priceTv;
        @Bind(R.id.main_body_tv)
        TextView mainBodyTv;
        @Bind(R.id.tel_tv)
        TextView telTv;
        @Bind(R.id.location_iv)
        TextView locationIv;
        @Bind(R.id.phone_call_iv)
        ImageView phoneCallIv;
        @Bind(R.id.chat_iv)
        ImageView chatIv;
        @Bind(R.id.expand_view)
        LinearLayout expandView;
        @Bind(R.id.wrapper)
        RelativeLayout wrapper;


        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            wrapper.setOnClickListener(v -> {
                if (expandView.getVisibility() == View.GONE) {
                    expand();
                } else {
                    expandView.setVisibility(View.GONE);
                    phoneCallIv.setVisibility(View.INVISIBLE);
                    chatIv.setVisibility(View.INVISIBLE);
                    fall(wrapper);
                }

            });
        }

        private void expand() {
            expandView.setVisibility(View.VISIBLE);
            SpringUtils.popOut(50, 6, 300, phoneCallIv, chatIv);
            raise(wrapper);
        }

        private void raise(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.animate().translationZ(ScreenUtils.dpToPx(mContext, 2))
                        .setDuration(ANIMATION_SCALE)
                        .setInterpolator(new EaseSineOutInterpolator())
                        .start();
            }
        }

        private void fall(View view) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.animate().translationZ(0)
                        .setInterpolator(new EaseSineInInterpolator())
                        .setDuration(ANIMATION_SCALE)
                        .start();
            }
        }
    }
}
