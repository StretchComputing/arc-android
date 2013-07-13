package com.arcmobileapp.activities;

import java.util.ArrayList;
import java.util.UUID;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.CarouselScrollView;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.MerchantObject;
import com.arcmobileapp.utils.ScrollViewListener;
import com.arcmobileapp.web.GetMerchantsTask;
import com.arcmobileapp.web.GetTokenTask;

public class Home extends BaseActivity implements ScrollViewListener {

	private TextView txtTitle;
	private LinearLayout theView;
	private Button btnPayBill;
	private Button btnExplore;
	private CarouselScrollView scrollView;
	private ArrayList<MerchantObject> merchants;
	private ProgressDialog loadingDialog;
	private TextView currentMerchantText;
	private int currentScrollPos;
	private TextView homeTitle;
	private int currentImageWidth;
	private TextView currentMerchantAddressText;

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
		scrollView = (CarouselScrollView) findViewById(R.id.scroll);
		mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
		theView.setAnimation(AnimationUtils.loadAnimation(this,
				R.anim.login_fade_in));
		
		homeTitle = (TextView) findViewById(R.id.home_title);
		
		homeTitle.setTextColor(Color.rgb(190,190,190));
		
		currentMerchantText = (TextView) findViewById(R.id.current_merchant);
		currentMerchantText.setText("");
		currentMerchantAddressText = (TextView) findViewById(R.id.current_address);
		currentMerchantAddressText.setText("");

		//txtTitle = (TextView) findViewById(R.id.title);
		//txtTitle.setFocusable(true);
		//txtTitle.setTextColor(Color.rgb(128,128,128));
		//txtTitle.setTypeface(getModernPicsTypeface()); 
		
		btnPayBill = (Button) findViewById(R.id.pay_bill_button);
		//btnExplore = (Button) findViewById(R.id.explore_button);
		btnPayBill.setVisibility(View.VISIBLE);
		//.setVisibility(View.GONE);
	

		 
		loadingDialog = new ProgressDialog(Home.this);
		loadingDialog.setTitle("Finding Nearby Merchants");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		loadingDialog.show();
		
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
				
					MerchantObject merchant = merchants.get(0);
					
					currentMerchantText.setText(merchant.merchantName);
					currentMerchantAddressText.setText(merchant.merchantAddress);
					
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
        int imageWidth = (int) (displayMetrics.widthPixels / 2.0);
                
        Logger.d("IMAGE WIDTH**************************** " + imageWidth);
        // Populate the carousel with items
        ImageView imageItem;
        mCarouselContainer.removeAllViews();
        
        //Padding before
        LinearLayout l1 = new LinearLayout(this);
        
        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 82, getResources().getDisplayMetrics());
        currentImageWidth = space;

        l1.setLayoutParams(new LayoutParams(space, LayoutParams.MATCH_PARENT));
        mCarouselContainer.addView(l1);

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
            
            View carouselItem = createCarouselItem(imageItem, title, i);
            
           
            carouselItem.setOnClickListener(new OnClickListener() {
				
     				@Override
     				public void onClick(View v) {
     					clickCarousel(pos);
     				}
     			});
            
            mCarouselContainer.addView(carouselItem);
        }
        
        //Padding After
        LinearLayout l2 = new LinearLayout(this);
        l2.setLayoutParams(new LayoutParams(currentImageWidth, LayoutParams.MATCH_PARENT));
        mCarouselContainer.addView(l2);
        
        
        scrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				touchCarousel();
				return false;
			}
		});
       // scrollView.setScrollViewListener(this);
        scrollView.setScrollViewListener(this);
        
    }
	
	protected void touchCarousel() {
		btnPayBill.setVisibility(View.VISIBLE);
	}
	
	public RelativeLayout createCarouselItem(ImageView image, String title, int index) {
		
		//round corners
		Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
		Bitmap newMap = getRoundedCornerBitmap(bitmap);
		image.setImageBitmap(newMap);
		
		LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		RelativeLayout rLayout = (RelativeLayout) inflater.inflate(R.layout.carousel_item, null);
		 
		 
		ImageView itemImage = (ImageView) rLayout.findViewById(R.id.itemImage);
		itemImage.setImageDrawable(image.getDrawable());
		
		TextView itemText = (TextView) rLayout.findViewById(R.id.itemText);
		itemText.setText(title);
		itemText.setGravity(Gravity.CENTER | Gravity.BOTTOM);
		itemText.setVisibility(View.INVISIBLE);
		return rLayout;
	}

	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
	        bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);
	 
	    //final int color = 0xff424242;
	    final int color = Color.BLACK;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	    final RectF rectF = new RectF(rect);
	    final float roundPx = 5;
	 
	    paint.setAntiAlias(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	 
	    return output;
	  }
	
	
	public void onPayBillClick(View v) {
		
	
		int index = getCurrentIndex(currentScrollPos);        

        this.clickCarousel(index);
	}
	
	public void onExploreClick(View v) {
		toastShort("Explore");
	}

	@Override
	public void onScrollChanged(CarouselScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		

		currentScrollPos = x;
		int index = getCurrentIndex(x);
		
		MerchantObject merchant = merchants.get(index);
		
		currentMerchantText.setText(merchant.merchantName);
		currentMerchantAddressText.setText(merchant.merchantAddress);


	}
	
	
	private int getCurrentIndex(int scrollPos){
		int index = 0;
		
		int num = 170;
		if (scrollPos < num){
			return 0;
		}else{
			
			index = (scrollPos - num)/340 + 1;
		}
		
		return index;
	}
   
}
