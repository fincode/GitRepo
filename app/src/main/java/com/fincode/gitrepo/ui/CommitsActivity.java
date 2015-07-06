package com.fincode.gitrepo.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.Commit;
import com.fincode.gitrepo.model.Repository;
import com.fincode.gitrepo.model.Status;
import com.fincode.gitrepo.model.User;
import com.fincode.gitrepo.model.enums.MethodName;
import com.fincode.gitrepo.model.enums.StatusCode;
import com.fincode.gitrepo.service.WebAsync;
import com.fincode.gitrepo.ui.adapter.CommitsTableAdapter;
import com.fincode.gitrepo.utils.Utils;
import com.joanzapata.android.asyncservice.api.annotation.InjectService;
import com.joanzapata.android.asyncservice.api.annotation.OnMessage;
import com.joanzapata.android.asyncservice.api.internal.AsyncService;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CommitsActivity extends Activity {

    public static final String EXTRA_REPO_NAME = "repo_name";
    public static final String EXTRA_OWNER_LOGIN = "owner_login";

    private List<Commit> mCommits;
    private ListView mLvCommits;
    private LinearLayout mLlLoading;
    private TextView mTxtLoading;
    private ProgressBar mPbLoading;
    private FloatingActionButton mFabClose;

    @InjectService
    WebAsync webAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_repos);
        AsyncService.inject(this);

        mLvCommits = (ListView) findViewById(R.id.lvRepositories);
        mLlLoading = (LinearLayout) findViewById(R.id.llRepositoriesLoading);
        mPbLoading = (ProgressBar) findViewById(R.id.pbRepositoriesLoading);
        mTxtLoading = (TextView) findViewById(R.id.txtRepositoriesLoading);
        mFabClose = (FloatingActionButton) findViewById(R.id.fab);
        mFabClose.setColorNormalResId(R.color.md_red_700);
        mFabClose.setColorPressedResId(R.color.md_red_a700);
        mFabClose.setImageResource(R.drawable.btn_close);
        mFabClose.setOnClickListener(v -> finish());
        // Извлечение данных о репозитории
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String repoName = bundle.getString(EXTRA_REPO_NAME);
            String ownerLogin = bundle.getString(EXTRA_OWNER_LOGIN);
            User user = Utils.GetUserInfo(this);
            if (!user.getLogin().isEmpty() && !user.getPassword().isEmpty()
                    && !repoName.isEmpty() && !ownerLogin.isEmpty()) {
                // Получение коммитов
                Repository tmpRepo = new Repository(repoName, "", new User(ownerLogin, ""), "", 0,0);
                fetchCommits(user, tmpRepo);
                return;
            }
        }
        updateGUI(new Status(StatusCode.ERROR,
                getString(R.string.error_internal_bad_data)));

    }

    // Инициализация заголовка таблицы
    private void initTableHeader() {
        View header = getLayoutInflater().inflate(
                R.layout.list_item_repositories, mLvCommits, false);
        ImageView img = (ImageView) header
                .findViewById(R.id.imgRepositorieAvatar);
        img.setVisibility(View.GONE);
        TextView txtMessage = (TextView) header
                .findViewById(R.id.txtRepositorieDescription);
        TextView txtHash = (TextView) header
                .findViewById(R.id.txtRepositorieName);
        TextView txtAuthor = (TextView) header
                .findViewById(R.id.txtRepositorieLogin);
        TextView txtDate = (TextView) header
                .findViewById(R.id.txtRepositorieWatchers);
        header.findViewById(R.id.txtRepositorieForks).setVisibility(View.GONE);

        txtHash.setLayoutParams(new LinearLayout.LayoutParams(0,
                LayoutParams.MATCH_PARENT, .30f));
        txtHash.setText(getString(R.string.lbl_hash));
        txtHash.setBackgroundResource(R.drawable.bg_header);
        txtMessage.setText(getString(R.string.lbl_commit));
        txtMessage.setBackgroundResource(R.drawable.bg_header);
        txtAuthor.setText(getString(R.string.lbl_author));
        txtAuthor.setBackgroundResource(R.drawable.bg_header);
        txtDate.setText(getString(R.string.lbl_date));
        txtDate.setBackgroundResource(R.drawable.bg_header);
        mLvCommits.addHeaderView(header, null, false);
    }

    @OnMessage
    public void onRequestSuccess(WebAsync.RequestSuccess response) {
        if (CommitsActivity.this == null)
            return;
        Status status = new Status();
        status.setCode(StatusCode.SUCCESS);
        Object res = response.getObject();
        if (res != null) {
            // Ответ является списком коммитов
            if (res instanceof ArrayList<?>) {
                List tmp = (ArrayList<?>) res;
                if (tmp.size() > 0 && tmp.get(0) instanceof Commit)
                    mCommits = tmp;
            }
        }
        // Неизвестная ошибка
        else {
            status.setCode(StatusCode.ERROR);
            status.setMessage(getString(R.string.error_unknown));
        }
        updateGUI(status);
    }

    @OnMessage
    public void onRequestError(WebAsync.RequestError requestError) {
        if (CommitsActivity.this == null)
            return;
        updateGUI(new Status(StatusCode.ERROR, requestError.getMessage()));
    }

    @Override
    public void onDestroy() {
        AsyncService.unregister(this);
        super.onDestroy();
    }

    // Асинхронный вызов получения коммитов
    private void fetchCommits(User user, Repository repo) {
        Status status = new Status(StatusCode.LOADING,
                getString(R.string.lbl_commits_loading));
        updateGUI(status);
        webAsync.SendRequest(user, MethodName.GetCommits, repo);
    }

    // Обновление GUI
    public void updateGUI(Status status) {
        if (status.getCode() == StatusCode.SUCCESS) {
            mLlLoading.setVisibility(View.GONE);
            mLvCommits.setVisibility(View.VISIBLE);
            initTableHeader();
            refreshTable();
            return;
        }
        mLvCommits.setVisibility(View.GONE);
        mLlLoading.setVisibility(View.VISIBLE);
        mTxtLoading.setText(status.getMessage().toString());
        mPbLoading
                .setVisibility(status.getCode() == StatusCode.LOADING ? View.VISIBLE
                        : View.GONE);
    }

    // Инициализация таблицы
    private boolean refreshTable() {
        if (mCommits == null)
            return false;
        CommitsTableAdapter listAdapter = new CommitsTableAdapter(this,
                mCommits);
        mLvCommits.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        return true;
    }
}
