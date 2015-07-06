package com.fincode.gitrepo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.Status;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.model.enums.MethodName;
import com.fincode.gitrepo.model.enums.StatusCode;
import com.fincode.gitrepo.service.WebAsync;
import com.fincode.gitrepo.ui.adapter.RepositoriesTableAdapter;
import com.fincode.gitrepo.utils.Utils;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static com.joanzapata.android.asyncservice.api.annotation.OnMessage.Sender.ALL;

// Фрагмент, отображающий список репозиториев
public class ReposFragment extends Fragment {

    private List<Repository> mRepositories;
    private Activity mActivity;

    private LinearLayout mLlLoading;
    private TextView mTxtLoading;
    private ProgressBar mPbLoading;
    private ImageView imgError;
    private ListView mLvRepositories;
    private FloatingActionButton mFabCreateRepo;


    @InjectService
    WebAsync webAsync;

    public static final String EXTRA_REPOSITORIES = "extra_repositories";

    public ReposFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        AsyncService.inject(this);

        View rootView = inflater.inflate(R.layout.fragment_repos, container,
                false);
        mActivity = getActivity();
        mActivity.setTitle(getString(R.string.repositories_list));
        mLlLoading = (LinearLayout) rootView
                .findViewById(R.id.llRepositoriesLoading);
        mPbLoading = (ProgressBar) rootView
                .findViewById(R.id.pbRepositoriesLoading);
        mTxtLoading = (TextView) rootView
                .findViewById(R.id.txtRepositoriesLoading);
        imgError = (ImageView) rootView.findViewById(R.id.imgError);

        // Инициализация списка репозиториев
        mLvRepositories = (ListView) rootView.findViewById(R.id.lvRepositories);
        mLvRepositories.setOnItemClickListener((AdapterView<?> parent, View view,
                                                int position, long id) -> {
            // Переход к списку коммитов репозитория
            Intent intent = new Intent(mActivity, CommitsActivity.class);
            Repository repo = mRepositories.get(position - 1);
            intent.putExtra(CommitsActivity.EXTRA_REPO_NAME, repo.getName());
            intent.putExtra(CommitsActivity.EXTRA_OWNER_LOGIN, repo
                    .getOwner().getLogin());
            startActivity(intent);
        });

