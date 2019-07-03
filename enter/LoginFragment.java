package com.healbe.healbe_example_andorid.enter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.healbe.healbe_example_andorid.R;
import com.healbe.healbe_example_andorid.tools.TextWatcherAdapter;
import com.healbe.healbesdk.business_api.HealbeSdk;
import com.healbe.healbesdk.business_api.exceptions.ServerWrongCodeException;
import com.healbe.healbesdk.business_api.user.data.HealbeSessionState;
import com.healbe.healbesdk.server_api.ResponseCodes;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import timber.log.Timber;

public class LoginFragment extends Fragment {
    public interface LoginListener {
        void onLoginSucceeded();
    }

    private LoginListener listener;
    private CompositeDisposable destroy = new CompositeDisposable();

    private TextInputLayout mailLayout;
    private TextInputEditText mailText;
    private TextInputLayout passLayout;
    private TextInputEditText passText;
    private Button button;
    private ProgressBar progress;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mailLayout = view.findViewById(R.id.mail_layout);
        mailText = view.findViewById(R.id.email);
        passLayout = view.findViewById(R.id.pass_layout);
        passText = view.findViewById(R.id.password);
        button = view.findViewById(R.id.button);
        progress = view.findViewById(R.id.progress);

        mailText.setText("");
        passText.setText("");

        initialState();

        // nice and short text watcher for validation login data and interact button visibility
        mailText.addTextChangedListener(new TextWatcherAdapter(this::validate));
        passText.addTextChangedListener(new TextWatcherAdapter(this::validate));
    }

    private void initialState() {
        button.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
        button.setOnClickListener(this::onButtonClick);

        mailLayout.setErrorEnabled(false);
        passLayout.setErrorEnabled(false);
    }

    @SuppressWarnings("ConstantConditions")
    private void validate() {
        String email = mailText.getText().toString();
        String password = passText.getText().toString();

        boolean emailOk = TextUtils.isEmpty(email) || isValidEmailAddress(email);
        boolean buttonActive = emailOk && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);

        mailLayout.setErrorEnabled(!emailOk);
        mailLayout.setError(!emailOk ? getString(R.string.login_error) : null);
        button.setEnabled(buttonActive);

        button.setVisibility(View.VISIBLE);
        progress.setVisibility(View.INVISIBLE);
    }

    // send login pair
    @SuppressWarnings({"unused", "ConstantConditions"})
    //TODO 4.Healbe账户登录
    private void onButtonClick(View v) {
        button.setVisibility(View.INVISIBLE);
        progress.setVisibility(View.VISIBLE);

        String email = mailText.getText().toString();
        String password = passText.getText().toString();

        destroy.add(HealbeSdk.get().USER.login(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sessionState -> {
                    // see if login returns valid state
                    if (listener != null && HealbeSessionState.isUserValid(sessionState))
                        listener.onLoginSucceeded();
                    else if (listener != null) // or invalid, not detached
                        onErrorLogin(null, sessionState);
                }, throwable -> onErrorLogin(throwable, HealbeSessionState.USER_NOT_AUTHORIZED)));
    }

    @SuppressWarnings("PointlessNullCheck")
    private void onErrorLogin(Throwable throwable, HealbeSessionState state) {
        if(throwable != null)
            Timber.e(throwable);
        // we don't need valid states here, just states what has errors or we don't want to implement
        switch (state) {
            case USER_NOT_AUTHORIZED:
                //back to initial fragment state
                initialState();

                List<Integer> codes = new ArrayList<>();
                //here we see how to use server response codes from consumed throwable
                if (throwable != null && throwable instanceof ServerWrongCodeException)
                    codes = ((ServerWrongCodeException) throwable).getCodes();

                // if too much wrong tries
                if(codes.contains(ResponseCodes.RESPONSE_CODE_TOO_MUCH_WRONG_TRIES))
                    Toast.makeText(getActivity(), R.string.too_much, Toast.LENGTH_LONG).show();
                // if wrong email/pass
                else if (codes.contains(ResponseCodes.RESPONSE_CODE_LOGIN_OR_PASSWORD_WRONG))
                    Toast.makeText(getActivity(), R.string.wrong_login, Toast.LENGTH_LONG).show();
                else // if we don't know what's going wrong (just for example)
                    Toast.makeText(getActivity(), getString(R.string.auth_error, this.getClass().getSimpleName()), Toast.LENGTH_LONG).show();
                break;

            case NEED_TO_FILL_PERSONAL: // need to fill/correct profile fields
            case NEED_TO_FILL_PARAMS:   // but we won't do this in example
                //so show toast about it
                Toast.makeText(getActivity(), R.string.login_wrong_user, Toast.LENGTH_LONG).show();
                //logout and back to initial fragment state
                destroy.add(HealbeSdk.get().USER.logout()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::validate, Timber::e));
                break;
        }
    }


    // validating of email
    private static boolean isValidEmailAddress(CharSequence email) {
        if (TextUtils.isEmpty(email)) return false;
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginListener) {
            listener = (LoginListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement LoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroy.clear();
    }
}
