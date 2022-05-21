package k.k.helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import me.relex.circleindicator.CircleIndicator3;


public class MainActivity_Login extends FragmentActivity {
    private ViewPager2 mPager;
    private FragmentStateAdapter pagerAdapter;
    private int num_page = 5;
    private CircleIndicator3 mIndicator;
    WebView myWebView;

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        //ViewPager2
        mPager = findViewById(R.id.viewpager);
        //Adapter
        pagerAdapter = new MyAdapter(this, num_page);
        mPager.setAdapter(pagerAdapter);
        //Indicator
        mIndicator = findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        mIndicator.createIndicators(num_page, 0);
        //ViewPager Setting
        mPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        mPager.setCurrentItem(1000);
        mPager.setOffscreenPageLimit(5);

        // viewPager
        mPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (positionOffsetPixels == 0) {
                    mPager.setCurrentItem(position);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mIndicator.animatePageSelected(position % num_page);
            }

        });

        final float pageMargin = getResources().getDimensionPixelOffset(R.dimen.pageMargin);
        final float pageOffset = getResources().getDimensionPixelOffset(R.dimen.offset);

        mPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float myOffset = position * -(2 * pageOffset + pageMargin);
                if (mPager.getOrientation() == ViewPager2.ORIENTATION_HORIZONTAL) {
                    if (ViewCompat.getLayoutDirection(mPager) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                        page.setTranslationX(-myOffset);
                    } else {
                        page.setTranslationX(myOffset);
                    }
                } else {
                    page.setTranslationY(myOffset);
                }
            }
        });

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tap_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

                        Intent intent1 = new Intent(getApplicationContext(), MainActivity_Login.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.tab_map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();

                        Intent intent2 = new Intent(getApplicationContext(), NavigationFullMap_Login.class);
                        startActivity(intent2);
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.tab_like:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment3).commit();
                        return true;

                    case R.id.tab_more:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment4).commit();
                        return true;
                }
                return false;
            }
        });
    }

    public void onLogoutClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);

        Toast.makeText(getApplicationContext(), String.format("로그아웃되었습니다"), Toast.LENGTH_SHORT).show();
    }

    public void onDinnerClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), Dinner.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onCafeClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), Cafe.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onDrinkClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), Drink.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onGameClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), Game.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onHouseClicked (View v) {
        Intent intent = new Intent(getApplicationContext(), House.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public void onPreparingClicked (View v) {
        Toast.makeText(getApplicationContext(), String.format("준비중입니다."), Toast.LENGTH_SHORT).show();
    }

}