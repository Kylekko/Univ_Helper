package k.k.helper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyAdapter extends FragmentStateAdapter {

    public int mCount;

    public MyAdapter(FragmentActivity fa, int count) {
        super(fa);
        mCount = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int index = getRealPosition(position);

        if(index==0) return new viewPager1();
        else if(index==1) return new viewPager2();
        else if(index==2) return new viewPager3();
        else if(index==3) return new viewPager4();
        else return new viewPager5();

    }

    @Override
    public int getItemCount() {
        return 2000;
    } //2000번 슬라이드 가능하게 구현

    public int getRealPosition(int position) { return position % mCount; }

}