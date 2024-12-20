package com.zzdayss.yunyou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.zzdayss.yunyou.R;
import com.zzdayss.yunyou.TripDetailActivity;
import com.zzdayss.yunyou.dao.TripDao;
import com.zzdayss.yunyou.dao.UserDao;

import java.util.List;
import java.util.Map;

public class TripAdapter extends BaseAdapter {
    private final Activity activity;
    private List<Map<String, Object>> tripList;
    private final TripDao tripDao; // 添加UserDao

    public TripAdapter(Activity activity, List<Map<String, Object>> tripList, TripDao tripDao) {
        this.activity = activity;
        this.tripList = tripList;
        this.tripDao = tripDao; // 初始化UserDao
    }

    @Override
    public int getCount() {
        return tripList.size();
    }

    @Override
    public Object getItem(int position) {
        return tripList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(activity).inflate(R.layout.adapter_trips, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.name);
            holder.address = convertView.findViewById(R.id.address);
            holder.date = convertView.findViewById(R.id.date);
            holder.deleteText = convertView.findViewById(R.id.delete_text);
            holder.planText = convertView.findViewById(R.id.plan_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Map<String, Object> trip = tripList.get(position);

        holder.name.setText((String) trip.get("getDeparture"));
        holder.address.setText((String) trip.get("getDestination"));
        holder.date.setText((String) trip.get("getDate"));

        holder.deleteText.setOnClickListener(v -> {
            int id = (int) trip.get("id");
            System.out.println(id);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    tripDao.deleteTrip(id);
                }
            }).start();
            System.out.println("position:"+position);
            tripList.remove(position);
            notifyDataSetChanged();
            Toast.makeText(activity, "Deleted successfully", Toast.LENGTH_SHORT).show();
        });

        holder.planText.setOnClickListener(v -> {
            Intent intent = new Intent(activity, TripDetailActivity.class);
            intent.putExtra("id",(int)trip.get("id"));
            intent.putExtra("departure", (String) trip.get("getDeparture"));
            intent.putExtra("destination", (String) trip.get("getDestination"));
            intent.putExtra("date", (String) trip.get("getDate"));
            activity.startActivity(intent);
        });

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView address;
        TextView date;
        TextView deleteText;
        TextView planText;
    }
}
