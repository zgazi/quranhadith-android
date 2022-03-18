package com.greenledge.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.os.ResultReceiver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Context;

import com.greenledge.quran.R;

/**
 * @author Rahat
 * date : 19-04-15
 * dialog for browsing folders and selecting file
 */
public class MyDialogFragment extends DialogFragment 
implements OnClickListener, OnItemClickListener, OnItemSelectedListener{

	private TextView textView;
	private ListView listView;
	
	private BaseAdapter adapter;
	private List <String> displayList;
	private Context context;
	
	OnItemChosenListener onItemChosenListener;
	ResultReceiver resultReceiver;
	Bundle bundle;
	String[] itemEntries, itemEntryValues;
	String[] returnValues = {}; // 3 ways to return (by Listener, by ResultReceiver Bundle, byTargetFragment
	public static final String RESULTS = "RESULTS";
	
	public MyDialogFragment(Context ctx, String[] list, String[] values) {

		this.itemEntries=list;
		this.itemEntryValues = values;
		this.returnValues = new String[itemEntries.length];
		this.context = ctx; //getParentFragment().getActivity().getApplicationContext();
		this.bundle = new Bundle();
	}
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

	    bundle = savedInstanceState;
		
		final Dialog dialog = new Dialog(getActivity());

		dialog.setContentView(R.layout.dialogfragment);
				
		dialog.setTitle(R.string.select);
		dialog.setCancelable(true);

		Button buttonBack = (Button) dialog.findViewById(R.id.middleButton);
		buttonBack.setOnClickListener(this);

		Button buttonCancel = (Button) dialog.findViewById(R.id.leftButton);
		buttonCancel.setOnClickListener(this);
		
		final LayoutInflater layoutInflater = getActivity().getLayoutInflater();

		//textView = (TextView) dialog.findViewById(R.id.text1);
		LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.layout);
		
		listView = new ListView(context);

		displayList = new ArrayList<String>(); //Arrays.asList(this.itemEntries);
		
		// adapter for listview
		adapter = new BaseAdapter() {

			@SuppressLint("InflateParams")
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				if (view == null) {
					view = layoutInflater.inflate(android.R.layout.activity_list_item, null);
				}
				TextView textView = (TextView) view.findViewById(android.R.id.text1);

				textView.setText(displayList.get(position));
				
				ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
				
				if (displayList.get(position).isEmpty())
					imageView.setImageResource(android.R.drawable.list_selector_background);
				else
					//img.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.my_image));
					imageView.setImageResource(getResources().getIdentifier(itemEntries[position] , "drawable", context.getPackageName()));
				return view;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return displayList.get(position);
			}

			@Override
			public int getCount() {
				return displayList.size();
			}
		};

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		layout.addView(listView);
		
		updatedisplayList();
		return dialog;

	}

	@Override
	public void onItemClick(AdapterView<?> av, View view,	int position, long arg3) {
		String itemName = av.getAdapter().getItem(position).toString();
		if (itemName.isEmpty()) {
			updatedisplayList();
		} else {
	        for (int i = 0; i < itemEntries.length; i++) {
	            if (itemEntries[i].equalsIgnoreCase(itemName)) {
	            	returnValues[0] = (String) itemEntryValues[i];
	            }
	        }
	        if (onItemChosenListener != null) onItemChosenListener.onItemChosen(displayList.get(position));
			getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent().putExtra("RESULTS", returnValues));
			this.dismiss();
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> source, View view, int idx, long idx2) {
	switch (source.getId())	{
		case R.id.set_cal_method:
			/*enable/disable area*/	
			//this.setMethod(idx);
			break;
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.middleButton:
			updatedisplayList();
		case R.id.rightButton:
		case R.id.leftButton:
		    resultReceiver = bundle.getParcelable("receiver");
			bundle.putStringArray(RESULTS, returnValues);
			resultReceiver.send(1, bundle);
			this.dismiss();
		}
	}
    
	public static interface OnItemChosenListener {
		public void onItemChosen(String item);
	}

	public void setOnItemChosenListener(OnItemChosenListener listener) {
		this.onItemChosenListener = listener;
	}

	private void updatedisplayList() {

		//textView.setText(parentDir.getAbsolutePath());

		displayList.clear();

		if (itemEntries != null) {
			for (int i = 0; i < itemEntries.length; i++) {
					displayList.add(itemEntries[i]);
			}
			
			Collections.sort(displayList,new Comparator<String>() {

				@Override
				public int compare(String lhs, String rhs) {
					return lhs.toLowerCase(Locale.getDefault())
							.compareTo(rhs.toLowerCase(Locale.getDefault()));
				}
			});
		} /*else {
			Toast.makeText(getActivity(), "No Appropriate file found",
					Toast.LENGTH_SHORT).show();
		}*/

		adapter.notifyDataSetChanged();
	}
	
	boolean isAcceptedFormat(String fileName){
		for(int i=0;i<itemEntries.length;i++){
			if(fileName.endsWith(itemEntries[i])){
				return true;
			}
		}
		return false;
	}
}

