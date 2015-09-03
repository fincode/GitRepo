package com.fincode.gitrepo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.network.ServerCommunicator;
import com.fincode.gitrepo.ui.custom.AnimationButton;
import com.fincode.gitrepo.ui.custom.TransparentProgressDialog;
import com.fincode.gitrepo.utils.Utils;

import rx.Observable;
import rx.Subscriber;
import rx.android.widget.WidgetObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends Activity {

    private EditText mTxtLogin;
    private EditText mTxtPassword;
    private TextView mTxtError;
    private AnimationButton mBtnEnter;
    private TransparentProgressDialog pdLoading;

    public static long sBackPressed;
    static final String STATE_LOADING = "loading";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //AsyncService.inject(this);

        mTxtLogin = (EditText) findViewById(R.id.et_login_name);
        mTxtPassword = (EditText) findViewById(R.id.et_login_password);
        mBtnEnter = (AnimationButton) findViewById(R.id.btn_login_enter);
        mTxtError = (TextView) findViewById(R.id.txtLoginError);
        pdLoading = new TransparentProgressDialog(
                LoginActivity.this, R.drawable.spinner);
        mBtnEnter.setOnClickListener(v -> Auth());
        mBtnEnter.setEnabled(false);

        Observable<CharSequence> mLoginObs = WidgetObservable.text(mTxtLogin).map(e -> e.text());
        Observable<CharSequence> mPassObs = WidgetObservable.text(mTxtPassword).map(e -> e.text());

        Observable<Boolean> registerEnabled =
                Observable.combineLatest(mLoginObs.map(t -> !t.toString().isEmpty()), mPassObs.map(t -> !t.toString().isEmpty()), (a, b) -> a && b);
        registerEnabled.subscribe(enabled -> mBtnEnter.setEnabled(enabled));

        if (savedInstanceState != null && savedInstanceState.getBoolean(STATE_LOADING, false)) {
            if (!pdLoading.isShowing())
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

        ServerCommunicator communicator = App.inst().getCommunicator();
        Observable<User> mObservableRequest =  Observable.just("")
                .subscribeOn(Schedulers.newThread())
                .flatMap(empty -> communicator.auth(user))
                .observeOn(AndroidSchedulers.mainThread());

        mObservableRequest.subscribe(new Subscriber<User>() {
                                         @Override
                                         public void onCompleted() {
                                         }

                                         @Override
                                         public void onError(Throwable e) {
                                             if (pdLoading.isShowing())
                                                 pdLoading.dismiss();
                                             String message = Utils.getErrorMessage(e);
                                             showError(message);
                                         }

                                         @Override
                                         public void onNext(User user) {
                                             loginSuccess(user);
                                         }
                                     }
        );

    }

    private void loginSuccess(User user) {
        pdLoading.dismiss();
        user.setPassword(mTxtPassword.getText().toString());
        // Сохранение данных о пользователе
        if (Utils.SaveUser(LoginActivity.this, user)) {
            pdLoading.dismiss();
            MainActivity.startActivity(this);
            finish();
            return;
        } else
            showError(getString(R.string.error_save_user));
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroy() {
        if (pdLoading.isShowing())
            pdLoading.dismiss();
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
