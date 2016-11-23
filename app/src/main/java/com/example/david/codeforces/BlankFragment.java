package com.example.david.codeforces;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    @BindView(R.id.problem)
    TextView problem;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.memory)
    TextView memory;
    private Unbinder unbinder;
    String typeText, idText, nameText;
    SharedPreferences pref;
    public BlankFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        typeText = pref.getString("type","");
        idText = pref.getString("id","");
        nameText = pref.getString("name","");
        Log.d("Output", nameText + " ");
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        name.setText(nameText);
        pref = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String response = pref.getString("response", "");
        Document doc = Jsoup.parse(response);
        Elements tables = doc.getElementsByClass("ttypography");
        String timeLimits [] = tables.get(0).getElementsByClass("time-limit").toString().split("</div>");
        String memoryLimits [] = tables.get(0).getElementsByClass("memory-limit").toString().split("</div>");
        time.setText(timeLimits[1]);
        memory.setText(memoryLimits[1]);
        String problemset = response.substring(response.indexOf("</div></div><div><p>"),response.indexOf("class=\"sample-tests\">"));
        problem.setText(Html.fromHtml(problemset));
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        edit.apply();
    }


}
