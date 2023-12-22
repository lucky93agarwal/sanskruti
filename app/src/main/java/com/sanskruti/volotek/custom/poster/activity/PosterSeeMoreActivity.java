package com.sanskruti.volotek.custom.poster.activity;

import static com.sanskruti.volotek.utils.Constant.TAG_SEARCH_TERM;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.custom.poster.adapter.SeeMoreThumbnailListAdapter;
import com.sanskruti.volotek.databinding.ActivitySearchBinding;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.PaginationListener;
import com.sanskruti.volotek.utils.PreferenceManager;

import java.util.ArrayList;

public class PosterSeeMoreActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    public int currentPage = 1;
    public boolean isLastPage = false;
    public boolean isLoading = false;
    public String selectedCat;
    SeeMoreThumbnailListAdapter seeMorePosterListAdapter;
    StaggeredGridLayoutManager mLayoutManager;
    PreferenceManager preferenceManager;
    int page = 1;
    private ArrayList<Object> snapDataThumb;

    private Activity context;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        preferenceManager = new PreferenceManager(this);

        snapDataThumb = new ArrayList<>();

        binding.shimmerViewContainer.startShimmer();
        binding.shimmerViewContainer.setVisibility(View.VISIBLE);
        binding.allVideos.setVisibility(View.GONE);

        new AdsUtils(context).showBannerAds(context);

        Intent intent = getIntent();

        if (intent.getExtras() != null) {

            selectedCat = intent.getStringExtra(TAG_SEARCH_TERM).trim();

            binding.toolbar.toolName.setText("" + selectedCat);
            binding.etSearch.setText(selectedCat);

            setUpRecyclerView();

        }

        binding.refreshLayout.setOnRefreshListener(() -> {

            binding.refreshLayout.setRefreshing(false);

            isLoading = false;

            page = 1;

            if (seeMorePosterListAdapter != null) seeMorePosterListAdapter.clearData();

            new Handler(Looper.getMainLooper()).postDelayed(this::getPosterList, 100);


        });

        binding.toolbar.back.setOnClickListener(view -> onBackPressed());

    }


    private void setUpRecyclerView() {

        this.mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.allVideos.setLayoutManager(this.mLayoutManager);
        binding.allVideos.setHasFixedSize(true);
        getPosterList();
    }


    public void getPosterList() {

        Constant.getTemplateBasedViewModel(this).getPosterSearch(page, selectedCat).observe(this, thumbnailThumbFull -> {

            if (thumbnailThumbFull != null) {

                snapDataThumb.addAll(thumbnailThumbFull.getData());
                loadData();

            }

            binding.refreshLayout.setRefreshing(false);
            binding.noData.setVisibility(View.GONE);
            binding.shimmerViewContainer.startShimmer();
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.allVideos.setVisibility(View.VISIBLE);

            if (snapDataThumb.isEmpty()) {
                binding.noData.setVisibility(View.VISIBLE);
            }

        });


    }

    public void getPosterListMore() {

        Constant.getTemplateBasedViewModel(this).getPosterSearch(page, selectedCat).observe(this, thumbnailThumbFull -> {

            if (thumbnailThumbFull != null && thumbnailThumbFull.getData() !=null) {

                if (!thumbnailThumbFull.getData().isEmpty()){
                    snapDataThumb.clear();

                    snapDataThumb = new ArrayList<>(thumbnailThumbFull.getData());

                    seeMorePosterListAdapter.addData(snapDataThumb);
                }



            }

        });


    }


    private void loadData() {
        binding.allVideos.addOnScrollListener(new PaginationListener(this.mLayoutManager) {

            public void loadMoreItems() {
                isLoading = true;
                page = page+1;
                getPosterListMore();

            }

            public boolean isLastPage() {
                return isLastPage;
            }

            public boolean isLoading() {
                return isLoading;
            }
        });

        this.seeMorePosterListAdapter = new SeeMoreThumbnailListAdapter(snapDataThumb, context);
        binding.allVideos.setAdapter(this.seeMorePosterListAdapter);


    }


    public int round(double d) {
        return Math.abs(d - Math.floor(d)) > 0.1d ? ((int) d) + 1 : (int) d;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }
}
