package com.sanskruti.volotek.ui.activities;


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
import com.sanskruti.volotek.adapters.FeatureAdapter;
import com.sanskruti.volotek.adapters.SubCategoryAdapter;
import com.sanskruti.volotek.custom.animated_video.adapters.TemplateListAdapter;
import com.sanskruti.volotek.databinding.ActivityPhotoFrameBinding;
import com.sanskruti.volotek.model.CategoryItem;
import com.sanskruti.volotek.model.FeatureItem;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.MyUtils;
import com.sanskruti.volotek.utils.PaginationListener;
import com.sanskruti.volotek.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class GreetingActivity extends AppCompatActivity {

    ActivityPhotoFrameBinding binding;
    TemplateListAdapter templateListAdapter;

    PreferenceManager preferenceManager;
    private String selectedCat = "Latest";
    private int page = 1;
    private Activity context;
    private StaggeredGridLayoutManager layoutManager;
    private boolean isLoading = false;
    FeatureAdapter featureAdapter;

    List<FeatureItem> featureItemList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        context = this;
        featureItemList = new ArrayList<>();
        hideShimmmerLayout(true);

        preferenceManager = new PreferenceManager(context);

        new AdsUtils(context).showBannerAds(context);

        Intent intent = getIntent();

        String name = intent.getStringExtra("name");
        featureAdapter = new FeatureAdapter(context, "HOME");
        binding.allVideos.setAdapter(featureAdapter);



        setUpRecyclerView();


        binding.refreshLayout.setOnRefreshListener(() -> {

            if (templateListAdapter != null) templateListAdapter.clearData();

            page = 1;

            isLoading = false;

            new Handler(Looper.getMainLooper()).postDelayed(this::getData, 100);

            binding.refreshLayout.setRefreshing(false);

            binding.progreee.setVisibility(View.VISIBLE);


        });

        binding.toolbar.back.setOnClickListener(view -> onBackPressed());
        binding.toolbar.toolName.setText(name);

        loadCategories();

    }

    private void loadCategories() {

        List<CategoryItem> categoryItemList = new ArrayList<>();
        categoryItemList.add(new CategoryItem("-1", "All", "", false));

        Constant.getHomeViewModel(this).getCategories(Constant.GREETING).observe(this, categoryItems -> {

            if (categoryItems != null) {

                SubCategoryAdapter categoryAdapter = new SubCategoryAdapter(context, data -> {

                    Intent intent = new Intent(context, PreviewActivity.class);
                    intent.putExtra(Constant.INTENT_TYPE, Constant.GREETING);
                    intent.putExtra(Constant.INTENT_FEST_ID, data.id);
                    intent.putExtra(Constant.INTENT_FEST_NAME, data.getName());
                    intent.putExtra(Constant.INTENT_POST_IMAGE, "");
                    intent.putExtra(Constant.INTENT_VIDEO,false);
                    intent.putExtra("From", "greeting");
                    context.startActivity(intent);

                }, false);

                categoryItemList.addAll(categoryItems);

                categoryAdapter.setCategories(categoryItemList);

                binding.rvCategory.setAdapter(categoryAdapter);

            }


        });

    }

    private void setUpRecyclerView() {

        layoutManager = new StaggeredGridLayoutManager(1, 1);
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

                binding.progreee.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> loadDataMore(), 100);


            }
        });


        if (isConnectingToInternet(context)) {

            getData();

        }

    }


    private void hideShimmmerLayout(boolean isHide) {

        if (isHide) {
            binding.shimmerViewContainer.startShimmer();
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.allVideos.setVisibility(View.GONE);
            binding.progreee.setVisibility(View.GONE);
        } else {

            binding.shimmerViewContainer.hideShimmer();
            binding.shimmerViewContainer.stopShimmer();
            binding.shimmerViewContainer.setVisibility(View.GONE);
            binding.allVideos.setVisibility(View.VISIBLE);
        }

    }

    private void getData() {


        Constant.getHomeViewModel(this).getGreetingData(selectedCat, page).observe(this, featureItems -> {

            if (featureItems != null) {

                MyUtils.showResponse(featureItems);

                binding.refreshLayout.setRefreshing(false);
                featureAdapter.setFeatureItemList(featureItems);
                hideShimmmerLayout(false);

            }else{

                binding.noData.setVisibility(View.VISIBLE);
            }

            binding.progreee.setVisibility(View.GONE);


        });

    }

    private void loadDataMore() {

        Constant.getHomeViewModel(this).getGreetingData(selectedCat, page).observe(this, featureItems -> {

            if (featureItems != null) {


             //   featureItemList.addAll(featureItems);
                featureAdapter.addFeatureItems(featureItems);
                isLoading = false;
            }

            binding.refreshLayout.setRefreshing(false);
            binding.progreee.setVisibility(View.GONE);

        });

    }


}
