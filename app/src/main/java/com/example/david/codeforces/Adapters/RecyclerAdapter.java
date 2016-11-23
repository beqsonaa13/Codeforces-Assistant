package com.example.david.codeforces.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.david.codeforces.Activities.DetailedActivity;
import com.example.david.codeforces.Activities.MainActivity;
import com.example.david.codeforces.Model.MainModel;
import com.example.david.codeforces.Model.ProblemModel;
import com.example.david.codeforces.R;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<MainModel> problemsetList = new ArrayList<>();
    private OnItemClickListener mItemClickListener;
    private Context context;
    private RealmResults<ProblemModel> result;
    private Realm realm;

    public RecyclerAdapter(ArrayList<MainModel> problemsetList, Context context, RealmResults<ProblemModel> result) {
        this.problemsetList = problemsetList;
        this.context = context;
        this.result = result;
        Realm.init(context);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(problemsetList.get(position).getId() + "");
        holder.name.setText(problemsetList.get(position).getName());
        holder.type.setText(problemsetList.get(position).getType());
        holder.count.setText(problemsetList.get(position).getCount() + "");
        String tagsString = problemsetList.get(position).getTags() + "";
        holder.tags.setText(tagsString.replace("[","").replace("]",""));
        ProblemModel results = realm.where(ProblemModel.class).equalTo("problemId",problemsetList.get(position).getId() + problemsetList.get(position).getType()).findFirst();
        if (results != null){
            Log.v("Output", "done");
            holder.button.setBackgroundResource(R.drawable.ic_cloud_done_black_24dp);
        }else{
            Log.v("Output", "not done");
            holder.button.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return problemsetList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.cardview)
        LinearLayout card;
        @BindView(R.id.button)
        ImageView button;
        @BindView(R.id.id) TextView id;
        @BindView(R.id.name) TextView name;
        @BindView(R.id.type) TextView type;
        @BindView(R.id.count) TextView count;
        @BindView(R.id.tags) TextView tags;

        ViewHolder(View view) {
            super(view);
            if (id == null) {
                ButterKnife.bind(this, view);
                card.setOnClickListener(this);
                button.setOnClickListener(this);
            }
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(itemView, getAdapterPosition());
               if (v.getId() == R.id.button){
                    button.setBackgroundResource(R.drawable.ic_cloud_done_black_24dp);
                   ((MainActivity)context).downloadProblem(problemsetList.get(getAdapterPosition()).getId(), problemsetList.get(getAdapterPosition()).getType(), getAdapterPosition(),
                           problemsetList.get(getAdapterPosition()).getName(),problemsetList.get(getAdapterPosition()).getCount(),problemsetList.get(getAdapterPosition()).getTags());
               }
                if (v.getId() == R.id.cardview){
                    SharedPreferences sharedpreferences;
                    sharedpreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("id", problemsetList.get(getAdapterPosition()).getId() + "");
                    editor.putString("type", problemsetList.get(getAdapterPosition()).getType());
                    editor.putString("name", problemsetList.get(getAdapterPosition()).getName());
                    editor.apply();

                    Intent intent = new Intent(context, DetailedActivity.class);
                    context.startActivity(intent);
                }
            }
        }
    }
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener =  mItemClickListener;
    }


}
