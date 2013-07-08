package com.arcmobileapp.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.R;
import com.arcmobileapp.activities.Funds;
import com.arcmobileapp.activities.Home;
import com.arcmobileapp.activities.Social;
import com.arcmobileapp.activities.Support;
import com.arcmobileapp.utils.Enums.ModernPicTypes;
import com.arcmobileapp.utils.Utils;

public class MenuListFragment extends ListFragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		MenuAdapter adapter = new MenuAdapter(getActivity());
		adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.World), "Home"));
		adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Dollar), "Funds"));
		adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Info), "About"));
		//adapter.add(new MenuItem(Utils.convertModernPicType(ModernPicTypes.Girl), "Social"));
		setListAdapter(adapter);
	}

//	private class SampleItem {
//		public String tag;
//		public int iconRes;
//		public SampleItem(String tag, int iconRes) {
//			this.tag = tag; 
//			this.iconRes = iconRes;
//		}
//	}
	
	private class MenuItem {
		public String icon;
		public String text;
		public MenuItem(String icon, String text) {
			this.icon = icon; 
			this.text = text;
		}
	}

	public class MenuAdapter extends ArrayAdapter<MenuItem> {

		public MenuAdapter(Context context) {
			super(context, 0);
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_row, null);
			}
//			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
//			icon.setImageResource(getItem(position).iconRes);
			
			TextView icon = (TextView) convertView.findViewById(R.id.row_icon);
			icon.setText(getItem(position).icon);
			icon.setTextSize(75);
			icon.setTypeface(ArcMobileApp.getModernPicsTypeface());
			TextView title = (TextView) convertView.findViewById(R.id.row_title);
			title.setText(getItem(position).text);
			convertView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					onMenuTouch(position);		
					return false;  //consume the view?
				}
			});

			return convertView;
		}
		
		private void onMenuTouch(int position) {
			switch(position) {
			case 0:
				goHome();
				break;
			case 1:
				goToFunds();				
				break;
			case 2:
				//showInfoDialog();
				goAboutScreen();
				break;
			case 3:
				goToSocial();
				break;
			}
			
		}
		
		private void goHome() {
			Intent home = (new Intent(getContext(), Home.class));
			home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
		}
		
		private void goToFunds() {
			Intent funds = (new Intent(getContext(), Funds.class));
			startActivity(funds);
		}
		
		private void goToSocial() {
			Intent social = (new Intent(getContext(), Social.class));
			startActivity(social);
		}
		
		private void goAboutScreen(){
			Intent about = (new Intent(getContext(), Support.class));
			startActivity(about);
			
		}
		private void showInfoDialog() {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setTitle(getString(R.string.app_dialog_title));
			builder.setMessage("Thanks for using Arc, the simplest way to pay your restaurant bill.");
			//builder.setIcon(R.drawable.logo);
			builder.setPositiveButton("ok",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.create().show();
		}

	}
}
