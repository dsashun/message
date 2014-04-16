package com.david.dsashun;

import java.util.List;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

public class FriendsFragment extends ListFragment {

	protected static final String TAG = FriendsFragment.class.getSimpleName();
	// Using angle brackets because it is a generic type
	protected ParseRelation<ParseUser> mFriendsRelation;
	protected ParseUser mCurrentUser;
	protected List <ParseUser> mFriends;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_friends,
				container, false);
		
		return rootView;
	}
	@Override
	public void onResume() {
		super.onResume();
		//getting the current user
		mCurrentUser=ParseUser.getCurrentUser();
		mFriendsRelation =mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
		
		getActivity().setProgressBarIndeterminateVisibility(true);
		ParseQuery <ParseUser> query=mFriendsRelation.getQuery();
		query.addAscendingOrder(ParseConstants.KEY_USERNAME);
		query.findInBackground(new FindCallback<ParseUser>() {;
		
			@Override
			public void done(List<ParseUser> friends, ParseException e) {
				getActivity().setProgressBarIndeterminateVisibility(false);
				
				if (e==null) {	
				mFriends	=	friends;
				

				String [] usernames= new String[mFriends.size()];
				
				int i=0;
				for(ParseUser user:mFriends) {
					usernames[i]=user.getUsername();
					i++;
					
				}
				ArrayAdapter<String> adapter= new ArrayAdapter<String>(
						getListView().getContext(), 
						android.R.layout.simple_list_item_1, usernames);
						setListAdapter(adapter);
			}
			else {
				Log.e(TAG,e.getMessage());

				AlertDialog.Builder builder =new AlertDialog.Builder(getListView().getContext());
				builder.setMessage(e.getMessage());
				builder.setTitle(R.string.error_title);
				builder.setPositiveButton(android.R.string.ok, null);
				AlertDialog dialog =builder.create();
				dialog.show();
			}
		}	
		});
	}
}
