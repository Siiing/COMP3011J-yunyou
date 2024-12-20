package com.zzdayss.yunyou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author Siying.Li
 * @Date 2024/11/1 18:36
 * @Version 1.0
 */
public class MapActivity extends AppCompatActivity {

    private EditText locationSearch;
    private Button searchButton;
    // 其他地图相关的变量和设置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        locationSearch = findViewById(R.id.location_search);
        searchButton = findViewById(R.id.search_button);

        searchButton.setOnClickListener(v -> {
            String location = locationSearch.getText().toString();
            // 执行搜索并显示结果
        });

        // 处理用户选择地点的逻辑，并返回数据
    }

    private void returnSelectedLocation(String location) {
        Intent intent = new Intent();
        intent.putExtra("selectedLocation", location);
        setResult(Activity.RESULT_OK, intent);
        finish(); // 关闭当前 Activity
    }
}
