package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.zzdayss.yunyou.dao.TripDao;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.TripDetail;

import java.util.ArrayList;
import java.util.List;

public class TripDetailActivity extends AppCompatActivity {

    private EditText dateEditText;
    private EditText positionEditText;
    private Button editButton;
    private Button saveButton;
    private Button addButton;
    private LinearLayout editTextContainer;
    private TextView dateTextView;
    private boolean isEditable = false;
    public static Boolean selectFlag = false;
    public static String address = null;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_detail);
        setStatusBar();

        //hide actionbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_trip_detail);

        TextView departureTextView = findViewById(R.id.departure_text);
        TextView destinationTextView = findViewById(R.id.destination_text);
        TextView dateTextView = findViewById(R.id.date_text);
        editButton = findViewById(R.id.edit_button);
        saveButton = findViewById(R.id.save_button);
        addButton = findViewById(R.id.add_button);
        editTextContainer = findViewById(R.id.edit_text_container);

        // get the data
        Intent intent = getIntent();
        int id = intent.getIntExtra("id", -1);
        String departure = intent.getStringExtra("departure");
        String destination = intent.getStringExtra("destination");
        String date = intent.getStringExtra("date");

        departureTextView.setText(departure);
        destinationTextView.setText(destination);
        dateTextView.setText(date);

        if (id != -1) {
            loadTripDetails(id);
        }

        setEditTextsEnabled(false);

        // edit_button
        editButton.setOnClickListener(v -> setEditTextsEnabled(true));

        // save_button
        saveButton.setOnClickListener(v -> {
            List<String> dates = new ArrayList<>();
            List<String> positions = new ArrayList<>();


            for (int i = 0; i < editTextContainer.getChildCount(); i++) {
                View child = editTextContainer.getChildAt(i);
                if (child instanceof LinearLayout) {
                    LinearLayout group = (LinearLayout) child;

                    if (group.getChildCount() >= 2) {
                        View dateView = group.getChildAt(0);
                        View positionView = group.getChildAt(1);

                        if (dateView instanceof EditText && positionView instanceof EditText) {
                            String inputDate = ((EditText) dateView).getText().toString().trim();
                            String inputPosition = ((EditText) positionView).getText().toString().trim();

                            inputDate = inputDate.replace("Time: ", "");
                            inputDate = inputDate.replace("[","");
                            inputDate = inputDate.replace("]","");
                            inputDate = inputDate.replace("【","");
                            inputDate = inputDate.replace("】","");
                            Log.d("test", "onCreate: "+inputDate);
                            inputPosition = inputPosition.replace("Position: ", "");

                            dates.add(inputDate);
                            positions.add(inputPosition);
                        }
                    }
                }
            }

            if (id == -1) {
                Toast.makeText(this, "id error", Toast.LENGTH_SHORT).show();
                return;
            }


            UserDao userDao = new UserDao();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean success = userDao.batchInsert(id, positions,dates);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (success) {
                                Toast.makeText(TripDetailActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                setEditTextsEnabled(false);
                            } else {
                                Toast.makeText(TripDetailActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }).start();
        });
        addButton.setOnClickListener(v -> addEditTextGroup());

        //show address
        EditText editText = findViewById(R.id.address_input2);
        if (address!=null){
            editText.setText("Position: " + address);
        }

        //select address
        ImageButton selectAddressButton = findViewById(R.id.select_address2);
        if (selectAddressButton != null) {
            selectAddressButton.setOnClickListener(view -> {
                TripDetailActivity.selectFlag = true;
                Intent intent2 = new Intent(TripDetailActivity.this, MainActivity.class);
                startActivity(intent2);
            });
        } else {
            Log.e("TripDetailActivity", "select_address2 is null");
        }

        findViewById(R.id.back_button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadTripDetails(int id) {
        editTextContainer.removeAllViews();

        new Thread(() -> {
            TripDao tripDao = new TripDao();
            List<TripDetail> details = tripDao.getTripDetails(id);

            runOnUiThread(() -> {
                if (details.isEmpty()) {
                    addEditTextGroup();
                } else {
                    for (TripDetail detail : details) {
                        LinearLayout newGroup = new LinearLayout(this);
                        newGroup.setOrientation(LinearLayout.VERTICAL);
                        newGroup.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));

                        EditText dateEditText = new EditText(this);
                        dateEditText.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        dateEditText.setText("Time: " + detail.getDate());
                        dateEditText.setEnabled(false);

                        EditText positionEditText = new EditText(this);
                        positionEditText.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        positionEditText.setText("Position: " + detail.getPosition());
                        positionEditText.setEnabled(false);

                        newGroup.addView(dateEditText);
                        newGroup.addView(positionEditText);
                        editTextContainer.addView(newGroup);
                    }
                }
            });
        }).start();
    }

    private void setEditTextsEnabled(boolean enabled) {
        for (int i = 0; i < editTextContainer.getChildCount(); i++) {
            View child = editTextContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout group = (LinearLayout) child;
                for (int j = 0; j < group.getChildCount(); j++) {
                    View view = group.getChildAt(j);
                    if (view instanceof EditText) {
                        view.setEnabled(enabled);
                    }
                }
            }
        }
    }

    private void addEditTextGroup() {
        LinearLayout newGroup = new LinearLayout(this);
        newGroup.setOrientation(LinearLayout.VERTICAL);
        newGroup.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        EditText newDateEditText = new EditText(this);
        newDateEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newDateEditText.setText("Time: ");

        EditText newPositionEditText = new EditText(this);
        newPositionEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newPositionEditText.setText("Position: ");

        newGroup.addView(newDateEditText);
        newGroup.addView(newPositionEditText);
        editTextContainer.addView(newGroup);
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