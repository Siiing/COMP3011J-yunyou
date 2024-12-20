package com.zzdayss.yunyou.ocr;


import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BaiduOCR {

    private static final String AUTH_URL = "https://aip.baidubce.com/oauth/2.0/token";
    private static final String API_KEY = "pdxE0vuTXYlqbSp7XrQ8C11h";
    private static final String SECRET_KEY = "hq79zhXFRAAgHIgyhFyInrZnj7Hgwebj";
    private static final String OCR_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";

    public static String recognizeText(String accessToken, String imageBase64) throws Exception {
        OkHttpClient client = new OkHttpClient();
        // 使用 FormBody 构建请求体
        FormBody formBody = new FormBody.Builder()
                .add("image", imageBase64)
                .build();
        // 构建请求
        Request request = new Request.Builder()
                .url(OCR_URL + "?access_token=" + accessToken)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            // 提取文字
            JSONArray wordsResults = jsonObject.getJSONArray("words_result");
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < wordsResults.length(); i++) {
                JSONObject wordObject = wordsResults.getJSONObject(i);
                if (wordObject.has("words")) {
                    String words = wordObject.getString("words");
                    result.append(words);
                }
            }
            return result.toString();
        } else {
            throw new Exception("识别图片失败");
        }
    }
    public static String getAccessToken() throws Exception {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", API_KEY)
                .add("client_secret", SECRET_KEY)
                .build();

        Request request = new Request.Builder()
                .url(AUTH_URL)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            return jsonObject.getString("access_token");
        } else {
            throw new Exception("获取access_token失败");
        }
    }
}