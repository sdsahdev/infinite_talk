package com.example.infinitetalk.listtenear;

import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.Model.Userss;
import com.google.firebase.firestore.auth.User;

public interface UserListner {
    void onuserClicked(Users user);

}
