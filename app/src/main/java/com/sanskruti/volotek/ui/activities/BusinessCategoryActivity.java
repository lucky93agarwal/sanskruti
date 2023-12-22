package com.sanskruti.volotek.ui.activities;

import static com.sanskruti.volotek.utils.Constant.BUSINESS;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.CategoryAdapter;
import com.sanskruti.volotek.custom.animated_video.adapters.VideoCategoryAdapter;
import com.sanskruti.volotek.databinding.ActivityBusinessCategoryBinding;
import com.sanskruti.volotek.listener.ClickListener;
import com.sanskruti.volotek.model.CategoryItem;
import com.sanskruti.volotek.utils.Constant;

public class BusinessCategoryActivity extends AppCompatActivity implements ClickListener<CategoryItem> {

    ActivityBusinessCategoryBinding binding;
    CategoryAdapter adapter;
    private VideoCategoryAdapter videoCategoryAdapter;

    Activity activity;
    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;


        setUpUi();

        new AdsUtils(this).showBannerAds(activity);

    }

    private void setUpUi() {
        binding.toolbar.toolName.setText(getResources().getString(R.string.business_category));
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        adapter = new CategoryAdapter(this, this, false);
        binding.rvCustomCategory.setAdapter(adapter);
        loadData();
    }

    private void loadData() {

        Constant.getTemplateBasedViewModel(this).getDashboardTemplate(page).observe(this, dashboardTemplateResponseMain -> {

            if (dashboardTemplateResponseMain != null){

                videoCategoryAdapter = new VideoCategoryAdapter(activity, dashboardTemplateResponseMain.getData());

                binding.rvCustomCategory.setAdapter(videoCategoryAdapter);

            }

        });

    }

    @Override
    public void onClick(CategoryItem data) {
        Intent intent = new Intent(this, BusinessSubCatActivity.class);
        intent.putExtra(Constant.INTENT_TYPE, BUSINESS);
        intent.putExtra(Constant.INTENT_FEST_ID, data.id);
        intent.putExtra(Constant.INTENT_FEST_NAME, data.name);
        intent.putExtra(Constant.INTENT_POST_IMAGE, "");
        intent.putExtra(Constant.INTENT_VIDEO, data.video);
        startActivity(intent);
    }

}