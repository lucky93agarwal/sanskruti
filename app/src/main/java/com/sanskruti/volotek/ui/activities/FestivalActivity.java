package com.sanskruti.volotek.ui.activities;

import static com.sanskruti.volotek.utils.Constant.FESTIVAL;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.FestivalAdapter;
import com.sanskruti.volotek.databinding.ActivityFestivalBinding;
import com.sanskruti.volotek.listener.ClickListener;
import com.sanskruti.volotek.model.FestivalItem;
import com.sanskruti.volotek.ui.dialog.UniversalDialog;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.PreferenceManager;

public class FestivalActivity extends AppCompatActivity implements ClickListener<FestivalItem> {

    ActivityFestivalBinding binding;
    FestivalAdapter festivalAdapter;

    PreferenceManager preferenceManager;
    Activity activity;
    boolean isVideo = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFestivalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        preferenceManager = new PreferenceManager(activity);

        shimmer(View.VISIBLE,View.GONE);

        setUiViews();
        setData();

        //show Banner Ads
        new AdsUtils(this).showBannerAds(activity);
    }

    private void setUiViews() {

        isVideo = getIntent().getBooleanExtra(Constant.INTENT_VIDEO, false);


        binding.toolbar.toolName.setText(getResources().getString(R.string.latest_festivals));

        binding.toolbar.back.setOnClickListener(v -> onBackPressed());

        festivalAdapter = new FestivalAdapter(this, this, false);
        binding.rvFestival.setAdapter(festivalAdapter);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(true);
            setData();
        });

    }

    private void shimmer(int gone, int visible) {
        binding.rvFestival.setVisibility(visible);
        binding.shimmerViewContainer.startShimmer();
        binding.shimmerViewContainer.setVisibility(gone);
    }


    private void setData() {

        Constant.getHomeViewModel(this).getFestivals(FESTIVAL, isVideo).observe(this, festivalItems -> {
            if (festivalItems != null && !festivalItems.isEmpty()) {
                binding.swipeRefresh.setRefreshing(false);

                festivalAdapter.setFestData(festivalItems);
                binding.llNotFound.setVisibility(View.GONE);
                shimmer(View.GONE,View.VISIBLE);

            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
                shimmer(View.GONE,View.VISIBLE);
            }

        });


    }

    @Override
    public void onClick(FestivalItem data) {
        if (!data.isActive) {
            UniversalDialog universalDialog = new UniversalDialog(this, true);
            universalDialog.showWarningDialog(getString(R.string.no_festival_image), getString(R.string.festival_image_create),
                    getString(R.string.ok), false);
            universalDialog.show();
            return;
        }
        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra(Constant.INTENT_TYPE, FESTIVAL);
        intent.putExtra(Constant.INTENT_FEST_ID, data.id);
        intent.putExtra(Constant.INTENT_FEST_NAME, data.name);
        intent.putExtra(Constant.INTENT_POST_IMAGE, "");
        intent.putExtra(Constant.INTENT_VIDEO, data.video);
        startActivity(intent);
    }

}