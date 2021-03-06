package com.bia.fragment.pages;

import java.io.IOException;

import com.bia.AvatarView;
import com.bia.HelloWorldActivity;
import com.bia.LoginActivity;
import com.bia.R;
import com.fasterxml.jackson.databind.ObjectMapper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import api.Server;
import entity.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MeFragment extends Fragment {

	View view;
	TextView infoView;
	ProgressBar progress;
	AvatarView avatar;
	Button btnLogOut;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.fragment_page_my_profile, null);
			infoView=(TextView) view.findViewById(R.id.me_info);
			progress=(ProgressBar) view.findViewById(R.id.progress);
			avatar=(AvatarView) view.findViewById(R.id.avatar);
			
			btnLogOut=(Button) view.findViewById(R.id.btn_meloginout);
			btnLogOut.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					new AlertDialog.Builder(getActivity())
					.setTitle("注销")
					.setMessage("确认注销吗")
					.setNegativeButton("否", null)
					.setPositiveButton("是",new OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							Intent intent = new Intent(getActivity(), LoginActivity.class);
							startActivity(intent);
							getActivity().finish();
						}
					})
					.show();
					
				}
			});
		}
		return view;
	}
	
	
	@Override
	public void onResume() {
		
		super.onResume();
		
		infoView.setVisibility(View.GONE);
		progress.setVisibility(View.VISIBLE);
		OkHttpClient client =Server.getsharedClient();
		Request request = Server.requestBuilderWithApi("me")
				.method("get", null)
				.build();
		
		client.newCall(request).enqueue(new Callback() {
			
			@Override
			public void onResponse(final Call arg0,final Response arg1) throws IOException {
				try{
				final User user =new ObjectMapper().readValue(arg1.body().bytes(),User.class);
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						MeFragment.this.onResponse(arg0,user);
						
					}
				});}catch (final Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							MeFragment.this.onFailure(arg0, e);
							
						}
					});
				}
			}
			
			@Override
			public void onFailure(final Call arg0,final IOException arg1) {
				getActivity().runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						MeFragment.this.onFailure(arg0, arg1);
					}
				});
				
			}
		});
		
	}


	protected void onFailure(Call arg0, Exception arg1) {
		// TODO Auto-generated method stub
		progress.setVisibility(View.GONE);
		infoView.setVisibility(View.VISIBLE);
		infoView.setTextColor(Color.RED);
		infoView.setText(arg1.getMessage());
	}


	protected void onResponse(Call arg0, User user) {
		progress.setVisibility(View.GONE);
		avatar.load(user);
		infoView.setVisibility(View.VISIBLE);
		infoView.setTextColor(Color.BLACK);
		infoView.setText("Hello  "+user.getName());
		
	}
}
