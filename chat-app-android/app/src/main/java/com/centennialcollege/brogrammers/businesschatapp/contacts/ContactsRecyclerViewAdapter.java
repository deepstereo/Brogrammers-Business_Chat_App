package com.centennialcollege.brogrammers.businesschatapp.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.Map;

/**
 * RecyclerView adapter to display the list of all contacts on All Contacts screen.
 */

public class ContactsRecyclerViewAdapter extends FirebaseRecyclerAdapter<User, ContactsRecyclerViewAdapter.ContactViewHolder> {

    private Map<String, Boolean> selectedContacts;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public ContactsRecyclerViewAdapter(FirebaseRecyclerOptions<User> options, Map<String, Boolean> selectedContacts) {
        super(options);
        this.selectedContacts = selectedContacts;
    }

    @Override
    public ContactsRecyclerViewAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_list_item, parent, false);
        return new ContactsRecyclerViewAdapter.ContactViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(ContactsRecyclerViewAdapter.ContactViewHolder holder, int position, User model) {
        holder.bind(model);
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvEmail;
        private ImageView ivTick;
        private View view;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            ivTick = v.findViewById(R.id.iv_tick);
        }

        void bind(final User model) {
            tvUsername.setText("Username: " + model.getUsername());
            tvEmail.setText("Email: " + model.getEmail());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedContacts.containsKey(model.getId())) {
                        selectedContacts.remove(model.getId());
                        ivTick.setVisibility(View.INVISIBLE);
                    } else {
                        selectedContacts.put(model.getId(), true);
                        ivTick.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

}
