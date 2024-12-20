package com.zzdayss.yunyou.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zzdayss.yunyou.R;
import com.zzdayss.yunyou.dao.FeedbackDao;
import com.zzdayss.yunyou.dao.FriendDao;

import java.util.List;

public class InviteAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> friendAccounts;
    private String currentUserAccount;
    private Integer feedbackId;

    public InviteAdapter(Context context, List<String> userAccounts, String currentUserAccount, Integer feedbackId) {
        super(context, 0, userAccounts);
        this.context = context;
        this.friendAccounts = userAccounts;
        this.currentUserAccount = currentUserAccount;
        this.feedbackId = feedbackId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_invite, parent, false);
        }

        // get current user account
        String friendAccount = friendAccounts.get(position);

        TextView userAccountText = convertView.findViewById(R.id.user_account_text);
        Button inviteFriendButton = convertView.findViewById(R.id.invite_friend_button);
        userAccountText.setText(friendAccount);
        new InviteFriendTask(friendAccount, inviteFriendButton).execute();

        return convertView;
    }

    private class InviteFriendTask extends AsyncTask<Void, Void, Boolean> {
        private String friendAccount;
        private Button inviteFriendButton;

        public InviteFriendTask(String friendAccount, Button inviteFriendButton) {
            this.friendAccount = friendAccount;
            this.inviteFriendButton = inviteFriendButton;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            return true;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            Log.d("inviteFriendButton", inviteFriendButton+"");
            this.inviteFriendButton.setOnClickListener(v -> {
                new Thread(() ->{
                    FeedbackDao feedbackDao = new FeedbackDao();
                    feedbackDao.updateInviter(feedbackId, friendAccount);
                }).start();
                Toast.makeText(context, "invite friend successfully!", Toast.LENGTH_SHORT).show();
            });

        }
    }
}