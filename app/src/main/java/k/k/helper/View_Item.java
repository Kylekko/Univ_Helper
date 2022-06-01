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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.clustering.ClusterManager;

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

public class View_Item extends AppCompatActivity {
    private static String IP_ADDRESS = "http://helper.dothome.co.kr";
    private static String TAG = "php로그";

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

    private class GetData extends AsyncTask<String, String, String> {

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

    private void showResult(){

        String TAG_JSON="helper";
        String TAG_NAME = "name";
        String TAG_CLASS = "class";
        String TAG_ADDR ="addr";


        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String itemClass = item.getString(TAG_CLASS);
                String addr = item.getString(TAG_ADDR);

                PersonalData personalData = new PersonalData();

                personalData.setName(name);
                personalData.setItemClass(itemClass);
                personalData.setAddress(addr);

                Log.d(TAG, name + itemClass + addr);

                mArrayList.add(personalData);
                mAdapter.notifyDataSetChanged();
            }



        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }

    private void addItems() {
        double lat[] = new double[]{36.84082025252936,36.84057872227313,36.84054502042929,36.84135309408008,36.840929708490435,36.841102982740644,36.84082023551096,36.84156160334769,36.841060982327235,36.84187722467452,36.84094761097701,36.84184569039077,36.84081057700755,36.84043271052631,36.842429438117236,36.841135229524866};
        double lng[] = new double[]{127.18046587264169  ,127.18078482158457 ,127.18072588398928  ,127.1811650228917,127.18107713776622 ,127.18120086926538,127.18047708374063 ,127.18181856986371,127.18067384499851   ,127.18166516177315  ,127.1811556580755   ,127.18166228436095,127.18090308426909   ,127.18050980638733  ,127.18294736740182   ,127.18073568117393  };
        String name[] = new String[]{"육회한날","한라맥주","장미맨숀","딥인싸이드","알촌","에셀나무","청년다방","썬오브비앤피","게임스토리pc방","와우pc방","메이크엠박스노래방","스타싱어코인노래연습장","공차","이디야커피","피카플레이스","블랙컨테이너"};
        for(int i=0 ; i<16; i++) {
            MyItem offsetItem = new MyItem(lat[i], lng[i],name[i]);
            //MarkerOptions markerOptions = new MarkerOptions().position(offsetItem.getPosition()).title(offsetItem.getTitle());
            //googleMap.addMarker(markerOptions);
            myItemClusterManager.addItem(offsetItem);

        }
    }
}
