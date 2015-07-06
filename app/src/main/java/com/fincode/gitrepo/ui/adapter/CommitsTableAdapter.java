package com.fincode.gitrepo.ui.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.fincode.gitrepo.R;
import com.fincode.gitrepo.model.Commit;
import com.fincode.gitrepo.model.CommitInfo;
import com.fincode.gitrepo.utils.ISO8601;

public class CommitsTableAdapter extends BaseAdapter {

	private final Activity mActivity;
	private List<Commit> mCommits;

	static class ViewHolder {
		public TextView txtHash;
		public TextView txtMessage;
		public TextView txtAuthor;
		public TextView txtDate;
	}

	public CommitsTableAdapter(Activity activity, List<Commit> commits) {
		this.mActivity = activity;
		this.mCommits = commits;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = mActivity.getLayoutInflater();
			view = inflater.inflate(R.layout.list_item_repositories, parent,
					false);
			holder = new ViewHolder();
			view.findViewById(R.id.imgRepositorieAvatar).setVisibility(
					View.GONE);
			holder.txtHash = (TextView) view
					.findViewById(R.id.txtRepositorieName);
			holder.txtMessage = (TextView) view
					.findViewById(R.id.txtRepositorieDescription);
			holder.txtAuthor = (TextView) view
					.findViewById(R.id.txtRepositorieLogin);
			holder.txtDate = (TextView) view
					.findViewById(R.id.txtRepositorieWatchers);
			view.findViewById(R.id.txtRepositorieForks)
					.setVisibility(View.GONE);
			holder.txtHash.setLayoutParams(new LinearLayout.LayoutParams(0,
					LayoutParams.MATCH_PARENT, 0.3f));

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Commit commit = mCommits.get(position);
		if (commit != null) {
			CommitInfo info = commit.getInfo();
			if (info == null) {
				return view;
			}
			holder.txtHash.setText(commit.getSha());
			holder.txtMessage.setText(info.getMessage());
			holder.txtAuthor.setText(info.getAuthor().getName());
			String date = info.getAuthor().getDate();
			try {
				DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd",
						Locale.ENGLISH);
				String formattedDate = targetFormat.format(ISO8601.toCalendar(
						date).getTime());
				holder.txtDate.setText(formattedDate);
			} catch (ParseException e) {
				holder.txtDate.setText(date);
			}

		}
		return view;
	}

	public int getCount() {
		return mCommits.size();
	}

	public Commit getItem(int position) {
		return mCommits.get(position);
	}

	public long getItemId(int p) {
		return p;
	}

}
