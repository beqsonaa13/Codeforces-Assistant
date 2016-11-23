package com.example.david.codeforces.Fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.david.codeforces.Adapters.InputRecyclerAdapter;
import com.example.david.codeforces.Model.InputModel;
import com.example.david.codeforces.R;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InputFragment extends Fragment {
    @BindView(R.id.inputRecycler)
    RecyclerView recyclerView;
    InputRecyclerAdapter adapter;
    private Unbinder unbinder;
    ArrayList<InputModel> inputList = new ArrayList<>();

    public InputFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        SharedPreferences pref = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String response = pref.getString("response", "");
        Document doc = Jsoup.parse(response);
        Elements tables = doc.getElementsByClass("ttypography");
        Elements inputs = tables.first().getElementsByClass("input");
        Elements outputs = tables.first().getElementsByClass("output");
        for (int i = 0; i < inputs.size(); i++){
            InputModel inputModel = new InputModel();
            String input [] = inputs.get(i).getElementsByTag("pre").toString().split("<br />");
            String output [] = outputs.get(i).getElementsByTag("pre").toString().split("<br />");
            inputModel.setInput(" " + Arrays.toString(input).replace(",","\n").replace("[","").replace("]","").replace("<pre>","").replace("</pre>",""));
            inputModel.setOutput(" " + Arrays.toString(output).replace(",","\n").replace("[","").replace("]","").replace("<pre>","").replace("</pre>",""));
            inputList.add(inputModel);
        }
        adapter = new InputRecyclerAdapter(inputList);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

    }

}
