package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.MessagesRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private EditText etMessageField;
    private ImageView sendButton;
    private ImageView galleryButton;
    private String chatId;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;

    private RecyclerView mMessageRecyclerView;
    private MessagesRecyclerViewAdapter mMessagesRecyclerViewAdapter;

    public ChatActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
        setupRecyclerView();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);

        storageRef = FirebaseStorage.getInstance().getReference().child(Constants.PHOTO_MESSAGES_CHILD).child(chatId);

        sendButton = findViewById(R.id.button_send);
        galleryButton = findViewById(R.id.button_gallery);
        etMessageField = findViewById(R.id.et_message_field);

        galleryButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        sendButton.setOnClickListener(view -> {
            DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

            DatabaseReference messageReference = dbReference.child(Constants.MESSAGES_CHILD).child(chatId);
            DatabaseReference lastMessageTimeReference = dbReference.child(Constants.CHATS_CHILD)
                    .child(chatId).child(Constants.CHAT_LAST_MESSAGE_TIMESTAMP);

            // FixMe : Set isMultimedia based on message type. Setting false for now.
            boolean isMultimedia = false;

            // Retrieve and send the text message in the message field to firebase database.
            Message message = new Message(etMessageField.getText().toString(), isMultimedia
                    ,firebaseAuth.getCurrentUser().getUid());

            // Evaluate the relative paths for writing message object and updating lastMessageTimestamp in one go.
            String messageReferenceRelativeChildKey = messageReference.toString().replace(dbReference.toString(), "");
            String lastMessageTimeReferenceRelativeChildKey = lastMessageTimeReference.toString().replace(dbReference.toString(), "");

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(messageReferenceRelativeChildKey + "/" + messageReference.push().getKey(), message);
            childUpdates.put(lastMessageTimeReferenceRelativeChildKey, message.getTimeSent());

            dbReference.updateChildren(childUpdates);

            etMessageField.setText("");
            etMessageField.requestFocus();
        });

        mMessageRecyclerView = findViewById(R.id.rv_chats);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Todo: Complete this implementation.
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, "Error occured while choosing photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                UploadTask uploadTask = storageRef.putStream(inputStream);
                uploadTask.addOnFailureListener(exception -> {
                    // Handle unsuccessful uploads
                }).addOnSuccessListener(taskSnapshot -> {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                });

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

        mMessagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(options, this);

        mMessageRecyclerView.setLayoutManager(layoutManager);
        mMessageRecyclerView.setAdapter(mMessagesRecyclerViewAdapter);
    }

}
