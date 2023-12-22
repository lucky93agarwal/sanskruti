package com.sanskruti.volotek.ui.activities;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.sanskruti.volotek.AdsUtils.AdsUtils;
import com.sanskruti.volotek.R;
import com.sanskruti.volotek.databinding.ActivityFavoriteListBinding;
import com.sanskruti.volotek.databinding.ActivityFestivalBinding;
import com.sanskruti.volotek.ui.fragments.AllCardFragment;
import com.sanskruti.volotek.ui.fragments.MyCardFragment;
import com.sanskruti.volotek.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class DigitalCardActivity extends AppCompatActivity {
    Activity context;
    PreferenceManager preferenceManager;
    ActivityFavoriteListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavoriteListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        preferenceManager = new PreferenceManager(context);

        binding.back.setOnClickListener(view -> onBackPressed());
        //show Banner Ads
         setupViewPager(binding.viewpager);
        binding.tabLayout.setupWithViewPager(binding.viewpager);
        new AdsUtils(this).showBannerAds(context);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new MyCardFragment(), "MY CARD");
        adapter.addFragment(new AllCardFragment(), "ALL CARDS");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());

    }

    public void businessAdded() {

        // re set the viewpager once the businesscard is added to the the user.
        setupViewPager(binding.viewpager);

    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
