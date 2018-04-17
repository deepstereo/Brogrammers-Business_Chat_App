package com.centennialcollege.brogrammers.businesschatapp.ui.profile;

import android.databinding.DataBindingUtil;
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

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment implements ProfileContract.View {

    private ProfileContract.Presenter presenter;

    private FragmentProfileBinding binding;

    private AlertDialog authDialog;

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

}
