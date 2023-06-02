 package com.example.infinitetalk.Activet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.infinitetalk.Model.ChatMessage;
import com.example.infinitetalk.Model.Users;
import com.example.infinitetalk.Network.ApiClient;
import com.example.infinitetalk.Network.ApiService;
import com.example.infinitetalk.R;
import com.example.infinitetalk.adapters.ChatAdapter;
import com.example.infinitetalk.databinding.ActivityChatBinding;
import com.example.infinitetalk.utilties.Constants;
import com.example.infinitetalk.utilties.PrefrenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends BaseActivity {

    private ActivityChatBinding binding;
    private Users receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PrefrenceManager prefrenceManage;
    private FirebaseFirestore database;
    private String conversaionId = null;
    private Boolean isReceivedAvailble = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListenear();
        loadRicivdDetails();
        init();
        listMessage();

    }

    private void init() {
        prefrenceManage = new PrefrenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        Log.d("TAG", "init: devi1 init  start");

        chatAdapter = new ChatAdapter(
                chatMessages,
                getbitmapforencodeString(receiverUser.image),
                prefrenceManage.getSrting(Constants.KEY_USER_ID)
        );
        Log.d("TAG", "init: devi1 " + getbitmapforencodeString(receiverUser.image));

        binding.chatrcv.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
        Log.d("TAG", "init: devi1 init end");
    }

    private void sendMessage() {
        HashMap<String, Object> message = new HashMap<>();
        message.put(Constants.KEY_SENDER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID));
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        message.put(Constants.KEY_MESSAGE, binding.inputmsg.getText().toString());
        message.put(Constants.KEY_TIMESTAMP, new Date());
        database.collection(Constants.KEY_COLLATION_CHAT).add(message);

        if (conversaionId != null) {
            Log.d("TAG", "sendMessage: devi1 conver not null");
            updateConversion(binding.inputmsg.getText().toString());
        } else {
            Log.d("TAG", "sendMessage: devi1 conver is null");
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, prefrenceManage.getSrting(Constants.KEY_NAME));
            conversion.put(Constants.KEY_SENDER_IMAGE, prefrenceManage.getSrting(Constants.KEY_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.name);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.image);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputmsg.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            addConverion(conversion);
        }

        if (!isReceivedAvailble) {
            try {
                JSONArray tokens = new JSONArray();
                tokens.put(receiverUser.token);

                JSONObject data = new JSONObject();
                data.put(Constants.KEY_USER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID));
                data.put(Constants.KEY_NAME, prefrenceManage.getSrting(Constants.KEY_NAME));
                data.put(Constants.KEY_FCM_TOKEN, prefrenceManage.getSrting(Constants.KEY_FCM_TOKEN));
                data.put(Constants.KEY_MESSAGE, binding.inputmsg.getText().toString());

                JSONObject body = new JSONObject();
                body.put(Constants.REMOTE_MSG_DATA, data);
                body.put(Constants.REMOTE_MSG_REGISTRETION_IDS, tokens);

                sendNotification(body.toString());
            } catch (Exception e) {
                showToat(e.getMessage());
            }
        }
        binding.inputmsg.setText(null);
    }

    private void showToat(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(String messaheBody) {
        Log.d("TAG", "sendNotification: devi1 sendnoti");
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(), messaheBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                if (response.isSuccessful()) {
                    try {
                        if (response.body() != null) {
                            JSONObject responsJson = new JSONObject(response.body());
                            JSONArray results = responsJson.getJSONArray("results");

                            if (responsJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToat(error.getString("error"));
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("TAG", "onResponse: devi2 Notification send Successfully ");
                    showToat("Notification send Successfully");
                } else {
                    Log.d("TAG", "onFailure: devi1 Error  " + response.code());
                    showToat("Error" + response.code());
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Log.d("TAG", "onFailure: devi1 failuar " + t.getMessage());
                showToat(t.getMessage());
            }
        });
    }

    private void listenAvailableofReceiver() {
        database.collection(Constants.KEY_COLLAECTION_USERS).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                if (value.getLong(Constants.KEY_AVAILABILITY) != null) {
                    int avability = Objects.requireNonNull(
                            value.getLong(Constants.KEY_AVAILABILITY)
                    ).intValue();
                    isReceivedAvailble = avability == 1;
                }
                receiverUser.token = value.getString(Constants.KEY_FCM_TOKEN);
                if (receiverUser.image == null) {
                    receiverUser.image = value.getString(Constants.KEY_IMAGE);
                    chatAdapter.setReceiverProfileImage(getbitmapforencodeString(receiverUser.image));
                    chatAdapter.notifyItemRangeInserted(0, chatMessages.size());
                }
            }
            if (isReceivedAvailble) {
                binding.textAvailability.setVisibility(View.VISIBLE);
            } else {
                binding.textAvailability.setVisibility(View.GONE);
            }
        });
    }

    private void listMessage() {
        database.collection(Constants.KEY_COLLATION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLATION_CHAT)
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, prefrenceManage.getSrting(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    chatMessage.receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_MESSAGE);
                    chatMessage.dateTime = getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, (obj1, obj2) -> obj1.dateObject.compareTo(obj2.dateObject));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatrcv.smoothScrollToPosition(chatMessages.size() - 1);

            }
            binding.chatrcv.setVisibility(View.VISIBLE);
        }
        binding.prograssBar.setVisibility(View.GONE);
        if (conversaionId == null) {
            checkForConversion();
        }
    };

    private Bitmap getbitmapforencodeString(String encodeedImage) {

        if (encodeedImage != null) {
            byte[] bytes = Base64.decode(encodeedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }

    }

    private void loadRicivdDetails() {
        receiverUser = (Users) getIntent().getSerializableExtra(Constants.KEY_USER);
        binding.textname.setText(receiverUser.name);

    }

    private void setListenear() {
        binding.imageback.setOnClickListener(v -> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> sendMessage());

    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addConverion(HashMap<String, Object> conversion) {
        database.collection(Constants.KEY_COLLATION_CONVERSATION)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversaionId = documentReference.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference = database.collection(Constants.KEY_COLLATION_CONVERSATION).document(conversaionId);
        documentReference.update(Constants.KEY_LAST_MESSAGE, message, Constants.KEY_TIMESTAMP, new Date());

    }

    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotly(
                    prefrenceManage.getSrting(Constants.KEY_USER_ID),
                    receiverUser.id);
            checkForConversionRemotly(
                    receiverUser.id,
                    prefrenceManage.getSrting(Constants.KEY_USER_ID));
        }
    }

    private void checkForConversionRemotly(String senderId, String receiverId) {
        database.collection(Constants.KEY_COLLATION_CONVERSATION)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversationComplete);
    }

    private final OnCompleteListener<QuerySnapshot> conversationComplete = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversaionId = documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listenAvailableofReceiver();
    }
}