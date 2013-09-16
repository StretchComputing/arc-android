package com.arcmobileapp.activities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.domain.Check;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.CarouselScrollView;
import com.arcmobileapp.utils.Constants;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.MerchantObject;
import com.arcmobileapp.utils.ScrollViewListener;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.GetCheckTask;
import com.arcmobileapp.web.GetMerchantsTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class Home extends BaseActivity implements ScrollViewListener {

	private TextView txtTitle;
	private RelativeLayout theView;
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
	private boolean isGoingRestaurant = false;
	
	private LinearLayout mCarouselContainer;

	public Home() {
		super();
	}

	public Home(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			
			//If there is a guest token or customer token, go to HOME
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			
			if (customerToken != null && customerToken.length() > 0){
				AppActions.add("Home - On Create - Using as Guest");

			}else{
				AppActions.add("Home - On Create - Using as Customer");

			}
			
			super.onCreate(savedInstanceState);
			setContentView(R.layout.home);
			
			theView = (RelativeLayout) findViewById(R.id.home_layout);
			scrollView = (CarouselScrollView) findViewById(R.id.scroll);
			mCarouselContainer = (LinearLayout) findViewById(R.id.carousel);
			theView.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.login_fade_in));
			
			homeTitle = (TextView) findViewById(R.id.home_title);
			homeTitle.setTypeface(ArcMobileApp.getLatoLightTypeface());
			homeTitle.setTextColor(Color.rgb(170,170,170));
			
			currentMerchantText = (TextView) findViewById(R.id.current_merchant);
			currentMerchantText.setText("");
			currentMerchantText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
			currentMerchantAddressText = (TextView) findViewById(R.id.current_address);
			currentMerchantAddressText.setText("");
			currentMerchantAddressText.setTypeface(ArcMobileApp.getLatoLightTypeface());
			
			

			//txtTitle = (TextView) findViewById(R.id.title);
			//txtTitle.setFocusable(true);
			//txtTitle.setTextColor(Color.rgb(128,128,128));
			//txtTitle.setTypeface(getModernPicsTypeface()); 
			
			btnPayBill = (Button) findViewById(R.id.pay_bill_button);
			//btnExplore = (Button) findViewById(R.id.explore_button);
			btnPayBill.setVisibility(View.VISIBLE);
			btnPayBill.setTypeface(ArcMobileApp.getLatoBoldTypeface());



			 
		
			
			boolean didLogOut = getIntent().getBooleanExtra(Constants.LOGGED_OUT, false);
			
			if (didLogOut){
				toastShort("Logout Successful!  You may continue to use Dutch as a guest.");
			}
						
		} catch (NotFoundException e) {
			(new CreateClientLogTask("Home.onCreate", "Exception Caught", "error", e)).execute();

		}

		
	}
	

	
	protected void getMerchantsFromWeb() {
		try {
			GetMerchantsTask getMerchantsTask = new GetMerchantsTask(getApplicationContext()) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						merchants = new ArrayList<MerchantObject>();
						
						merchants = getMerchants();
						

						loadingDialog.hide();
						if (merchants != null && merchants.size() > 0){
						
							AppActions.add("Home - Get Merchants Succeeded - Number Of Merchants:" + merchants.size());

							MerchantObject merchant = merchants.get(0);
							
							currentMerchantText.setText(merchant.merchantName);
							currentMerchantAddressText.setText(merchant.merchantAddress);
							
							initCarousel();
										        
						}else{
							AppActions.add("Home - Get Merchants Failed - Error Code:" + errorCode);

							//Remove carousel view?
							scrollView.setVisibility(View.INVISIBLE);
							currentMerchantText.setVisibility(View.INVISIBLE);
							currentMerchantAddressText.setVisibility(View.INVISIBLE);

							
							if (errorCode != 0){
								
								String errorMsg = "";
								
								if(errorCode == 999) {
					                errorMsg = "Can not find nearby merchants.";
					            } else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("Error retrieving nearby merchants.");

							}


						}
					} catch (Exception e) {
						
						scrollView.setVisibility(View.INVISIBLE);
						currentMerchantText.setVisibility(View.INVISIBLE);
						currentMerchantAddressText.setVisibility(View.INVISIBLE);
						
						toastShort("Error retrieving nearby merchants.");

						(new CreateClientLogTask("Home.getMerchantsFromWeb.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getMerchantsTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("Home.getMerchantsFromWeb", "Exception Caught", "error", e)).execute();

		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		//getTokensFromWeb();
		isGoingRestaurant = false;
		
		loadingDialog = new ProgressDialog(Home.this);
		loadingDialog.setTitle("Finding Nearby Merchants");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		loadingDialog.show();
		
		getMerchantsFromWeb();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	protected void clickCarousel(int pos){
		try {
			

			String name = "";
			String theId = "";
			
			name = merchants.get(pos).merchantName;
			theId = merchants.get(pos).merchantId;

			AppActions.add("Home - Carousel Item Clicked - Index:" + pos + ", Merchant Name:" + name);

			loadingDialog.dismiss();

			
			if (!isGoingRestaurant){
				isGoingRestaurant = true;
				Intent viewCheck = new Intent(getApplicationContext(), GetCheck.class);
				viewCheck.putExtra(Constants.VENUE, name);
				viewCheck.putExtra(Constants.VENUE_ID, theId); 
				startActivity(viewCheck);
			}
		
		} catch (Exception e) {
			(new CreateClientLogTask("Home.clickCarousel", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	//http://mobile.smashingmagazine.com/2013/02/01/android-carousel-design-pattern/
	protected void initCarousel() {
        try {
        	
			AppActions.add("Home - Initializing Carousel");

			// Compute the width of a carousel item based on the screen width and number of initial items.
			final DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
			int imageWidth = (int) (displayMetrics.widthPixels / 2.0);
			        
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
			    
			    
			    if (merchants.get(i).merchantId.equalsIgnoreCase("12")){
			    	imageName = "untitled";

			    }else if (merchants.get(i).merchantId.equalsIgnoreCase("13")){
			    	imageName = "union";
			    }else if (merchants.get(i).merchantId.equalsIgnoreCase("14")){
			    	imageName = "roka";

			    }else{
			    	imageName = "silverware";
			    	
			    	getImageForIndex(i+1, merchants.get(i).merchantId);
			        
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
			
			scrollView.scrollTo(0,0);
			scrollView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					touchCarousel();
					return false;
				}
			});
      // scrollView.setScrollViewListener(this);
			scrollView.setVisibility(View.VISIBLE);
			currentMerchantText.setVisibility(View.VISIBLE);
			currentMerchantAddressText.setVisibility(View.VISIBLE);
			scrollView.setScrollViewListener(this);
		} catch (Exception e) {
			(new CreateClientLogTask("Home.initCarousel", "Exception Caught", "error", e)).execute();

		}
        
    }
	
	protected void touchCarousel() {
		btnPayBill.setVisibility(View.VISIBLE);
	}
	
	public RelativeLayout createCarouselItem(ImageView image, String title, int index) {
		
		try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("Home.createCarouselItem", "Exception Caught", "error", e)).execute();
			return null;

		}
	}

	
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
	    try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("Home.getRoundedCornerBitimap", "Exception Caught", "error", e)).execute();
			return null;

		}
	  }
	
	
	public void onPayBillClick(View v) {
		
	
		try {
			
			if (merchants != null && merchants.size() > 0){
				

				int index = getCurrentIndex(currentScrollPos);        
				AppActions.add("Home - Pay Bill Clicked - Determined Index: " + index);

				this.clickCarousel(index);
			}else{
				
				AppActions.add("Home - Pay Bill Clicked - No Merchants");

				toastShort("No nearby merchants found, please try again.");
			}
			
		} catch (Exception e) {
			(new CreateClientLogTask("Home.onPayBillClick", "Exception Caught", "error", e)).execute();

		}
	}
	
	public void onExploreClick(View v) {
		toastShort("Explore");
	}

	@Override
	public void onScrollChanged(CarouselScrollView scrollView, int x, int y,
			int oldx, int oldy) {
		

		try {
			currentScrollPos = x;
			int index = getCurrentIndex(x);
			
			if (index >= merchants.size()){
				index = merchants.size() - 1;
			}
			MerchantObject merchant = merchants.get(index);
			
			currentMerchantText.setText(merchant.merchantName);
			currentMerchantAddressText.setText(merchant.merchantAddress);
		} catch (Exception e) {
			(new CreateClientLogTask("Home.onScrollChanged", "Exception Caught", "error", e)).execute();

		}


	}
	
	
	private int getCurrentIndex(int scrollPos){
		try {
			int index = 0;
			
			int num = 170;
			if (scrollPos < num){
				return 0;
			}else{
				
				index = (scrollPos - num)/340 + 1;
			}
			
			return index;
		} catch (Exception e) {
			(new CreateClientLogTask("Home.getCurrentIndex", "Exception Caught", "error", e)).execute();
			return 0;
		}
	}
	
	/*
	 * 
	 * public RelativeLayout createCarouselItem(ImageView image, String title, int index) {
		
		try {
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
		} catch (Exception e) {
			(new CreateClientLogTask("Home.createCarouselItem", "Exception Caught", "error", e)).execute();
			return null;

		}
	}
	 */
	
	
	private void getImageForIndex(int index, String merchantId){
		
		try{
			
			AppActions.add("Home - Get Image For Index: + " + index + " and MerchantId: " + merchantId);

			
			
			HomeXMLTask imageTask = new HomeXMLTask(index) {
				@Override
		        protected void onPostExecute(Bitmap result) {
					
					try{
						if (result != null){
							int theIndex = getCurrentIndex();
							
							RelativeLayout myView = (RelativeLayout)mCarouselContainer.getChildAt(theIndex);
							
							
							ImageView itemImage = (ImageView) myView.findViewById(R.id.itemImage);
							
							itemImage.setImageBitmap(result);
						}
					}catch(Exception e){
						(new CreateClientLogTask("Home.getImageForIndex.onPostExecute", "Exception Caught", "error", e)).execute();

					}
					
					

				}
			};
			
			String url = "http://arc.dagher.mobi/Images/App/Logos/"+merchantId+ ".jpg";
			imageTask.execute(new String[] { url });
		}catch(Exception e){
			(new CreateClientLogTask("Home.getImageForIndex", "Exception Caught", "error", e)).execute();

		}
	
	}
	
	private class HomeXMLTask extends AsyncTask<String, Void, Bitmap> {
		
		private int mcurrentIndex;
		
		public int getCurrentIndex(){
			return mcurrentIndex;
		}
		
		public HomeXMLTask(int index) {
			super();
			mcurrentIndex = index;
		}
		
        @Override
        protected Bitmap doInBackground(String... urls) {
        	try{
        		Bitmap map = null;
                for (String url : urls) {
                    map = downloadImage(url);
                }
                return map;
        	}catch(Exception e){
        		return null;
        	}
            
        }
 
        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
        	try{
        		if (result != null){
                    //helpImage.setImageBitmap(result);
            	}
        	}catch(Exception e){
        		
        	}
        }
 
        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
           try{
        	   Bitmap bitmap = null;
               InputStream stream = null;
               BitmapFactory.Options bmOptions = new BitmapFactory.Options();
               bmOptions.inSampleSize = 1;
    
               try {
                   stream = getHttpConnection(url);
                   bitmap = BitmapFactory.
                           decodeStream(stream, null, bmOptions);
                   stream.close();
               } catch (IOException e1) {
                   e1.printStackTrace();
               }
               return bitmap;
           }catch(Exception e){
        	   return null;
           }
        }
 
        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
           try{
        	   InputStream stream = null;
               URL url = new URL(urlString);
               URLConnection connection = url.openConnection();
    
               try {
                   HttpURLConnection httpConnection = (HttpURLConnection) connection;
                   httpConnection.setRequestMethod("GET");
                   httpConnection.connect();
    
                   if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                       stream = httpConnection.getInputStream();
                   }
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
               return stream;
           }catch(Exception e){
        	   return null;
           }
        }
    }

}
