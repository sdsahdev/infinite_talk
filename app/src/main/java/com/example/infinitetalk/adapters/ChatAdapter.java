package com.example.infinitetalk.adapters;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitetalk.Model.ChatMessage;
import com.example.infinitetalk.databinding.ItemContainerRecivedMsgBinding;
import com.example.infinitetalk.databinding.ItemContenerSendMsgBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> chatMessages;
    private Bitmap receivedprofileImage;
    private final String senderId;
    private static final int VIEW_TYPE_SEND = 1;
    private static final int VIEW_TYPE_RECIVED = 2;

    public void setReceiverProfileImage(Bitmap bitmap) {
        receivedprofileImage = bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receivedprofileImage, String senderId) {
        this.chatMessages = chatMessages;
        Log.d("TAG", " devi1 ChatAdapter: recivedprofileImage1" + receivedprofileImage);
        this.receivedprofileImage = receivedprofileImage;
        this.senderId = senderId;

    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SEND) {
            Log.d("TAG", "onCreateViewHolder: devi1 VIEW_TYPE_SEND ");
            return new SentMessageViewHolder(
                    ItemContenerSendMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false));
        } else {
            Log.d("TAG", "onCreateViewHolder: devi1 VIEW_TYPE_RECIVED ");
            return new RecivedmsgViewHolder(
                    ItemContainerRecivedMsgBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SEND) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {

            ((RecivedmsgViewHolder) holder).setData(chatMessages.get(position), receivedprofileImage);
            Log.d("TAG", "ChatAdapter: devi1 recivedprofileImage2" + receivedprofileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SEND;
        } else {
            return VIEW_TYPE_RECIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContenerSendMsgBinding binding;

        SentMessageViewHolder(ItemContenerSendMsgBinding itemContenerSendMsgBinding) {
            super(itemContenerSendMsgBinding.getRoot());
            binding = itemContenerSendMsgBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textmessage.setText(chatMessage.message);
            binding.textdatetime.setText(chatMessage.dateTime);
        }
    }

    static class RecivedmsgViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerRecivedMsgBinding binding;


        RecivedmsgViewHolder(ItemContainerRecivedMsgBinding itemContainerRecivedMsgBinding) {
            super(itemContainerRecivedMsgBinding.getRoot());
            binding = itemContainerRecivedMsgBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receivedprofileImage) {
            Log.d("TAG", "setData: devi1 recivedprofileImage3 Astart " + receivedprofileImage);
            binding.textmessage.setText(chatMessage.message);
            binding.textdatetime.setText(chatMessage.dateTime);
            if (receivedprofileImage != null) {
                binding.iamgeprofile.setImageBitmap(receivedprofileImage);
            }
            Log.d("TAG", "setData: devi1 recivedprofileImage3 AEnd " + receivedprofileImage);
        }
    }
}
