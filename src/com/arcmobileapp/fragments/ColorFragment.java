package com.arcmobileapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ColorFragment extends Fragment {
	
	private int mColorRes = -1;
	public static final String COLOR_KEY = "COLOR_KEY";
	
	public ColorFragment() {
		mColorRes = android.R.color.white;
		setRetainInstance(true);
	}
	
	protected void setColorRes(int colorId) {
		mColorRes = colorId;
	}
	
	protected void setColorResFromBundle() {
		setColorRes(getArguments().getInt(COLOR_KEY));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null)
			setColorRes(savedInstanceState.getInt(COLOR_KEY));
		else 
			setColorResFromBundle();
		
		int color = getResources().getColor(mColorRes);
		
		// construct the RelativeLayout
		RelativeLayout v = new RelativeLayout(getActivity());
		v.setBackgroundColor(color);		
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(COLOR_KEY, mColorRes);
	}
	
}
