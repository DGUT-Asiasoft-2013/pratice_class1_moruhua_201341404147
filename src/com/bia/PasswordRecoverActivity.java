package com.bia;

import java.io.IOException;

import com.bia.fragment.PasswordRecoverStep1Fragment;
import com.bia.fragment.PasswordRecoverStep1Fragment.OnGoNextListener;
import com.bia.fragment.PasswordRecoverStep2Fragment;
import com.bia.fragment.PasswordRecoverStep2Fragment.OnPasswordRecoverListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import api.Server;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PasswordRecoverActivity extends Activity {

	PasswordRecoverStep1Fragment step1Fragment=new PasswordRecoverStep1Fragment();
	PasswordRecoverStep2Fragment step2Fragment=new PasswordRecoverStep2Fragment();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password_recover);
		
		step1Fragment.setOnGoNextListener(new OnGoNextListener() {			
			@Override
			public void onGoNext() {
				
				goStep2();				
			}
		});
		
		step2Fragment.setOnPasswordRecoverListener(new OnPasswordRecoverListener() {
			
			@Override
			public void onPasswordRecover() {
				gorepassword();
				
			}
		});
		getFragmentManager()
		.beginTransaction()
		.replace(R.id.container, step1Fragment)
		.commit();//����
	}
	
	
	
	 void gorepassword() {
		 
		 MultipartBody.Builder requestBodyBulider=new MultipartBody.Builder()
					.setType(MultipartBody.FORM)
					.addFormDataPart("email",step1Fragment.getText())
					.addFormDataPart("passwordHash",step2Fragment.getText());
		 
		 OkHttpClient client =Server.getsharedClient();
		 Request request = Server.requestBuilderWithApi("repassword")
					.method("post", null)
					.post(requestBodyBulider.build())
					.build();
		 
		 client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0,final Response arg1) throws IOException {	
				
					try {
						final String responseString = arg1.body().string();
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								PasswordRecoverActivity.this.onResponse(arg0, responseString);
								
							}
						});

					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								PasswordRecoverActivity.this.onFailure(arg0, e);
								
							}
						});
					}	
					
				
				
			}
			
			@Override
			public void onFailure(final Call arg0,final IOException arg1) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						PasswordRecoverActivity.this.onFailure(arg0, arg1);					
					}
				});
				
			}
		});
		
	}



	void onFailure(Call arg0, Exception e) {
		new AlertDialog.Builder(PasswordRecoverActivity.this)
		.setTitle("ʧ��RU��")
		.setMessage(e.getLocalizedMessage())
		.setPositiveButton("Rua!",null)
		.show();
		
	}



	void onResponse(Call arg0,String string) {
		 new AlertDialog.Builder(PasswordRecoverActivity.this)
			.setMessage(string+"�޸�����ɹ�")
			.setPositiveButton("Rua!",null)
			.show();		
	}



	void goStep2(){
		getFragmentManager()
		.beginTransaction()
		.setCustomAnimations(R.animator.slide_in_right,
				R.animator.slide_out_left,
				R.animator.slide_in_left, 
				R.animator.slide_out_right)
		.replace(R.id.container, step2Fragment)
		.addToBackStack(null)
		.commit();
	}
	
	
}
