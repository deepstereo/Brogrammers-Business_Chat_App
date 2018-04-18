package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.List;
import java.util.Map;

/**
 * RecyclerView adapter to display the list of all contacts on All Contacts screen.
 */

public class ContactsRecyclerViewAdapter extends RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactViewHolder> {

    private List<User> users;
    private Map<String, Boolean> selectedContacts;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     */
    public ContactsRecyclerViewAdapter(List<User> users, Map<String, Boolean> selectedContacts) {
        this.users = users;
        this.selectedContacts = selectedContacts;
    }

    @Override
    public ContactsRecyclerViewAdapter.ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactsRecyclerViewAdapter.ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactsRecyclerViewAdapter.ContactViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ContactViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvEmail;
        private ImageView ivTick;
        private TextView tvPlaceHolderAvatar;
        private CardView cvAvatar;
        private ImageView ivAvatar;
        private View view;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            ivTick = v.findViewById(R.id.iv_tick);
            tvPlaceHolderAvatar = v.findViewById(R.id.tv_placeholder_avatar);
            cvAvatar = v.findViewById(R.id.cv_avatar);
            ivAvatar = v.findViewById(R.id.iv_avatar);
        }

        void bind(final User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            boolean isAvatarImageAvailable = !TextUtils.isEmpty(user.getAvatarURL());

            if (isAvatarImageAvailable) {
                cvAvatar.setVisibility(View.VISIBLE);
                Glide.with(view.getContext())
                        .load(user.getAvatarURL())
                        .centerCrop()
                        .into(ivAvatar);
                tvPlaceHolderAvatar.setVisibility(View.GONE);
            } else {
                cvAvatar.setVisibility(View.GONE);
                tvPlaceHolderAvatar.setVisibility(View.VISIBLE);
                tvPlaceHolderAvatar.setText(String.valueOf(user.getUsername().toUpperCase().charAt(0)));
            }

            view.setOnClickListener(v -> {
                if (selectedContacts.containsKey(user.getId())) {
                    selectedContacts.remove(user.getId());
                    ivTick.setVisibility(View.INVISIBLE);
                } else {
                    selectedContacts.put(user.getId(), true);
                    ivTick.setVisibility(View.VISIBLE);
                }
            });
        }
    }

}
