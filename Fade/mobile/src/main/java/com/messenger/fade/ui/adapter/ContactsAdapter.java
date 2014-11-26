//package com.messenger.fade.ui.adapter;
//
//import android.app.Activity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.*;
//
//import com.messenger.fade.R;
//import com.messenger.fade.ui.model.HeaderItem;
//import com.messenger.fade.ui.model.ItemType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ContactsAdapter extends BaseAdapter implements Filterable, SectionIndexer {
//	private static final class ViewHolder {
//		private TextView txtTitle;
//		private TextView txtHandle;
//		private ImageView imgPhoto;
//		private ImageView imgJustMeUser;
//	}
//
//	private Activity activity;
//	private HeaderItem[] sectionArr;
//	private List<ItemType> itemList;
//	private List<ItemType> currentItemList;
//	private LayoutInflater inflater;
//
//	public ContactsAdapter(Activity activity) {
//		inflater = LayoutInflater.from(activity);
//		this.itemList = new ArrayList<ItemType>();
//		this.currentItemList = new ArrayList<ItemType>();
//		this.activity = activity;
//	}
//
//	public void updateList(List<ItemType> contactLetterList) {
//		this.itemList.clear();
//		this.itemList.addAll(contactLetterList);
//		updateCurrentItemList(contactLetterList);
//	}
//
//	public List<ItemType> getItemList() {
//		return itemList;
//	}
//
//	public List<ItemType> getCurrentItemList() {
//		return itemList;
//	}
//
//	public void updateCurrentItemList(List<ItemType> contactList) {
//		if (currentItemList == null) {
//			currentItemList = new ArrayList<ItemType>();
//		}
//
//		currentItemList.clear();
//		currentItemList.addAll(contactList);
//
//		List<HeaderItem> headerItemList = new ArrayList<HeaderItem>();
//		for (int i = 0; i < contactList.size(); i++) {
//			ItemType itemType = contactList.get(i);
//			if (itemType.getType() == ItemType.TYPE_HEADER) {
//				HeaderItem headerItem = (HeaderItem) itemType;
//				headerItemList.add(headerItem);
//			}
//		}
//
//		sectionArr = new HeaderItem[headerItemList.size()];
//
//		for (int i = 0; i < headerItemList.size(); i++) {
//			sectionArr[i] = headerItemList.get(i);
//		}
//
//		notifyDataSetChanged();
//	}
//
//	public View getChildView(int position, View convertView) {
//		View resultView = convertView;
//		ViewHolder holder;
//		if (resultView == null) {
//			resultView = inflater.inflate(R.layout.participants_row, null);
//			holder = new ViewHolder();
//			holder.txtTitle = (TextView) resultView.findViewById(R.id.follow_name);
//			holder.txtHandle = (TextView) resultView.findViewById(R.id.txtHandle);
//			holder.imgPhoto = (ImageView) resultView.findViewById(R.id.follow_avatar);
//			holder.imgJustMeUser = (ImageView) resultView.findViewById(R.id.imgJustMeUser);
//			resultView.setTag(holder);
//		} else {
//			holder = (ViewHolder) resultView.getTag();
//		}
//
//		final Contact contact = (Contact) getItem(position);
//
//		holder.imgPhoto.setImageResource(R.drawable.default_avatar);
//		holder.txtTitle.setText(contact.getDisplayName());
//
//		if (contact.getJustMeUserList().size() > 0) {
//			holder.imgJustMeUser.setVisibility(View.VISIBLE);
//			holder.txtHandle.setVisibility(View.VISIBLE);
//			holder.txtHandle.setText("*" + contact.getJustMeUserList().get(0).getHandle());
//		} else {
//			holder.txtHandle.setVisibility(View.GONE);
//			holder.imgJustMeUser.setVisibility(View.GONE);
//			holder.imgPhoto.setImageResource(R.drawable.default_avatar);
//			JustMeLoader.load(activity, contact.getNativeId(), holder.imgPhoto, R.drawable.default_avatar);
//		}
//
//		return resultView;
//	}
//
//	public View getGroupView(int position, View theConvertView) {
//		View resultView = theConvertView;
//		ViewHolder holder;
//
//		if (resultView == null) {
//			resultView = inflater.inflate(R.layout.contacts_header, null);
//			holder = new ViewHolder();
//			holder.txtTitle = (TextView) resultView.findViewById(R.id.txtTitle);
//			resultView.setTag(holder);
//		} else {
//			holder = (ViewHolder) resultView.getTag();
//		}
//
//		final HeaderItem item = (HeaderItem) getItem(position);
//		holder.txtTitle.setText(item.getTitle());
//
//		return resultView;
//	}
//
//	@Override
//	public int getCount() {
//		return currentItemList.size();
//	}
//
//	@Override
//	public ItemType getItem(int position) {
//		return currentItemList.get(position);
//	}
//
//	@Override
//	public long getItemId(int position) {
//		return position;
//	}
//
//	@Override
//	public int getViewTypeCount() {
//		return 2;
//	}
//
//	@Override
//	public int getItemViewType(int position) {
//		ItemType item = currentItemList.get(position);
//		return item.getType();
//	}
//
//	@Override
//	public boolean hasStableIds() {
//		return true;
//	}
//
//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		View view = null;
//		if (getItemViewType(position) == ItemType.TYPE_HEADER) {
//			view = getGroupView(position, convertView);
//		} else if (getItemViewType(position) == ItemType.TYPE_SECTION) {
//			view = getChildView(position, convertView);
//		}
//
//		return view;
//	}
//
//	/*
//	 * SectionIndexer begin
//	 */
//
//	@Override
//	public Object[] getSections() {
//		return sectionArr;
//	}
//
//	@Override
//	public int getPositionForSection(int section) {
//		section = section >= sectionArr.length ? 0 : section;
//		int index = 0;
//		HeaderItem headerItem = sectionArr[section];
//
//		List<ItemType> suggestList = getCurrentItemList();
//		for (int i = 0; i < suggestList.size(); i++) {
//			ItemType itemType = suggestList.get(i);
//			if (itemType.getType() == ItemType.TYPE_HEADER) {
//				HeaderItem c = (HeaderItem) itemType;
//				if (c.getTitle().equals(headerItem.getTitle())) {
//					index = i;
//					break;
//				}
//			}
//		}
//		return index;
//	}
//
//	@Override
//	public int getSectionForPosition(int position) {
//		return 0;
//	}
//
//	/*
//	 * SectionIndexer end
//	 */
//
//	/*
//	 * Filter begin
//	 */
//
//	@Override
//	public Filter getFilter() {
//		return filter;
//	}
//
//	// TODO maybe we should do detailed filtering in separate task
//	private Filter filter = new Filter() {
//		@Override
//		protected FilterResults performFiltering(CharSequence constraint) {
//			FilterResults filterResults = new FilterResults();
//
//			if (constraint == null) {
//				return filterResults;
//			}
//			ArrayList<ItemType> resultList = new ArrayList<ItemType>();
//			if (itemList != null && itemList.size() > 0) {
//				for (ItemType item : itemList) {
//					if (item.getType() == ItemType.TYPE_HEADER) {
//						resultList.add(item);
//					} else if (item.getType() == ItemType.TYPE_SECTION) {
//						Contact contact = (Contact) item;
//						if (match(constraint.toString().trim(), contact.getDisplayName())) { // TODO
//																								// improve
//																								// filter
//							resultList.add(item);
//						}
//					}
//				}
//
//				filterResults.values = resultList;
//				filterResults.count = resultList.size();
//			}
//
//			return filterResults;
//		}
//
//		private boolean match(String constraint, String name) {
//			return name.toLowerCase().contains(constraint.toLowerCase());
//		}
//
//		@Override
//		protected void publishResults(CharSequence constraint, FilterResults results) {
//			if (results == null) {
//				return;
//			}
//
//			ArrayList<ItemType> filteredList = (ArrayList<ItemType>) results.values;
//			updateCurrentItemList(filteredList);
//		}
//	};
//
//	/*
//	 * Filter end
//	 */
//}
