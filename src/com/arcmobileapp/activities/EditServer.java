package com.arcmobileapp.activities;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.arcmobileapp.ArcMobileApp;
import com.arcmobileapp.BaseActivity;
import com.arcmobileapp.R;
import com.arcmobileapp.utils.ArcPreferences;
import com.arcmobileapp.utils.Keys;
import com.arcmobileapp.utils.Logger;
import com.arcmobileapp.utils.ServerObject;
import com.arcmobileapp.web.ErrorCodes;
import com.arcmobileapp.web.GetDutchServersTask;
import com.arcmobileapp.web.SetServerTask;
import com.arcmobileapp.web.rskybox.AppActions;
import com.arcmobileapp.web.rskybox.CreateClientLogTask;

public class EditServer extends BaseActivity {

	private ArrayList<ServerObject> servers;
	private ProgressDialog loadingDialog;
	private ListView list;
	private ArrayAdapter<ServerObject> adapter;

	private TextView titleText;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_server);
		
		titleText = (TextView)findViewById(R.id.title_text);
		titleText.setTypeface(ArcMobileApp.getLatoBoldTypeface());
		
		loadingDialog = new ProgressDialog(EditServer.this);
		loadingDialog.setTitle("Getting Dutch Servers");
		loadingDialog.setMessage("Please Wait...");
		loadingDialog.setCancelable(false);
		loadingDialog.show();
		
		getDutchServers();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		//inflater.inflate(R.menu.action_bar_menu, menu);
		return true;
	}
	
	
	protected void getDutchServers() {
		try {
			
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			
			GetDutchServersTask getServerTask = new GetDutchServersTask(customerToken, getApplicationContext()) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						
						int errorCode = getErrorCode();

						servers = new ArrayList<ServerObject>();
						
						servers = getServers();
						

						loadingDialog.hide();
						

						if (servers != null && servers.size() > 0){
						
							toastShort("Get Servers Success!");

							AppActions.add("EditServer - Get Servers Succeeded - Number Of Servers:" + servers.size());

							populateListView();
							registerClickCallback();
										        
						}else{
							AppActions.add("EditServer - Get Servers Failed - Error Code:" + errorCode);

					

							
							if (errorCode != 0){
								
								String errorMsg = "";
								
								if(errorCode == 999) {
					                errorMsg = "Error retrieving server list.";
					            } else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("Error retrieving server list.");

							}


						}
					} catch (Exception e) {
						
			
						
						toastShort("Error retrieving server list.");

						(new CreateClientLogTask("EditServer.getDutchServers.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			getServerTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("EditServer.getDutchServers", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	private void populateListView() {
		adapter = new MyListAdapter();
	    list = (ListView) findViewById(R.id.serverList);
		list.setAdapter(adapter);
	}
	
	private void registerClickCallback() {
		

		try {
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View viewClicked,
						int position, long id) {
					
					try {
						Logger.d("Setting New Server");
						ServerObject server = servers.get(position);
						
						setNewServer(server.serverId);
					
							

						
					} catch (Exception e) {
						Logger.d("EXCEPTOIN: " + e.getMessage());
						(new CreateClientLogTask("EditServer.onRegisterClickCallBack.onItemClick", "Exception Caught", "error", e)).execute();

					}
					
				
				}
			});
			
		
		} catch (Exception e) {
			(new CreateClientLogTask("EditServer.registerClickCallback", "Exception Caught", "error", e)).execute();

		}
	}
	
	private class MyListAdapter extends ArrayAdapter<ServerObject> {
		public MyListAdapter() {
			super(EditServer.this, R.layout.server_row, servers);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Make sure we have a view to work with (may have been given null)
			try {
				View itemView = convertView;
				if (itemView == null) {
					itemView = getLayoutInflater().inflate(R.layout.server_row, parent, false);
				}
				
				// Find the car to work with.
				ServerObject currentItem = servers.get(position);
			
				// Name
				TextView serverName = (TextView) itemView.findViewById(R.id.server_name);
				serverName.setText(currentItem.serverName);
				serverName.setTypeface(ArcMobileApp.getLatoBoldTypeface());

				// URL:
				TextView serverUrl = (TextView) itemView.findViewById(R.id.server_url);
				serverUrl.setText(currentItem.serverUrl);
				serverUrl.setTypeface(ArcMobileApp.getLatoLightTypeface());

				// CHECK:
				ImageView serverCheck = (ImageView) itemView.findViewById(R.id.server_check);
				serverCheck.setVisibility(View.INVISIBLE);
				
				
				String currentServer = new ArcPreferences(getApplicationContext()).getServer();
			
				
				if (currentServer.contains(currentItem.serverUrl)){
					serverCheck.setVisibility(View.VISIBLE);
				}
				
				
				
				return itemView;
			} catch (Exception e) {
				(new CreateClientLogTask("ViewCheck.MyListAdapter.getView", "Exception Caught", "error", e)).execute();
				return convertView;

			}
		}				
	}
	
	
	
	protected void setNewServer(int serverId) {
		try {
			
			loadingDialog.setTitle("Setting New Server");
			loadingDialog.show();
			ArcPreferences myPrefs = new ArcPreferences(getApplicationContext());
			String customerToken = myPrefs.getString(Keys.CUSTOMER_TOKEN);
			String customerId = myPrefs.getString(Keys.CUSTOMER_ID);

			SetServerTask setServerTask = new SetServerTask(customerToken, customerId, serverId, getApplicationContext()) {

				@Override
				protected void onPostExecute(Void result) {
					try {
						super.onPostExecute(result);
						loadingDialog.hide();
						int errorCode = getErrorCode();

						

						if (getSuccess()){
						
							AppActions.add("EditServer - Set Server Succeeded");

							toastShort("Set server successful!");
							
							getCurrentServer();

							loadingDialog.dismiss();

							Intent goBackProfile = new Intent(getApplicationContext(), UserProfile.class);
							goBackProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(goBackProfile);
										        
						}else{
							AppActions.add("EditServer - Set Server Failed - Error Code:" + errorCode);

					

							
							if (errorCode != 0){
								
								String errorMsg = "";
								
								if(errorCode == 999) {
					                errorMsg = "Error setting new server.";
					            } else {
					                errorMsg = ErrorCodes.ARC_ERROR_MSG;
					            }
								
								
								
								toastShort(errorMsg);
								
							}else{
								toastShort("Error setting new server.");

							}


						}
					} catch (Exception e) {
						
			
						
						toastShort("Error retrieving server list.");

						(new CreateClientLogTask("EditServer.setDutchServers.onPostExecute", "Exception Caught", "error", e)).execute();

					}

				}
				
			};
			setServerTask.execute();
		} catch (Exception e) {
			(new CreateClientLogTask("EditServer.setDutchServers", "Exception Caught", "error", e)).execute();

		}
	}
	
	
	
	

}
