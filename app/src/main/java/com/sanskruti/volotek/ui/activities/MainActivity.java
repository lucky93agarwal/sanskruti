package com.sanskruti.volotek.ui.activities;

import static com.sanskruti.volotek.utils.Constant.OFFER_IMAGE;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.sanskruti.volotek.AdsUtils.GDPRChecker;
import com.sanskruti.volotek.AdsUtils.InterstitialsAdsManager;
import com.sanskruti.volotek.BuildConfig;
import com.sanskruti.volotek.MyApplication;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.binding.GlideDataBinding;
import com.sanskruti.volotek.custom.animated_video.activities.FavouriteActivity;
import com.sanskruti.volotek.custom.poster.activity.BaseActivity;
import com.sanskruti.volotek.custom.poster.model.ServerData;
import com.sanskruti.volotek.model.AppUpdate;
import com.sanskruti.volotek.model.BusinessItem;
import com.sanskruti.volotek.ui.dialog.UniversalDialog;
import com.sanskruti.volotek.ui.fragments.MyBusinessFragmentBottomSheet;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.FrameUtils;
import com.sanskruti.volotek.utils.MyUtils;
import com.sanskruti.volotek.utils.PermissionUtils;
import com.sanskruti.volotek.utils.PreferenceManager;
import com.sanskruti.volotek.utils.Util;
import com.sanskruti.volotek.viewmodel.HomeViewModel;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity {

    static int b=20;
    static void lucky(){
        b = 10;
    }
    public static ArrayList<ServerData> fontArraylist = new ArrayList<>();
    com.sanskruti.volotek.utils.PreferenceManager preferenceManager;
    UniversalDialog universalDialog;
    String offerImage;
    InterstitialsAdsManager manager;
    Activity activity;
    MyBusinessFragmentBottomSheet fragment;


    public void loadAppData() {


        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.getAppInfo().observe(this, appInfos -> {


            if (appInfos != null) {

                MyUtils.showResponse(appInfos);

                preferenceManager.setString(Constant.PRIVACY_POLICY, appInfos.getAppInfo().getPrivacyPolicy());

                preferenceManager.setString(Constant.TERM_CONDITION, appInfos.getAppInfo().getTermsCondition());

                preferenceManager.setString(Constant.REFUND_POLICY, appInfos.getAppInfo().getRefundPolicy());

                preferenceManager.setString(Constant.PRIVACY_POLICY_LINK, appInfos.getAppInfo().getPrivacyPolicy());

                preferenceManager.setString(Constant.ONESIGNAL_APP_ID, appInfos.getAppInfo().getOnesignalAppId());

                preferenceManager.setString(Constant.PUBLISHER_ID, appInfos.getAdsModel().getAdmobAppId());


                preferenceManager.setString(Constant.BANNER_AD_ID, appInfos.getAdsModel().getAdmobBanner());
                preferenceManager.setString(Constant.INTERSTITIAL_AD_ID, appInfos.getAdsModel().getAdmobInter());
                preferenceManager.setBoolean(Constant.ADS_ENABLE, appInfos.getAdsModel().getAdEnabled() == 1);
                preferenceManager.setBoolean(Constant.INTERSTITIAL_AD_ENABLED, appInfos.getAdsModel().getInterstitialAdsEnable() == 1);
                preferenceManager.setBoolean(Constant.BANNER_AD_ENABLED, appInfos.getAdsModel().getBannerAdsEnable() == 1);
                preferenceManager.setBoolean(Constant.NATIVE_AD_ENABLED, appInfos.getAdsModel().getNativeAdsEnable() == 1);
                preferenceManager.setBoolean(Constant.OPEN_APP_AD_ENABLED, appInfos.getAdsModel().getAppOpensAdsEnable() == 1);
                preferenceManager.setBoolean(Constant.REWARD_AD_ENABLED, appInfos.getAdsModel().getRewardedAdsEnable() == 1);

                preferenceManager.setInt(Constant.INTERSTITIAL_AD_CLICK, appInfos.getAdsModel().getAdmobInterCount());
                preferenceManager.setString(Constant.NATIVE_AD_ID, appInfos.getAdsModel().getAdmobNative());
                preferenceManager.setInt(Constant.NATIVE_AD_COUNT, appInfos.getAdsModel().getAdmobNativeCount());
                preferenceManager.setString(Constant.REWARD_AD, appInfos.getAdsModel().getAdmobReward());
                preferenceManager.setString(Constant.OPEN_AD_ID, appInfos.getAdsModel().getAdmobAppOpen());


                if (preferenceManager.getBoolean(Constant.ADS_ENABLE)) {

                    initializeAds();

                    if (preferenceManager.getString(Constant.PUBLISHER_ID) != null) {

                        MyUtils.updateAdmobAppid(MainActivity.this, preferenceManager.getString(Constant.PUBLISHER_ID));
                    }


                }


                preferenceManager.setString(Constant.RAZORPAY_KEY_ID, appInfos.getAppInfo().getRazorpayKeyId());

                preferenceManager.setString(Constant.PAYTM_ID, appInfos.getAppInfo().paytmMerchantId);
                preferenceManager.setString(Constant.PAYTM_KEY, appInfos.getAppInfo().paytmMerchantKey);

                preferenceManager.setString(Constant.STRIPE_KEY, appInfos.getAppInfo().stripePublishableKey);
                preferenceManager.setString(Constant.STRIPE_SECRET_KEY, appInfos.getAppInfo().stripeSecretKey);

                preferenceManager.setString(Constant.RazorPay, appInfos.getAppInfo().razorpayEnable);
                preferenceManager.setString(Constant.Paytm, appInfos.getAppInfo().paytm_enable);
                preferenceManager.setString(Constant.Stripe, appInfos.getAppInfo().stripeEnable);

                preferenceManager.setString(Constant.CURRENCY, appInfos.getAppInfo().currency);


                if (appInfos.getOfferItem() != null) {

                    preferenceManager.setString(Constant.OFFER_IMAGE, appInfos.getOfferItem().image);

                } else {

                    preferenceManager.remove(Constant.OFFER_IMAGE);

                }


                checkAppUpdate(appInfos.getAppUpdate());

            }


        });


    }

    private void initializeAds() {
        if (preferenceManager.getBoolean(Constant.ADS_ENABLE)) {
            new GDPRChecker()
                    .withContext(MainActivity.this)
                    .withPrivacyUrl(preferenceManager.getString(Constant.PRIVACY_POLICY_LINK))
                    .withPublisherIds(preferenceManager.getString(Constant.PUBLISHER_ID))
                    .check();

        }
    }


    private void checkAppUpdate(AppUpdate appUpdate) {

        if (appUpdate.updatePopupShow.equals("1") && !appUpdate.newAppVersionCode.equals("" + BuildConfig.VERSION_CODE)) {

            universalDialog.showAppInfoDialog(getString(R.string.force_update__button_update), getString(R.string.app__cancel),
                    getString(R.string.force_update_true), appUpdate.versionMessage);
            universalDialog.show();


            if (appUpdate.cancelOption.equals("0")) {
                universalDialog.cancelBtn.setVisibility(View.GONE);
                universalDialog.setCancelable(false);
            } else {
                universalDialog.setCancelable(true);
            }
            universalDialog.cancelBtn.setOnClickListener(v -> {
                universalDialog.cancel();

            });

            universalDialog.okBtn.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(appUpdate.appLink))));

        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyUtils.hideNavigation(this, true);

        setContentView(R.layout.activity_main);
        activeBusinessName = (TextView) findViewById(R.id.active_business_name);
        circularImageView = (CircularImageView) findViewById(R.id.circularImageView);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        llBusiness = (LinearLayout)findViewById(R.id.ll_business);
        toolName = (TextView)findViewById(R.id.tool_name);
        ivPremium = (ImageView)findViewById(R.id.ivPremium);
        favorite = (ImageView)findViewById(R.id.favorite);
        dailyPost = (LottieAnimationView) findViewById(R.id.daily_post);
        addBusiness = (LinearLayout)findViewById(R.id.add_business);
        toolbar = (RelativeLayout)findViewById(R.id.toolbar);
        activity = this;

        preferenceManager = new PreferenceManager(this);

        manager = new InterstitialsAdsManager(this);

        universalDialog = new UniversalDialog(this, false);

        openOfferDialog();

        checkSubscriptionPlansExpireDialog();

        // Save Watermark
   //     MyUtils.saveWatermark(this);


        if (!preferenceManager.getBoolean(Constant.NOTIFICATION_FIRST)) {
            preferenceManager.setBoolean(Constant.NOTIFICATION_ENABLED, true);
            preferenceManager.setBoolean(Constant.NOTIFICATION_FIRST, true);
        }

        checkPermission();

        setUiViews();

    }

    TextView activeBusinessName,toolName;
    CircularImageView circularImageView;
    BottomNavigationView bottomNavigationView;
    LinearLayout llBusiness, addBusiness;
    ImageView ivPremium, favorite;
    LottieAnimationView dailyPost;
    private void setDefault(BusinessItem businessItem) {
        activeBusinessName.setText(businessItem.getName());
        GlideDataBinding.bindImage(circularImageView, businessItem.getLogo());
        Constant.setDefaultBusiness(MainActivity.this, businessItem);

        MyUtils.saveLogoPath(this, businessItem.getLogo(), new FrameUtils.OnLogoDownloadListener() {
            @Override
            public void onLogoDownloaded(String logoPath) {

                preferenceManager.setString(Constant.BUSINESS_LOGO_PATH, logoPath);

            }

            @Override
            public void onLogoDownloadError() {

            }
        });

        MyUtils.showResponse(businessItem);

    }

    private ActivityResultLauncher<String[]> mPermissionResult = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onActivityResult(Map<String, Boolean> result) {

            boolean allPermissionClear = true;
            List<String> blockPermissionCheck = new ArrayList<>();
            for (String key : result.keySet()) {
                if (!(result.get(key))) {
                    allPermissionClear = false;
                    blockPermissionCheck.add(MyUtils.getPermissionStatus(MainActivity.this, key));
                }
            }
            if (blockPermissionCheck.contains("blocked")) {
                showPermissionSettingsDialog();
            }

        }
    });

    private void showPermissionSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Permissions Required");
        builder.setMessage("This app requires certain permissions to function. Please grant the permissions from the app settings.");
        builder.setPositiveButton("Open Settings", (dialog, which) -> {
            openAppSettings();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    private void checkPermission() {

        PermissionUtils permissionUtils = new PermissionUtils(this, mPermissionResult);

        if (!permissionUtils.isPermissiongGranted()) {
            permissionUtils.requestPermissions();
        }
    }
    private void checkSubscriptionPlansExpireDialog() {
        String todayDateTime = new SimpleDateFormat(Constant.TODAY_DATE_PATTERN, Locale.getDefault()).format(System.currentTimeMillis());
        String planEndDate = preferenceManager.getString(Constant.PLAN_END_DATE);

        if (planEndDate != null && !planEndDate.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constant.TODAY_DATE_PATTERN, Locale.getDefault());
            try {
                Date todayDate = dateFormat.parse(todayDateTime);
                Date endDate = dateFormat.parse(planEndDate);

                if (endDate != null && endDate.before(todayDate)) {
                    // Your plan got expired. Show dialog
                    universalDialog.showWarningDialog(getString(R.string.upgrade), getString(R.string.your_plan_expired),
                            getString(R.string.upgrade), true);
                    universalDialog.show();
                    universalDialog.okBtn.setOnClickListener(view -> {
                        universalDialog.cancel();
                        startActivity(new Intent(MainActivity.this, SubsPlanActivity.class));
                    });

                    universalDialog.cancelBtn.setOnClickListener(view -> universalDialog.cancel());

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
    private void openOfferDialog() {
        offerImage = preferenceManager.getString(OFFER_IMAGE);
        if (offerImage != null && !offerImage.isEmpty()) {
            universalDialog.showOfferDialog(offerImage);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) universalDialog.cvOffer.getLayoutParams();

            int width = MyApplication.getColumnWidth(1, getResources().getDimension(com.intuit.ssp.R.dimen._15ssp));

            params.width = width;
            params.height = (int) (width * 1.3);

            universalDialog.cvOffer.setLayoutParams(params);

            universalDialog.show();
            universalDialog.ivOffer.setOnClickListener(v -> {
                universalDialog.cancel();
                startActivity(new Intent(MainActivity.this, SubsPlanActivity.class));
            });
            universalDialog.ivCancel.setOnClickListener(v -> universalDialog.cancel());

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // To pass the value to the fragment. Do not remove this.

        super.onActivityResult(requestCode, resultCode, data);

    }
    private void loadDefaultBusiness() {


        Constant.getUserViewModel(this).getDefaultBusiness(preferenceManager.getString(Constant.USER_ID), "get_default").observe(this, businessItem -> {

            if (businessItem != null) {

                setDefault(businessItem);

            } else {
                Constant.setDefaultBusiness(MainActivity.this, null);
            }

        });


    }
    private void setUiViews() {
        NavController navController = Navigation.findNavController(this, R.id.fl_main);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home_menu:

                    navController.navigate(R.id.homeFragment);
                    topBar(R.string.app_name, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);

                    return true;
                case R.id.custom_menu:

//                    navController.navigate(R.id.customFragment);
//                    topBar(R.string.menu_custom, View.GONE, View.VISIBLE, View.VISIBLE, View.VISIBLE);
                    Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.business_menu:

//                    navController.navigate(R.id.myBusinessFragment);
//                    topBar(R.string.my_business, View.GONE, View.GONE, View.GONE, View.GONE);
                    Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.profile_menu:

                    navController.navigate(R.id.profileFragment);
                    topBar(R.string.menu_profile, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);


                    return true;
                case R.id.download_menu:

                   /* navController.navigate(R.id.allSavedDataFragment);
                    topBar(R.string.my_creation, View.GONE, View.VISIBLE, View.GONE, View.VISIBLE);*/

                    Toast.makeText(activity, "Coming Soon", Toast.LENGTH_SHORT).show();

                    return true;
                default:

                    return false;
            }
        });

        if (preferenceManager.getString(Constant.api_key).equals(Constant.api_key)) {
            Util.loadFirebase(this);
        }

        llBusiness.setVisibility(View.VISIBLE);
        toolName.setVisibility(View.GONE);

        ivPremium.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, SubsPlanActivity.class)));
        favorite.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, FavouriteActivity.class)));
        dailyPost.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DailyPostActivity.class)));

        addBusiness.setOnClickListener(v -> {

            fragment = new MyBusinessFragmentBottomSheet(businessItem -> {

                fragment.dismiss();
                setDefault(businessItem);


            });

            fragment.show(getSupportFragmentManager(), "");


        });

        // Make Directory & Copy Assets Fonts to Directory.

        makeStickerDir(MainActivity.this);
        new CopyFontBG().execute("");

        topBar(R.string.app_name, View.VISIBLE, View.GONE, View.GONE, View.VISIBLE);


    }

    @Override
    protected void onResume() {
        super.onResume();


        loadDefaultBusiness();


    }


    private RelativeLayout toolbar;
    private void topBar(int menu_custom, int gone, int visible, int visible1, int visible3) {
        toolName.setText(getResources().getString(menu_custom));
        llBusiness.setVisibility(gone);
        toolbar.setVisibility(visible3);
        toolName.setVisibility(visible);
        favorite.setVisibility(visible1);
        dailyPost.setVisibility(gone);
        ivPremium.setVisibility(gone);
    }


    @Override
    public void onBackPressed() {

        if (bottomNavigationView.getSelectedItemId() == R.id.home_menu) {

            UniversalDialog universalDialog = new UniversalDialog(this, false);
            universalDialog.showConfirmDialog(getString(R.string.menu_exit), getString(R.string.do_you_want_to_exit), getString(R.string.yes), getString(R.string.no));
            universalDialog.show();
            universalDialog.okBtn.setOnClickListener(v -> {
                universalDialog.cancel();

                try {
                    new Thread(() -> {

                        runOnUiThread(() -> Glide.get(MainActivity.this).clearMemory());

                        try {
                            Glide.get(MainActivity.this).clearDiskCache();
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                super.onBackPressed();
                finish();
                System.exit(0);
            });
        } else {
            bottomNavigationView.setSelectedItemId(R.id.home_menu);
        }


    }

}