package com.zzdayss.yunyou.dao;

import com.zzdayss.yunyou.utils.JDBCUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendDao {
    // insert into friend table
    public static boolean addFriend(String userAccount, String friendAccount) {

        Connection conn = JDBCUtils.getConn();
        if (conn == null) {
            return false;
        }

        String sql = "INSERT INTO friends (userAccount, friendAccount, status) VALUES (?, ?, 1)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userAccount);  // set userAccount
            stmt.setString(2, friendAccount); // set friendAccount

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // get according friendAccount
    public static List<String> getFriendAccountByUserAccount(String userAccount) {
        Connection conn = JDBCUtils.getConn();
        if (conn == null) {
            return null;
        }

        List<String> friendList = new ArrayList<>();
        String query = "SELECT friendAccount FROM friends WHERE userAccount = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userAccount);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                friendList.add(rs.getString("userAccount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendList;
    }

    public static int getFriendStatus(String userAccount, String friendAccount) {
        int status = 0;
        Connection conn = JDBCUtils.getConn();
        try {
            String query = "SELECT status FROM friends WHERE userAccount = ? AND friendAccount = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userAccount);
            stmt.setString(2, friendAccount);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                status = rs.getInt("status");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public List<String> listFriend(String userAccount, String searchTerm) {
        Connection conn = JDBCUtils.getConn();
        if (conn == null) {
            return null;
        }

        List<String> friendList = new ArrayList<>();
        String query = "SELECT friendAccount FROM friends WHERE userAccount = ? and friendAccount like ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userAccount);
            stmt.setString(2, "%"+searchTerm+"%");
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                friendList.add(rs.getString("friendAccount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendList;
    }
}
