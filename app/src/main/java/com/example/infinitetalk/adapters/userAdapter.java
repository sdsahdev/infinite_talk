package com.example.infinitetalk.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.Model.Userss;
import com.example.infinitetalk.databinding.ItemContailnerUserBinding;
import com.example.infinitetalk.listtenear.UserListner;

import java.util.List;

public class userAdapter extends RecyclerView.Adapter<userAdapter.userViewHolder> {

    private final List<Users> users;
    private final UserListner userListner;

    public userAdapter(List<Users> users, UserListner userListner) {
        this.users = users;
        this.userListner = userListner;
    }


    @NonNull
    @Override
    public userViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContailnerUserBinding itemContailnerUserBinding = ItemContailnerUserBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new userViewHolder(itemContailnerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull userViewHolder holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class userViewHolder extends RecyclerView.ViewHolder {

        ItemContailnerUserBinding binding;

        public userViewHolder(ItemContailnerUserBinding itemContailnerUserBinding) {
            super(itemContailnerUserBinding.getRoot());
            binding = itemContailnerUserBinding;
        }

        void setUserData(Users user) {
            binding.tetxname.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageprofile.setImageBitmap(getuserimage(user.image));
            binding.getRoot().setOnClickListener(v-> userListner.onuserClicked(user));
        }

        private Bitmap getuserimage(String encodedimage) {
            byte[] bytes = Base64.decode(encodedimage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }
}
