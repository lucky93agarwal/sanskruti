package com.sanskruti.volotek.custom.animated_video.activities;


import static com.sanskruti.volotek.utils.Constant.NATIVE_AD_COUNT;
import static com.sanskruti.volotek.utils.MyUtils.isConnectingToInternet;

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
import com.sanskruti.volotek.custom.animated_video.adapters.TemplateListAdapter;
import com.sanskruti.volotek.databinding.ActivitySearchBinding;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.PaginationListener;
import com.sanskruti.volotek.utils.PreferenceManager;
import com.sanskruti.volotek.custom.animated_video.model.TemplateModel;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;
    TemplateListAdapter templateListAdapter;
    ArrayList<TemplateModel> templateModels = new ArrayList<>();
    PreferenceManager preferenceManager;
    private String selectedCat = "Latest";
    private int page = 1;
    private Activity context;
    private StaggeredGridLayoutManager layoutManager;
    private boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;

        hideShimmmerLayout(true);

        preferenceManager = new PreferenceManager(context);

        new AdsUtils(context).showBannerAds(context);
        binding.etSearch.setVisibility(View.GONE);
        Intent intent = getIntent();

        if (intent.getExtras() != null) {

            selectedCat = intent.getStringExtra(Constant.TAG_SEARCH_TERM).trim();

            binding.toolbar.toolName.setText(intent.getStringExtra(Constant.TAG_SEARCH_TERM));

            templateListAdapter = new TemplateListAdapter(this, templateModels);

            setUpRecyclerView();

        }


        binding.refreshLayout.setOnRefreshListener(() -> {

            if (templateListAdapter != null) templateListAdapter.clearData();

            page = 1;

            isLoading = false;

            new Handler(Looper.getMainLooper()).postDelayed(this::loadData, 100);

            binding.refreshLayout.setRefreshing(false);


        });

        binding.toolbar.back.setOnClickListener(view ->{
            onBackPressed();
            finish();
        });

    }

    private void setUpRecyclerView() {

        layoutManager = new StaggeredGridLayoutManager(2, 1);
        binding.allVideos.setLayoutManager(layoutManager);

        binding.allVideos.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public void loadMoreItems() {
                isLoading = true;

                page = page + 1;

                new Handler(getMainLooper()).postDelayed(SearchActivity.this::loadDataMore, 100);

            }
        });


        if (isConnectingToInternet(context)) {

            loadData();

        }

    }

    private void loadData() {

        Constant.getTemplateBasedViewModel(this).getSearchedTemplates(page, selectedCat,Constant.DATA_LIMIT).observe(this, templateResponse -> {

            if (templateResponse != null) {


                int i = 0;

                while (i < templateResponse.getMsg().size()) {
                    if (i % preferenceManager.getInt(NATIVE_AD_COUNT) == 0 && i != 0) {

                        templateModels.add(null);
                    }
                    templateModels.add(templateResponse.getMsg().get(i));
                    i++;
                }

                templateListAdapter = new TemplateListAdapter(context, templateModels);
                binding.allVideos.setAdapter(templateListAdapter);
                binding.progreee.setVisibility(View.GONE);


            }

            hideShimmmerLayout(false);
            binding.refreshLayout.setRefreshing(false);
            if (templateModels.isEmpty()) {

                binding.noData.setVisibility(View.VISIBLE);
            }
        });

    }

    private void loadDataMore() {

        Constant.getTemplateBasedViewModel(this).getSearchedTemplates(page, selectedCat,Constant.DATA_LIMIT).observe(this, templateResponse -> {

            if (templateResponse != null) {



                int i = 0;

                while (i < templateResponse.getMsg().size()) {
                    if (i % preferenceManager.getInt(NATIVE_AD_COUNT) == 0 && i != 0) {
                        templateModels.add(null);
                    }
                    templateModels.add(templateResponse.getMsg().get(i));
                    i++;
                }

                templateListAdapter.setdata(templateModels);

                binding.progreee.setVisibility(View.GONE);

            }

        });


    }

    private void hideShimmmerLayout(boolean isHide) {

        if (isHide) {
            binding.shimmerViewContainer.startShimmer();
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.allVideos.setVisibility(View.GONE);
        } else {

            binding.shimmerViewContainer.hideShimmer();
            binding.shimmerViewContainer.stopShimmer();
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.allVideos.setVisibility(View.VISIBLE);
        }


    }



}
