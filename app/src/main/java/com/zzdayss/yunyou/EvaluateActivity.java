package com.zzdayss.yunyou;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zzdayss.yunyou.dao.FeedbackDao;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.entity.Feedback;
import com.zzdayss.yunyou.ocr.BaiduOCR;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jp.wasabeef.richeditor.RichEditor;

public class EvaluateActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private SharedPreferences sp;
    private String userAccount, feedback, Stars;
    private RichEditor richEditor;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> takePhotoLauncher;  //launch take photos
    private ActivityResultLauncher<Intent> chooseFromAlbumLauncher;  //launch open album
    private ActivityResultLauncher<Intent> chooseFromAlbumToOcrLauncher;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    public static Boolean selectFlag = false;
    public static String address = null;
    private int currentFontSize;
    private TextView fontSizeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        setStatusBar();
        //hide actionbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_evaluate);

        EditText editText = findViewById(R.id.address_input);
        if (address!=null){
            editText.setText(address);
        }

        sp = getSharedPreferences("Personal", MODE_PRIVATE);

        richEditor = findViewById(R.id.feedback);
        richEditor.setEditorHeight(200);
        richEditor.setEditorFontSize(22);
        richEditor.setEditorFontColor(Color.BLACK);
        richEditor.focusEditor();

        currentFontSize=4;
        richEditor.setFontSize( currentFontSize);
        richEditor.setEditorFontColor(Color.BLACK);
        richEditor.focusEditor();

        //modify font size
        ImageButton fontSizeButton = findViewById(R.id.fontSizeButton);
        fontSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFontSizeDialog();
            }
        });

        //modify font color
        ImageButton fontColorButton = findViewById(R.id.fontColorButton);
        fontColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFontColorDialog();
            }
        });


        findViewById(R.id.back_button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.select_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EvaluateActivity.selectFlag = true;
                Intent intent = new Intent(EvaluateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String feedback = richEditor.getHtml();
                RatingBar ratingBar = findViewById(R.id.ratingBar);
                Stars = String.valueOf(ratingBar.getRating());
                String address = editText.getText().toString();

                new Thread(() -> {
                    int msg = 0;
                    FeedbackDao feedbackDao = new FeedbackDao();
                    if ("".equals(feedback) || feedback == null) {
                        msg = 0;
                    } else {
                        boolean flag = feedbackDao.feedback(sp.getString("userAccount",""), address, feedback, Stars);
                        Log.d("feed", "feedback:::" + feedback);
                        Log.d("feed", "Stars:::" + ratingBar.getRating());
                        if (flag) {
                            msg = 1;
                        } else {
                            msg = 2;
                        }
                    }
                    hand.sendEmptyMessage(msg);

                    runOnUiThread(() -> {
                        Intent intent = new Intent(EvaluateActivity.this, EvaluateShowActivity.class);
                        intent.putExtra("address", address);
                        intent.putExtra("content", feedback);
                        intent.putExtra("star",ratingBar.getRating());
                        startActivity(intent);
                    });

                }).start();
            }
        });

        findViewById(R.id.photoButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooseDialog();
            }
        });

        //set launcher
        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                }
        );

        chooseFromAlbumLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            saveImageToAppDirectory(imageUri);
                        }
                    }
                });
        chooseFromAlbumToOcrLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Uri imageUri = data.getData();
                            String base64Image = uriToBase64(imageUri);
                            ocrImage(base64Image);
                        }
                    }
                });

        findViewById(R.id.OCRButton).setOnClickListener(view -> {
            chooseFromAlbumByOcr();
        });

        //check the camera right
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void showFontSizeDialog(){
        View dialogView = getLayoutInflater().inflate(R.layout.font_size_dialog, null);
        fontSizeTextView = dialogView.findViewById(R.id.fontSizeTextView);

        //create font size dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Modify Font Size");

        //get increase and decrease button
        ImageButton increaseButton = dialogView.findViewById(R.id.increaseFontSizeButton);
        ImageButton decreaseButton = dialogView.findViewById(R.id.decreaseFontSizeButton);

        //increase font size
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFontSize += 1;
                if (currentFontSize > 7) {
                    currentFontSize = 7;
                    Toast.makeText(EvaluateActivity.this, "Font size cannot be larger", Toast.LENGTH_SHORT).show();
                }
                updateFontSize();
            }
        });


        //decrease font size
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentFontSize -=1;
                if (currentFontSize <1){
                    currentFontSize=1;
                    Toast.makeText(EvaluateActivity.this,"Font size cannot be smaller",Toast.LENGTH_SHORT).show();
                }
                updateFontSize();
            }
        });

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        //builder.show();
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }


    private void updateFontSize() {
        fontSizeTextView.setText(currentFontSize + " size");     //update font size
        richEditor.setFontSize( currentFontSize);
    }

    private void showFontColorDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.font_color_dialog, null);

        //create font size dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Modify Font Color");

        //get color button
        ImageButton blackButton = dialogView.findViewById(R.id.textblack);
        ImageButton redButton = dialogView.findViewById(R.id.textred);
        ImageButton orangeButton = dialogView.findViewById(R.id.textorange);
        ImageButton yellowButton = dialogView.findViewById(R.id.textyellow);
        ImageButton greenButton = dialogView.findViewById(R.id.textgreen);
        ImageButton blueButton = dialogView.findViewById(R.id.textblue);
        ImageButton purpleButton = dialogView.findViewById(R.id.textpurple);

        // Define click listeners for each color button
        blackButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textblack, null))); // Black
        redButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textred, null))); // Red
        orangeButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textorange, null))); // Orange
        yellowButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textyellow, null))); // Yellow
        greenButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textgreen, null))); // Green
        blueButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textblue, null))); // Blue
        purpleButton.setOnClickListener(v -> updateFontColor(getResources().getColor(R.color.textpurple, null))); // Purple

        builder.setPositiveButton("OK", null);
        builder.setNegativeButton("Cancel", null);
        //builder.show();
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
    }

    private void updateFontColor(int color) {
        // richEditor.setEditorFontColor(color);
        richEditor.setTextColor(color);

    }


    private void chooseFromAlbumByOcr() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.setType("image/*");
            chooseFromAlbumToOcrLauncher.launch(intent);
        }

    }

    private void ocrImage(String base64Image) {
        // 使用ExecutorService来执行后台任务
        executor.execute(() -> {
            try {
                String accessToken = BaiduOCR.getAccessToken();
                final String result = BaiduOCR.recognizeText(accessToken, base64Image);

                // 更新UI需要在主线程进行
                runOnUiThread(() -> {
                    if (result != null) {
                        // 处理识别结果
                        String html = richEditor.getHtml();
                        richEditor.setHtml(html+result);
                        Log.d("OCR Result", result);

                    } else {
                        // 处理错误
                        Log.e("OCR Error", "cannot recognize this image");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                // 同样地，在主线程更新UI
                runOnUiThread(() -> {
                    Log.e("OCR Error", "Failed to recognize text: " + e.getMessage());
                });
            }
        });
    }
    /**
     * uri转base64
     * @param imageUri
     * @return
     */
    public  String uriToBase64( Uri imageUri) {
        try {
            // 获取 ContentResolver
            ContentResolver contentResolver = getContentResolver();

            // 打开图片的输入流
            InputStream inputStream = contentResolver.openInputStream(imageUri);

            // 将输入流读取为 Bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 将 Bitmap 转换为字节数组
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            // 将字节数组编码为 Base64 字符串
            String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);

            // 关闭输入流
            inputStream.close();

            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 保存图片到app目录
     * @param uri 图片的Uri
     */
    private void saveImageToAppDirectory(Uri uri) {
        try {
            // 获取应用程序的私有目录
            File imageFile = new File(getExternalCacheDir(), UUID.randomUUID() + ".jpg");

            // 使用 ContentResolver 获取 InputStream
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(imageFile);

            // 将 InputStream 写入 FileOutputStream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            if (Build.VERSION.SDK_INT < 24) {
                imageUri = Uri.fromFile(imageFile);
            } else {
                imageUri = FileProvider.getUriForFile(EvaluateActivity.this, "com.yunyou.fileprovider", imageFile);
            }
            // 关闭流
            inputStream.close();
            outputStream.close();
            // 图片保存成功
            //Toast.makeText(this, "图片保存到：" + imageFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "图片获取失败", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (imageUri==null)return;
        richEditor.focusEditor();
        richEditor.insertImage(String.valueOf(imageUri), "image", 300, 200);
    }

    private void showChooseDialog() {
        AlertDialog.Builder choiceBuilder = new AlertDialog.Builder(this);
        choiceBuilder.setCancelable(false);
        choiceBuilder.setTitle("add images")
                .setSingleChoiceItems(new String[]{"take a photo", "choose from album"}, -1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                switch (i) {
                                    case 0:
                                        takePhoto();
                                        break;
                                    case 1:
                                        chooseFromAlbum();
                                        break;
                                    default:
                                        break;
                                }
                                dialogInterface.dismiss();
                            }
                        })
                .setNeutralButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        choiceBuilder.create();
        choiceBuilder.show();
    }

    private void takePhoto() {
        //use to store the token photos
        File outputImage = new File(getExternalCacheDir(), UUID.randomUUID() + ".jpg");
        try {  //ensure the photo file is existing
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            imageUri = FileProvider.getUriForFile(EvaluateActivity.this, "com.yunyou.fileprovider", outputImage);
        }
        //launch camera
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        takePhotoLauncher.launch(intent);
    }

    /**
     * choose photos from album
     */
    private void chooseFromAlbum() {
        //openAlbum();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();  //open album
        }
    }


    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
        intent.setType("image/*");
        chooseFromAlbumLauncher.launch(intent);
    }

    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(getApplicationContext(), "提交失败,请填写完整信息", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "提交成功", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MyActivity.class));
            } else if (msg.what == 2) {
                Toast.makeText(getApplicationContext(), "提交失败,连接数据库出错", Toast.LENGTH_LONG).show();
            }
        }
    };

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