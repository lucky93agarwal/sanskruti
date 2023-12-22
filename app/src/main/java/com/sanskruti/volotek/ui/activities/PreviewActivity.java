package com.sanskruti.volotek.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sanskruti.volotek.MyApplication.context;
import static com.sanskruti.volotek.utils.Constant.BUSINESS;
import static com.sanskruti.volotek.utils.Constant.INTENT_POS;
import static com.sanskruti.volotek.utils.Constant.INTENT_POST_IMAGE;
import static com.sanskruti.volotek.utils.Constant.INTENT_TYPE;
import static com.sanskruti.volotek.utils.Constant.INTENT_VIDEO;
import static com.sanskruti.volotek.utils.Constant.getUserItem;
import static com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.arthenica.mobileffmpeg.FFmpeg;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.sanskruti.volotek.AdsUtils.InterstitialsAdsManager;
import com.sanskruti.volotek.AdsUtils.RewardAdsManager;
import com.sanskruti.volotek.MyApplication;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.LanguageAdapter;
import com.sanskruti.volotek.adapters.PreviewAdapter;
import com.sanskruti.volotek.api.ApiClient;
import com.sanskruti.volotek.api.ApiStatus;
import com.sanskruti.volotek.binding.GlideDataBinding;
import com.sanskruti.volotek.custom.poster.activity.ShareImageActivity;
import com.sanskruti.volotek.custom.poster.activity.ThumbnailActivity;
import com.sanskruti.volotek.model.BusinessItem;
import com.sanskruti.volotek.model.FrameModel;
import com.sanskruti.volotek.model.PostItem;
import com.sanskruti.volotek.model.UserItem;
import com.sanskruti.volotek.ui.dialog.UniversalDialog;
import com.sanskruti.volotek.ui.fragments.MyBusinessFragmentBottomSheet;
import com.sanskruti.volotek.utils.Constant;
import com.sanskruti.volotek.utils.MyUtils;
import com.sanskruti.volotek.utils.NetworkConnectivity;
import com.sanskruti.volotek.utils.PaginationListener;
import com.sanskruti.volotek.utils.PreferenceManager;
import com.sanskruti.volotek.utils.SnapHelperOneByOne;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class PreviewActivity extends AppCompatActivity {

    int screenWidth;
    String type;

    PreviewAdapter adapter;

    PreferenceManager preferenceManager;

    List<PostItem> postItemList;
    private SnapHelperOneByOne helper;

    int position = 0;
    UserItem userItem;
    BusinessItem businessItem;
    NetworkConnectivity networkConnectivity;
    UniversalDialog universalDialog;

    ExoPlayer absPlayerInternal;
    Dialog dialogLanguage;
    String videourl;

    private String ratio;
    CustomPagerAdapter customPagerAdapter;
    List<FrameModel> frameModelList;
    private Activity activity;
    private boolean video = false;

    private Animation animSlideDown;

    private FrameLayout fl_logo, fl_name, fl_phone, fl_email, fl_website, fl_address;
    private ImageView iv_logo, iv_party_logo, iv_image, iv_frame, iv_phone, iv_email, iv_website, iv_address, iv_close_phone, iv_close_name, iv_close_website, iv_close_email, iv_close_address;
    private TextView tv_name, tv_website, tv_phone, tv_email, tv_address;
    private String filePath;
    private InterstitialsAdsManager interstitialsAdsManager;
    private Dialog dialogPremium;
    private ProgressBar progressBarPremium;
    private String language;
    private String categoryID;
    private String postType = "all";
    private String typePost = "images";   // images / video/ greating
    private StaggeredGridLayoutManager layoutManager;
    private boolean loading = false;
    private int pageCount = 1;
    private MyBusinessFragmentBottomSheet bussinessBottomSheetBinding;


    public void showLanguageDialog() {

        dialogLanguage = new Dialog(PreviewActivity.this);
        dialogLanguage.setContentView(R.layout.language_dialog);

        ImageView cancel = dialogLanguage.findViewById(R.id.cancel);
        TextView allLanguage = dialogLanguage.findViewById(R.id.tv_lang_name);

        allLanguage.setOnClickListener(view -> {
            preferenceManager.setString(Constant.USER_LANGUAGE, "");
            finish();
            startActivity(getIntent());

        });

        RecyclerView rvLanguage = dialogLanguage.findViewById(R.id.rv_language);

        Constant.getHomeViewModel(this).getLanguagess().observe(this, languageItems -> {

            if (languageItems != null) {

                if (languageItems.size() > 0) {
                    rvLanguage.setLayoutManager(new GridLayoutManager(activity, 2));
                    LanguageAdapter languageAdapter = new LanguageAdapter(activity, (data) -> {

                        language = data.id;

                        dialogLanguage.dismiss();
                        reloadData();

                        pageCount = 1;
                        loading = false;
                        getPostdata();

                    });
                    languageAdapter.setLanguageItemList(languageItems);
                    rvLanguage.setAdapter(languageAdapter);
                } else {
                    Toast.makeText(activity, "data not found", Toast.LENGTH_SHORT).show();
                }

            }


        });

        if (dialogLanguage.getWindow() != null) {

            dialogLanguage.getWindow().setAttributes(getLayoutParams(dialogLanguage));

            this.dialogLanguage.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            cancel.setOnClickListener(view -> dialogLanguage.dismiss());

        }
    }

    private void reloadData() {
        pageCount = 1;
        position = 0;
        loading = false;
        llNotFound.setVisibility(GONE);
        postItemList.clear();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        universalDialog.showLoadingDialog(PreviewActivity.this, "Loading...");

        new Handler(Looper.getMainLooper()).postDelayed(PreviewActivity.this::getPostdata, 1000);
    }

    public static WindowManager.LayoutParams getLayoutParams(@NonNull Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    private RecyclerView recyclerFrame,rvPost;
    TextView toolName,tvSize, tvAll, tvSquare, tvStory, tvLandscape, tvPortrait;
    LinearLayout progreee, llNotFound;
    ShimmerFrameLayout shimmerViewContainer;
    ImageView ivShow, ivPlayVideo,ivCross, imageView2;
    PlayerView videoPlayer;

    ImageView toolbarIvLanguage, ivCrossVideo, back;
    LinearLayout btnEdit, llSave,size, btnDownload,llSize, llTab;
    CardView cardView,cvMenu;
    TabLayout tabLayout;
    ConstraintLayout constraint, rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        activity = this;
        recyclerFrame =(RecyclerView) findViewById(R.id.recyclerFrame);
        tvLandscape = (TextView)findViewById(R.id.tv_landscape);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        llTab = (LinearLayout)findViewById(R.id.ll_tab);
        imageView2 = (ImageView)findViewById(R.id.imageView2);
        tvSquare = (TextView)findViewById(R.id.tv_square);
        tvPortrait = (TextView)findViewById(R.id.tv_portrait);
        tvStory = (TextView)findViewById(R.id.tv_story);
        tvAll = (TextView)findViewById(R.id.tv_all);
        llSize = (LinearLayout)findViewById(R.id.ll_size);
        back = (ImageView)findViewById(R.id.back); 
        rvPost = (RecyclerView)findViewById(R.id.rv_post);
        btnDownload = (LinearLayout)findViewById(R.id.btn_download); 
        ivCrossVideo = (ImageView)findViewById(R.id.iv_cross_video);
        constraint = (ConstraintLayout)findViewById(R.id.constraint);
        rootView = (ConstraintLayout)findViewById(R.id.rootView); 
        ivCross = (ImageView)findViewById(R.id.iv_cross);
        toolName = (TextView) findViewById(R.id.tool_name);
        progreee = (LinearLayout)findViewById(R.id.progreee);
        tvSize = (TextView)findViewById(R.id.tv_size);
        llNotFound = (LinearLayout)findViewById(R.id.llNotFound);
        cvMenu = (CardView)findViewById(R.id.cv_menu); 
        toolbarIvLanguage = (ImageView)findViewById(R.id.toolbar_iv_language);
        btnEdit = (LinearLayout)findViewById(R.id.btn_edit);
        size = (LinearLayout)findViewById(R.id.size);
        cardView = (CardView)findViewById(R.id.cardView);
        llSave = (LinearLayout)findViewById(R.id.ll_save);
        videoPlayer = (PlayerView)findViewById(R.id.videoPlayer);
        shimmerViewContainer = (ShimmerFrameLayout)findViewById(R.id.shimmer_view_container);
        ivShow = (ImageView)findViewById(R.id.iv_show);
        ivPlayVideo = (ImageView)findViewById(R.id.iv_play_video);
        preferenceManager = new PreferenceManager(this);
        interstitialsAdsManager = new InterstitialsAdsManager(this);

        screenWidth = MyApplication.getColumnWidth(1, getResources().getDimension(com.intuit.ssp.R.dimen._10ssp));

        networkConnectivity = new NetworkConnectivity(this);
        universalDialog = new UniversalDialog(this, false);

        showLanguageDialog();

        postItemList = new ArrayList<>();

        helper = new SnapHelperOneByOne();
        helper.attachToRecyclerView(recyclerFrame);

        loadingShimmerEffect();

        setDialogPremium();

        // Get Saved Business

        businessItem = Constant.getBusinessItem(this);

        Intent intent = getIntent();

        userItem = getUserItem(PreviewActivity.this);

        if (intent.getExtras() != null) {
            categoryID = intent.getStringExtra(Constant.INTENT_FEST_ID);
            type = intent.getStringExtra(INTENT_TYPE);
            videourl = intent.getStringExtra(INTENT_POST_IMAGE);
            position = intent.getIntExtra(INTENT_POS, 0);
            video = intent.getBooleanExtra(INTENT_VIDEO, false);
            language = preferenceManager.getString(Constant.USER_LANGUAGE);

            getPostdata();

        }

        setUpUi();

        showFrames();

    }

    private void getPostdata() {
        toolName.setText(getIntent().getStringExtra(Constant.INTENT_FEST_NAME));

        progreee.setVisibility(VISIBLE);

        Log.d("datassss", "" + type + " " + categoryID + " " + language + video);

        postItemList.clear();
        if (adapter != null) {

            adapter.notifyDataSetChanged();

        }

        Constant.getHomeViewModel(this).getAllPostsByCategory(pageCount, type, postType, categoryID, language, video).observe(this, postItems -> {

            if (postItems != null) {

                if (!postItems.isEmpty()) {

                    MyUtils.showResponse(postItems);

                    postItemList.addAll(postItems);
                    setData(postItemList);

                    llNotFound.setVisibility(GONE);

                } else {

                    llNotFound.setVisibility(VISIBLE);
                    shimmerViewContainer.setVisibility(GONE);

                }

                universalDialog.dissmissLoadingDialog();
                progreee.setVisibility(GONE);

            }

            if (postItemList.isEmpty()) {

                llNotFound.setVisibility(VISIBLE);
            }

            universalDialog.dissmissLoadingDialog();
            shimmerViewContainer.setVisibility(GONE);
            progreee.setVisibility(GONE);

        });

    }


    private void getPostDataMore() {

        Constant.getHomeViewModel(this).getAllPostsByCategory(pageCount, type, postType, categoryID, language, video).observe(this, postItems -> {
            if (postItems != null) {

                MyUtils.showResponse(postItems);

                if (!postItems.isEmpty()) {

                    postItemList.addAll(postItems);
                    adapter.setData(postItemList);

                }

                llNotFound.setVisibility(GONE);
                progreee.setVisibility(GONE);


            }

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        businessItem = Constant.getBusinessItem(PreviewActivity.this);

        if (businessItem != null) {
            universalDialog.cancel();
        }


        if (video && filePath != null) {

            loadVideo(filePath);

        }

    }

    private void loadImages() {

        toolName.setText(getIntent().getStringExtra(Constant.INTENT_FEST_NAME));

        Glide.with(MyApplication.context).asBitmap().load(getIntent().getStringExtra(Constant.INTENT_POST_IMAGE)).into(new CustomTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                ivShow.setImageBitmap(resource);

                ratio = resource.getWidth() + ":" + resource.getHeight();

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });


    }


    private void applyFrameNMusicProcess(String framePath) {


        universalDialog.showLoadingDialog(activity, "Saving Post...");

        String outputDir = MyUtils.getStoreVideoExternalStorage(activity) + File.separator + System.currentTimeMillis() + ".mp4";

        List<String> ffmpegCommandList = new ArrayList<>();
        ffmpegCommandList.add("-i");
        ffmpegCommandList.add(filePath);
        ffmpegCommandList.add("-i");
        ffmpegCommandList.add(framePath);


        ffmpegCommandList.add("-filter_complex");

        ffmpegCommandList.add("[0:v][1:v]overlay[out]");
        ffmpegCommandList.add("-map");
        ffmpegCommandList.add("[out]");
        ffmpegCommandList.add("-c:a");
        ffmpegCommandList.add("copy");

        ffmpegCommandList.add("-s");
        ffmpegCommandList.add(ratio);
        ffmpegCommandList.add("-c:v");
        ffmpegCommandList.add("libx264");
        ffmpegCommandList.add("-preset");
        ffmpegCommandList.add("ultrafast");
        ffmpegCommandList.add("-crf");
        ffmpegCommandList.add("20");
        ffmpegCommandList.add("-shortest");
        ffmpegCommandList.add("-y");
        ffmpegCommandList.add(outputDir);

        String[] ffmpegCommand = ffmpegCommandList.toArray(new String[ffmpegCommandList.size()]);

        FFmpeg.executeAsync(ffmpegCommand, (executionId, returnCode) -> {

            ivPlayVideo.setVisibility(VISIBLE);
            btnEdit.setVisibility(VISIBLE);


            if (returnCode == 1) {
                FFmpeg.cancel(executionId);

                universalDialog.dissmissLoadingDialog();

                MyUtils.showToast(activity, "Try Again!!");
            } else if (returnCode == 0) {
                FFmpeg.cancel(executionId);

                openShareActivity(outputDir);
            } else if (returnCode == 255) {
                android.util.Log.e("finalProcess__", "Command execution cancelled by user.");
            } else {
                String str = String.format("Command execution failed with rc=%d and the output below.",
                        Arrays.copyOf(new Object[]{Integer.valueOf(returnCode)}, 1));
                android.util.Log.i("finalProcess__", str);
            }
        });
    }

    private void saveImage(Bitmap bitmap, boolean z) {

        universalDialog.showLoadingDialog(activity, "Saving Post...");

        File file = new File(MyUtils.getFolderPath(activity, "cache"));

        String extension = z ? ".png" : ".jpg";
        String fileName = "Photo_" + System.currentTimeMillis() + extension;
        String filePath = file.getPath() + File.separator + fileName;

        this.filePath = filePath;

        if (isSaved(bitmap, filePath)) {

            openShareActivity(filePath);
            universalDialog.dissmissLoadingDialog();

        } else {
            MyUtils.showToast(this, getString(R.string.error));
        }

    }


    private boolean isSaved(Bitmap bitmap, String filePath) {
        boolean success;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            bitmap.recycle();
            fileOutputStream.flush();

            MediaScannerConnection.scanFile(this, new String[]{filePath},
                    null, (path, uri) -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append("-> uri=");
                        sb.append(uri);
                        sb.append("-> FILE=");
                        sb.append(path);
                        Uri muri = Uri.parse(path);
                    });
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
            success = false;
        }
        return success;
    }

    public void openShareActivity(String filePath) {

        universalDialog.dissmissLoadingDialog();

        MediaScannerConnection.scanFile(context, new String[]{filePath}, new String[]{filePath.contains("mp4") ? "video/mp4" : "image/png"}, null);

        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));

        Intent intent = new Intent(context, ShareImageActivity.class);
        intent.putExtra("uri", filePath);
        intent.putExtra("way", "Poster");
        startActivity(intent);
    }


    private void setUpUi() {

       back.setOnClickListener(v -> {
            pauseVideo();
            onBackPressed();
            preferenceManager.setString(Constant.USER_LANGUAGE, "");
        });

        toolbarIvLanguage.setOnClickListener(v -> dialogLanguage.show());

        btnDownload.setOnClickListener(v -> {
            if (filePath == null) {
                filePath = postItemList.get(position).image_url;
            }

            Log.d("file_path", filePath);

            if (filePath.contains(".mp4")) {

                ivPlayVideo.setVisibility(GONE);
                btnEdit.setVisibility(GONE);

                int width = Integer.parseInt(ratio.split(":")[0]);
                int height = Integer.parseInt(ratio.split(":")[1]);

                applyFrameNMusicProcess(ThumbnailActivity.convertViewToPng(activity, constraint, width, height));

            }
            else {
                saveImage(viewToBitmap(constraint), true);
            }
        });
        llSave.setOnClickListener(v -> {
            if (filePath == null) {
                filePath = postItemList.get(position).image_url;
            }

            Log.d("file_path", filePath);

            if (filePath.contains(".mp4")) {

                ivPlayVideo.setVisibility(GONE);
                btnEdit.setVisibility(GONE);

                int width = Integer.parseInt(ratio.split(":")[0]);
                int height = Integer.parseInt(ratio.split(":")[1]);

                applyFrameNMusicProcess(ThumbnailActivity.convertViewToPng(activity, constraint, width, height));

            }
            else {

                saveImage(viewToBitmap(constraint), true);

            }

        });
        btnEdit.setOnClickListener(v -> {
            interstitialsAdsManager.showInterstitialAd(this::editPost);
        });
        setupPreviewAdapter();
        // Hide video tab on greetings type.
        if (type.equals(Constant.GREETING)) {

            tabLayout.removeTabAt(1);
            typePost = Constant.GREETING;
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabLayout.selectTab(tab);

                switch (tabLayout.getSelectedTabPosition()) {
                    case 0:

                        video = false;

                        //  getPostdata();

                        if (absPlayerInternal != null) {
                            absPlayerInternal.setPlayWhenReady(false);
                            absPlayerInternal.stop();
                            absPlayerInternal.seekTo(0);
                        }
                        videoPlayer.setVisibility(GONE);
                        reloadData();

                        break;

                    case 1:

                        video = true;

                        //  getPostdata();
                        reloadData();

                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        selectSize();
    }
    private void selectSize() {

        rootView.setOnClickListener(v -> {
            if (size.getVisibility() == VISIBLE) {
                size.setVisibility(GONE);
                TransitionManager.beginDelayedTransition(cvMenu);
            }
        });

        cvMenu.setVisibility(VISIBLE);

        llSize.setOnClickListener(v -> {

            if (size.getVisibility() == View.VISIBLE) {
                size.setVisibility(GONE);
                TransitionManager.beginDelayedTransition(cvMenu);

            } else {

                size.setVisibility(VISIBLE);
                TransitionManager.beginDelayedTransition(cvMenu);
            }


        });

        tvAll.setOnClickListener(v -> {
            postType = "all";
            tvSize.setText(tvAll.getText().toString());
            size.setVisibility(GONE);
            TransitionManager.beginDelayedTransition(cvMenu);
            reloadData();

        });

        tvSquare.setOnClickListener(v -> {
            postType = "square";
            tvSize.setText(tvSquare.getText().toString());
            size.setVisibility(GONE);
            TransitionManager.beginDelayedTransition(cvMenu);
            reloadData();

        });

        tvStory.setOnClickListener(v -> {
            postType = "story";
            tvSize.setText(tvSquare.getText().toString());
            size.setVisibility(GONE);
            TransitionManager.beginDelayedTransition(cvMenu);
            reloadData();

        });

        tvLandscape.setOnClickListener(v -> {
            postType = "landscape";
            tvSize.setText(tvLandscape.getText().toString());
            size.setVisibility(GONE);
            TransitionManager.beginDelayedTransition(cvMenu);
            reloadData();

        });

        tvPortrait.setOnClickListener(v -> {
            postType = "portrait";
            tvSize.setText(tvPortrait.getText().toString());
            size.setVisibility(GONE);
            TransitionManager.beginDelayedTransition(cvMenu);
            reloadData();
        });
    }
    private Bitmap viewToBitmap(View view) {

        //  tvEdit.setVisibility(GONE);

        try {

            Bitmap createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);


            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } finally {
            view.destroyDrawingCache();
        }
    }
    private void setupPreviewAdapter() {

        adapter = new PreviewAdapter(this, (data) -> {
            if (postItemList != null) {
                position = data;

                setImageShow(postItemList.get(data));
            }
        }, 3, getResources().getDimension(com.intuit.ssp.R.dimen._2ssp));
        rvPost.setAdapter(adapter);


        layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);

        rvPost.setLayoutManager(layoutManager);


        rvPost.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return loading;
            }

            @Override
            public void loadMoreItems() {
                loading = true;
                pageCount++;

                progreee.setVisibility(View.VISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(PreviewActivity.this::getPostDataMore, 100);

            }
        });


        ivPlayVideo.setOnClickListener(v -> {
            ivPlayVideo.setVisibility(GONE);
            absPlayerInternal.seekTo(0);
            absPlayerInternal.setPlayWhenReady(true);
        });
    }
    public void setDialogPremium() {

        dialogPremium = new Dialog(activity, R.style.MyAlertDialog);
        dialogPremium.setContentView(R.layout.dialog_layout_watermark_option);

        Window window = dialogPremium.getWindow();


        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        ImageView close = dialogPremium.findViewById(R.id.iv_close);
        LinearLayout premium = dialogPremium.findViewById(R.id.cv_no);

        TextView tvTitle = dialogPremium.findViewById(R.id.tv_title);
        TextView dialogMessageTextView = dialogPremium.findViewById(R.id.dialogMessageTextView);

        tvTitle.setText("Premium Template");
        dialogMessageTextView.setText(R.string.please_subscribe_videp);

        progressBarPremium = dialogPremium.findViewById(R.id.pb_loading);

        close.setOnClickListener(view -> {

            dialogPremium.dismiss();
            progressBarPremium.setVisibility(GONE);

        });

        premium.setOnClickListener(view -> {

            startActivity(new Intent(context, SubsPlanActivity.class));
            dialogPremium.dismiss();

        });

        LinearLayout yes = dialogPremium.findViewById(R.id.cv_yes);

        yes.setOnClickListener(view -> {

            progressBarPremium.setVisibility(View.VISIBLE);

            new RewardAdsManager(PreviewActivity.this, new RewardAdsManager.OnAdLoaded() {
                @Override
                public void onAdClosed() {
                    Toast.makeText(context, "Ad Not loaded", Toast.LENGTH_SHORT).show();
                    progressBarPremium.setVisibility(GONE);
                }

                @Override

                public void onAdWatched() {

                    // Open Next Activity if Ad Showes.

                    openNextActivity();

                }
            });

        });

    }

    public void showFrames() {

        universalDialog.showLoadingDialog(activity, "Loading...");

        frameModelList = new ArrayList<>();

        ApiClient.getApiDataService().getDesiredCustomFrame(preferenceManager.getString(Constant.USER_ID), "png").enqueue(new Callback<ApiStatus>() {
            @Override
            public void onResponse(Call<ApiStatus> call, Response<ApiStatus> response) {

                if (response.isSuccessful() && response.body() != null) {

                    // Set all the  desired frames.
                    frameModelList.addAll(response.body().getDesiredFramesModels());

                }

                setDefaultFramesAdapter();
                universalDialog.dissmissLoadingDialog();

            }

            @Override
            public void onFailure(Call<ApiStatus> call, Throwable t) {

                setDefaultFramesAdapter();

                universalDialog.dissmissLoadingDialog();
                t.printStackTrace();
            }
        });


    }

    private void setDefaultFramesAdapter() {

        frameModelList.addAll(getBusinessFrames());

        customPagerAdapter = new CustomPagerAdapter(frameModelList);

        customPagerAdapter.setFrameModelList(frameModelList);
        recyclerFrame.setAdapter(customPagerAdapter);

        ScrollingPagerIndicator recyclerIndicator = findViewById(R.id.indicator);
        recyclerIndicator.attachToRecyclerView(recyclerFrame);

    }

    public static List<FrameModel> getBusinessFrames() {

        List<FrameModel> frameModels = new ArrayList<>();


        frameModels.add(new FrameModel("0", "", "", "", "", R.layout.frame_2, R.drawable.frame_preview_2, false, BUSINESS, ""));
        frameModels.add(new FrameModel("1", "", "", "", "", R.layout.frame_1, R.drawable.frame_preview_1, false, BUSINESS, ""));
        frameModels.add(new FrameModel("2", "", "", "", "", R.layout.frame_3, R.drawable.frame_preview_3, false, BUSINESS, ""));
        frameModels.add(new FrameModel("3", "", "", "", "", R.layout.frame_4, R.drawable.frame_preview_4, false, BUSINESS, ""));
        frameModels.add(new FrameModel("4", "", "", "", "", R.layout.frame_5, R.drawable.frame_preview_5, false, BUSINESS, ""));

        return frameModels;
    }

    public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.ViewHolder> {

        List<FrameModel> frameModels;

        public CustomPagerAdapter(List<FrameModel> frameModels) {
            this.frameModels = frameModels;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        public CustomPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CustomPagerAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_frame, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CustomPagerAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);


            FrameModel frameModel = frameModels.get(position);


            holder.imageView.setVisibility(GONE);
            holder.frameLay.setVisibility(VISIBLE);
            setBusinessFrameData(frameModel, holder.frameLay);


        }

        private void setBusinessFrameData(FrameModel frame, FrameLayout frameLay) {

            View view = LayoutInflater.from(context).inflate(frame.getLayout(), null);


            View frameView = view;

            frameLay.removeAllViews();
            frameLay.addView(frameView);

            fl_logo = frameView.findViewById(R.id.fl_logo);
            fl_name = frameView.findViewById(R.id.fl_name);
            fl_email = frameView.findViewById(R.id.fl_email);
            fl_website = frameView.findViewById(R.id.fl_website);
            fl_phone = frameView.findViewById(R.id.fl_phone);
            fl_address = frameView.findViewById(R.id.fl_address);

            tv_name = frameView.findViewById(R.id.tv_name);
            tv_website = frameView.findViewById(R.id.tv_website);
            tv_phone = frameView.findViewById(R.id.tv_phone);
            tv_email = frameView.findViewById(R.id.tv_email);
            tv_address = frameView.findViewById(R.id.tv_address);
            iv_logo = frameView.findViewById(R.id.iv_logo);

            iv_phone = frameView.findViewById(R.id.iv_phone);
            iv_email = frameView.findViewById(R.id.ivEmail);
            iv_website = frameView.findViewById(R.id.iv_website);
            iv_address = frameView.findViewById(R.id.iv_address);

            iv_close_phone = frameView.findViewById(R.id.iv_phone_close);
            iv_close_name = frameView.findViewById(R.id.iv_name_close);
            iv_close_email = frameView.findViewById(R.id.iv_email_close);
            iv_close_website = frameView.findViewById(R.id.iv_website_close);
            iv_close_address = frameView.findViewById(R.id.iv_address_close);

            fl_logo.setVisibility(VISIBLE);

            if (businessItem != null) {

                tv_name.setText(businessItem.name);
                tv_email.setText(businessItem.email);
                tv_phone.setText(businessItem.phone);
                tv_website.setText(businessItem.website);
                tv_address.setText(businessItem.address);

                GlideDataBinding.bindImage(iv_logo, businessItem.logo);

            }

        }

        @Override
        public int getItemCount() {
            return frameModels.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public void setFrameModelList(List<FrameModel> frameModelList) {

            this.frameModels = frameModelList;

        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            FrameLayout frameLay;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                frameLay = itemView.findViewById(R.id.frameLay);
                imageView = itemView.findViewById(R.id.iv_frame);

            }
        }
    }

    private void openNextActivity() {
        Toast.makeText(context, "Congratulations, You unlocked it", Toast.LENGTH_SHORT).show();
        progressBarPremium.setVisibility(GONE);
        dialogPremium.dismiss();

        pauseVideo();

        Intent intent = new Intent(PreviewActivity.this, ThumbnailActivity.class);
        intent.putExtra("backgroundImage", postItemList.get(position).image_url);
        intent.putExtra("type", typePost);
        intent.putExtra("sizeposition", ratio);
        startActivity(intent);


    }

    private void editPost() {
        if (postItemList != null && postItemList.size() > 0) {

            if (!networkConnectivity.isConnected()) {
                universalDialog.showErrorDialog(getString(R.string.error_message__no_internet), getString(R.string.ok));
                universalDialog.setCustomAnimationResource("no_internet.json");
                universalDialog.show();
                return;
            }

            if (!preferenceManager.getBoolean(Constant.IS_LOGIN)) {
                universalDialog.showWarningDialog(getString(R.string.please_login), getString(R.string.login_first_login), getString(R.string.login_login), false);
                universalDialog.setCustomAnimationResource("login.json");

                universalDialog.show();
                universalDialog.okBtn.setOnClickListener(view -> {
                    universalDialog.cancel();
                    startActivity(new Intent(PreviewActivity.this, LoginActivity.class));
                });
                return;
            }

            if (businessItem == null) {

                bussinessBottomSheetBinding = new MyBusinessFragmentBottomSheet(businessItem1 -> {

                    if (businessItem1 != null) {

                        bussinessBottomSheetBinding.dismiss();

                        Constant.setDefaultBusiness(PreviewActivity.this, businessItem1);

                        businessItem = Constant.getBusinessItem(PreviewActivity.this);

                        editPost();


                    }


                });

                bussinessBottomSheetBinding.show(getSupportFragmentManager(), "");

                return;
            }

            if (!preferenceManager.getBoolean(Constant.IS_SUBSCRIBE) && postItemList.get(position).is_premium) {

                dialogPremium.show();
                return;
            }

            pauseVideo();


            Intent intent = new Intent(PreviewActivity.this, ThumbnailActivity.class);
            intent.putExtra("backgroundImage", postItemList.get(position).image_url);
            intent.putExtra("isTamplate", 0);
            intent.putExtra("cat_id", 1);
            intent.putExtra("type", typePost);
            intent.putExtra("sizeposition", ratio);
            startActivity(intent);
        }
    }


    private void setData(List<PostItem> data) {

        MyUtils.showResponse(data);

        ivShow.setVisibility(VISIBLE);
        cardView.setVisibility(VISIBLE);
        rvPost.setVisibility(VISIBLE);
        btnEdit.setVisibility(VISIBLE);
        llTab.setVisibility(VISIBLE);

        llSave.setVisibility(GONE);

        if (type.equals(BUSINESS)) {
            toolbarIvLanguage.setVisibility(GONE);
        } else {
            toolbarIvLanguage.setVisibility(GONE);
        }

        shimmerViewContainer.setVisibility(GONE);

        adapter.setData(data);

        if (getIntent().getStringExtra(INTENT_POST_IMAGE).equals("")) {

            position = new Random().nextInt(data.size());
            setImageShow(data.get(position));

        } else {

            loadImages();

        }

    }

    private void setImageShow(PostItem postItem) {

        androidx.transition.Transition transition1 = new AutoTransition();
        transition1.setDuration(200);
        TransitionManager.beginDelayedTransition(rootView, transition1);

        if (video) {

            ivCrossVideo.setVisibility(postItem.is_premium ? VISIBLE : GONE);
            if (userItem != null && userItem.isSubscribed) {
                ivCrossVideo.setVisibility(GONE);
            }

            pauseVideo();
            loadVideo(postItem.image_url);


        } else {
            videoPlayer.setVisibility(GONE);
            ivCross.setVisibility(postItem.is_premium ? VISIBLE : GONE);
            if (userItem != null && userItem.isSubscribed) {
                ivCross.setVisibility(GONE);
            }
            ivShow.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            Glide.with(MyApplication.context).asBitmap().load((postItem.image_url)).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {

                    ivShow.setImageBitmap(resource);

                    ratio = resource.getWidth() + ":" + resource.getHeight();

                    /*if (ratio.equals("720:1280")){
                        swipeFrame.setVisibility(View.INVISIBLE);
                    }else{

                        swipeFrame.setVisibility(VISIBLE);
                    }*/

                    // Update the ImageView's layout params
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) ivShow.getLayoutParams();
                    params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
                    ivShow.setLayoutParams(params);


                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }

    }

    private void loadVideo(String videoURL) {
        videoPlayer.setVisibility(VISIBLE);
        videoPlayer.setShowBuffering(true);
       toolName.setText(getIntent().getStringExtra(Constant.INTENT_FEST_NAME));


        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoURL);
        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));


        ratio = width + ":" + height;

        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        absPlayerInternal = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef); //creating a player instance

        int appNameStringRes = R.string.app_name;
        String userAgent = Util.getUserAgent(this, this.getString(appNameStringRes));
        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
        Uri uriOfContentUrl = Uri.parse(videoURL);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source

        absPlayerInternal.prepare(mediaSource);
        absPlayerInternal.setPlayWhenReady(true); // start loading video and play it at the moment a chunk of it is available offline

        videoPlayer.setPlayer(absPlayerInternal); // attach surface to the view
        videoPlayer.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING);
        absPlayerInternal.addListener(new Player.EventListener() {

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Player.EventListener.super.onLoadingChanged(isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.EventListener.super.onPlayerStateChanged(playWhenReady, playbackState);

                if (playbackState == ExoPlayer.STATE_ENDED) {
                    //  ivPlayVideo.setVisibility(VISIBLE);
                }
            }
        });

        videoPlayer.setOnTouchListener(new View.OnTouchListener() {

            private final GestureDetector gestureDetector = new GestureDetector(PreviewActivity.this, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);

                    if (!absPlayerInternal.getPlayWhenReady()) {
                        absPlayerInternal.setPlayWhenReady(true);
                        videoPlayer.hideController();


                    } else {

                        new Handler(getMainLooper()).postDelayed(() -> absPlayerInternal.setPlayWhenReady(false), 100);
                    }


                    return true;
                }


                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if (!absPlayerInternal.getPlayWhenReady()) {
                        absPlayerInternal.setPlayWhenReady(true);
                    }

                    return super.onDoubleTap(e);

                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void loadingShimmerEffect() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imageView2.getLayoutParams();
        lp.width = screenWidth;
        lp.height = screenWidth;

        imageView2.setLayoutParams(lp);
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pauseVideo();
        if (absPlayerInternal != null) {
            absPlayerInternal.stop();
            absPlayerInternal.setPlayWhenReady(false);
            absPlayerInternal.release();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        pauseVideo();
    }

    private void pauseVideo() {
        if (absPlayerInternal != null) {
            absPlayerInternal.setPlayWhenReady(false);
            absPlayerInternal.stop();
            absPlayerInternal.seekTo(0);
        }
    }

}