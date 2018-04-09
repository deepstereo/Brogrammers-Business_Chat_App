package com.centennialcollege.brogrammers.businesschatapp.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.adapter.MessagesRecyclerViewAdapter;
import com.centennialcollege.brogrammers.businesschatapp.model.Message;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ChatActivity extends AppCompatActivity {

    EditText etMessageField;
    ImageView sendButton;
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
        init();
        setupRecyclerView();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        chatId = getIntent().getStringExtra(Constants.KEY_CHAT_ID);

        sendButton = findViewById(R.id.button_send);
        etMessageField = findViewById(R.id.et_message_field);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.MESSAGES_CHILD).child(chatId);

                // Retrieve and send the text message in the message field to firebase database.
                Message message = new Message(etMessageField.getText().toString(),
                        firebaseAuth.getCurrentUser().getEmail());

                reference.push().setValue(message);
                etMessageField.setText("");
                etMessageField.requestFocus();
            }
        });

        mMessageRecyclerView = findViewById(R.id.rv_chats);
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
        final LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);

        Query query = FirebaseDatabase.getInstance().getReference().child(Constants.MESSAGES_CHILD).child(chatId);

        FirebaseRecyclerOptions<Message> options =
                new FirebaseRecyclerOptions.Builder<Message>()
                        .setQuery(query, Message.class)
                        .build();

        mMessagesRecyclerViewAdapter = new MessagesRecyclerViewAdapter(options, this);

        mMessageRecyclerView.setLayoutManager(mLinearLayoutManager);
        mMessageRecyclerView.setAdapter(mMessagesRecyclerViewAdapter);
    }

}
