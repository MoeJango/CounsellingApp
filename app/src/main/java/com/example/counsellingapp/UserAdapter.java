package com.example.counsellingapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> users;
    String callerType;
    String currentUserID;

    public UserAdapter(Context context, ArrayList<User> users, String callerType, String currentUserID) {
        this.context = context;
        this.users = users;
        this.callerType = callerType;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public UserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user_layout, parent, false);
        return new UserAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.MyViewHolder holder, int position) {
        String userType = users.get(position).getUserType();
        if (userType.equals("counsellor")) {
            holder.name.setText("Counsellor " + users.get(position).getName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("senderID", currentUserID);
                    intent.putExtra("receiverID", users.get(position).getId());
                    intent.putExtra("name", users.get(position).getName());
                    intent.putExtra("callerType", callerType);
                    context.startActivity(intent);

                }
            });
        }
        else if (userType.equals("user")) {
            holder.name.setText("Patient " + (position + 1));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("senderID", currentUserID);
                    intent.putExtra("receiverID", users.get(position).getId());
                    intent.putExtra("name", users.get(position).getName());
                    context.startActivity(intent);

                }
            });
        }
        else {
            if (callerType.equals("user")) {
                holder.name.setText("Please wait while we assign you a counsellor");
            } else if (callerType.equals("counsellor")) {
                holder.name.setText("Please wait while we assign you users");

            }
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.nameText);
        }
    }
}
