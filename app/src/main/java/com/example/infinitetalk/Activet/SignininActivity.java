package com.example.infinitetalk.Activet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitetalk.databinding.ActivitySignininBinding;
import com.example.infinitetalk.utilties.Constants;
import com.example.infinitetalk.utilties.PrefrenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignininActivity extends AppCompatActivity {
    private ActivitySignininBinding binding;
    private PrefrenceManager prefrenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        binding = ActivitySignininBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (prefrenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setListners();
    }
    private void setListners() {
        binding.textcreatenewacoount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignupupActivity.class)));
        binding.buttonsignin.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    private void signIn() {

        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLAECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputemail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputpassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        prefrenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        prefrenceManager.putSrting(Constants.KEY_USER_ID, documentSnapshot.getId());
                        prefrenceManager.putSrting(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                        prefrenceManager.putSrting(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Unable to Sigin");
                    }
                });
    }

    private void loading(Boolean isloading) {
        if (isloading) {
            binding.buttonsignin.setVisibility(View.INVISIBLE);
            binding.prograssBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonsignin.setVisibility(View.VISIBLE);
            binding.prograssBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputemail.getText().toString().trim().isEmpty()) {
            showToast(" Enter email ");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputemail.getText().toString()).matches()) {
            showToast("Enter valid email");
            return false;
        } else if (binding.inputpassword.getText().toString().trim().isEmpty()) {
            showToast(" Enter password ");
            return false;
        } else {
            return true;
        }
    }
}