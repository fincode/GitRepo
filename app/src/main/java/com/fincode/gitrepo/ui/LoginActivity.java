package com.fincode.gitrepo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.model.enums.MethodName;
import com.fincode.gitrepo.service.WebAsync;
import com.fincode.gitrepo.ui.custom.AnimationButton;
import com.fincode.gitrepo.ui.custom.TransparentProgressDialog;
import com.fincode.gitrepo.utils.Utils;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;

import static com.joanzapata.android.asyncservice.api.annotation.OnMessage.Sender.ALL;

public class LoginActivity extends Activity {

    private EditText mTxtLogin;
    private EditText mTxtPassword;
    private TextView mTxtError;
    private AnimationButton mBtnEnter;
    private TransparentProgressDialog pdLoading;

    public static long sBackPressed;
    static final String STATE_LOADING = "loading";

    @InjectService
    WebAsync webAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AsyncService.inject(this);

        mTxtLogin = (EditText) findViewById(R.id.txtLoginName);
        mTxtPassword = (EditText) findViewById(R.id.txtLoginPassword);
        mBtnEnter = (AnimationButton) findViewById(R.id.btnLoginEnter);
        mTxtError = (TextView) findViewById(R.id.txtLoginError);
        pdLoading = new TransparentProgressDialog(
                LoginActivity.this, R.drawable.spinner);
        mBtnEnter.setOnClickListener(v -> Auth());
        mTxtLogin.addTextChangedListener(new GenericTextWatcher());
        mTxtPassword.addTextChangedListener(new GenericTextWatcher());

        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_LOADING, false)) {
            pdLoading.show();
            return;
        }
        // Загрузка сохраненных данных о пользователе
        // Если имеются - заходим, иначе форма ввода логина/пароля
        User user = Utils.GetUserInfo(this);
        if (user != null && !user.getLogin().isEmpty()
                && !user.getPassword().isEmpty()) {
            try {
                mTxtLogin.setText(user.getLogin());
                mTxtPassword.setText(user.getPassword());
                Auth();
            } catch (Exception e) {
                Utils.RemoveUserInfo(this);
                return;
            }
        } else {
            Utils.RemoveUserInfo(this);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(STATE_LOADING, pdLoading.isShowing());
        super.onSaveInstanceState(savedInstanceState);
    }

    // Активация кнопки "Войти"
    private boolean isBtnEnabled() {
        return !mTxtLogin.getText().toString().isEmpty()
                && !mTxtPassword.getText().toString().isEmpty();
    }

    // Перехват события изменения текста логина/пароля
    private class GenericTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mBtnEnter.setEnabled(isBtnEnabled());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    // Вывод ошибки
    private void showError(String text) {
        mTxtError.setVisibility(View.VISIBLE);
        mTxtError.setText(text);
    }

    // Асинхронная авторизация пользователя
    private void Auth() {
        mTxtError.setVisibility(View.GONE);
        pdLoading.show();
        User user = new User(mTxtLogin.getText().toString(), mTxtPassword
                .getText().toString());
        webAsync.SendRequest(user, MethodName.Auth, null);
    }

    @OnMessage(from = ALL)
    public void onRequestSuccess(WebAsync.RequestSuccess response) {
        if (LoginActivity.this == null)
            return;
        pdLoading.dismiss();
        Object res = response.getObject();
        if (res != null) {
            if (res instanceof User) {
                User user = (User) res;
                user.setPassword(mTxtPassword.getText().toString());
                // Сохранение данных о пользователе
                if (Utils.SaveUser(LoginActivity.this, user)) {
                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                    pdLoading.dismiss();
                    startActivity(intent);
                    finish();
                    return;
                } else
                    showError(getString(R.string.error_save_user));
            }
        } else {
            showError(getString(R.string.error_unknown));
        }
    }

    @OnMessage(from = ALL)
    public void onRequestError(WebAsync.RequestError requestError) {
        if (LoginActivity.this == null)
            return;
        pdLoading.dismiss();
        showError(requestError.getMessage());
    }

    @Override
    public void onDestroy() {
        AsyncService.unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (sBackPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else
            Toast.makeText(this, getString(R.string.press_back_for_exit),
                    Toast.LENGTH_SHORT).show();
        sBackPressed = System.currentTimeMillis();
    }

}
