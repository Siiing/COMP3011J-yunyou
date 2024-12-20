package com.zzdayss.yunyou.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzdayss.yunyou.R;
import com.zzdayss.yunyou.dao.FriendDao;
import com.zzdayss.yunyou.utils.JDBCUtils;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> userAccounts;
    private String currentUserAccount;

    public FriendAdapter(Context context, List<String> userAccounts,String currentUserAccount) {
        super(context, 0, userAccounts);
        this.context = context;
        this.userAccounts = userAccounts;
        this.currentUserAccount = currentUserAccount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        // get current user account
        String userAccount = userAccounts.get(position);

        TextView userAccountText = convertView.findViewById(R.id.user_account_text);
        Button addFriendButton = convertView.findViewById(R.id.add_friend_button);

        userAccountText.setText(userAccount);


        // use AsyncTask to query friend statusm. if is, the add button won't show
        new CheckFriendStatusTask(userAccount, addFriendButton).execute();

        return convertView;
    }

    private class CheckFriendStatusTask extends AsyncTask<Void, Void, Integer> {
        private String userAccount;
        private Button addFriendButton;

        public CheckFriendStatusTask(String userAccount, Button addFriendButton) {
            this.userAccount = userAccount;
            this.addFriendButton = addFriendButton;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            // query current user and friend status
            FriendDao friendDao = new FriendDao();
            return friendDao.getFriendStatus(currentUserAccount, userAccount);
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);

            // if status == 1, the button will be invisible
            if (status == 1) {
                addFriendButton.setVisibility(View.GONE);
            } else {
                addFriendButton.setVisibility(View.VISIBLE);

                addFriendButton.setOnClickListener(v -> {
                    new AddFriendTask(userAccount).execute();
                });
            }
        }
    }

    private class AddFriendTask extends AsyncTask<Void, Void, Boolean> {
        private String friendAccount;
        private List<String> friendAccountList = null;

        public AddFriendTask(String userAccount) {
            this.friendAccount = userAccount;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            if (currentUserAccount.isEmpty()){
                return false;
            }

            // get friendAccounts
            FriendDao friendDao = new FriendDao();
            friendAccountList = friendDao.getFriendAccountByUserAccount(currentUserAccount);
            if (friendAccountList == null || friendAccountList.isEmpty()) {
                return friendDao.addFriend(currentUserAccount, friendAccount);
            }

            if (friendAccountList.contains(friendAccount)){
                return false;
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            if (friendAccount == null) {
                Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
            } else {
                if (success) {
                    Toast.makeText(context, "Friend request sent to " + friendAccount, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to send friend request", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}