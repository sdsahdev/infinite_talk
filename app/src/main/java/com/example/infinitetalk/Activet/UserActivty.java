package com.example.infinitetalk.Activet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.adapters.userAdapter;
import com.example.infinitetalk.databinding.ActivityUserActivtyBinding;
import com.example.infinitetalk.listtenear.UserListner;
import com.example.infinitetalk.utilties.Constants;
import com.example.infinitetalk.utilties.PrefrenceManager;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserActivty extends BaseActivity implements UserListner {
    private ActivityUserActivtyBinding binding;
    private PrefrenceManager prefrenceManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserActivtyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrenceManage = new PrefrenceManager(getApplicationContext());
        getusers();
        setListner();
    }

    private void setListner() {
        binding.imageback.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    private void getusers() {
        loadingdata(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLAECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loadingdata(false);
                    String currentuserId = prefrenceManage.getSrting(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Users> users = new ArrayList<>();
                        for (QueryDocumentSnapshot querydocumentsnapshort : task.getResult()) {
                            if (currentuserId.equals(querydocumentsnapshort.getId())) {
                                continue;
                            }
                            Users user = new Users();
                            user.name = querydocumentsnapshort.getString(Constants.KEY_NAME);
                            user.email = querydocumentsnapshort.getString(Constants.KEY_EMAIL);
                            user.image = querydocumentsnapshort.getString(Constants.KEY_IMAGE);
                            user.token = querydocumentsnapshort.getString(Constants.KEY_FCM_TOKEN);
                            user.id = querydocumentsnapshort.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            userAdapter userAdapteree = new userAdapter(users, this);
                            binding.userrecyclerview.setAdapter(userAdapteree);
                            binding.userrecyclerview.setVisibility(View.VISIBLE);
                        } else {
                            showerrormesg();
                        }
                    } else {
                        showerrormesg();
                    }
                });

    }

    private void showerrormesg() {
        binding.textErrormsg.setText(String.format("%s", "no user available"));
        binding.textErrormsg.setVisibility(View.VISIBLE);

    }

    private void loadingdata(Boolean isloading) {
        if (isloading) {
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onuserClicked(Users user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}
