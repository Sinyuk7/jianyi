package com.sinyuk.jianyimaterial.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import cimi.com.easeinterpolator.EaseSineOutInterpolator;

/**
 * Created by Sinyuk on 15.12.21.
 */
public class CategoryMenuAdapter extends RecyclerView.Adapter<CategoryMenuAdapter.CategoryItemViewHolder> implements View.OnClickListener {


    private String[] titles;
    public final int[] iconResIds = new int[]{
            R.drawable.ic_tshirt_crew_white_48dp,//1
            R.drawable.ic_guitar_electric_white_48dp,
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
    private Context context;

    private OnCategoryMenuItemClickListener onCategoryMenuItemClickListener;
    private boolean iconsAnimationLocked = false;
    private int lastAnimatedItem = -1;

    public boolean isIconsAnimationLocked() {
        return iconsAnimationLocked;
    }

    public void setIconsAnimationLocked(boolean iconsAnimationLocked) {
        this.iconsAnimationLocked = iconsAnimationLocked;
    }

    public void setOnCategoryMenuItemClickListener(OnCategoryMenuItemClickListener onCategoryMenuItemClickListener) {
        this.onCategoryMenuItemClickListener = onCategoryMenuItemClickListener;
    }

    public CategoryMenuAdapter(Context context) {
        this.context = context;
        initData();
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
        holder.icon.setImageResource(iconResIds[position]);
        holder.wrapper.setOnClickListener(this);

        holder.title.setTag(position);
        holder.icon.setTag(position);
        holder.wrapper.setTag(position);


        animateIcons(holder);
        if (lastAnimatedItem < position)
            lastAnimatedItem = position;
    }


    @Override
    public int getItemCount() {
        return Math.min(titles.length, iconResIds.length);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.item_wrapper) {
            if (onCategoryMenuItemClickListener != null)
                onCategoryMenuItemClickListener.onCategoryMenuItemClick(v, (Integer) v.getTag());
        }
    }

    private void animateIcons(final CategoryItemViewHolder viewHolder) {
        if (!iconsAnimationLocked) {
            if (lastAnimatedItem == viewHolder.getAdapterPosition()) {
                setIconsAnimationLocked(true);
            }

            long animationDelay = (long) (viewHolder.getAdapterPosition() * 50 + AnimUtils.ACTIVITY_TRANSITION_DELAY * 1.5);


            viewHolder.wrapper.setTranslationY(ScreenUtils.dpToPx(context, 100));
            viewHolder.wrapper.setAlpha(0f);
            viewHolder.wrapper.animate()
                    .translationY(0)
                    .alpha(1.f)
                    .setDuration(AnimUtils.ANIMATION_TIME_SHORT_EXTRA)
                    .setInterpolator(new EaseSineOutInterpolator())
                    .setStartDelay(animationDelay)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            iconsAnimationLocked = true;
                            viewHolder.wrapper.setTranslationY(0);
                            viewHolder.wrapper.setAlpha(1.f);
                        }
                    })
                    .start();
        }
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

    public interface OnCategoryMenuItemClickListener {
        void onCategoryMenuItemClick(View view, int position);
    }

}
