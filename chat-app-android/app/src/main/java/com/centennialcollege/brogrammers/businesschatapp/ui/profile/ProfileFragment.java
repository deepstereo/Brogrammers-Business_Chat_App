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
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.databinding.FragmentProfileBinding;
import com.centennialcollege.brogrammers.businesschatapp.model.User;
import com.centennialcollege.brogrammers.businesschatapp.util.UserAttributesUtils;
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
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private static final int PICK_IMAGE = 1;

    private ProfileContract.Presenter presenter;

    private FragmentProfileBinding binding;

    private AlertDialog authDialog;
    private AlertDialog changePasswordDialog;

    private String userId;

    private boolean isCurrentUser = true;

    public ProfileFragment() {
    } // Required empty public constructor

    public static ProfileFragment getInstance(@NonNull String userId) {
        ProfileFragment profileFragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(Constants.USER_ID, userId);
        profileFragment.setArguments(args);
        return profileFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ProfilePresenter();
        userId = getArguments().getString(Constants.USER_ID);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        setHasOptionsMenu(true);

        binding.btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.takeView(this);

        binding.tvPlaceholderAvatar.setOnClickListener(v -> launchPhotoGallery());
        binding.ivAvatar.setOnClickListener(view1 -> launchPhotoGallery());
    }

    private void launchPhotoGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private void initUserAvatar(User user) {
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
                    String avatarUrl = dataSnapshot.getValue(String.class);

                    if (!TextUtils.isEmpty(avatarUrl) && getContext() != null) {
                        binding.cvAvatar.setVisibility(View.VISIBLE);
                        Glide.with(getContext())
                                .load(avatarUrl)
                                .centerCrop()
                                .into(binding.ivAvatar);
                        binding.tvPlaceholderAvatar.setVisibility(View.GONE);
                    } else {
                        binding.cvAvatar.setVisibility(View.GONE);
                        binding.tvPlaceholderAvatar.setVisibility(View.VISIBLE);
                        UserAttributesUtils.setAccountColor(binding.tvPlaceholderAvatar, user.getUsername(), getContext());
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
        inflater.inflate(R.menu.fragment_profile, menu);
        MenuItem item = menu.findItem(R.id.menu_save);
        item.setVisible(isCurrentUser);
        super.onCreateOptionsMenu(menu, inflater);
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
            case ERROR_USERNAME_TOO_SHORT:
                Toast.makeText(getContext(),
                        getString(R.string.profile_error_username_too_short, Constants.MINIMUM_USERNAME_LENGTH),
                        Toast.LENGTH_SHORT).show();
                break;
            case ERROR_ALL_FIELDS_REQUIRED:
                Toast.makeText(getContext(), R.string.profile_error_all_fields_required, Toast.LENGTH_SHORT).show();
                break;
            case ERROR_NEW_PASSWORD_TOO_SHORT:
                Toast.makeText(getContext(),
                        getString(R.string.profile_error_password_too_short, Constants.MINIMUM_PASSWORD_LENGTH),
                        Toast.LENGTH_SHORT).show();
                break;
            case ERROR_PASSWORDS_NOT_MATCHING:
                Toast.makeText(getContext(), R.string.profile_error_passwords_not_same, Toast.LENGTH_SHORT).show();
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
    public void setUserInfo(User user) {
        binding.etUsername.setText(user.getUsername());
        binding.etEmail.setText(user.getEmail());
        initUserAvatar(user);
    }

    @Override
    public void setUserInfoReadOnly() {
        binding.tvPlaceholderAvatar.setOnClickListener(null);
        binding.ivAvatar.setOnClickListener(null);
        binding.btnChangePassword.setVisibility(View.GONE);
        makeEditableTextFieldReadOnly(binding.etUsername);
        makeEditableTextFieldReadOnly(binding.etEmail);
    }

    private void makeEditableTextFieldReadOnly(EditText editText) {
        isCurrentUser = false;
        editText.setFocusable(false);
        editText.setFocusableInTouchMode(false);
        editText.setClickable(false);
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public String getUserId() {
        return userId;
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

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data.getData() == null) {
                Toast.makeText(getContext(), "Error occurred while choosing photo.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                StorageReference storageRef;
                if (userId == null) return;
                storageRef = FirebaseStorage.getInstance().getReference()
                        .child(Constants.USER_IMAGES_CHILD).child(userId);

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
                                    .centerCrop()
                                    .into(binding.ivAvatar);

                            addUserAvatarToDb(downloadUrl);
                        });

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void addUserAvatarToDb(Uri avatarUrl) {
        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference();

        DatabaseReference userAvatarUrlReference = dbReference.child(Constants.USERS_CHILD).child(userId).child(Constants.USER_AVATAR_URL);
        DatabaseReference userAvatarReference = dbReference.child(Constants.USERS_CHILD).child(userId).child(Constants.USER_AVATAR);

        // Evaluate the relative path for writing Avatar URL and Avatar availability status.
        String userAvatarUrlReferenceRelativeKey = userAvatarUrlReference.toString().replace(dbReference.toString(), "");
        String userAvatarReferenceRelativeKey = userAvatarReference.toString().replace(dbReference.toString(), "");

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(userAvatarUrlReferenceRelativeKey, avatarUrl.toString());
        childUpdates.put(userAvatarReferenceRelativeKey, true);

        dbReference.updateChildren(childUpdates);
    }

    @Override
    public void showChangePasswordDialog() {
        AlertDialog.Builder changePasswordDialogBuilder = new AlertDialog.Builder(getContext());

        final TextView tvOldPassword = new TextView(getContext());
        final TextView tvNewPassword = new TextView(getContext());
        final TextView tvNewConfirmPassword = new TextView(getContext());

        tvOldPassword.setText(R.string.profile_hint_old_password);
        tvNewPassword.setText(R.string.profile_hint_new_password);
        tvNewConfirmPassword.setText(R.string.profile_hint_new_confirm_password);

        final EditText etOldPassword = new EditText(getContext());
        final EditText etNewPassword = new EditText(getContext());
        final EditText etNewConfirmPassword = new EditText(getContext());

        etOldPassword.setHint(R.string.profile_hint_old_password);
        etNewPassword.setHint(R.string.profile_hint_new_password);
        etNewConfirmPassword.setHint(R.string.profile_hint_new_confirm_password);

        etOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etNewConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams LLParams =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(LLParams);

        linearLayout.addView(tvOldPassword);
        linearLayout.addView(etOldPassword);

        linearLayout.addView(tvNewPassword);
        linearLayout.addView(etNewPassword);

        linearLayout.addView(tvNewConfirmPassword);
        linearLayout.addView(etNewConfirmPassword);

        changePasswordDialogBuilder.setTitle(R.string.profile_title_change_password)
                .setView(linearLayout)
                .setPositiveButton(R.string.profile_ok, (dialogInterface, i) -> {
                })
                .setNegativeButton(R.string.profile_cancel, (dialog, id) -> dialog.cancel());

        changePasswordDialog = changePasswordDialogBuilder.create();

        changePasswordDialog.show();

        changePasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v ->
                presenter.attemptChangePassword(
                        etOldPassword.getText().toString(),
                        etNewPassword.getText().toString(),
                        etNewConfirmPassword.getText().toString()
                        ));
    }

    @Override
    public void closeChangePasswordDialog() {
        if (changePasswordDialog != null) changePasswordDialog.dismiss();
    }

}