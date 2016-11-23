package com.example.david.codeforces;

import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.david.codeforces.Model.MainModel;
import com.example.david.codeforces.Model.ProblemModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.activity_main)
    SwipeRefreshLayout swipetorefresh;
    RecyclerAdapter adapter;
    ProgressDialog progressDialog;
    int tempid = 1;
    ArrayList<MainModel> problemsetList = new ArrayList<>();
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        ButterKnife.bind(this);
        swipetorefresh.setOnRefreshListener(this);
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        adapter = new RecyclerAdapter(problemsetList, this, getDbItems());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        RecyclerAdapter.OnItemClickListener onItemClickListener = new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

            }
        };
        adapter.setOnItemClickListener(onItemClickListener);
        loadData();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0)
                {
                    int visibleItemCount = mLayoutManager.getChildCount();
                    int totalItemCount = mLayoutManager.getItemCount();
                    int pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
                    if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                    {
                        tempid++;
//                        getData(tempid);
                    }
                    }
                }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.sort) {
            problemsetList.clear();
            startDialog();
            recyclerView.getRecycledViewPool().clear();
            getData(1, "?order=BY_SOLVED_DESC");
        }
        return super.onOptionsItemSelected(item);
    }
    private void getData(int id, String solved){
        startDialog();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://codeforces.com/problemset/page/" + id + solved;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new AsyncExecution().execute(response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Network Error");
            }

        });
        queue.add(stringRequest);
    }

    public void downloadProblem(final String id, final String type, final int position, final String name, final int count, final String tags){
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url ="http://codeforces.com/problemset/problem/" + id +  "/" + type;
        Log.d("Output", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        new AsyncExecution().execute(response);
                        saveInDb(response, id + type, position, name, count, tags);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", "Network Error");
            }

        });
        queue.add(stringRequest);
    }

    private class AsyncExecution extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            try {
                Document doc = Jsoup.parse(params[0]);
                Elements tables = doc.getElementsByClass("datatable");
                Element table1 = tables.first();
                Elements ids = table1.getElementsByClass("id");
                Elements trs = table1.getElementsByTag("tr");

                //Getting all the information from the HTML
                for (int i = 0; i < ids.size(); i++) {
                    ArrayList<String> tagsArray = new ArrayList<>();
                    String output[] = ids.get(i).getElementsByTag("a").first().toString().split("/");
                    String name[] = trs.get(i + 1).getElementsByTag("div").first().toString().split(">");
                    String count[] = trs.get(i + 1).getElementsByTag("td").last().toString().split("x");
                    Element tagDiv = trs.get(i + 1).getElementsByTag("div").get(1);
                    Elements tags = tagDiv.getElementsByTag("a");
                    for (Element tag : tags) {
                        tagsArray.add(tag.attr("title"));
                    }
                    String tempId = "";
                    String tempType = "";
                    String tempName = "";
                    int tempCount = 0;
                    try {
                        //Assigning the values
                        tempId = output[3];
                        tempType = output[4].substring(0, 1);
                        tempName = name[2].substring(0, name[2].length() - 3);
                        tempCount = Integer.parseInt(count[1].substring(0, count[1].length() - 10));
                    } catch (Exception ignored) {

                    } finally {
                        //Setting the model
                        MainModel model = new MainModel(tempId, tempType, tempName, tempCount, tagsArray.toString());
                        problemsetList.add(model);
                    }
                }
            } catch (Exception ignored) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            adapter.notifyDataSetChanged();
//            progressDialog.dismiss();
//            progressDialog.cancel();
            swipetorefresh.setRefreshing(false);
        }
    }

    private void loadData(){
        if (isNetworkConnected()){
            getData(1, "");
        }else {
            RealmResults<ProblemModel> items = getDbItems();
            for (ProblemModel item : items){
                MainModel model = new MainModel(item.getProblemId().substring(0,item.getProblemId().length() - 1), item.getProblemId().substring(item.getProblemId().length() - 1),
                        item.getName(), item.getCount(), item.getTags());
                problemsetList.add(model);
                adapter.notifyDataSetChanged();
                if (swipetorefresh.isRefreshing()){
                    swipetorefresh.setRefreshing(false);
                }
            }

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void saveInDb(final String response, final String id, final int position, final String name, final int count, final String tags){
        Log.d("Output", response);
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                ProblemModel problemmodel = bgRealm.createObject(ProblemModel.class, id);
                problemmodel.setId(position);
                problemmodel.setProblem(response);
                problemmodel.setName(name);
                problemmodel.setCount(count);
                problemmodel.setTags(tags);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("Output", "=========== Success =============");
                makeToast("Downloaded Successfully");
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Log.d("Output", "=========== Failed ============="  );
                makeToast("Problem is already saved in the database!");
                error.printStackTrace();
            }
        });
    }

    public RealmResults<ProblemModel> getDbItems(){
        return realm.where(ProblemModel.class).findAll();
    }

    private void makeToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void startDialog(){
//        progressDialog = ProgressDialog.show(this,
//                "Loading!","Please wait");
//        progressDialog.setCanceledOnTouchOutside(true);
//        progressDialog.show();
        swipetorefresh.setRefreshing(true);
    }

    @Override
    public void onRefresh() {
        problemsetList.clear();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
