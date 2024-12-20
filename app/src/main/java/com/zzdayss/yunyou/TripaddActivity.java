package com.zzdayss.yunyou;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.zzdayss.yunyou.dao.TripDao;
import com.zzdayss.yunyou.dao.UserDao;

import java.util.Calendar;

public class TripaddActivity  extends Activity{

    private TextView date,time;
    private EditText departure,destination;
    private SharedPreferences sp;
    private static final int LOCATION_REQUEST_CODE=1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tripadd);
        setStatusBar();

        departure = findViewById(R.id.departure);
        destination = findViewById(R.id.destination);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        sp = getSharedPreferences("Personal", MODE_PRIVATE);
        Calendar cal = Calendar.getInstance();
        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH);
        int d = cal.get(Calendar.DATE);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int mi = cal.get(Calendar.MINUTE);
        int s = cal.get(Calendar.SECOND);
        date.setText(y + "年" + m + "月" + d + "日");
        time.setText(h + "时" + mi + "分" + s + "秒");

        findViewById(R.id.back_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public void submit(View view){
        String userAccount = sp.getString("userAccount","");
        String departurecontext = (departure.getText().toString());
        String destinationcontext = (destination.getText().toString());
        String date1 = (date.getText().toString()+time.getText().toString());
        new Thread(() -> {
            int msg = 0;
            TripDao tripDao = new TripDao();
            if("".equals(departurecontext)||"".equals(destinationcontext)){
                msg = 0;
            } else{
                boolean flag = tripDao.tripadd(userAccount,departurecontext,destinationcontext,date1);
                Log.d("tripadd","departurecontext:::"+departurecontext);
                Log.d("tripadd","destinationcontext:::"+destinationcontext);
                if(flag){
                    msg = 1;
                }else {
                    msg = 2;
                }
            }
            hand.sendEmptyMessage(msg);

        }).start();

    }
    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler()
    {
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                Toast.makeText(getApplicationContext(),"添加失败,请填写完整信息",Toast.LENGTH_LONG).show();
            } else if(msg.what == 1) {
                Toast.makeText(getApplicationContext(),"添加成功",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),TripActivity.class));
            }else if(msg.what == 2) {
                Toast.makeText(getApplicationContext(),"添加失败,连接数据库出错",Toast.LENGTH_LONG).show();
            }
        }
    };



    public void adddate(View view){
        showdate();
    }

    private void showdate() {
        Calendar calendar = Calendar.getInstance();//调用Calendar类获取年月日
        int  mYear = calendar.get(Calendar.YEAR);//年
        int  mMonth = calendar.get(Calendar.MONTH);//月份要加一个一，这个值的初始值是0。不加会日期会少一月。
        int  mDay = calendar.get(Calendar.DAY_OF_MONTH);//日
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                date.setText(i + "年" + (i1+1) + "月" + i2 + "日");//当选择完后将时间显示,记得月份i1加一
            }
        }, mYear,mMonth, mDay);//将年月日放入DatePickerDialog中，并将值传给参数
        datePickerDialog.show();//显示dialog
    }

    public void addtime(View view){
        showtime();
    }

    public void showtime(){
        //获取日历的一个实例，里面包含了当前的时分秒
        Calendar calendar=Calendar.getInstance();
        //构建一个时间对话框，该对话框已经集成了时间选择器
        //TimePickerDialog的第二个构造参数指定了事件监听器
        TimePickerDialog dialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener(){
            //一旦点击对话框上的确定按钮，触发该方法
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                time.setText(i+"时"+i1+"分");//获取时间对话框设定的小时和分钟数
            }
        },calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//true表示使用二十四小时制
        //把时间对话框显示在界面上
        dialog.show();
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