package com.sinyuk.jianyimaterial.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.glide.ColorFilterTransformation;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cimi.com.easeinterpolator.EaseSineOutInterpolator;

/**
 * Created by Sinyuk on 15.12.21.
 */
public class CategoryMenuAdapter extends RecyclerView.Adapter<CategoryMenuAdapter.CategoryItemViewHolder> implements View.OnClickListener {


    private static final float ANIM_SCALE_FACTOR = 1;
    public final int[] iconResIds = new int[]{
            R.drawable.ic_hanger_white_48dp,//1
            R.drawable.ic_android_studio_white_48dp,
            R.drawable.ic_umbrella_outline_white_48dp,//2
            R.drawable.ic_face_unlock_white_48dp,//3
            R.drawable.ic_dribbble_white_48dp,
            R.drawable.ic_bike_white_48dp,//4
            R.drawable.ic_cellphone_android_white_48dp,
            R.drawable.ic_book_open_variant_white_48dp,
            R.drawable.ic_credit_card_white_48dp,
            R.drawable.ic_wallet_travel_white_48dp,
            R.drawable.ic_food_white_48dp,
    };
    private final DrawableRequestBuilder<Integer> iconRequest;
    private String[] titles;
    private Context context;

    private OnCategoryMenuItemClickListener onCategoryMenuItemClickListener;
    private int lastAnimatedPosition = -1;

    public CategoryMenuAdapter(Context context) {
        this.context = context;
        iconRequest = Glide.with(context).fromResource()
                .bitmapTransform(new ColorFilterTransformation(context, context.getResources().getColor(R.color.colorPrimary)))
                .dontAnimate().diskCacheStrategy(DiskCacheStrategy.RESULT);
        initData();
    }

    public void setOnCategoryMenuItemClickListener(OnCategoryMenuItemClickListener onCategoryMenuItemClickListener) {
        this.onCategoryMenuItemClickListener = onCategoryMenuItemClickListener;
    }

    private void initData() {
        titles = context.getResources().getStringArray(R.array.category_menu_items);
    }

    @Override
    public CategoryMenuAdapter.CategoryItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cotegory_menu, parent, false);
        return new CategoryItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryMenuAdapter.CategoryItemViewHolder holder, int position) {
        holder.title.setText(titles[position]);
        iconRequest.load(iconResIds[position]).into(holder.icon);
        holder.wrapper.setOnClickListener(this);

        holder.title.setTag(position);
        holder.wrapper.setTag(position);

        runEnterAnimation(holder.wrapper, position);
    }


    @Override
    public int getItemCount() {
        return Math.min(titles.length, iconResIds.length);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_wrapper) {
            if (onCategoryMenuItemClickListener != null) {
                onCategoryMenuItemClickListener.onCategoryMenuItemClick(v, (Integer) v.getTag());
            }
        }
    }

    private void runEnterAnimation(final View view, int position) {
        if (position > lastAnimatedPosition) {
            lastAnimatedPosition = position;
            view.setTranslationY(ScreenUtils.dpToPx(context, 56));
            view.setPivotX(0);
            view.setPivotY(0);
            view.setRotation(5);
            view.setAlpha(0.f);
            view.animate()
                    .translationY(0).alpha(1.f).rotation(0)
                    .setStartDelay((long) (ANIM_SCALE_FACTOR * 80 * position))
                    .setInterpolator(new EaseSineOutInterpolator())
                    .setDuration(AnimUtils.ANIMATION_TIME_SHORT)
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

    public interface OnCategoryMenuItemClickListener {
        void onCategoryMenuItemClick(View view, int position);
    }

    public class CategoryItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.item_wrapper)
        RelativeLayout wrapper;
        @Bind(R.id.title_tv)
        TextView title;
        @Bind(R.id.icon_iv)
        ImageView icon;

        public CategoryItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
