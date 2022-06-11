package k.k.helper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class View_Item extends AppCompatActivity {
    private static String IP_ADDRESS = "http://helper.dothome.co.kr";
    private static String TAG = "PHP Log:";

    private ArrayList<PersonalData> mArrayList;
    private ItemAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private EditText mEditTextSearchKeyword;
    private String mJsonString;
    private TextView mTextViewResult;

    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment4;

    private GoogleMap googleMap;
    private ClusterManager<MyItem> myItemClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_item);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEditTextSearchKeyword = (EditText) findViewById(R.id.edit_text);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        SupportMapFragment appFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        appFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                View_Item.this.googleMap = googleMap;
                final LatLng latLng = new LatLng(36.84082025252936, 127.18046587264169);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(16));


                myItemClusterManager = new ClusterManager<MyItem>(View_Item.this, googleMap);
                googleMap.setOnCameraIdleListener(myItemClusterManager);
                googleMap.setOnMarkerClickListener(myItemClusterManager);
                myItemClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
                    @Override
                    public boolean onClusterItemClick(MyItem item) {
                        double distance = (int) SphericalUtil.computeDistanceBetween(latLng, item.getPosition());
                        Toast.makeText(View_Item.this, "백석대에서" + item.getTitle() + "까지의 거리는 " + distance + "m", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                addItem();
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

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mEditTextSearchKeyword = (EditText) findViewById(R.id.edit_text);

        mArrayList = new ArrayList<>();

        mAdapter = new ItemAdapter(this, mArrayList);
        mRecyclerView.setAdapter(mAdapter);

        Button button_search = (Button) findViewById(R.id.select_btn);
        button_search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                String Keyword = mEditTextSearchKeyword.getText().toString();
                mEditTextSearchKeyword.setText("");

                GetData task = new GetData();
                task.execute(IP_ADDRESS + "/query.php", Keyword);
            }
        });

        Button button_all = (Button) findViewById(R.id.select_all);
        button_all.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                mArrayList.clear();
                mAdapter.notifyDataSetChanged();

                GetData task = new GetData();
                task.execute(IP_ADDRESS + "/getjson.php", "");
            }
        });
    }

    private class GetData extends AsyncTask<String, String , String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //진행다일로그 시작
            progressDialog = new ProgressDialog(View_Item.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("데이터를 읽어오는 중입니다.");
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "name=" + params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    String name_list[] = new String[20];
    Double lat_list[] = new Double[20];
    Double longitude_list[] = new Double[20];

    public void showResult(){
        String TAG_JSON="helper";
        String TAG_NAME = "name";
        String TAG_CLASS = "class";
        String TAG_ADDR ="addr";
        String TAG_LAT = "lat";
        String TAG_LONGITUDE ="longitude";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<=jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String itemClass = item.getString(TAG_CLASS);
                String addr = item.getString(TAG_ADDR);

                Double lat = item.getDouble(TAG_LAT);
                Double longitude = item.getDouble(TAG_LONGITUDE);

                name_list[i] = name;
                lat_list[i] = lat;
                longitude_list[i] = longitude;

                PersonalData personalData = new PersonalData();

                personalData.setName(name);
                personalData.setItemClass("카테고리: " + itemClass);
                personalData.setAddress("주소: " + addr);

                //Log.d(TAG, name + itemClass + addr + lat + longitude);

                Log.d(TAG, name_list[i] + " | " + lat_list[i] + " | " + longitude_list[i]);
                Log.d(TAG, name_list[i].getClass().getName() + " | " + lat_list[i].getClass().getName() + " | " + longitude_list[i].getClass().getName());

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
    public void addItem() {
        for (int j = 0; j < name_list.length; j++) {

            if (name_list[j] == null)
                continue;

            MyItem offsetItem = new MyItem(lat_list[j], longitude_list[j], name_list[j]);
            myItemClusterManager.addItem(offsetItem);
        }
    }
}
