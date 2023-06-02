package com.example.infinitetalk.Activet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;


import com.example.infinitetalk.Model.ChatMessage;
import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.adapters.RecentConversationAdapter;
import com.example.infinitetalk.databinding.ActivityMainBinding;
import com.example.infinitetalk.listtenear.ConversionListener;
import com.example.infinitetalk.utilties.Constants;
import com.example.infinitetalk.utilties.PrefrenceManager;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {
    private ActivityMainBinding binding;
    private PrefrenceManager prefrenceManage;
    private List<ChatMessage> conversation;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrenceManage = new PrefrenceManager(getApplicationContext());
        init();
        loaduserDetails();
        gettoken();
        setListner();
        listConversation();
    }


    private void init() {
        conversation = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversation, this);
        binding.conversaionRcv.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListner() {
        binding.imagesignout.setOnClickListener(v -> {
            signout();
        });
        binding.fabnewchat.setOnClickListener(n -> {
            Intent intent = new Intent(getApplicationContext(), UserActivty.class);
            startActivity(intent);
        });


    }

    private void listConversation() {
        database.collection(Constants.KEY_COLLATION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);

        database.collection(Constants.KEY_COLLATION_CONVERSATION)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receicverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receicverId;

                    if (prefrenceManage.getSrting(Constants.KEY_USER_ID).equals(senderId)) {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    } else {

                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);

                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversation.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversation.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversation.get(i).senderId.equals(senderId) && conversation.get(i).receiverId.equals(receiverId)) {
                            conversation.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversation.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }

            Collections.sort(conversation, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.conversaionRcv.smoothScrollToPosition(0);
            binding.conversaionRcv.setVisibility(View.VISIBLE);
            binding.prograssbar.setVisibility(View.GONE);
        }
    };

    private void gettoken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updatetoken);
    }

    private void loaduserDetails() {
        binding.textname.setText(prefrenceManage.getSrting(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(String.valueOf(prefrenceManage.getSrting(Constants.KEY_IMAGE)), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageprofile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updatetoken(String token) {
        prefrenceManage.putSrting(Constants.KEY_FCM_TOKEN,token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLAECTION_USERS).document(String.valueOf(prefrenceManage.getSrting(Constants.KEY_USER_ID)));
        documentReference.update(Constants.KEY_FCM_TOKEN, token)

                .addOnFailureListener(e -> showToast("unable to update token"));
    }

    private void signout() {
        showToast("Signout");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLAECTION_USERS).document(String.valueOf(prefrenceManage.getSrting(Constants.KEY_USER_ID))
                );

        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    prefrenceManage.clear();
                    startActivity(new Intent(getApplicationContext(), SignininActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("unable to signout"));
    }

    @Override
    public void onConversionClicked(Users users) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, users);
        startActivity(intent);
    }
}