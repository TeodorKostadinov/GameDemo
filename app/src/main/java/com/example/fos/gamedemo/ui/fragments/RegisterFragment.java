package com.example.fos.gamedemo.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fos.gamedemo.R;
import com.example.fos.gamedemo.db.WebServiceHelper;
import com.example.fos.gamedemo.utils.InputUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fos on 8.12.2015 г..
 */
public class RegisterFragment extends Fragment {

    @Bind(R.id.edt_username)
    EditText edtUsername;
    @Bind(R.id.edt_password)
    EditText edtPassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_register)
    public void onRegisterClicked() {
        WebServiceHelper helper = WebServiceHelper.getInstance(getActivity());
        helper.createUser(edtUsername.getText().toString(), edtPassword.getText().toString(), listener);
        InputUtils.hideKeyboard(getActivity());
    }

    private WebServiceHelper.UserAuthenticationListener listener = new WebServiceHelper.UserAuthenticationListener() {
        @Override
        public void onCreateSuccess() {
            Toast.makeText(RegisterFragment.this.getActivity(), getString(R.string.reg_success), Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

        @Override
        public void onCreateError() {
            Toast.makeText(RegisterFragment.this.getActivity(), "Error. Try again.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onAuthSuccess() {

        }

        @Override
        public void onAuthError() {

        }
    };
}
