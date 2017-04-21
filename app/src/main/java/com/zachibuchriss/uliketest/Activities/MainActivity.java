package com.zachibuchriss.uliketest.Activities;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.util.TimeUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.zachibuchriss.uliketest.Object.Action;
import com.zachibuchriss.uliketest.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.attr.columnDelay;
import static android.R.attr.key;
import static android.R.attr.progress;
import static android.R.attr.timeZone;

public class MainActivity extends AppCompatActivity {

    private Button press, open_action_list;
    private ArrayList<Action> actionArray;
    private int cool_down;
    private int progressTime = 0;
    private SharedPreferences myPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        press = (Button) findViewById(R.id.pressBtn);
        open_action_list = (Button)findViewById(R.id.open_action_list);

        myPrefs = getApplicationContext().getSharedPreferences("MyPref", 0);
        try {
            JSONArray arr = new JSONArray(loadJSONFromAsset());
            actionArray = new ArrayList<>();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject jo_inside = arr.getJSONObject(i);
                JSONArray days_list =  jo_inside.getJSONArray("valid_days");
                int[] days = new int[days_list.length()];
                for(int r = 0; r < days_list.length(); r++){
                    days[r] = days_list.getInt(r);
                }
                actionArray.add(new Action(jo_inside.getString("type"), jo_inside.getBoolean("enabled"), jo_inside.getInt("priority"), days , jo_inside.getInt("cool_down")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForCoolDown();
            }
        });

        open_action_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActionListActivity.class);
                startActivity(intent);
            }
        });

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

    private void Toast(){
        Toast.makeText(this, "Current Action Is Toast", Toast.LENGTH_SHORT).show();
    }

    private void Animate(){
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        animation.setRepeatCount(0);
        press.startAnimation(animation);
    }

    private void openCall(){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        startActivity(intent);
    }

    private void Notification(){
        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon) // notification icon
                .setContentTitle("Notification!") // title for notification
                .setContentText("Current action is Notification") // message for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(Intent.ACTION_DIAL);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent , PendingIntent.FLAG_ONE_SHOT);
        mBuilder.setContentIntent(pi);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void checkForCoolDown() {
        if (cool_down == 0) {
            Choose();
        } else {
            final ProgressDialog progress = new ProgressDialog(this);

            progress.setProgress(progressTime);
            progress.setMax(cool_down);
            progress.setCancelable(false);
            CountDownTimer mCountDownTimer = new CountDownTimer(cool_down, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    int time = (int) millisUntilFinished / 1000;
                    progress.setMessage("Wait for cool down  -  " + time);
                    progressTime += 1000;
                    progress.setProgress(progressTime);

                }

                @Override
                public void onFinish() {
                    progress.dismiss();
                    Choose();
                }
            };
            mCountDownTimer.start();
            progress.show();
        }
    }

    private void Choose(){
            final MediaPlayer mp = MediaPlayer.create(this, R.raw.error);
            Random r = new Random();
            int rand = r.nextInt(actionArray.size());
            Action act = actionArray.get(rand);
            Calendar cal = Calendar.getInstance();
            int day = cal.get(Calendar.DAY_OF_WEEK);
            boolean isValidDay = false;
            for (int i = 0; i < act.getValid_days().length; i++) {
                if (act.getDay(i) == day) {
                    isValidDay = true;
                    break;
                } else {
                    isValidDay = false;
                }
            }
            if (isValidDay && act.isEnabled()) {
                cool_down = act.getCool_down();
                int max = actionArray.get(0).getPriority();
                int index = 0;
                for (int i = 0; i < actionArray.size(); i++) {
                    if (actionArray.get(i).getPriority() > max) {
                        max = actionArray.get(i).getPriority();
                        index = i;
                    }
                }
                switch (actionArray.get(index).getType()) {
                    case "toast":
                        if(isNetworkAvailable()) {
                            Toast();
                        }
                        else{
                            Toast.makeText(this, "No Internet Connection - Toast Action Didn't Worked", Toast.LENGTH_SHORT).show();
                        }
                        myPrefs.edit().putString("last_use_toast", getCurrentDate()).apply();
                        break;
                    case "animation":
                        Animate();
                        myPrefs.edit().putString("last_use_animation", getCurrentDate()).apply();
                        break;
                    case "call":
                        openCall();
                        myPrefs.edit().putString("last_use_open_call", getCurrentDate()).apply();
                        break;
                    case "notification":
                        Notification();
                        myPrefs.edit().putString("last_use_notification", getCurrentDate()).apply();
                        break;
                }
            } else {
                mp.start();
                Toast.makeText(this, "Failed to run this action", Toast.LENGTH_SHORT).show();
                cool_down = 0;
            }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy / MM / dd ");
        String strDate = "Last use : " + mdformat.format(calendar.getTime());
        return strDate;
    }

}
