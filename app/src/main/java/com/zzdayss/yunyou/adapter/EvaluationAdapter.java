package com.zzdayss.yunyou.adapter;

/**
 * @Author Siying.Li
 * @Date 2024/11/26 14:38
 * @Version 1.0
 */

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zzdayss.yunyou.R;
import com.zzdayss.yunyou.dao.UserDao;
import com.zzdayss.yunyou.EvaluateShowActivity;

import java.util.List;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class EvaluationAdapter extends BaseAdapter {
    private final Activity activity;
    private List<Map<String, Object>> evaluateList;
    private final UserDao userDao;

    public EvaluationAdapter(Activity activity, List<Map<String, Object>> evaluateList, UserDao userDao) {
        this.activity = activity;
        this.evaluateList = evaluateList;
        this.userDao = userDao;
    }

    @Override
    public int getCount() {
        return evaluateList.size();
    }

    @Override
    public Object getItem(int position) {
        return evaluateList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.activity_evaluate_show, parent, false);
            holder = new ViewHolder();
            holder.addressTextView = convertView.findViewById(R.id.address_input_show);
            holder.contentTextView = convertView.findViewById(R.id.feedback_show);
            holder.ratingBar = convertView.findViewById(R.id.ratingBar_show);
            holder.deleteButton = convertView.findViewById(R.id.delete_evaluation);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> evaluation = evaluateList.get(position);

        // 设置评价数据
        String address = (String) evaluation.get("address");
        String content = (String) evaluation.get("content");
        float rating = (float) evaluation.get("rating");

        holder.addressTextView.setText(address != null ? address : "No Address Provided");
        holder.contentTextView.setHtml(content != null ? content : "<p>No Content Provided</p>");
        holder.ratingBar.setRating(rating);

        // 删除按钮点击事件
        holder.deleteButton.setOnClickListener(v -> {
            int id = (int) evaluation.get("id");

            // 删除数据库中的评价
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userDao.deleteEvaluation(id);
                }
            }).start();

            // 从列表中移除并更新视图
            evaluateList.remove(position);
            notifyDataSetChanged();

            // 提示用户删除成功
            Toast.makeText(activity, "Evaluation deleted successfully", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }

    static class ViewHolder {
        TextView addressTextView;
        RichEditor contentTextView;
        RatingBar ratingBar;
        TextView deleteButton;
    }
}

