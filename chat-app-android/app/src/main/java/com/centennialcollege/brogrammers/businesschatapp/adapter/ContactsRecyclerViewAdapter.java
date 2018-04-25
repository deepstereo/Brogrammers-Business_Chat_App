package com.centennialcollege.brogrammers.businesschatapp.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.util.UserAttributesUtils;
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
        private TextView tvPlaceholderAvatar;
        private CardView cvAvatar;
        private ImageView ivAvatar;
        private View view;

        ContactViewHolder(View v) {
            super(v);
            view = v;
            tvUsername = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            ivTick = v.findViewById(R.id.iv_tick);
            tvPlaceholderAvatar = v.findViewById(R.id.tv_placeholder_avatar);
            cvAvatar = v.findViewById(R.id.cv_avatar);
            ivAvatar = v.findViewById(R.id.iv_avatar);
        }

        void bind(final User user) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            if (user.getAvatar()) {
                cvAvatar.setVisibility(View.VISIBLE);
                Glide.with(view.getContext())
                        .load(user.getAvatarURL())
                        .centerCrop()
                        .into(ivAvatar);
                tvPlaceholderAvatar.setVisibility(View.GONE);
            } else {
                cvAvatar.setVisibility(View.GONE);
                tvPlaceholderAvatar.setVisibility(View.VISIBLE);
                UserAttributesUtils.setAccountColor(tvPlaceholderAvatar, user.getUsername(), view.getContext());
            }

            ivTick.setVisibility(selectedContacts.containsKey(user.getId()) ? View.VISIBLE : View.INVISIBLE);

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
