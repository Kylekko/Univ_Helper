package k.k.helper;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

public class NavigationFullMap extends AppCompatActivity {
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;

    private GoogleMap googleMap;
    private ClusterManager<MyItem> myItemClusterManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_full_map);

        SupportMapFragment appFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        appFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                NavigationFullMap.this.googleMap=googleMap;
                final LatLng latLng = new LatLng(36.8420109940228, 127.18203492778801);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));


                myItemClusterManager = new ClusterManager<MyItem>(NavigationFullMap.this, googleMap);
                googleMap.setOnCameraIdleListener(myItemClusterManager);
                googleMap.setOnMarkerClickListener(myItemClusterManager);
                myItemClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        double distance = (int)SphericalUtil.computeDistanceBetween(latLng,item.getPosition());
                        Toast.makeText(NavigationFullMap.this,"백석대에서"+item.getTitle()+"까지의 거리는 "+distance+"m", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                addItems();

            }
        });

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tap_home:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

                        Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(0, 0);

                        return true;

                    case R.id.tab_map:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment2).commit();

                        Intent intent2 = new Intent(getApplicationContext(), NavigationFullMap.class);
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

    private void addItems(){
        double lat[] = new double[]{36.84082025252936,36.84057872227313,36.84054502042929,36.84135309408008,36.840929708490435,36.841102982740644,36.84082023551096,36.84156160334769,36.841060982327235,36.84187722467452,36.84094761097701,36.84184569039077,36.84081057700755,36.84043271052631,36.842429438117236,36.841135229524866};
        double lng[] = new double[]{127.18046587264169  ,127.18078482158457 ,127.18072588398928  ,127.1811650228917,127.18107713776622 ,127.18120086926538,127.18047708374063 ,127.18181856986371,127.18067384499851   ,127.18166516177315  ,127.1811556580755   ,127.18166228436095,127.18090308426909   ,127.18050980638733  ,127.18294736740182   ,127.18073568117393  };
        String name[] = new String[]{"육회한날","한라맥주","장미회관","딥인싸이드","알촌","에셀나무","청년다방","썬오브비앤피","게임스토리pc방","와우pc방","메이크엠박스노래방","스타싱어코인노래연습장","공차(카페)","이디야커피(카페)","피카플레이스(카페)","블랙컨테이너(카페)"};
        for(int i=0 ; i<16; i++) {
            MyItem offsetItem = new MyItem(lat[i], lng[i],name[i]);
            //MarkerOptions markerOptions = new MarkerOptions().position(offsetItem.getPosition()).title(offsetItem.getTitle());
            //googleMap.addMarker(markerOptions);
            myItemClusterManager.addItem(offsetItem);

        }
    }
}