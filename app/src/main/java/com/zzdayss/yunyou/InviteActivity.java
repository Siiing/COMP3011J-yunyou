package com.zzdayss.yunyou;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.zzdayss.yunyou.adapter.FriendAdapter;
import com.zzdayss.yunyou.adapter.InviteAdapter;
import com.zzdayss.yunyou.dao.FriendDao;
import com.zzdayss.yunyou.utils.JDBCUtils;

import java.util.ArrayList;
import java.util.List;

public class InviteActivity extends AppCompatActivity {

    private EditText searchBox;
    private Button searchButton;
    private ListView resultListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        Integer feedbackId = Integer.parseInt(getIntent().getStringExtra("feedbackId"));

        //hide actionbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setStatusBar();

        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_button);
        resultListView = findViewById(R.id.result_list_view);

        searchButton.setOnClickListener(v -> {
            String query = searchBox.getText().toString().trim();
            if (query.isEmpty()) {
                Toast.makeText(InviteActivity.this, "Please enter a userAccount to search", Toast.LENGTH_SHORT).show();
            } else {
                searchUser(query, feedbackId);
            }
        });
    }

    // query user account
    private void searchUser(String searchTerm, Integer feedbackId) {
        new Thread(() -> {
            SharedPreferences personal = getSharedPreferences("Personal", MODE_PRIVATE);
            String userAccount = personal.getString("userAccount","");
            List<String> results = new FriendDao().listFriend(userAccount, searchTerm);

            runOnUiThread(() -> {
                if (results.isEmpty()) {
                    Toast.makeText(InviteActivity.this, "No matching userAccount found", Toast.LENGTH_SHORT).show();
                } else {
                    InviteAdapter adapter = new InviteAdapter(InviteActivity.this, results,userAccount,feedbackId);
                    resultListView.setAdapter(adapter);
                }
            });
        }).start();
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
