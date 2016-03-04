package net.moltendorf.checklists;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moltendorf on 16/3/3.
 */
public class DataModel implements Iterable {
	public static final String TAG = "DataModel";

	private static DataModel sInstance;

	protected static DataModel getInstance() {
		if (sInstance != null) {
			return sInstance;
		}

		return sInstance = new DataModel();
	}

	private File mFile   = null;
	private int  mNextId = 0;

	Map<Integer, Checklist> mChecklists = new LinkedHashMap<>();

	private DataModel() {
		// Do something?
	}

	public boolean isLoaded() {
		return mFile != null;
	}

	public Checklist getNewChecklist() {
		Checklist checklist = new Checklist(mNextId++);
		mChecklists.put(checklist.getId(), checklist);

		save();

		return checklist;
	}

	public Checklist getChecklist(int id) {
		return mChecklists.get(id);
	}

	public Checklist getChecklistByPosition(int position) {
		if (position >= 0 && position < mChecklists.size()) {
			return mChecklists.get(mChecklists.keySet().toArray()[position]);
		}

		return null;
	}

	public void deleteChecklist(int id) {
		// Call clear on removed checklist to break circular references.
		mChecklists.remove(id).mItems.clear();

		save();
	}

	public int size() {
		return mChecklists.size();
	}

	void setFile(File file) {
		if (mFile == null) {
			mFile = file;

			if (mFile != null) {
				load();
			}
		}
	}

	private void load() {
		if (mFile.exists()) {
			try {
				FileInputStream input = new FileInputStream(mFile);

				try {
					byte[] bytes = new byte[(int) mFile.length()];
					input.read(bytes);

					JSONObject rootObject;

					try {
						rootObject = new JSONObject(new String(bytes));
					} catch (JSONException exception) {
						rootObject = new JSONObject();
					}

					mNextId = rootObject.optInt("nextId");

					JSONObject checklistsObject = rootObject.optJSONObject("checklists");
					Iterator<String> checklistKeys = checklistsObject.keys();

					while (checklistKeys.hasNext()) {
						String key = checklistKeys.next();
						int id = Integer.parseInt(key);

						mChecklists.put(id, new Checklist(id, checklistsObject.optJSONObject(key)));
					}
				} catch (IOException exception) {
					// This shouldn't happen.
				}
			} catch (FileNotFoundException exception) {
				// This shouldn't happen.
			}
		}
	}

	private void save() {
		try {
			FileOutputStream output = new FileOutputStream(mFile);

			try {
				String json = toJSON().toString();

				Log.d(TAG, "save: " + json);

				output.write(json.getBytes());
			} catch (IOException | JSONException exception) {
				// This shouldn't happen.
			}

			try {
				output.close();
			} catch (IOException exception) {
				// This shouldn't happen.
			}
		} catch (FileNotFoundException exception) {
			// This shouldn't happen.
		}
	}

	private JSONObject toJSON() throws JSONException {
		JSONObject rootObject = new JSONObject();
		rootObject.put("nextId", mNextId);

		JSONObject checklistsObject = new JSONObject();

		for (Checklist checklist : mChecklists.values()) {
			checklistsObject.put(Integer.toString(checklist.getId()), checklist.toJSON());
		}

		rootObject.put("checklists", checklistsObject);

		return rootObject;
	}

	@Override
	public Iterator iterator() {
		return Collections.unmodifiableMap(mChecklists).entrySet().iterator();
	}

	public static class Checklist implements Iterable {
		private final int    mId;
		private       String mTitle;

		private List<Item> mItems = new ArrayList<>();

		private Checklist(int id) {
			mId = id;
			mTitle = "";
		}

		private Checklist(int id, JSONObject jsonObject) {
			mId = id;
			mTitle = jsonObject.optString("title");

			JSONArray jsonArray = jsonObject.optJSONArray("items");

			for (int i = 0, j = jsonArray.length(); i < j; ++i) {
				mItems.add(new Item(this, jsonArray.optJSONObject(i)));
			}
		}

		public int getId() {
			return mId;
		}

		public String getTitle() {
			return mTitle;
		}

		public void setTitle(String name) {
			mTitle = name;

			getInstance().save();
		}

		public int size() {
			return mItems.size();
		}

		public Item getNewItem() {
			Item item = new Item(this);
			mItems.add(item);

			getInstance().save();

			return item;
		}

		public Item getItem(int i) {
			return mItems.get(i);
		}

		public void deleteItem(int i) {
			mItems.remove(i);

			getInstance().save();
		}

		private JSONObject toJSON() throws JSONException {
			JSONObject checklistObject = new JSONObject();
			checklistObject.put("title", mTitle);

			JSONArray itemsArray = new JSONArray();

			for (Item item : mItems) {
				itemsArray.put(item.toJSON());
			}

			checklistObject.put("items", itemsArray);

			return checklistObject;
		}

		@Override
		public Iterator iterator() {
			return Collections.unmodifiableList(mItems).iterator();
		}

		public static class Item {
			private final Checklist mChecklist;

			private boolean mAutoFocus;

			private String  mText;
			private boolean mChecked;

			private Item(Checklist checklist) {
				mChecklist = checklist;

				mAutoFocus = true;

				mText = "";
				mChecked = false;
			}

			private Item(Checklist checklist, JSONObject jsonObject) {
				mChecklist = checklist;

				mAutoFocus = false;

				mText = jsonObject.optString("text");
				mChecked = jsonObject.optBoolean("checked");
			}

			public String getText() {
				return mText;
			}

			public void setText(String text) {
				mText = text;

				getInstance().save();
			}

			public boolean getChecked() {
				return mChecked;
			}

			public void setChecked(boolean checked) {
				mChecked = checked;

				getInstance().save();
			}

			public Checklist getChecklist() {
				return mChecklist;
			}

			public boolean getAutoFocus() {
				return mAutoFocus;
			}

			public void setAutoFocus(boolean autoFocus) {
				mAutoFocus = autoFocus;
			}

			private JSONObject toJSON() throws JSONException {
				JSONObject itemObject = new JSONObject();
				itemObject.put("text", mText);
				itemObject.put("checked", mChecked);

				return itemObject;
			}
		}
	}
}
