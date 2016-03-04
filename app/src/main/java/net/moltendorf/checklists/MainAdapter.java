package net.moltendorf.checklists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created by moltendorf on 16/3/2.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainItemHolder> {
	public static final String TAG = "MainAdapter";

	WeakReference<MainActivity> mActivity;

	DataModel mDataModel;

	public MainAdapter(MainActivity activity, DataModel dataModel) {
		mActivity = new WeakReference<>(activity);

		mDataModel = dataModel;
	}

	@Override
	public MainItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		MainItemView recyclerListView = (MainItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false);

		return new MainItemHolder(recyclerListView);
	}

	@Override
	public void onBindViewHolder(MainItemHolder holder, int position) {
		holder.bindTo(mActivity, mDataModel.getChecklistByPosition(position));
	}

	@Override
	public int getItemCount() {
		return mDataModel.size();
	}

	public static final class MainItemHolder extends RecyclerView.ViewHolder {
		private MainItemView mainItemView;

		public MainItemHolder(MainItemView itemView) {
			super(itemView);

			mainItemView = itemView;
		}

		public void bindTo(WeakReference<MainActivity> activity, DataModel.Checklist checklist) {
			mainItemView.bindTo(activity, checklist);
		}
	}
}
