package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zzdayss.yunyou.adapter.TripAdapter;
import com.zzdayss.yunyou.dao.TripDao;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.Trip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TripActivity extends Activity {

    private SharedPreferences sp;
    private ListView triplist;
    private String userAccount;
    private List<Map<String,Object>> lists;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        triplist = findViewById(R.id.trip_list);
        setStatusBar();

        findViewById(R.id.back_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripActivity.this,MyActivity.class);
                startActivity(intent);
            }
        });

        View planText = findViewById(R.id.plan_text);
        if (planText != null) {
            planText.setOnClickListener(view -> {
                Intent intent = new Intent(TripActivity.this, TripDetailActivity.class);
                startActivity(intent);
            });
        } else {
            Log.e("TripActivity", "plan_text is null, please check your layout");
        }

        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        userAccount = sp.getString("userAccount","");
        trip();
    }

    public void trip() {
        new Thread(() -> {
            TripDao tripDao = new TripDao();
            List<Trip> trip = tripDao.gettrip(userAccount);
            lists = new ArrayList<>();
            for (Trip t : trip) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", t.getId());
                map.put("getDeparture", t.getDeparture());
                map.put("getDestination", t.getDestination());
                map.put("getDate", t.getDate());
                lists.add(map);
            }

            // 创建一个 message 对象
            Message message = Message.obtain();
            message.what = 1;
            message.obj = lists;
            handler.sendMessage(message);
        }).start();
    }


    @SuppressLint("HandlerLeak")
    final Handler handler =new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            final ListView triplist=findViewById(R.id.trip_list);
            if(msg.what==1){
                TripAdapter tripAdapter = new TripAdapter(TripActivity.this, lists,new TripDao());
                triplist.setAdapter(tripAdapter);
            }
        }
    };

    public void tripadd(View view){
        finish();
        startActivity(new Intent(getApplicationContext(), TripaddActivity.class));
    }

    //是否使用特殊的标题栏背景颜色，android5.0以上可以设置状态栏背景色，如果不使用则使用透明色值
    protected boolean useThemestatusBarColor = false;
    //是否使用状态栏文字和图标为暗色，如果状态栏采用了白色系，则需要使状态栏和图标为暗色，android6.0以上可以设置
    protected boolean useStatusBarColor = true;
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemestatusBarColor) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colortheme));//设置状态栏背景色
            } else {
                getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            Toast.makeText(this, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
