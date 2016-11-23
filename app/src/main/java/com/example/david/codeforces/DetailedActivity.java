package com.example.david.codeforces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.david.codeforces.Model.ProblemModel;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import butterknife.ButterKnife;
import io.realm.Realm;

public class DetailedActivity extends FragmentActivity {
    SharedPreferences pref;
    ProgressDialog progressDialog;
    private Realm realm;
    String type,id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        ButterKnife.bind(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        pref = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        type = pref.getString("type","");
        id = pref.getString("id","");
        getDataFromDB(id + type);

    }

    private void getData(String type, String id){
        progressDialog = ProgressDialog.show(this,
                "Loading!","Please wait.");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url ="http://codeforces.com/problemset/problem/" + id +  "/" + type;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Output", url);
                        saveData(response);
                        progressDialog.cancel();
                        buildViewpager();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Network Error");
            }

        });
        queue.add(stringRequest);
    }

    private void getDataFromDB(String id){
        ProblemModel result = realm.where(ProblemModel.class).equalTo("problemId", id).findFirst();
        if (result == null){
            Log.d("Output", "---------ERROR--------");
            getData(type, this.id);
        }
        else {
            Log.d("Output", "=====> " + result.getProblem());
            saveData(result.getProblem());
            buildViewpager();
        }
    }

    private void saveData(String response){
        pref = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("response", response);
        editor.apply();
    }

    private void buildViewpager(){
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Problem", BlankFragment.class)
                .add("Input", InputFragment.class)
                .create());
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
