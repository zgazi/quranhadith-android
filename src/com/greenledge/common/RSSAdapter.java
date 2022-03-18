package com.greenledge.common;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenledge.common.DownloadBitmap;
import com.greenledge.quran.R;

public class RSSAdapter extends BaseAdapter {

	private List<RSSItem> items=null;
	private final Context context;
	//public ImageLoader imageLoader;
	private ArrayList<RSSItem> filterItems=null;

	public RSSAdapter(Context context, List<RSSItem> items)
	{
		this.items = items;
		this.context = context;
		this.filterItems = new ArrayList<RSSItem>();
		this.filterItems.addAll(items);
	}


	
	@Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public Object getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int id)
	{
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = new ViewHolder();

		if (convertView == null)
		{
			convertView = View.inflate(context, R.layout.list_item, null);

			// Set the item title
			viewHolder.itemTitle = (TextView) convertView.findViewById(R.id.itemTitle);
			viewHolder.itemDate = (TextView) convertView.findViewById(R.id.itemDate);
			viewHolder.itemImage = (ImageView) convertView.findViewById(R.id.itemImage);

	        convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.itemTitle.setText(items.get(position).getTitle());
		viewHolder.itemDate.setText(items.get(position).getDescription());
		try
		{
			viewHolder.itemImage.setTag(items.get(position).getImageUrl());
			new DownloadBitmap().execute(viewHolder.itemImage);
		}
		catch(Exception e){}


		return convertView;
	}

	// Holds the item title in a text view
	static class ViewHolder {
        ImageView itemImage;
		TextView itemTitle;
		TextView itemDate;
    }

	// Filter Class
	public void filter(String charText) {
		charText = charText.toLowerCase(Locale.getDefault());
		items.clear();
		if (charText.length() == 0) {
			items.addAll(filterItems);
		}
		else {
			for (RSSItem item : filterItems) {
				if (item.getTitle().toLowerCase(Locale.getDefault()).contains(charText)) {
					items.add(item);
				}
			}
		}
		notifyDataSetChanged();
	}

}
