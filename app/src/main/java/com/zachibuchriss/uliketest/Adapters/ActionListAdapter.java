package com.zachibuchriss.uliketest.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zachibuchriss.uliketest.Object.Action;
import com.zachibuchriss.uliketest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ZAHI on 21/04/2017.
 */

public class ActionListAdapter extends RecyclerView.Adapter<ActionListAdapter.ActionListViewHolder> {

    private ArrayList<Action> actionArrayList;
    private Context context;
    private SharedPreferences myPrefs;


    public ActionListAdapter(ArrayList<Action> actionArrayList, Context context) {
        this.actionArrayList = actionArrayList;
        this.context = context;
    }


    @Override
    public ActionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.action_item, parent, false);

        return new ActionListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActionListViewHolder holder, int position) {
        final Action action = actionArrayList.get(position);
        myPrefs = context.getSharedPreferences("MyPref", 0);
        holder.type.setText("type: " + action.getType());
        holder.enabled.setText("enabled: " + String.valueOf(action.isEnabled()));
        holder.priority.setText("priority: " + String.valueOf(action.getPriority()));
        List<Integer> days = new ArrayList<>();
        for(int i = 0; i < action.getValid_days().length; i++){
            days.add(action.getDay(i));
        }
        holder.valid_days.setText("valid days:" + String.valueOf(days));
        holder.cool_down.setText("cool down: " + String.valueOf(action.getCool_down()));

        switch (action.getType()){
            case "toast":
                if(myPrefs.getString("last_use_toast", null) == null){
                    holder.last_use.setText(R.string.never_used);
                }
                else{
                    holder.last_use.setText(myPrefs.getString("last_use_toast", null));
                }
                break;
            case "animation":
                if(myPrefs.getString("last_use_animation", null) == null){
                    holder.last_use.setText(R.string.never_used);
                }
                else{
                    holder.last_use.setText(myPrefs.getString("last_use_animation", null));
                }
                break;
            case "call":
                if(myPrefs.getString("last_use_open_call", null) == null){
                    holder.last_use.setText(R.string.never_used);
                }
                else{
                    holder.last_use.setText(myPrefs.getString("last_use_open_call", null));
                }
                break;
            case "notification":
                if(myPrefs.getString("last_use_notification", null) == null){
                    holder.last_use.setText(R.string.never_used);
                }
                else{
                    holder.last_use.setText(myPrefs.getString("last_use_notification", null));
                }
                break;
        }

    }

    @Override
    public int getItemCount() {
        return actionArrayList.size();
    }

    protected class ActionListViewHolder extends RecyclerView.ViewHolder {

        private TextView type, enabled, priority, valid_days, cool_down, last_use;

        private ActionListViewHolder(View v) {
            super(v);

            type = (TextView)v.findViewById(R.id.type);
            enabled = (TextView)v.findViewById(R.id.enabled);
            priority = (TextView)v.findViewById(R.id.priority);
            valid_days = (TextView)v.findViewById(R.id.valid_days);
            cool_down = (TextView)v.findViewById(R.id.cool_down);
            last_use = (TextView)v.findViewById(R.id.last_use);

        }
    }

}
