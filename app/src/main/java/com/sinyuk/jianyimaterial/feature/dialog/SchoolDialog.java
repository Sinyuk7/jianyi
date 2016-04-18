package com.sinyuk.jianyimaterial.feature.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.ExtendedRecyclerViewAdapter;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.ui.DividerItemDecoration;
import com.sinyuk.jianyimaterial.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.4.18.
 */
public class SchoolDialog extends BottomSheetDialogFragment implements SchoolModel.LoadSchoolsCallback {
    public static final String TAG = "SchoolDialog";
    private static SchoolDialog sInstance;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressBar;
    private Context mContext;
    private SchoolListAdapter mAdapter;
    private List<School> mSchoolList = new ArrayList<>();


    public static SchoolDialog getInstance() {
        if (sInstance == null) {
            synchronized (SchoolDialog.class) {
                if (sInstance == null) {
                    sInstance = new SchoolDialog();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_school_dialog, container);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        SchoolModel.getInstance(mContext).fetchSchools(this);
        showLoadProgress();
    }

    private void showLoadProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void dismissLoadProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupRecyclerView() {

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new SchoolListAdapter(mContext);

        mAdapter.setData(mSchoolList);

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        mSchoolList = schoolList;
        mAdapter.setData(schoolList);
        mAdapter.notifyDataSetChanged();
        dismissLoadProgress();

    }

    @Override
    public void onLoadSchoolParseError(String message) {
        dismissLoadProgress();
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        dismissLoadProgress();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private class SchoolListAdapter extends ExtendedRecyclerViewAdapter<School, MyViewHolder> {


        public SchoolListAdapter(Context context) {super(context);}

        @Override
        public void footerOnVisibleItem() {

        }

        @Override
        public MyViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_school_dialog_list_item, parent, false);
            return new MyViewHolder(v);
        }

        @Override
        public void onBindDataItemViewHolder(MyViewHolder holder, int position) {
            School data = getData().get(position);

            holder.mNameTv.setText(data.getName());

            holder.mNameTv.setOnClickListener(v -> LogUtils.simpleLog(SchoolDialog.class, "Select school at: " + (position + 1)));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.name_tv)
        TextView mNameTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
