package com.fincode.gitrepo.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.Status;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.model.enums.StatusCode;
import com.fincode.gitrepo.network.ServerCommunicator;
import com.fincode.gitrepo.ui.activity.CommitsActivity;
import com.fincode.gitrepo.ui.adapter.RepositoriesTableAdapter;
import com.fincode.gitrepo.utils.Utils;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.widget.WidgetObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.schedulers.Schedulers;

// Фрагмент, отображающий список репозиториев
public class ReposFragment extends Fragment {

    private List<Repository> mRepositories = new ArrayList<>();
    RepositoriesTableAdapter mReposAdapter;
    private Activity mActivity;

    private LinearLayout mLlLoading;
    private TextView mTxtLoading;
    private ProgressBar mPbLoading;
    private ImageView imgError;
    private ListView mLvRepositories;
    private FloatingActionButton mFabCreateRepo;


    public static final String EXTRA_REPOSITORIES = "extra_repositories";


    public ReposFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        mReposAdapter = new RepositoriesTableAdapter(
                mActivity, mRepositories);
        mLvRepositories.setAdapter(mReposAdapter);

        setRetainInstance(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null) {
            fetchRepositories();
        } else {
            refreshReposList(savedInstanceState
                    .getParcelableArrayList(EXTRA_REPOSITORIES));

        }
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
                R.layout.row_repositories, mLvRepositories, false);
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

    // Асинхронная загрузка репозиториев
    private void fetchRepositories() {
        Status status = new Status(StatusCode.LOADING,
                getString(R.string.repositories_loading));
        updateGUI(status);
        User user = Utils.GetUserInfo(mActivity);
        ServerCommunicator communicator = App.inst().getCommunicator();

        communicator.getRepos(user)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getReposSubscriber);
    }

    private Subscriber<List<Repository>> getReposSubscriber = new Subscriber<List<Repository>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            String message = Utils.getErrorMessage(e);
            updateGUI(new Status(StatusCode.ERROR, message));
        }

        @Override
        public void onNext(List<Repository> repositories) {
            refreshReposList(repositories);
        }
    };


    private void refreshReposList(List<Repository> repositories) {
        mRepositories.clear();
        mRepositories.addAll(repositories);
        mReposAdapter.notifyDataSetChanged();
        updateGUI(new Status(StatusCode.SUCCESS, null));
    }

    // Асинхронный запрос на создание репозитория
    private void createRepo(Repository repo) {
        User user = Utils.GetUserInfo(mActivity);
        ServerCommunicator communicator = App.inst().getCommunicator();
        //communicator.createRepo(user, repo)
        Observable.just(1)
                    .subscribeOn(Schedulers.newThread())
                .map(integer -> new Repository())
                .doOnNext(new Action1<Repository>() {
                    @Override
                    public void call(Repository repository) {
                        mRepositories.clear();
                        mReposAdapter.notifyDataSetChanged();
                    }
                })
                .flatMap(repository -> Observable.just(repository))
                .flatMap(repository1 -> communicator.getRepos(user))
                .flatMap(repos -> Observable.from(repos))
                .map(new Func1<Repository, Object>() {
                    @Override
                    public Object call(Repository repository) {
                        mRepositories.add(repository);
                        mReposAdapter.notifyDataSetChanged();
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Subscriber<Repository> createRepoSubscriber = new Subscriber<Repository>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            String message = Utils.getErrorMessage(e);
            updateGUI(new Status(StatusCode.ERROR, message));
        }

        @Override
        public void onNext(Repository repositorie) {
            Toast.makeText(
                    mActivity,
                    getString(R.string.lbl_repo_create_success),
                    Toast.LENGTH_SHORT).show();
            fetchRepositories();
        }
    };

    // Открытие диалога создания репозитория
    private void openCreateDialog() {

        MaterialDialog dialogCreate = new MaterialDialog.Builder(mActivity)
                .customView(R.layout.dialog_create_repo, true)
                .backgroundColorRes(R.color.white)
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
        WidgetObservable
                .text(txtName)
                .map(e -> e.text())
                .map(repoName -> !repoName.toString().isEmpty())
                .subscribe(enabled -> btnDialogPositive.setEnabled(enabled));
        dialogCreate.show();
    }
}