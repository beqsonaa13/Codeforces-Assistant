package com.example.david.codeforces.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.david.codeforces.Model.InputModel;
import com.example.david.codeforces.R;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;


public class InputRecyclerAdapter extends RecyclerView.Adapter<InputRecyclerAdapter.ViewHolder> {
    private ArrayList<InputModel> problemsetList = new ArrayList<>();

    public InputRecyclerAdapter(ArrayList<InputModel> problemsetList) {
        this.problemsetList = problemsetList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.input_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.input.setText(problemsetList.get(position).getInput());
        holder.output.setText(problemsetList.get(position).getOutput());
    }

    @Override
    public int getItemCount() {
        return problemsetList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.input) TextView input;
        @BindView(R.id.output) TextView output;

        ViewHolder(View view) {
            super(view);
            if (input == null) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
