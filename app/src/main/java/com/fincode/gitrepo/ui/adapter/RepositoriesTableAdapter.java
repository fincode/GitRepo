package com.fincode.gitrepo.ui.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fincode.gitrepo.App;
import com.fincode.gitrepo.R;
import com.fincode.gitrepo.ui.custom.CircleTransform;
import com.fincode.gitrepo.model.Repository;
import com.squareup.picasso.Picasso;

public class RepositoriesTableAdapter extends BaseAdapter {

    private final Activity mActivity;
    private List<Repository> mRepositories;
    private static LayoutInflater mInflater = null;

    static class ViewHolder {
        public ImageView imgAvatar;
        public TextView txtName;
        public TextView txtDescription;
        public TextView txtLogin;
        public TextView txtForksCount;
        public TextView txtWatchersCount;
    }

    public RepositoriesTableAdapter(Activity activity,
                                    List<Repository> repositories) {
        this.mActivity = activity;
        this.mRepositories = repositories;
        mInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view = convertView;
        if (view == null) {
            mInflater = mActivity.getLayoutInflater();
            view = mInflater.inflate(R.layout.row_repositories, parent,
                    false);
            holder = new ViewHolder();
            holder.imgAvatar = (ImageView) view
                    .findViewById(R.id.imgRepositorieAvatar);
            holder.txtName = (TextView) view
                    .findViewById(R.id.txtRepositorieName);
            holder.txtDescription = (TextView) view
                    .findViewById(R.id.txtRepositorieDescription);
            holder.txtLogin = (TextView) view
                    .findViewById(R.id.txtRepositorieLogin);
            holder.txtForksCount = (TextView) view
                    .findViewById(R.id.txtRepositorieForks);
            holder.txtWatchersCount = (TextView) view
                    .findViewById(R.id.txtRepositorieWatchers);

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Repository repository = mRepositories.get(position);
        if (repository != null) {
            holder.txtName.setText(repository.getName());
            holder.txtDescription.setText(repository.getDescription());
            holder.txtLogin.setText(repository.getOwner().getLogin());
            holder.txtForksCount.setText(String.valueOf(repository
                    .getForksCount()));
            holder.txtWatchersCount.setText(String.valueOf(repository
                    .getWatchersCount()));

            String url = repository.getOwner().getAvatar_url();
            if (url != null && !url.isEmpty())
                Picasso.with(App.inst()).load(url)
                        .error(R.drawable.no_image)
                        .placeholder(R.drawable.no_image)
                        .transform(new CircleTransform(1))
                        .into(holder.imgAvatar);

        }
        return view;
    }

    public int getCount() {
        return mRepositories.size();
    }

    public Repository getItem(int position) {
        return mRepositories.get(position);
    }

    public long getItemId(int p) {
        return p;
    }

}
