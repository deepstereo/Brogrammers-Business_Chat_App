package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.MessagesRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.centennialcollege.brogrammers.businesschatapp.ui.profile.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    public static final int CHAT_TYPE_PERSONAL = 2;
    public static final int CHAT_TYPE_GROUP = 3;

    private static final int PICK_IMAGE = 1;

    private EditText etMessageField;
    private String chatId;
    private FirebaseAuth firebaseAuth;

    private RecyclerView mMessageRecyclerView;
    private MessagesRecyclerViewAdapter mMessagesRecyclerViewAdapter;

    public ChatActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle(getIntent().getStringExtra(Constants.KEY_CHAT_NAME));
        init();
        setupRecyclerView();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);

        ImageView sendButton = findViewById(R.id.button_send);
        ImageView galleryButton = findViewById(R.id.button_gallery);
        etMessageField = findViewById(R.id.et_message_field);
        mMessageRecyclerView = findViewById(R.id.rv_chats);

        galleryButton.setOnClickListener(v -> launchPhotoGallery());

        sendButton.setOnClickListener(view -> {
            String message = etMessageField.getText().toString();
            if (!TextUtils.isEmpty(message)) {
                sendMessage(false, message, System.currentTimeMillis());

                etMessageField.setText("");
                etMessageField.requestFocus();
            }
        });

    }

    private void sendMessage(boolean isMultimediaMessage, String messageContent, long timeStamp) {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference messageReference = dbReference.child(Constants.MESSAGES_CHILD).child(chatId);
        DatabaseReference lastMessageTimeReference = dbReference.child(Constants.CHATS_CHILD)
                .child(chatId).child(Constants.CHAT_LAST_MESSAGE_TIMESTAMP);

        // Retrieve and send the text message in the message field to firebase database.
        Message message = new Message(messageContent, isMultimediaMessage
                ,firebaseAuth.getCurrentUser().getUid(), timeStamp);

        // Evaluate the relative paths for writing message object and updating lastMessageTimestamp in one go.
        String messageReferenceRelativeChildKey = messageReference.toString().replace(dbReference.toString(), "");
        String lastMessageTimeReferenceRelativeChildKey = lastMessageTimeReference.toString().replace(dbReference.toString(), "");

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(messageReferenceRelativeChildKey + "/" + messageReference.push().getKey(), message);
            childUpdates.put(lastMessageTimeReferenceRelativeChildKey, message.getTimeSent());

        dbReference.updateChildren(childUpdates);
    }

    private void launchPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (firebaseAuth.getCurrentUser().getUid() == null) {
            return;
        }

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data.getData() == null) {
                Toast.makeText(this, "Error occurred while choosing photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                long timeStamp = System.currentTimeMillis();

                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child(Constants.PHOTO_MESSAGES_CHILD)
                        .child(chatId)
                        .child(timeStamp + firebaseAuth.getCurrentUser().getUid());

                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                if (inputStream != null) {
                    UploadTask uploadTask = storageRef.putStream(inputStream);
                    uploadTask.addOnSuccessListener(taskSnapshot -> {
                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                if (downloadUrl != null) {
                                    sendMessage(true, downloadUrl.toString(), timeStamp);
                                }
                            });
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMessagesRecyclerViewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMessagesRecyclerViewAdapter.stopListening();
    }

    private void setupRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        Query query = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES_CHILD).child(chatId);

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        mMessagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(options);

        mMessageRecyclerView.setLayoutManager(layoutManager);
        mMessageRecyclerView.setAdapter(mMessagesRecyclerViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_info:
                if (getIntent().getIntExtra(Constants.KEY_CHAT_ACTIVITY_CHAT_TYPE, 0) == CHAT_TYPE_PERSONAL) {

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.CHATS_CHILD).child(chatId).child(Constants.CHATS_MEMBERS);

                        // Attach a listener to read the data at our posts reference
                        ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    Map<String, Boolean> chatMembersId = (Map<String, Boolean>) dataSnapshot.getValue();

                                    Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
                                    for (String chatMemberId : chatMembersId.keySet()) {
                                        if (!TextUtils.equals(chatMemberId, FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            intent.putExtra(Constants.USER_ID, chatMemberId);
                                        }
                                    }
                                    startActivity(intent);
                                } catch (Exception e) {
                                    System.out.println("The read failed: " + e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("The read failed: " + databaseError.getCode());
                            }
                        });

                } else if (getIntent().getIntExtra(Constants.KEY_CHAT_ACTIVITY_CHAT_TYPE, 0) == CHAT_TYPE_GROUP) {
                    Intent intent = new Intent(this, ChatInfoActivity.class);
                    intent.putExtra(Constants.KEY_CHAT_ID, chatId);
                    startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
