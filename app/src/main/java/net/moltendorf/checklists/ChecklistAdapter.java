package net.moltendorf.checklists;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

/**
 * Created by moltendorf on 16/3/3.
 */
public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ChecklistItemHolder> {
	public static final String TAG = "ChecklistAdapter";

	WeakReference<ChecklistActivity> mActivity;

	DataModel.Checklist mChecklist;

	public ChecklistAdapter(ChecklistActivity activity, DataModel.Checklist checklist) {
		mActivity = new WeakReference<>(activity);

		mChecklist = checklist;
	}

	@Override
	public ChecklistItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ChecklistItemView recyclerListView = (ChecklistItemView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_checklist, parent, false);

		return new ChecklistItemHolder(recyclerListView);
	}

	@Override
	public void onBindViewHolder(ChecklistItemHolder holder, int position) {
		holder.bindTo(mActivity, mChecklist.getItem(position), position);
	}

	@Override
	public int getItemCount() {
		return mChecklist.size();
	}

	public static final class ChecklistItemHolder extends RecyclerView.ViewHolder {
		private ChecklistItemView checklistItemView;

		public ChecklistItemHolder(ChecklistItemView itemView) {
			super(itemView);

			checklistItemView = itemView;
		}

		public void bindTo(WeakReference<ChecklistActivity> activity, DataModel.Checklist.Item item, int position) {
			checklistItemView.bindTo(activity, item, position);
		}
	}
}
