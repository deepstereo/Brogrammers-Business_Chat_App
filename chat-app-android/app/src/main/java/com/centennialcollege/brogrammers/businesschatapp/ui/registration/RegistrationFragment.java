package com.centennialcollege.brogrammers.businesschatapp.ui.registration;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.centennialcollege.brogrammers.businesschatapp.Constants;
import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.activity.MainActivity;
import com.centennialcollege.brogrammers.businesschatapp.databinding.FragmentRegistrationBinding;


public class RegistrationFragment extends Fragment implements RegistrationContract.View {

    private RegistrationContract.Presenter presenter;

    private FragmentRegistrationBinding binding;

    public RegistrationFragment() {
    } // Required empty public constructor

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new RegistrationPresenter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);

        binding.btnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = binding.etUsername.getText().toString();
                String email = binding.etEmail.getText().toString();
                String password = binding.etPassword.getText().toString();
                String passwordConfirm = binding.etPasswordConfirm.getText().toString();

                presenter.attemptRegistration(username, email, password, passwordConfirm);
            }
        });

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
    public void showErrorEmailRequired() {
        binding.etEmail.setError(getString(R.string.registration_error_field_required));
        binding.etEmail.requestFocus();
    }

    @Override
    public void showErrorEmailInvalid() {
        binding.etEmail.setError(getString(R.string.registration_error_email_invalid));
        binding.etEmail.requestFocus();
    }

    @Override
    public void showErrorEmailExist() {
        binding.etEmail.setError(getString(R.string.registration_error_email_exist));
        binding.etEmail.requestFocus();
    }

    @Override
    public void showErrorUsernameRequired() {
        binding.etUsername.setError(getString(R.string.registration_error_field_required));
        binding.etUsername.requestFocus();
    }

    @Override
    public void showErrorPasswordConfirmRequired() {
        binding.etPasswordConfirm.setError(getString(R.string.registration_error_field_required));
        binding.etPasswordConfirm.requestFocus();
    }

    @Override
    public void showErrorPasswordsNotSame() {
        Toast.makeText(getContext(), R.string.registration_error_passwords_not_same, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorUsernameTooShort() {
        binding.etUsername.setError(getString(R.string.registration_error_username_too_short, Constants.MINIMUM_USERNAME_LENGTH));
        binding.etUsername.requestFocus();
    }

    @Override
    public void showErrorPasswordTooShort() {
        binding.etPassword.setError(getString(R.string.registration_error_password_too_short, Constants.MINIMUM_PASSWORD_LENGTH));
        binding.etPassword.requestFocus();
    }

    @Override
    public void showErrorPasswordRequired() {
        binding.etPassword.setError(getString(R.string.registration_error_field_required));
        binding.etPassword.requestFocus();
    }

    @Override
    public void showErrorRegistration() {
        Toast.makeText(getContext(), R.string.registration_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void hideErrors() {
        binding.etEmail.setError(null);
        binding.etPassword.setError(null);
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
    public void launchMainActivity() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
