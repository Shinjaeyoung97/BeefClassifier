package com.example.beefclassifier;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TotalBoardFragment extends Fragment {
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private String[] titles = new String[]{"게시판", "내가 쓴 글"};


    public TotalBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_total_board, container, false);
        viewPager2 = view.findViewById(R.id.Viewpager2);
        tabLayout = view.findViewById(R.id.TabLayout);
        viewPager2.setAdapter(new PageAdapter(this));
        viewPager2.setOffscreenPageLimit(1);
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(titles[position]);
            }
        }).attach();



        return view;
    }
}


class PageAdapter extends FragmentStateAdapter {


    public PageAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) { //프래그먼트 사용 포지션 설정 0 이 첫탭
            return new BoardFragment();
        } else {
            return new MyBoardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}