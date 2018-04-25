package com.centennialcollege.brogrammers.businesschatapp;

import android.app.Application;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

/**
 * Custom Application class for doing stuff which needs to be done in the beginning itself.
 */

public class BusinessChatApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        /* Enable disk persistence */
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//        database.setPersistenceEnabled(true);

        // Todo: Implement user online/offline status indicator functionality.
//        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        // since I can connect from multiple devices, we store each connection instance separately
//// any time that connectionsRef's value is null (i.e. has no children) I am offline
//        final FirebaseDatabase database = FirebaseDatabase.getInstance();
//
//        /* Enable disk persistence */
//        database.setPersistenceEnabled(true);
//
////        final DatabaseReference myConnectionsRef = database.getReference("users/joe/connections");
//        final DatabaseReference myConnectionsRef = database.getReference().child(Constants.USERS_CHILD)
//                .child(firebaseUser.getUid()).child(Constants.USER_CONNECTIONS);
//
//// stores the timestamp of my last disconnect (the last time I was seen online)
////        final DatabaseReference lastOnlineRef = database.getReference("/users/joe/lastOnline");
//        final DatabaseReference lastOnlineRef = database.getReference().child(Constants.USERS_CHILD)
//                .child(firebaseUser.getUid()).child(Constants.USER_LAST_ONLINE);
//
//        final DatabaseReference connectedRef = database.getReference(Constants.INFO_CONNECTED);
//        connectedRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot snapshot) {
//                boolean connected = snapshot.getValue(Boolean.class);
//                if (connected) {
//                    Log.d("ChatApplicationClass", "CONNECTED");
//                    DatabaseReference con = myConnectionsRef.push();
//
//                    // when this device disconnects, remove it
//                    con.onDisconnect().removeValue();
//
//                    // when I disconnect, update the last time I was seen online
//                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);
//
//                    // add this device to my connections list
//                    // this value could contain info about the device or a timestamp too
//                    con.setValue(Boolean.TRUE);
//                } else {
//                    Log.d("ChatApplicationClass", "DISCONNECTED");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                System.err.println("Listener was cancelled at .info/connected");
//            }
//        });


    }


}
