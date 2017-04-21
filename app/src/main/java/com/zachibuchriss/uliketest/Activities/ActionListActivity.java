package com.zachibuchriss.uliketest.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zachibuchriss.uliketest.Adapters.ActionListAdapter;
import com.zachibuchriss.uliketest.Object.Action;
import com.zachibuchriss.uliketest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ActionListActivity extends AppCompatActivity {

    private RecyclerView rv;
    private ActionListAdapter actionListAdapter;
    private ArrayList<Action> actionArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_list);

        try {
            JSONArray arr = new JSONArray(loadJSONFromAsset());
            actionArrayList = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject jo_inside = arr.getJSONObject(i);
                JSONArray days_list = jo_inside.getJSONArray("valid_days");
                int[] days = new int[days_list.length()];
                for (int r = 0; r < days_list.length(); r++) {
                    days[r] = days_list.getInt(r);
                }
                actionArrayList.add(new Action(jo_inside.getString("type"), jo_inside.getBoolean("enabled"), jo_inside.getInt("priority"), days, jo_inside.getInt("cool_down")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        rv = (RecyclerView) findViewById(R.id.rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ActionListActivity.this);
        rv.setLayoutManager(linearLayoutManager);
        actionListAdapter = new ActionListAdapter(actionArrayList, ActionListActivity.this);
        rv.setAdapter(actionListAdapter);
        rv.setHasFixedSize(true);


    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("butto_to_action_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
