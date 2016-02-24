package gcm.play.android.samples.com.gcmquickstart;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Ashish Agarwal on 18-08-2015.
 */
class TabsPagerAdapter extends FragmentPagerAdapter {


    Context c;
    public TabsPagerAdapter(FragmentManager fm , Context c) {
        super(fm);this.c = c;
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new MyPcFragment(c);
            case 1:
                // Games fragment activity
                return new OtherPcFragment(c);

        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 2;
    }

}
