package com.arcmobileapp.fragments.anim;

import android.graphics.Canvas;

import com.dutchmobileapp.R;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;
import com.slidingmenu.lib.SlidingMenu.CanvasTransformer;

public class CustomZoomAnimation extends CustomAnimation {

	public CustomZoomAnimation() {
		// see the class CustomAnimation for how to attach 
		// the CanvasTransformer to the SlidingMenu
		super(R.string.anim_zoom, new CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				try {

					float scale = (float) (percentOpen*0.25 + 0.75);
					canvas.scale(scale, scale, canvas.getWidth()/2, canvas.getHeight()/2);
				} catch (Exception e) {
					(new CreateClientLogTask("CustomZoomAnimation.transformCanvas", "Exception Caught", "error", e)).execute();

				}
			}
		});
	}
}
