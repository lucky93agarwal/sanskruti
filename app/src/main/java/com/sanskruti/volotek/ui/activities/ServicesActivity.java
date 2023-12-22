package com.sanskruti.volotek.ui.activities;

import static android.view.View.GONE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.ServicesAdapter;
import com.sanskruti.volotek.databinding.ActivityServicesBinding;
import com.sanskruti.volotek.utils.Constant;


public class ServicesActivity extends AppCompatActivity {

    ActivityServicesBinding binding;
    ServicesAdapter servicesAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityServicesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getData();

        binding.toolbar.back.setOnClickListener(view -> onBackPressed());
        binding.toolbar.toolName.setText(getString(R.string.services));


        binding.shimmerViewContainer.startShimmer();
        binding.shimmerViewContainer.setVisibility(View.VISIBLE);



        new AdsUtils(this).showBannerAds(this);

        binding.refreshLayout.setOnRefreshListener(() -> {

            new Handler(Looper.getMainLooper()).postDelayed(this::getData, 100);

            binding.refreshLayout.setRefreshing(false);

           // binding.progreee.setVisibility(View.VISIBLE);

            binding.shimmerViewContainer.startShimmer();
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);

        });
    }


    public void getData() {

        Constant.getHomeViewModel(this).getAllService().observe(this, serviceItems -> {


            if (serviceItems != null) {

                servicesAdapter = new ServicesAdapter(this, item -> {

                });

                servicesAdapter.setServiceItemList(serviceItems);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(GONE);

                binding.allList.setAdapter(servicesAdapter);

                Toast.makeText(this, "data "+serviceItems.size(), Toast.LENGTH_SHORT).show();
                binding.allList.setVisibility(View.VISIBLE);
                binding.noData.setVisibility(GONE);
                binding.progreee.setVisibility(GONE);
            } else {
                binding.noData.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(GONE);

            }

        });

    }


}