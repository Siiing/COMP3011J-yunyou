package com.zzdayss.yunyou.dao;

import static com.zzdayss.yunyou.utils.JDBCUtils.getConn;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.zzdayss.yunyou.entity.Feedback;
import com.zzdayss.yunyou.entity.Trip;
import com.zzdayss.yunyou.entity.TripDetail;
import com.zzdayss.yunyou.entity.User;
import com.zzdayss.yunyou.utils.JDBCUtils;

/**
 * author: yan
 * date: 2022.02.17
 * **/
public class UserDao {

    private static final String TAG = "mysql-party-UserDao";

    /**
     * function: 登录
     */
    public int login(String userAccount, String userPassword) {

        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn();
        Log.e(TAG, "connection1是：" + connection);
        int msg = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            // mysql简单的查询语句。这里是根据user表的userAccount字段来查询某条记录
            String sql = "select * from user where userAccount = ?";
            String loginsuccess = "update user set state = 1 where userAccount = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                ps = connection.prepareStatement(sql);
                if (ps != null) {
                    Log.e(TAG, "账号：" + userAccount);
                    //根据账号进行查询
                    ps.setString(1, userAccount);
                    // 执行sql查询语句并返回结果集
                    rs = ps.executeQuery();
                    int count = rs.getMetaData().getColumnCount();
                    //将查到的内容储存在map里
                    while (rs.next()) {
                        // 注意：下标是从1开始的
                        for (int i = 1; i <= count; i++) {
                            String field = rs.getMetaData().getColumnName(i);
                            map.put(field, rs.getString(field));
                        }
                    }

                    if (map.size() != 0) {
                        StringBuilder s = new StringBuilder();
                        //寻找密码是否匹配
                        for (String key : map.keySet()) {
                            if (key.equals("userPassword")) {
                                if (userPassword.equals(map.get(key))) {
                                    PreparedStatement ps1 = connection.prepareStatement(loginsuccess);
                                    ps1.setString(1, userAccount);
                                    int rs1 = ps1.executeUpdate();
                                    if (rs1 == 1) {
                                        msg = 1;            //密码正确
                                    } else {
                                        msg = 4;
                                    }
                                } else
                                    msg = 2;            //密码错误
                                break;
                            }
                        }
                    } else {
                        Log.e(TAG, "查询结果为空");
                        msg = 3;
                    }
                } else {
                    msg = 0;
                }
            } else {
                msg = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "异常login：" + e.getMessage());
            msg = 0;
        }
        return msg;
    }

    public void logout(String userAccount) {
        Connection connection = getConn();
        PreparedStatement ps = null;
        String logout = "update user set state = 0 where userAccount = ?";
        try {
            if (connection != null) {// connection不为null表示与数据库建立了连接
                ps = connection.prepareStatement(logout);
                if (ps != null) {
                    Log.e(TAG, "账号：" + userAccount);
                    //根据账号进行查询
                    ps.setString(1, userAccount);
                    // 执行sql查询语句并返回结果集
                    ps.executeUpdate();
                    //将查到的内容储存在map里

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int getrenshu() {
        Connection connection = getConn();
        String logout = "select count(*) from user where state = 1";
        int count = 0;
        try {
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(logout);
                if (ps != null) {
                    // 执行sql查询语句并返回结果集
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        count = rs.getInt(1);
                    }

                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
    }


    /**
     * function: 注册
     */
    public boolean register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        // 根据数据库名称，建立连接
        Connection connection = getConn();
        Log.e(TAG, "111111111connection2是：" + connection);

        try {
            String sql = "insert into user(userAccount,userPassword,userName) values (?,?,?)";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {

                    //将数据插入数据库
                    ps.setString(1, user.getUserAccount());
                    ps.setString(2, user.getUserPassword());
                    ps.setString(3, user.getUserName());

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


    public boolean batchInsert(int id, List<String> positions, List<String> dates) {
        Connection connection = null;
        PreparedStatement deletePs = null;
        PreparedStatement insertPs = null;
        boolean success = false;

        try {
            // 获取数据库连接
            connection = getConn();

            if (connection != null) {
                // 关闭自动提交，开启事务
                connection.setAutoCommit(false);

                // 先删除该id的所有记录
                String deleteSql = "DELETE FROM tripdetail WHERE userAccount = ?";
                deletePs = connection.prepareStatement(deleteSql);
                deletePs.setInt(1, id);
                deletePs.executeUpdate();

                // 再插入新记录
                String insertSql = "INSERT INTO tripdetail (userAccount, date, position) VALUES (?, ?, ?)";
                insertPs = connection.prepareStatement(insertSql);

                // 批量设置参数
                for (int i = 0; i < positions.size(); i++) {
                    insertPs.setInt(1, id);
                    insertPs.setString(2, dates.get(i));
                    insertPs.setString(3, positions.get(i));
                    insertPs.addBatch();
                }

                // 执行批处理
                int[] results = insertPs.executeBatch();

                // 提交事务
                connection.commit();

                // 检查是否所有记录都插入成功
                success = true;
                for (int result : results) {
                    if (result <= 0) {
                        success = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            // 发生异常时回滚事务
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                Log.d(TAG, "回滚失败：" + rollbackEx.getMessage());
            }

            e.printStackTrace();
            Log.d(TAG, "批量插入异常：" + e.getMessage());
            success = false;

        } finally {
            // 关闭资源
            try {
                if (deletePs != null) {
                    deletePs.close();
                }
                if (insertPs != null) {
                    insertPs.close();
                }
                if (connection != null) {
                    connection.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                Log.d(TAG, "关闭资源异常：" + e.getMessage());
            }
        }

        return success;
    }


    /**
     * function: 根据账号进行查找该用户是否存在
     */
    public User findUser(String userAccount) {

        // 根据数据库名称，建立连接
        Connection connection = getConn();
        Log.e(TAG, "connection3是：" + connection);
        User user = null;
        try {
            String sql = "select * from user where userAccount = ?";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, userAccount);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        //注意：下标是从1开始
                        int id = rs.getInt(1);
                        String userAccount1 = rs.getString(2);
                        String userPassword = rs.getString(3);
                        String userName = rs.getString(4);
                        user = new User(id, userAccount1, userPassword, userName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "异常findUser：" + e.getMessage());
            return null;
        }
        return user;
    }

    public String finduserName(String userAccount) {
        String userName = null;
        try {
            String sql = "select * from user where userAccount =" + userAccount + "";
            Connection conn = new JDBCUtils().getConn();
            Log.e(TAG, "connection2是：" + conn);
            PreparedStatement parstmt = conn.prepareStatement(sql);
            ResultSet rs = parstmt.executeQuery();
            while (rs.next()) {
                userName = rs.getString("userName");
            }
            parstmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userName;
    }


    /**
     * function delete evaluations
     */
    /**
     * function: delete evaluation
     */
    public boolean deleteEvaluation(int id) {
        System.out.println("Deleting evaluation with ID: " + id);
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            // 根据数据库名称，建立连接
            connection = getConn();
            System.out.println("Delete connection: " + connection);

            if (connection != null) {
                String sql = "DELETE FROM evaluation WHERE id = ?";
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

    // query userAccount
    public static ArrayList<String> searchUserAccount(String searchTerm) {
        ArrayList<String> userAccounts = new ArrayList<>();
        Connection conn = getConn();
        try {
            String query = "SELECT userAccount FROM user WHERE userAccount LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                userAccounts.add(rs.getString("userAccount"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return userAccounts;
    }
}
