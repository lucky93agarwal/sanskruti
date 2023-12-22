package com.sanskruti.volotek.ui.activities;

import static com.sanskruti.volotek.utils.Constant.BUSINESS_SUB;
import static com.sanskruti.volotek.utils.Constant.INTENT_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.adapters.FeatureAdapter;
import com.sanskruti.volotek.adapters.SubCategoryAdapter;
import com.sanskruti.volotek.databinding.ActivityBusinessSubCatBinding;
import com.sanskruti.volotek.listener.ClickListener;
import com.sanskruti.volotek.model.CategoryItem;
import com.sanskruti.volotek.model.HomeItem;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.PreferenceManager;

public class BusinessSubCatActivity extends AppCompatActivity implements ClickListener<CategoryItem> {
    ActivityBusinessSubCatBinding binding;
    SubCategoryAdapter subCategoryAdapter;
    String catName;
    String catID;
    PreferenceManager preferenceManager;
    String festtype;
    private HomeItem homeItems;
    private FeatureAdapter featureAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessSubCatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        preferenceManager = new PreferenceManager(this);
        binding.loadingView.setVisibility(View.VISIBLE);

        new AdsUtils(this).showBannerAds(this);

        Intent intent = getIntent();
        catName = intent.getStringExtra(Constant.INTENT_FEST_NAME);
        catID = intent.getStringExtra(Constant.INTENT_FEST_ID);
        festtype = intent.getStringExtra(INTENT_TYPE);

        getData();
        setUiViews();

    }

    private void setUiViews() {

        binding.toolbar.toolName.setText(catName);

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        subCategoryAdapter = new SubCategoryAdapter(this, this, false);
        binding.rvSubCat.setAdapter(subCategoryAdapter);

        featureAdapter = new FeatureAdapter(this, "Custom");
        binding.rvPost.setAdapter(featureAdapter);
        binding.rvPost.setNestedScrollingEnabled(false);
        binding.rvPost.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

                // no need to code
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

                // no need to code
            }
        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(true);
            getData();
        });


    }

    private void getData() {

        Constant.getHomeViewModel(this).getBusinessSubCategory("business_sub_category", catID).observe(this, categoryItems -> {
            if (categoryItems != null) {
                binding.swipeRefresh.setRefreshing(false);
                subCategoryAdapter.setCategories(categoryItems);
                binding.llNotFound.setVisibility(View.GONE);
                binding.loadingView.setVisibility(View.GONE);

            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
                binding.loadingView.setVisibility(View.GONE);
            }
        });

        Constant.getHomeViewModel(this).getSubBusinessCatHome(catID).observe(this, homeItem -> {
            if (homeItem != null) {
                binding.swipeRefresh.setRefreshing(false);
                featureAdapter.setFeatureItemList(homeItems.featureItemList);
            }
        });

    }

    @Override
    public void onClick(CategoryItem data) {

        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(Constant.INTENT_TYPE, BUSINESS_SUB);
        intent.putExtra(Constant.INTENT_FEST_ID, data.id);
        intent.putExtra(Constant.INTENT_FEST_NAME, data.name);
        intent.putExtra(Constant.INTENT_POST_IMAGE, "");
        intent.putExtra(Constant.INTENT_VIDEO, data.video);
        startActivity(intent);
    }
}