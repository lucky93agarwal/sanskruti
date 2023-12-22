package com.sanskruti.volotek.ui.activities;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.SubsPlanAdapter;
import com.sanskruti.volotek.databinding.ActivitySubsPlanBinding;
import com.sanskruti.volotek.model.UserItem;
import com.sanskruti.volotek.ui.dialog.UniversalDialog;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.NetworkConnectivity;
import com.sanskruti.volotek.utils.PreferenceManager;
import com.sanskruti.volotek.utils.Util;

public class SubsPlanActivity extends AppCompatActivity {

    ActivitySubsPlanBinding binding;
    SubsPlanAdapter subsPlanAdapter;
    UniversalDialog universalDialog;
    NetworkConnectivity networkConnectivity;
    PreferenceManager preferenceManager;
    UserItem userItem;
    String planId = "";
    String planPrice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubsPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        universalDialog = new UniversalDialog(this, false);
        networkConnectivity = new NetworkConnectivity(this);
        preferenceManager = new PreferenceManager(this);

        setUiViews();
        getPlanData();
    }

    public void getPlanData() {

        Constant.getHomeViewModel(this).getSubscriptionsPlanList().observe(this, subsPlanItems -> {

            if (subsPlanItems != null) {

                subsPlanAdapter.subsPlanItemList(subsPlanItems);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(GONE);
                binding.rvSubsplan.setVisibility(View.VISIBLE);

            } else {
                binding.llNotFound.setVisibility(View.VISIBLE);
            }

        });

    }

    private void setUiViews() {
        binding.toolbar.toolName.setText(getResources().getString(R.string.subscribe));

        binding.toolbar.back.setOnClickListener(v -> {

            if (getIntent().hasExtra(Constant.PLAN_EXPIRED)) {
                finish();
                startActivity(new Intent(SubsPlanActivity.this, MainActivity.class));
                return;
            }

            onBackPressed();
        });

        subsPlanAdapter = new SubsPlanAdapter(this, item -> {

            planId = item.id;
            planPrice = item.planPrice;
            if (!networkConnectivity.isConnected()) {
                Util.showToast(SubsPlanActivity.this, SubsPlanActivity.this.getResources().getString(R.string.error_message__no_internet));
                return;
            }

            if (!preferenceManager.getBoolean(Constant.IS_LOGIN)) {
                universalDialog.showWarningDialog(SubsPlanActivity.this.getString(R.string.login_login), SubsPlanActivity.this.getString(R.string.login_first_login), SubsPlanActivity.this.getString(R.string.login_login), false);
                universalDialog.setCustomAnimationResource("login.json");
                universalDialog.show();
                universalDialog.okBtn.setOnClickListener(v -> SubsPlanActivity.this.startActivity(new Intent(SubsPlanActivity.this, LoginActivity.class)));
                return;
            }

        });
        binding.rvSubsplan.setAdapter(subsPlanAdapter);
    }

    @Override
    public void onBackPressed() {

        if (getIntent().hasExtra(Constant.PLAN_EXPIRED)) {
            finish();
            startActivity(new Intent(SubsPlanActivity.this, MainActivity.class));
            return;
        }
        super.onBackPressed();
    }


}