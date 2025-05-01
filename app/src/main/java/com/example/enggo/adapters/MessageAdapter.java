package com.example.enggo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.enggo.R;
import com.example.enggo.models.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private static final int TYPE_USER = Message.TYPE_USER;
    private static final int TYPE_AI = Message.TYPE_AI;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    public static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        TextView userMessageText;

        public UserMessageViewHolder(View itemView) {
            super(itemView);
            userMessageText = itemView.findViewById(R.id.userMessageText);
        }

        public void bind(Message message) {
            userMessageText.setText(message.getText());
        }
    }

    public static class AiMessageViewHolder extends RecyclerView.ViewHolder {
        TextView aiMessageText;

        public AiMessageViewHolder(View itemView) {
            super(itemView);
            aiMessageText = itemView.findViewById(R.id.aiMessageText);
        }

        public void bind(Message message) {
            aiMessageText.setText(message.getText());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_user_message, parent, false);
            return new UserMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_ai_message, parent, false);
            return new AiMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof AiMessageViewHolder) {
            ((AiMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
