package k.k.helper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.clustering.ClusterManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class Dinner extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Dinner.ItemObject> list = new ArrayList();

    WebView webView;
    String source;

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;

    private GoogleMap googleMap;
    private ClusterManager<MyItem> myItemClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        new Dinner.Description().execute();

        SupportMapFragment appFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        appFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                Dinner.this.googleMap=googleMap;
                LatLng latLng = new LatLng(36.84082025252936,127.18046587264169);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(12));


                myItemClusterManager = new ClusterManager<MyItem>(Dinner.this,googleMap);
                googleMap.setOnCameraIdleListener(myItemClusterManager);
                googleMap.setOnMarkerClickListener(myItemClusterManager);
                myItemClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT).show();
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

        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment1).commit();

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
        double lat[] = new double[]{36.840929708490435,36.841102982740644,36.84082023551096,36.84156160334769};
        double lng[] = new double[]{127.18107713776622 ,127.18120086926538,127.18047708374063 ,127.18181856986371 };
        String name[] = new String[]{"알촌","에셀나무","청년다방","썬오브비앤피"};

        for(int i=0 ; i<4; i++) {
            MyItem offsetItem = new MyItem(lat[i], lng[i],name[i]);
            //MarkerOptions markerOptions = new MarkerOptions().position(offsetItem.getPosition()).title(offsetItem.getTitle());
            //googleMap.addMarker(markerOptions);
            myItemClusterManager.addItem(offsetItem);
        }
    }

    private class Description extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(Dinner.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터 가져오는 중...");
            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Document doc = Jsoup.connect("https://www.mangoplate.com/search/%EB%B0%B1%EC%84%9D%EB%8C%80").get();

                Elements mElementDataSize = doc.select("ul[class=list-restaurants]").select("figcaption");
                int mElementSize = mElementDataSize.size();

                for(Element elem : mElementDataSize) {

                    String my_title = elem.select("h2[class=title]").text();
                    String my_reward = "리뷰: " + elem.select("span[class=review_count]").text();
                    String my_place = "분류: " + elem.select("p[class=etc] span").text();
                    String my_view = "조회: " + elem.select("span[class=view_count]").text();

                    list.add(new Dinner.ItemObject(my_title, my_reward, my_place, my_view));
                }
                Log.d("debug :", "List " + mElementDataSize);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            CrawlingAdapter1 myAdapter = new CrawlingAdapter1(list);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(myAdapter);

            progressDialog.dismiss();
        }
    }

    public class ItemObject {
        private String title;
        private String reward;
        private String place;
        private String view;

        public ItemObject(String title, String reward, String place, String view) {
            this.title = title;
            this.reward = reward;
            this.place = place;
            this.view = view;
        }


        public String getTitle() { return title; }

        public String getReward() { return reward; }

        public String getPlace() { return place; }

        public String getView() { return view; }
    }
}
