package com.colorhaake.traveler.view.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.colorhaake.traveler.R;
import com.colorhaake.traveler.TravelerApp;
import com.colorhaake.traveler.injection.component.ActivityComponent;
import com.colorhaake.traveler.injection.component.DaggerActivityComponent;
import com.colorhaake.traveler.injection.module.ActivityModule;
import com.colorhaake.traveler.plain_object.ActivityGroup;
import com.colorhaake.traveler.plain_object.HomeData;
import com.colorhaake.traveler.view.home.ActivityGroupAdapter;
import com.colorhaake.traveler.view.home.ActivityHeader;
import com.cundong.recyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.cundong.recyclerview.RecyclerViewUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MainMvpView {

    public static final String TAG = MainActivity.class.getName();

    @Inject MainPresenter mMainPresenter;

    @Inject ActivityHeader header;

    @BindView(R.id.activity_group_list) RecyclerView listView;

    private ActivityGroupAdapter adapter;

    private ActivityComponent activityComponent;
    public ActivityComponent activityComponent() {
        if (activityComponent == null) {
            activityComponent = DaggerActivityComponent.builder()
                    .activityModule(new ActivityModule(this))
                    .applicationComponent(TravelerApp.get(this).component())
                    .build();
        }
        return activityComponent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        activityComponent().inject(this);

        // list view
        // TODO encapsulate to just one adapter with header/footer/data
        adapter = new ActivityGroupAdapter(new ArrayList<>());
        HeaderAndFooterRecyclerViewAdapter adapterWithHeader =
                new HeaderAndFooterRecyclerViewAdapter(adapter);

        listView.setAdapter(adapterWithHeader);
        listView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerViewUtils.setHeaderView(listView, header.view());

        mMainPresenter.attachView(this);
        mMainPresenter.viewReady(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        header.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        header.stopAutoCycle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mMainPresenter.detachView();
    }


    /***** MVP View methods implementation *****/

    @Override
    public void showHomePage(List<ActivityGroup> list) {
        adapter.setActivityGroups(list);
    }

    @Override
    public void updateHeaderView(HomeData headerInfo) {
        header.update(headerInfo);
    }
}
