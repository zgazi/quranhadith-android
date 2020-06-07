package com.greenledge.quran;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.greenledge.common.MainApplication;
import com.greenledge.common.Utils;

public class QuranSettingDialog extends DialogFragment 
implements android.view.View.OnClickListener, OnItemSelectedListener
{
	private MainApplication setting;
	private Context  context;
	private Boolean transliteration,translation,tafsir,comment;
	private int alert_before;
	private View view;
	
	public QuranSettingDialog(Context context)
	{
		this.context = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState)	{
	   super.onCreate(savedInstanceState);
	   //
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		if (view == null)
		{
	        view = inflater.inflate(R.layout.quransettingdialog, container, false);
	        this.reset(false);
	    }
		else
		{
	        ViewGroup parent = (ViewGroup) view.getParent();
	        parent.removeView(view);
	    }
	
		return view;
	}
	
	private void reset(boolean msg)	{
	   setting = MainApplication.getInstance();
	   List<String> methods = new ArrayList<String>(); 
		methods.add(context.getString(R.string.egypt_survey)); //EGYPT_SURVEY
		methods.add(context.getString(R.string.karachi_shaf)); //KARACHI_SHAF
		methods.add(context.getString(R.string.karachi_hanaf)); //KARACHI_HANAF
		methods.add(context.getString(R.string.north_america)); //NORTH_AMERICA
		methods.add(context.getString(R.string.muslim_league)); //MUSLIM_LEAGUE
		methods.add(context.getString(R.string.umm_alqurra)); //UMM_ALQURRA
		methods.add(context.getString(R.string.fixed_ishaa)); //FIXED_ISHAA
	
		Spinner spin = (Spinner) view.findViewById(R.id.set_cal_method);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, methods.toArray(new String[0])); 
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spin.setAdapter(adapter);      
		spin.setSelection(Integer.parseInt(setting.getMethod()));
		spin.setOnItemSelectedListener(this);
	
	   transliteration = setting.isSpkOn(1);
	   translation = setting.isSpkOn(4);
	   tafsir = setting.isSpkOn(8);
	   comment = setting.isSpkOn(16);

	   alert_before = Integer.parseInt(setting.getAlertOffset());
	   this.setNotification(true);
	   if (msg) 
	   	{
		   Toast.makeText(context, R.string.alert, Toast.LENGTH_SHORT).show();
	   	}
	
	}
	
	private void setSpk(int src,boolean active,boolean assign)	{
	ImageView img =	(ImageView) view.findViewById(src);
	img.setImageResource(active ? R.drawable.speaker_on : R.drawable.speaker_off);
	if (assign)
		img.setOnClickListener(this);
	}
	
	private void setNotification(boolean assign) {
	this.setSpk(R.id.set_transliteration, transliteration,assign);
	this.setSpk(R.id.set_translation, translation,assign);
	this.setSpk(R.id.set_tafsir, tafsir,assign);
	this.setSpk(R.id.set_comment, comment,assign);
	((EditText) view.findViewById(R.id.alert_before)).setText(""+alert_before);
	
	if (assign)
		{
		((Button)view.findViewById(R.id.set_notif_save)).setOnClickListener(this);
		((Button)view.findViewById(R.id.set_notif_reset)).setOnClickListener(this);
		}
	}
	
	private void save()	{
	int notif = 0;
	notif += transliteration ? 1 : 0;
	notif += translation ? 4: 0;
	notif += tafsir ? 8 : 0;
	notif += comment ? 16 : 0;

	setting.setAlert(notif);	
	setting.setAlertOffset( ( (EditText) view.findViewById(R.id.alert_before) ).getText().toString() );
	String[] offset = new String[6];
	offset[0] = ((TextView)view.findViewById(R.id.set_cal_fadjr)).getText().toString();
	offset[1] = ((TextView)view.findViewById(R.id.set_cal_shurooq)).getText().toString();
	offset[2] = ((TextView)view.findViewById(R.id.set_cal_zuhr)).getText().toString();
	offset[3] = ((TextView)view.findViewById(R.id.set_cal_asr)).getText().toString();
	offset[4] = ((TextView)view.findViewById(R.id.set_cal_maghrib)).getText().toString();
	offset[5] = ((TextView)view.findViewById(R.id.set_cal_isha)).getText().toString();
	setting.setTimeOffsets(offset);
	setting.savePreferences();
	getTargetFragment().onActivityResult(getTargetRequestCode(), 1, getActivity().getIntent().putExtra("listdata", offset));
	dismiss();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.set_transliteration: transliteration = !transliteration; break; 
		case R.id.set_translation: translation = !translation; break; 
		case R.id.set_tafsir: tafsir = !tafsir; break; 
		case R.id.set_comment: comment = !comment; break; 

		case R.id.set_notif_save : this.save(); break;
		case R.id.set_notif_reset : this.reset(true); break;
		}
		this.setNotification(false);
	}
	
	@Override
	public void onItemSelected(AdapterView<?> source, View view, int idx, long idx2) {
	switch (source.getId())
	{
	case R.id.set_cal_method:
		/*enable/disable area*/	
		setting.setMethod(idx);
		break;
	
	}	
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub
		
	}

}

