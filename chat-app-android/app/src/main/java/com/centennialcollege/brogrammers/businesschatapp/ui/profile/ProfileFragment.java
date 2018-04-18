package com.centennialcollege.brogrammers.businesschatapp.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private static final int PICK_IMAGE = 1;

    private ProfileContract.Presenter presenter;

    private FragmentProfileBinding binding;

    private AlertDialog authDialog;

    private String userId;

    public ProfileFragment() {
    } // Required empty public constructor

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProfilePresenter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.takeView(this);

        binding.ivUserImage.setOnClickListener(view1 -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE);
        });

        initUserAvatar();
    }

    private void initUserAvatar() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) return;
        userId = firebaseUser.getUid();

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS_CHILD)
                .child(userId)
                .child(Constants.USER_AVATAR_URL);

        // Attach a listener to read the data at our posts reference
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String url = dataSnapshot.getValue(String.class);

                    if (url != null && url.length() > 0) {
                        Context context = getContext();
                        if (context == null) return;

                        Glide.with(context)
                                .load(url)
                                .into(binding.ivUserImage);
                    }
                } catch (Exception e) {
                    System.out.println("The read failed: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.dropView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                String username = binding.etUsername.getText().toString();
                String email = binding.etEmail.getText().toString();

                presenter.attemptChangeUserInfo(username, email);
                return true;
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
        }
        return true;
    }

    @Override
    public void showError(ProfileContract.Error errorCode) {
        switch (errorCode) {
            case ERROR_EMAIL_REQUIRED:
                binding.etEmail.setError(getString(R.string.profile_error_field_required));
                binding.etEmail.requestFocus();
                break;
            case ERROR_USERNAME_REQUIRED:
                binding.etUsername.setError(getString(R.string.profile_error_field_required));
                binding.etUsername.requestFocus();
                break;
            case ERROR_EMAIL_INVALID:
                binding.etEmail.setError(getString(R.string.profile_error_email_invalid));
                binding.etEmail.requestFocus();
                break;
            case ERROR_UPDATE_USER_INFO:
                Toast.makeText(getContext(), R.string.profile_error_update, Toast.LENGTH_SHORT).show();
                break;
            case ERROR_EMAIL_ALREADY_IN_USE:
                binding.etEmail.setError(getString(R.string.profile_error_email_exist));
                binding.etEmail.requestFocus();
                break;
            case ERROR_PASSWORD_REQUIRED:
                Toast.makeText(getContext(), R.string.profile_error_password_required, Toast.LENGTH_SHORT).show();
                break;
            case ERROR_WRONG_PASSWORD:
                Toast.makeText(getContext(), R.string.profile_error_wrong_password, Toast.LENGTH_SHORT).show();
                break;
            case ERROR_GET_USER_INFO:
                Toast.makeText(getContext(), R.string.profile_error_get_user_info, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void hideErrors() {
        binding.etUsername.setError(null);
        binding.etEmail.setError(null);
    }

    @Override
    public void showProgress() {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBar.animate().setDuration(shortAnimTime).alpha(1);
    }

    @Override
    public void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
        binding.progressBar.animate().cancel();
    }

    @Override
    public void showUpdateSuccessMsg() {
        Toast.makeText(getContext(), R.string.profile_success_update, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUsername(String username) {
        binding.etUsername.setText(username);
    }

    @Override
    public void setUserEmail(String email) {
        binding.etEmail.setText(email);
    }

    @Override
    public void showReAuthenticationDialog() {
        AlertDialog.Builder authDialogBuilder = new AlertDialog.Builder(getContext());

        final EditText etPassword = new EditText(getContext());
        etPassword.setHint(R.string.profile_hint_password);

        authDialogBuilder.setTitle(R.string.profile_title_enter_password)
                .setView(etPassword)
                .setPositiveButton(R.string.profile_ok, (dialogInterface, i) -> {
                })
                .setNegativeButton(R.string.profile_cancel, (dialog, id) -> dialog.cancel());

        authDialog = authDialogBuilder.create();

        authDialog.show();

        authDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                presenter.attemptReAuthentication(etPassword.getText().toString()));
    }

    @Override
    public void closeReAuthenticationDialog() {
        if (authDialog != null) authDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        StorageReference storageRef;
        if (userId == null) return;
        storageRef = FirebaseStorage.getInstance().getReference()
                .child(Constants.USER_IMAGES_CHILD).child(userId);

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data.getData() == null) {
                Toast.makeText(getContext(), "Error occurred while choosing photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Context context = getContext();
                if (context == null) return;
                InputStream inputStream = context.getContentResolver().openInputStream(data.getData());
                if (inputStream == null) return;
                UploadTask uploadTask = storageRef.putStream(inputStream);
                uploadTask
                        .addOnSuccessListener(taskSnapshot -> {
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            if (downloadUrl == null) return;

                            Glide.with(context)
                                    .load(downloadUrl)
                                    .into(binding.ivUserImage);

                            addUserAvatarToDb(downloadUrl);
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUserAvatarToDb(Uri avatarUrl) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(Constants.USERS_CHILD)
                .child(userId)
                .child(Constants.USER_AVATAR_URL)
                .setValue(avatarUrl.toString());
    }

}
