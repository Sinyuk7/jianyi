package com.sinyuk.jianyimaterial.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.PhotoViewActivity;
import com.sinyuk.jianyimaterial.activities.ProductDetails;
import com.sinyuk.jianyimaterial.adapters.ExtendedRecyclerViewAdapter;
import com.sinyuk.jianyimaterial.api.JUser;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseFragment;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.YihuoDetailsService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;
import com.sinyuk.jianyimaterial.widgets.CheckableImageView;
import com.sinyuk.jianyimaterial.widgets.LabelView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.2.21.
 */
public class UserLikesFragment extends BaseFragment {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.root_view)
    FrameLayout rootView;

    private List<YihuoDetails> likesList;
    private LikesListAdapter adapter;

    private static UserLikesFragment instance;
    private YihuoDetailsService yihuoDetailsService;
    private String[] schoolArray;

    public static UserLikesFragment getInstance() {
        if (null == instance)
            instance = new UserLikesFragment();
        return instance;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_user_likes;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViewsAndEvent() {
        schoolArray = mContext.getResources().getStringArray(R.array.schools_sort);
        yihuoDetailsService = DaoUtils.getYihuoDetailsService();
        String uId = PreferencesUtils.getString(mContext, StringUtils.getRes(mContext, R.string.key_user_id));
        if (TextUtils.isEmpty(uId))
            return;
        likesList = yihuoDetailsService.queryAll();

        setupRecyclerView();


    }

    private void setupRecyclerView() {
        adapter = new LikesListAdapter(mContext);

        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(1, R.dimen.general_content_space, true, mContext));
        } else {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(1, R.dimen.tiny_content_space, true, mContext));
        }
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, OrientationHelper.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);

        if (likesList.size() == 0 || likesList.isEmpty()) {

            final View headView = LayoutInflater.from(mContext).inflate(R.layout.hint_nothing_here, recyclerView, false);
//
            adapter.setHeaderView(headView);
        } else {
            adapter.removeHeader();
            LogUtils.simpleLog(UserLikesFragment.class, "size: " + likesList.size());
        }

        adapter.setData(likesList);

    }


    public class LikesListAdapter extends ExtendedRecyclerViewAdapter<YihuoDetails, LikesListAdapter.LikeViewHolder> {



        public LikesListAdapter(Context context) {
            super(context);
        }

        @Override
        public void footerOnVisibleItem() {

        }

        @Override
        public LikeViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_like, parent, false);
            return new LikeViewHolder(v);
        }

        @Override
        public void onBindDataItemViewHolder(final LikeViewHolder holder, final int position) {
            final YihuoDetails itemData = getData().get(position);
            holder.titleTv.setText(StringUtils.check(mContext, itemData.getName(), R.string.unknown_title));
            holder.detailsTv.setText(StringUtils.check(mContext, itemData.getDetail(), R.string.unknown_yihuo_description));

            holder.addDateTv.setText(StringUtils.check(mContext,
                    FuzzyDateFormater.getTimeAgo(mContext, itemData.getDate()), R.string.unknown_date));

            holder.priceLabelView.setText(FormatUtils.formatPrice(itemData.getPrice()));

            holder.likeCheckableIv.setChecked(true);
            holder.likeCheckableIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getData().remove(position);
                    notifyMyItemRemoved(position);
                    notifyItemRangeChanged(position, getDataItemCount());

                    yihuoDetailsService.deleteByKey(itemData.getId());
                }
            });

            DrawableRequestBuilder<String> shotRequest = Glide.with(mContext).fromString()
                    .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_icon))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_icon))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .priority(Priority.IMMEDIATE)
                    .thumbnail(0.2f);

            shotRequest.load(JianyiApi.shotUrl(itemData.getPic())).into(holder.shotIv);

            holder.shotIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemData.getPic() != null) {
                        Intent intent = new Intent(mContext, PhotoViewActivity.class);
                        Bundle bundle = new Bundle();

                        ArrayList<String> list = new ArrayList<>();

                        list.add(JianyiApi.shotUrl(itemData.getPic()));

                        bundle.putStringArrayList("shot_urls", list);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                }
            });

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attemptGoto(holder, itemData.getUid(), itemData.getId(), itemData.getName(), itemData.getTime(), itemData.getPrice());
                }
            });
        }

        private void attemptGoto(final LikeViewHolder holder, String userId,
                                 final String id, final String title, final String time, final String price) {
            JsonRequest jsonRequest = new JsonRequest(Request.Method.GET, JianyiApi.userById(userId), null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    // the response is already constructed as a JSONObject!
                    final Gson gson = new Gson();
                    // 接受最原始的JSON数据
                    JUser jsonData = gson.fromJson(response.toString(), JUser.class);
                    // 转换成我的Model
                    JUser.Data data = jsonData.getData();
                    String trans = gson.toJson(data);

                    User userData = gson.fromJson(trans, User.class);

                    if (userData != null) {

                        LogUtils.simpleLog(UserLikesFragment.class, userData.toString());

                        YihuoProfile dummyProfile = new YihuoProfile();

                        dummyProfile.setUsername(userData.getName());
                        dummyProfile.setHeadImg(userData.getHeading());
                        dummyProfile.setName(title);
                        dummyProfile.setTime(time);
                        dummyProfile.setPrice(price);
                        dummyProfile.setTel(userData.getTel());
                        if (null != userData.getSchool() && Integer.parseInt(userData.getSchool()) <= schoolArray.length)
                            dummyProfile.setSchoolname(schoolArray[Integer.parseInt(userData.getSchool()) - 1]); // 它的index 是从1开始的 0 0
                        dummyProfile.setId(id);

                        LogUtils.simpleLog(UserLikesFragment.class, dummyProfile.toString());

                        holder.cardView.setClickable(false); // prevent fast double tap
                        Intent intent = new Intent(mContext, ProductDetails.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(YihuoProfile.TAG, dummyProfile);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        holder.cardView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.cardView.setClickable(true);
                            }
                        }, 1000);

                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: SnackBar提示一下 不过分吧
                    SnackBarFactory.errorNoAction(mContext, getView(), VolleyErrorHelper.getMessage(error, mContext)).show();
                }
            });
            Jianyi.getInstance().addRequest(jsonRequest, User.TAG);
        }

        public class LikeViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.shot_iv)
            ImageView shotIv;
            @Bind(R.id.title_tv)
            TextView titleTv;
            @Bind(R.id.details_tv)
            TextView detailsTv;
            @Bind(R.id.add_date_tv)
            TextView addDateTv;
            @Bind(R.id.like_checkable_iv)
            CheckableImageView likeCheckableIv;
            @Bind(R.id.price_label_view)
            LabelView priceLabelView;
            @Bind(R.id.card_view)
            CardView cardView;

            public LikeViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
