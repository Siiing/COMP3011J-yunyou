package com.zzdayss.yunyou.dao;

import static com.zzdayss.yunyou.utils.JDBCUtils.connection;

import android.util.Log;

import com.zzdayss.yunyou.entity.Feedback;
import com.zzdayss.yunyou.entity.User;
import com.zzdayss.yunyou.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {
    private static final String TAG = "mysql-party-FeedbackDao";
    /**
     * function: 反馈
     */
    public boolean feedback(String userAccount, String address, String content, String Stars) {
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        try {
            String sql = "insert into feedback(userAccount,address,content,Stars) values (?,?,?,?)";
            if (connection != null) {// connection不为null表示与数据库建立了连接
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    //将数据插入数据库
                    ps.setString(1, userAccount);
                    ps.setString(2, address);
                    ps.setString(3, content);
                    ps.setString(4, Stars);

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

    public List<Feedback> listMyFeedback(String userAccount) {
        // 根据数据库名称，建立连接
        Connection connection = JDBCUtils.getConn();
        PreparedStatement ps = null;
        ResultSet resultSet = null;
        try {
            String sql = "select * from feedback where userAccount=? or friendAccount=?";
            Log.d("connection", connection.toString());
            if (connection != null) {// connection不为null表示与数据库建立了连接
                ps = connection.prepareStatement(sql);
                if (ps != null) {
                    //将数据插入数据库
                    ps.setString(1, userAccount);
                    ps.setString(2, userAccount);

                    // 执行sql查询语句并返回结果集
                    List<Feedback> feedbackList = new ArrayList<>();
                    resultSet = ps.executeQuery();
                    Log.d("test", resultSet.toString());
                    while (resultSet.next()){
                        Feedback feedback = new Feedback();
                        feedback.setId(resultSet.getInt("id"));
                        feedback.setContent(resultSet.getString("content"));
                        feedback.setStars(resultSet.getString("stars"));
                        feedback.setAddress(resultSet.getString("address"));
                        feedbackList.add(feedback);
                    }
                    return feedbackList;
                } else {
                    return new ArrayList<>();
                }
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "获取评论列表异常：" + e.getMessage());
            return new ArrayList<>();
        }
    }
    /**
     * 根据id删除Feedback表内容
     * @param id FeedbackId
     * @return
     */
    public boolean deleteFeedbackById(int id) {
        System.out.println("Deleting Feedback with ID: " + id);
        Connection connection = JDBCUtils.getConn();
        PreparedStatement ps = null;
        try {
            // 根据数据库名称，建立连接
            System.out.println("Delete connection: " + connection);

            if (connection != null) {
                String sql = "DELETE FROM feedback WHERE id = ?";
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

    /**
     * 根据id获取Feedback表内容
     * @param id FeedbackId
     * @return
     */
    public Feedback getFeedbackById(int id) {
        Connection connection = JDBCUtils.getConn();
        PreparedStatement ps = null;
        Feedback feedback = null;
        try {
            if (connection != null) {
                String sql = "select * FROM feedback WHERE id = ?";
                ps = connection.prepareStatement(sql);
                ps.setInt(1, id);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    feedback = new Feedback();
                    feedback.setId(rs.getInt(1));
                    feedback.setUserAccount(rs.getString(2));
                    feedback.setAddress(rs.getString(3));
                    feedback.setContent(rs.getString(4));
                    feedback.setStars(rs.getString(5));
                }
                return feedback; // 如果有行受影响，则返回 true
            } else {
                System.out.println("连接数据库失败。");
                return feedback;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return feedback;
        } finally {
            // 确保关闭资源
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据id更新Feedback表内容
     * @param id FeedbackId
     * @return
     */
    public void updateFeedbackById(int id, String content) {
        Connection connection = JDBCUtils.getConn();
        PreparedStatement ps = null;
        String sql = "update feedback set content = ? where id = ?";
        try {
            if (connection != null) {
                ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, content);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            // 确保关闭资源
            try {
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateInviter(Integer feedbackId, String friendAccount) {
        Connection connection = JDBCUtils.getConn();
        PreparedStatement ps = null;
        String sql = "update feedback set friendAccount = ? where id = ?";
        try {
            if (connection != null) {
                ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, friendAccount);
                    ps.setInt(2, feedbackId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
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
