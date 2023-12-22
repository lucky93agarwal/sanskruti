package com.sanskruti.volotek.custom.poster.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.sanskruti.volotek.custom.poster.fragment.FrameFragment;
import com.sanskruti.volotek.model.FramesModelCategory;
import com.sanskruti.volotek.model.FrameModel;

import java.util.ArrayList;
import java.util.List;

public class FramesAdapter extends FragmentStatePagerAdapter {

    List<FramesModelCategory> list;
    List<FrameModel> frameModels;
    OnFrameSelect listner;



    public interface OnFrameSelect{
        void onSelect(int selectedFrame, FrameModel frameModel);
    }

    public FramesAdapter(@NonNull FragmentManager fm, List<FramesModelCategory> list, OnFrameSelect listener) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.list = list;
        this.listner = listener;
        frameModels = new ArrayList<>();


    }

    public void setList(List<FramesModelCategory> newList) {
        list.clear();
        list.addAll(newList);
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public Fragment getItem(int position) {

        frameModels = list.get(position).getFrameModels();

        return getFrameFragment();

    }


    @NonNull
    private FrameFragment getFrameFragment() {
        return new FrameFragment(frameModels, (position, frameModel) -> listner.onSelect(position,frameModel));
    }

    @Override
    public int getCount() {
        return /*list.size()*/1;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        return list.get(position).getName();
    }
}
