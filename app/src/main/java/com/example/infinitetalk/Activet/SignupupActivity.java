package com.example.infinitetalk.Activet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.infinitetalk.databinding.ActivitySignupupBinding;
import com.example.infinitetalk.utilties.Constants;
import com.example.infinitetalk.utilties.PrefrenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignupupActivity extends AppCompatActivity {


    private ActivitySignupupBinding binding;
    private PrefrenceManager prefrenceManager;
    private String encodedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrenceManager = new PrefrenceManager(getApplicationContext());
        setListner();

//        binding.hide.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(  binding.hide.isPressed()) {
//                    Toast.makeText(SignupupActivity.this, "yes", Toast.LENGTH_SHORT).show();
//                    binding.inputpassword.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
//                    return true;
//                }
//                return true;
//            }
//        });

//        binding.hide2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(  binding.hide.isPressed()) {
//                    Toast.makeText(SignupupActivity.this, "yes", Toast.LENGTH_SHORT).show();
//                    binding.inputconformpassword.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
//                    return true;
//                }
//                return true;
//            }
//        });
    }

    private void setListner() {

//        binding.hide2.setOnClickListener(v-> {
//            Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
//
//            Boolean pas = false;
//
//            if(pas == false)
//            {
//                Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
//                pas = true;
//                binding.inputpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//
//            }
//            else {
//                pas =false;
//                Toast.makeText(this, "yes", Toast.LENGTH_SHORT).show();
//
//                binding.inputpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            }
//
//        });

//        binding.hide.setOnClickListener(v-> {
//            Boolean pas2 = false;
//
//            if(pas2 == false)
//            {
//                pas2 = true;
//                binding.inputconformpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//            }
//            else {
//                binding.inputconformpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            }
//
//        });
        binding.textsignin.setOnClickListener(v -> onBackPressed());
        binding.buttonsignin.setOnClickListener(v -> {
            if (isvaliedSignUpDetails()) {
                signUp();
            }
        });
        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void signUp() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputname.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputemail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputpassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage );
        database.collection(Constants.KEY_COLLAECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    prefrenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    prefrenceManager.putSrting(Constants.KEY_USER_ID,documentReference.getId());
                    prefrenceManager.putSrting(Constants.KEY_NAME, binding.inputname.getText().toString());
                    prefrenceManager.putSrting(Constants.KEY_IMAGE, encodedImage );
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                })

                .addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWith = 150;
        int previewHight = bitmap.getHeight() * previewWith / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWith, previewHight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageprofile.setImageBitmap(bitmap);
                            binding.textAddimage.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private boolean isvaliedSignUpDetails() {
        if (encodedImage == null) {
            showToast("select profile Image");
            return false;
        } else if (binding.inputname.getText().toString().trim().isEmpty()) {
            showToast("Eenter Name");
            return false;
        } else if (binding.inputemail.getText().toString().trim().isEmpty()) {
            showToast("Enter Mail");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputemail.getText().toString()).matches()) {
            showToast("Enter valied email");
            return false;
        } else if (binding.inputpassword.getText().toString().trim().isEmpty()) {
            showToast("Enter valied password");
            return false;
        } else if (binding.inputconformpassword.getText().toString().trim().isEmpty()) {
            showToast("Conform your  password");
            return false;
        } else if (!binding.inputpassword.getText().toString().equals(binding.inputconformpassword.getText().toString())) {
            showToast("password & conform password must bee sem ");
            return false;
        } else {
            return true;
        }
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
}