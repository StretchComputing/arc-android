package com.arcmobileapp.activities;

import java.util.UUID;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.MerchantObject;

import com.arcmobileapp.web.GetMerchantsTask;
import com.arcmobileapp.web.GetTokenTask;

public class Home extends BaseActivity {

	private TextView txtTitle;
	private LinearLayout theView;
	private Button btnPayBill;
	private Button btnExplore;
	private HorizontalScrollView scrollView;
	private ArrayList<MerchantObject> merchants;
	private ProgressDialog loadingDialog;


	private LinearLayout mCarouselContainer;

	public Home() {
		super();
	}

	public Home(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		theView = (LinearLayout) findViewById(R.id.home_layout);
		scrollView = (HorizontalScrollView) findViewById(R.id.scroll);
		mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
		theView.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.login_fade_in));
		
		txtTitle = (TextView) findViewById(R.id.title);
		txtTitle.setFocusable(true);
		txtTitle.setTextColor(Color.rgb(128,128,128));
//		txtTitle.setTypeface(getModernPicsTypeface()); 
		
		btnPayBill = (Button) findViewById(R.id.pay_bill_button);
		btnExplore = (Button) findViewById(R.id.explore_button);
		btnPayBill.setVisibility(View.VISIBLE);
		btnExplore.setVisibility(View.GONE);
		
		loadingDialog = new ProgressDialog(Home.this);
		loadingDialog.setTitle("Finding Nearby Merchants");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		loadingDialog.show();
		
	}
	
	protected void getTokensFromWeb() {
		if(getString(Keys.DEV_TOKEN) == null) {
			// Get a token for this user, create guest account, generate and persist UUID
			String uuid = UUID.randomUUID().toString();
			putString(Keys.MY_UUID, uuid);  // THIS WILL BE THE USER'S UUID FOR LOGIN AS A GUEST
			GetTokenTask getTokenTask = new GetTokenTask(uuid, uuid, true, getApplicationContext()) {
				@Override
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					if(getSuccess()) {
						if(getDevToken()!=null) {
							putString(Keys.DEV_TOKEN, getDevToken());
							putString(Keys.DEV_CUSTOMER_ID, getDevCustomerId());
							Logger.d("SAVING DEV TOKEN '" + getDevToken() + "' id=" + getDevCustomerId());
						}						
						if(getProdToken()!=null) {
							putString(Keys.PROD_TOKEN, getProdToken());
							putString(Keys.PROD_CUSTOMER_ID, getProdCustomerId());
							Logger.d("SAVING PROD TOKEN '" + getProdToken() + "' id=" + getProdCustomerId());
						}
					}
				}
			};
			getTokenTask.execute();
		} else {
			Logger.d("STORED DEV TOKEN '" + getString(Keys.DEV_TOKEN) + "'");
			Logger.d("STORED PROD TOKEN '" + getString(Keys.PROD_TOKEN) + "'");
		}
	}
	
	protected void getMerchantsFromWeb() {
		GetMerchantsTask getMerchantsTask = new GetMerchantsTask(getApplicationContext()) {

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				merchants = new ArrayList<MerchantObject>();
				
				merchants = getMerchants();

				loadingDialog.hide();
				if (merchants.size() > 0){
					initCarousel();
				}

			}
			
		};
		getMerchantsTask.execute();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//getTokensFromWeb();
		getMerchantsFromWeb();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	protected void clickCarousel(int pos){
		String name = "";
		String theId = "";
		
		name = merchants.get(pos).merchantName;
		theId = merchants.get(pos).merchantId;

		
		Intent viewCheck = new Intent(getApplicationContext(), GetCheck.class);
		viewCheck.putExtra(Constants.VENUE, name);
		viewCheck.putExtra(Constants.VENUE_ID, theId); 
		startActivity(viewCheck);
	}
	
	
	//http://mobile.smashingmagazine.com/2013/02/01/android-carousel-design-pattern/
	protected void initCarousel() {
        // Compute the width of a carousel item based on the screen width and number of initial items.
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int imageWidth = (int) (displayMetrics.widthPixels / 2.0);

        // Populate the carousel with items
        ImageView imageItem;
        mCarouselContainer.removeAllViews();
        
        for (int i = 0 ; i < merchants.size(); i++) {
            // Create new ImageView
        	final int pos = i;
            imageItem = new ImageView(this);
     
            // Set the shadow background
            imageItem.setBackgroundResource(R.drawable.shadow_nine);

            // Set the image view resource
           // imageItem.setImageResource(puppyResourcesTypedArray.getResourceId(0, -1));

            String imageName = "";
            
            
            if (merchants.get(i).merchantName.equalsIgnoreCase("Isis Lab")){
            	imageName = "untitled";
            }else{
            	imageName = "union";
            }
            
            int id = getResources().getIdentifier("com.arcmobileapp:drawable/" + imageName, null, null);
            imageItem.setImageResource(id);
            
            // Set the size of the image view to the previously computed value
            imageItem.setLayoutParams(new LinearLayout.LayoutParams(imageWidth, imageWidth));

           
            /// Add image view to the carousel container
//            mCarouselContainer.addView(imageItem);
            
            String title = "hello";
            title = merchants.get(i).merchantName;
            
            View carouselItem = createCarouselItem(imageItem, title);
            
            carouselItem.setOnClickListener(new OnClickListener() {
				
     				@Override
     				public void onClick(View v) {
     					clickCarousel(pos);
     				}
     			});
            
            mCarouselContainer.addView(carouselItem);
        }
        scrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touchCarousel();
				return false;
			}
		});
    }
	
	protected void touchCarousel() {
		btnPayBill.setVisibility(View.VISIBLE);
		btnExplore.setVisibility(View.GONE);
	}
	
	public RelativeLayout createCarouselItem(ImageView image, String title) {
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		RelativeLayout rLayout = (RelativeLayout) inflater.inflate(R.layout.carousel_item, null);
		
		ImageView itemImage = (ImageView) rLayout.findViewById(R.id.itemImage);
		itemImage.setImageDrawable(image.getDrawable());
		
		TextView itemText = (TextView) rLayout.findViewById(R.id.itemText);
		itemText.setText(title);
		itemText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		return rLayout;
	}

	public void onPayBillClick(View v) {
		
		int scrollPosX = scrollView.getScrollX();
		Logger.d("Scroll Pos: " + scrollPosX);
		
		final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int imageWidth = (int) (displayMetrics.widthPixels / 2.0);
        
		

        int index = scrollPosX/ (imageWidth/merchants.size());
        
		Logger.d("Index: " + index);

        this.clickCarousel(index);
	}
	
	public void onExploreClick(View v) {
		toastShort("Explore");
	}
}
