package com.greenledge.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.greenledge.quran.Ayah;
import com.greenledge.quran.R;

/**
 * @author Rahat
 * date : 19-04-15
 * dialog for browsing folders and selecting file
 */
public class AlertDialogFragment extends DialogFragment 
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
	
	public AlertDialogFragment(Context ctx, String[] list, String[] values) {

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
		
		AlertDialog.Builder textSelectorBuilder = new AlertDialog.Builder(context);

		final int sid = 1; //Integer.parseInt(sharedPrefs.getString("prefStartupSura", "0"));

		//final SharedPreferences.Editor editor = sharedPrefs.edit();

		// one int element array to assign value from inner class

		textSelectorBuilder.setTitle(R.string.select);
		
		String allItems[]= itemEntries; //getResources().getStringArray(R.array.suraNames);
			
		textSelectorBuilder.setSingleChoiceItems(allItems,
				sid, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int whichIndex) {
						//editor.putString("prefStartupSura",	Integer.toString(whichIndex));
						//editor.commit();
						dialog.dismiss();
					}
				});

		textSelectorBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		
		
		AlertDialog dialog=textSelectorBuilder.create();
		
		dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				dialog.dismiss();
			}
		});
		
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


