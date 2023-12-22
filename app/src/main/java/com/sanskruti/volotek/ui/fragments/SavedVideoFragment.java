package com.sanskruti.volotek.ui.fragments;


import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sanskruti.volotek.R;
import com.sanskruti.volotek.adapters.SavedAdapter;
import com.sanskruti.volotek.custom.poster.activity.ShareImageActivity;
import com.sanskruti.volotek.databinding.FragmentDownloadBinding;
import com.sanskruti.volotek.model.DownloadItem;
import com.sanskruti.volotek.ui.dialog.UniversalDialog;
import com.sanskruti.volotek.utils.MyUtils;
import com.sanskruti.volotek.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SavedVideoFragment extends Fragment {

    FragmentDownloadBinding binding;
    SavedAdapter adapter;
    List<DownloadItem> uriList;
    int DELETE_REQUEST_URI_R = 11, DELETE_REQUEST_URI_Q = 12;
    DownloadItem downloadItem;
    UniversalDialog universalDialog;

    public SavedVideoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDownloadBinding.inflate(getLayoutInflater());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        uriList = new ArrayList<>();
        universalDialog = new UniversalDialog(getActivity(), false);
        setData();

        binding.swipeRefresh.setOnRefreshListener(() -> {
            binding.swipeRefresh.setRefreshing(false);
            setData();
        });

    }

    private void setData() {
        new SavedVideoFragment.LoadImages().execute();
    }


    private void goToNextActivity(Uri path) {

        Intent intent = new Intent(getContext(), ShareImageActivity.class);
        intent.putExtra("uri", path.toString());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == DELETE_REQUEST_URI_R) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getContext(), getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == DELETE_REQUEST_URI_Q) {
            if (resultCode == RESULT_OK) {
                int delete = getContext().getContentResolver().delete(downloadItem.uri, null, null);
                if (delete == 1) {
                    Toast.makeText(getContext(), getResources().getString(R.string.image_deleted), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.error_delete), Toast.LENGTH_SHORT).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class LoadImages extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (adapter == null) {
                adapter = new SavedAdapter(getActivity(), uriList, (item) -> {
                    downloadItem = item;

                    goToNextActivity(downloadItem.getUri());


                });
                binding.rvDownload.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
            if (uriList.size() > 0) {
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.rvDownload.setVisibility(View.VISIBLE);
                binding.llNotFound.setVisibility(GONE);
            } else {
                binding.shimmerViewContainer.stopShimmer();
                binding.rvDownload.setVisibility(View.GONE);
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.llNotFound.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            uriList.clear();

            try {
                File root = new File(MyUtils.getStoreVideoExternalStorage(getActivity()));


                if (root.exists()) {
                    File[] files = root.listFiles();

                    for (int i = 0; i < files.length; i++) {

                        if (files[i].getName().contains("mp4")) {
                            Util.showLog("URI VIDEO: " + Uri.parse(files[i].getAbsolutePath()));
                            uriList.add(new DownloadItem(Uri.parse(files[i].getAbsolutePath()), true));
                        }
                    }
                    Collections.reverse(uriList);
                }
            } catch (Exception e) {
                Util.showErrorLog(e.getMessage(), e);
            }
            return null;
        }
    }
}
