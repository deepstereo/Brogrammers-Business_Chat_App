package com.centennialcollege.brogrammers.businesschatapp.ui.login;

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

import com.centennialcollege.brogrammers.businesschatapp.R;
import com.centennialcollege.brogrammers.businesschatapp.activity.HomeActivity;
import com.centennialcollege.brogrammers.businesschatapp.databinding.FragmentLoginBinding;
import com.centennialcollege.brogrammers.businesschatapp.ui.registration.RegistrationActivity;

public class LoginFragment extends Fragment implements LoginContract.View {

    private LoginContract.Presenter presenter;

    private FragmentLoginBinding binding;

    public LoginFragment() {
    } // Required empty public constructor

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new LoginPresenter();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString();
            String password = binding.etPassword.getText().toString();

            presenter.attemptLogin(email, password);
        });
        binding.btnRegistration.setOnClickListener(v -> launchRegistrationActivity());

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
    public void showError(LoginContract.Error errorCode) {

        switch (errorCode) {
            case ERROR_EMAIL_REQUIRED:
                binding.etEmail.setError(getString(R.string.login_error_field_required));
                binding.etEmail.requestFocus();
                break;
            case ERROR_PASSWORD_REQUIRED:
                binding.etPassword.setError(getString(R.string.login_error_field_required));
                binding.etPassword.requestFocus();
                break;
            case ERROR_EMAIL_INVALID:
                binding.etEmail.setError(getString(R.string.login_error_email_invalid));
                binding.etEmail.requestFocus();
                break;
            case ERROR_WRONG_PASSWORD:
                binding.etPassword.setError(getString(R.string.login_error_wrong_password));
                binding.etPassword.requestFocus();
                break;
            case ERROR_AUTHORIZATION:
                Toast.makeText(getContext(), R.string.login_error_authorization, Toast.LENGTH_SHORT).show();
                break;
            case ERROR_EMAIL_DOES_NOT_EXIST:
                binding.etEmail.setError(getString(R.string.login_error_email_not_exist));
                binding.etEmail.requestFocus();
                break;
        }
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
        Intent intent = new Intent(getContext(), HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void launchRegistrationActivity() {
        startActivity(new Intent(getContext(), RegistrationActivity.class));
    }

}
