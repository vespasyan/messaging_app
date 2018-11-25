package tech.metaphor.www.metaphor;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


class TabsPagerAdapter extends FragmentPagerAdapter {
    //private final List<Fragment> fragmentList = new ArrayList<>();
    //private final List<String> titleList = new ArrayList<>();
    private int numOfTabs;
/*
    public void addFragment(Fragment fragment, String title){
        fragmentList.add(fragment);
        titleList.add(title);
    }
    */

    public TabsPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentAdd();
            case 1:
                return new FragmentChat();
            case 2:
                return new FragmentCall();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }


/*
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
*/



}
