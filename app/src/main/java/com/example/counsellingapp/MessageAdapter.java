package com.example.counsellingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ArrayList<Message> messages;
    String currentUserID;

    public MessageAdapter(Context context, ArrayList<Message> messages, String currentUserID) {
        this.context = context;
        this.messages = messages;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.received, parent, false);
            return new MessageAdapter.ReceivedViewHolder(view);
        }
        else {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            View view = layoutInflater.inflate(R.layout.sent, parent, false);
            return new MessageAdapter.SentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message currentMessage = messages.get(position);
        if (holder.getClass() == SentViewHolder.class) {
            ((SentViewHolder) holder).sentMessage.setText(currentMessage.getMessage());
        }
        else {
            ((ReceivedViewHolder) holder).receivedMessage.setText(currentMessage.getMessage());
        }
    }
    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentViewHolder extends RecyclerView.ViewHolder {

        TextView sentMessage;

        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            sentMessage = itemView.findViewById(R.id.sentMessage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message currentMessage = messages.get(position);
        if (currentUserID.equals(currentMessage.getSenderID())) {
            return 2;
        }
        else {
            return 1;
        }
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {

        TextView receivedMessage;

        public ReceivedViewHolder(@NonNull View itemView) {
            super(itemView);
            receivedMessage = itemView.findViewById(R.id.receivedMessage);
        }
    }
}