        // Инициализация кнопки создания репозитория
        mFabCreateRepo = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFabCreateRepo.attachToListView(mLvRepositories);
        mFabCreateRepo.setOnClickListener(v -> openCreateDialog());
        initTableHeader();
        if (savedInstanceState == null) {
            Status status = new Status(StatusCode.LOADING,
                    getString(R.string.repositories_loading));
            updateGUI(status);
            refreshRepositories();
        } else {
            mRepositories = savedInstanceState
                    .getParcelableArrayList(EXTRA_REPOSITORIES);
            updateGUI(new Status(StatusCode.SUCCESS, null));
        }
        return rootView;
    }

    @Override
    public void onDestroy() {
        AsyncService.unregister(this);
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mRepositories != null)
            outState.putParcelableArrayList(EXTRA_REPOSITORIES,
                    new ArrayList<Repository>(mRepositories));
    }

    // Инициализация хэдера таблицы
    private void initTableHeader() {
        View header = mActivity.getLayoutInflater().inflate(
                R.layout.list_item_repositories, mLvRepositories, false);
        header.findViewById(R.id.imgRepositorieAvatar).setVisibility(View.GONE);
        TextView txtDescription = (TextView) header
                .findViewById(R.id.txtRepositorieDescription);
        TextView txtName = (TextView) header
                .findViewById(R.id.txtRepositorieName);
        TextView txtLogin = (TextView) header
                .findViewById(R.id.txtRepositorieLogin);
        TextView txtWatchers = (TextView) header
                .findViewById(R.id.txtRepositorieWatchers);
        TextView txtForks = (TextView) header
                .findViewById(R.id.txtRepositorieForks);

        txtName.setLayoutParams(new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, .30f));
        txtName.setText(getString(R.string.lbl_name));
        txtName.setBackgroundResource(R.drawable.bg_header);
        txtDescription.setText(getString(R.string.lbl_description));
        txtDescription.setBackgroundResource(R.drawable.bg_header);
        txtLogin.setText(getString(R.string.lbl_login));
        txtLogin.setBackgroundResource(R.drawable.bg_header);
        txtWatchers.setText(getString(R.string.lbl_watchers));
        txtWatchers.setBackgroundResource(R.drawable.bg_header);
        txtForks.setText(getString(R.string.lbl_forks));
        txtForks.setBackgroundResource(R.drawable.bg_header);
        mLvRepositories.addHeaderView(header, null, false);
    }

    // Обновление GUI
    public void updateGUI(Status status) {
        if (status.getCode() == StatusCode.SUCCESS) {
            mLlLoading.setVisibility(View.GONE);
            mLvRepositories.setVisibility(View.VISIBLE);
            refreshTable();
            return;
        }
        mLvRepositories.setVisibility(View.GONE);
        mLlLoading.setVisibility(View.VISIBLE);
        mTxtLoading.setText(status.getMessage().toString());
        mPbLoading
                .setVisibility(status.getCode() == StatusCode.LOADING ? View.VISIBLE
                        : View.GONE);
        imgError.setVisibility(status.getCode() == StatusCode.ERROR ? View.VISIBLE : View.GONE);
        mFabCreateRepo.setVisibility(View.VISIBLE);
    }

    // Инициализация таблицы репозиториев
    private boolean refreshTable() {
        if (mRepositories == null)
            return false;
        RepositoriesTableAdapter listAdapter = new RepositoriesTableAdapter(
                mActivity, mRepositories);
        mLvRepositories.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        return true;
    }

    @OnMessage(from = ALL)
    public void onRequestComplete(WebAsync.RequestSuccess response) {
        if (mActivity == null)
            return;
        Status status = new Status();
        status.setCode(StatusCode.SUCCESS);
        Object res = response.getObject();
        if (res != null) {
            // Получение репозиториев
            if (res instanceof ArrayList<?>) {
                List tmp = (ArrayList<?>) res;
                if (tmp.size() > 0 && tmp.get(0) instanceof Repository)
                    mRepositories = tmp;
            }
            // Создание репозитория
            else if (res instanceof Repository) {
                Toast.makeText(
                        mActivity,
                        getString(R.string.lbl_repo_create_success),
                        Toast.LENGTH_SHORT).show();
                refreshRepositories();
            }
        }
        // Неизвестная ошибка
        else {
            status.setCode(StatusCode.ERROR);
            status.setMessage(mActivity
                    .getString(R.string.error_unknown));
        }
        updateGUI(status);
    }

    @OnMessage
    public void onRequestError(WebAsync.RequestError requestError) {
        if (mActivity == null)
            return;
        updateGUI(new Status(StatusCode.ERROR, requestError.getMessage()));
    }

    // Асинхронная загрузка репозиториев
    private void refreshRepositories() {
        User user = Utils.GetUserInfo(mActivity);
        webAsync.SendRequest(user, MethodName.GetRepositories, null);
    }

    // Асинхронный запрос на создание репозитория
    private void createRepo(Repository repo) {
        User user = Utils.GetUserInfo(mActivity);
        webAsync.SendRequest(user, MethodName.CreateRepository, repo);
    }

    // Открытие диалога создания репозитория
    private void openCreateDialog() {
        boolean wrapInScrollView = true;

        MaterialDialog dialogCreate = new MaterialDialog.Builder(mActivity)
                .customView(R.layout.dialog_create_repo, wrapInScrollView)
                .backgroundColorRes(R.color.md_white)
                .positiveText(R.string.lbl_create)
                .neutralText(R.string.lbl_cancel)
                .title(R.string.lbl_create_repo)
                .titleColorRes(R.color.md_grey_800)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        // Создание репозитория
                        TextView txtName = (TextView) dialog.getCustomView()
                                .findViewById(R.id.txtCreateName);
                        TextView txtDescription = (TextView) dialog
                                .getCustomView().findViewById(
                                        R.id.txtCreateDescription);
                        String name = txtName.getText().toString();
                        String description = txtDescription.getText()
                                .toString();
                        createRepo(new Repository(name, description, null, "",
                                0, 0));
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        dialog.dismiss();
                    }
                }).build();
        TextView txtName = (TextView) dialogCreate.getCustomView()
                .findViewById(R.id.txtCreateName);

        final View btnDialogPositive = dialogCreate
                .getActionButton(DialogAction.POSITIVE);
        btnDialogPositive.setEnabled(false);

        // Enable/disable кнопки "Создать"
        txtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                btnDialogPositive.setEnabled(!s.toString().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        dialogCreate.show();
    }
}