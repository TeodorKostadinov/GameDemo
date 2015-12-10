package com.example.fos.gamedemo.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.fos.gamedemo.R;
import com.example.fos.gamedemo.db.WebServiceHelper;
import com.example.fos.gamedemo.ui.fragments.RegisterFragment;
import com.example.fos.gamedemo.utils.PrefsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.edt_username)
    EditText edtUsername;
    @Bind(R.id.edt_password)
    EditText edtPassword;
    @Bind(R.id.check_remember)
    CheckBox checkRemember;
    @Bind(R.id.progress)
    ProgressBar progressBar;
    @Bind(R.id.btn_send)
    Button btnSend;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        prefs = PrefsUtils.getUserPrefs(this);
        String username = prefs.getString(PrefsUtils.KEY_USERNAME, null);
        String password = prefs.getString(PrefsUtils.KEY_PASSWORD, null);
        if (username != null && password != null) {
            edtUsername.setText(username);
            edtPassword.setText(password);
            onSendClicked();
        }
    }

    @OnClick(R.id.txt_register)
    public void onRegisterClicked() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.grp_frag_container, new RegisterFragment()).addToBackStack("fragment").commit();
    }

    @OnClick(R.id.btn_send)
    public void onSendClicked() {
        showProgressBar();

        if (checkRemember.isChecked()) {
            prefs.edit().putString(PrefsUtils.KEY_USERNAME, edtUsername.getText().toString())
                    .putString(PrefsUtils.KEY_PASSWORD, edtPassword.getText().toString())
                    .commit();
        }

        WebServiceHelper helper = WebServiceHelper.getInstance(this);
        helper.authUser(edtUsername.getText().toString(),
                edtPassword.getText().toString(), new WebServiceHelper.UserAuthenticationListener() {
                    @Override
                    public void onCreateSuccess() {

                    }

                    @Override
                    public void onCreateError() {

                    }

                    @Override
                    public void onAuthSuccess() {
                        Intent i = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(i);
                    }

                    @Override
                    public void onAuthError() {
                        hideProgressBar();
                        Toast.makeText(LoginActivity.this, "Error. Try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        btnSend.setEnabled(true);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);
    }
}
