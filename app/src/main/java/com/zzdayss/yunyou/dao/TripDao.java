package com.zzdayss.yunyou.dao;

import android.util.Log;

import com.zzdayss.yunyou.entity.Trip;
import com.zzdayss.yunyou.entity.TripDetail;
import com.zzdayss.yunyou.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TripDao {
    private static final String TAG = "mysql-party-TripDao";

    /**
     * function: 添加行程
     */
    public boolean tripadd(String userAccount, String departure, String destination, String date) {
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        System.out.println("addddddddddd");
        System.out.println(connection);
        try {
            String sql = "insert into trip(userAccount,departure,destination,date) values (?,?,?,?)";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    //将数据插入数据库
                    ps.setString(1, userAccount);
                    ps.setString(2, departure);
                    ps.setString(3, destination);
                    ps.setString(4, date);

                    // 执行sql查询语句并返回结果集
                    int rs = ps.executeUpdate();
                    if (rs > 0)
                        return true;
                    else
                        return false;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "异常register：" + e.getMessage());
            return false;
        }

    }

    /**
     * function: 根据账号进行查询行程
     *
     * @return
     */
    public List gettrip(String userAccount) {

        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        List<Trip> tripdata = new LinkedList<Trip>();
        try {
            String sql = "select * from trip where userAccount = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, userAccount);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        //注意：下标是从1开始
                        int id = rs.getInt(1);
                        String departure = rs.getString(3);
                        String destination = rs.getString(4);
                        String date = rs.getString(5);
                        Trip trip = new Trip(departure, destination, date);
                        trip.setId(id);
                        trip.setDeparture(departure);
                        trip.setDestination(destination);
                        trip.setDate(date);
                        tripdata.add(trip);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "异常findUser：" + e.getMessage());
            return null;
        }
        return tripdata;
    }
    public List<TripDetail> getTripDetails(int id) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TripDetail> details = new ArrayList<>();

        try {
            connection = JDBCUtils.getConn();

            if (connection != null) {
                String sql = "SELECT date, position FROM tripdetail WHERE userAccount = ? ORDER BY id";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, id);

                rs = ps.executeQuery();

                while (rs.next()) {
                    String date = rs.getString("date");
                    String position = rs.getString("position");
                    details.add(new TripDetail(date, position));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取行程详情失败: " + e.getMessage());
        }

        return details;
    }


    /**
     * function: delete trips
     */
    public boolean deleteTrip(int id) {
        System.out.println("222222222222222222222222");
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            // 根据数据库名称，建立连接
            connection = JDBCUtils.getConn();
            System.out.println("Delete connection: " + connection);

            if (connection != null) {
                String sql = "DELETE FROM trip WHERE id = ?";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, id);

                // 执行删除操作
                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0; // 如果有行受影响，则返回 true
            } else {
                System.out.println("连接数据库失败。");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // 确保关闭资源
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
