package com.centennialcollege.brogrammers.businesschatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.model.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The main chat screen where all messages sent by all users are visible.
 */
public class ChatActivity extends AppCompatActivity {

    private FirebaseListAdapter<ChatMessage> adapter;
    EditText etMessageField;
    ImageView sendButton;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        init();
        displayChatMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If the user is not logged in anymore, navigate to the Login Screen.
        if (!isUserLoggedIn()) {
            launchLoginActivity();
        }
    }

    /**
     * Initialize views.
     */
    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();

        sendButton = findViewById(R.id.button_send);
        etMessageField = findViewById(R.id.et_message_field);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve and send the text message in the message field to firebase database.
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(etMessageField.getText().toString(),
                        firebaseAuth.getCurrentUser().getEmail()));
                etMessageField.setText("");
                etMessageField.requestFocus();
            }
        });
    }

    /**
     * Checks if the user is still logged in or not.
     * @return : True if logged in, false otherwise.
     */
    private boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    private void launchLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out) {
            // Sign out the current user and navigate to Login Screen.
            firebaseAuth.signOut();
            launchLoginActivity();
        }
        return true;
    }

    /**
     * Display the list of all messages from the firebase database.
     */
    private void displayChatMessages() {

        ListView listOfMessage = findViewById(R.id.lv_messages);
        adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class, R.layout.message_list_item, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                //Initialize the views in message list item and set values for them.
                TextView tvMessageText = v.findViewById(R.id.tv_message_text);
                TextView tvMessageSender = v.findViewById(R.id.tv_message_sender);
                TextView tvMessageTime = v.findViewById(R.id.tv_message_time);

                tvMessageText.setText(model.getMessageText());
                tvMessageSender.setText(model.getMessageUser());
                tvMessageTime.setText(DateFormat.format(getString(R.string.message_date_format), model.getMessageTime()));
            }
        };
        listOfMessage.setAdapter(adapter);
    }
}
