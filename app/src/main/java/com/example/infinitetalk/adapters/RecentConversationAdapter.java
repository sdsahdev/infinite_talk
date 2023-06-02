package com.example.infinitetalk.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitetalk.Model.ChatMessage;
import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.databinding.ItemContailnerRecentConversationBinding;
import com.example.infinitetalk.listtenear.ConversionListener;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHolder> {

    private final List<ChatMessage> chatMessages;
    private final ConversionListener conversionListener;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, ConversionListener conversionListener) {
        this.chatMessages = chatMessages;
        this.conversionListener = conversionListener;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContailnerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecentConversationAdapter.ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {

         ItemContailnerRecentConversationBinding binding;

        ConversionViewHolder(ItemContailnerRecentConversationBinding itemContailnerRecentConversationBinding) {
            super(itemContailnerRecentConversationBinding.getRoot());
            binding = itemContailnerRecentConversationBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.imageprofile.setImageBitmap(getconvesionImage(chatMessage.conversionImage));
            binding.tetxname.setText(chatMessage.conversionName);
            binding.textRecentMessage.setText(chatMessage.message);
            binding.getRoot().setOnClickListener(v -> {
                Users user = new Users();
                user.id = chatMessage.conversionId;
                user.name = chatMessage.conversionName;
                user.image = chatMessage.conversionImage;
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getconvesionImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
