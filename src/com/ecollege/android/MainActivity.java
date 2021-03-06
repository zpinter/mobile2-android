package com.ecollege.android;

import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;

import com.ecollege.android.activities.ECollegeTabActivity;
import com.ecollege.api.ECollegeClient;
import com.ecollege.api.services.courses.FetchMyCoursesService;
import com.ecollege.api.services.users.FetchMeService;
import com.google.inject.Inject;

public class MainActivity extends ECollegeTabActivity {
	@Inject ECollegeApplication app;
	@Inject SharedPreferences prefs;
	protected ECollegeClient client;
	private boolean meLoaded;
	private boolean coursesLoaded;
	
	
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        	
        client = app.getClient();
        
        if (app.getCurrentUser() == null) {
	        String grantToken = prefs.getString("grantToken", null);
	        if (grantToken != null) {
	    		client.setupAuthentication(grantToken);
	        	fetchCurrentUserAndCourses();
	        } else {
	        	Intent myIntent = new Intent(this, LoginActivity.class);
	        	startActivityForResult(myIntent, LOGIN_REQUEST_CODE);
	        }        	
        } else {
        	setupActivity();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if (requestCode == LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
    		setupActivity();
    	}
    }
    
    protected void fetchCurrentUserAndCourses() {
    	buildService(new FetchMeService()).execute();
    	buildService(new FetchMyCoursesService()).execute();
    }    
    
    public void onServiceCallSuccess(FetchMeService service) {
		app.setCurrentUser(service.getResult());	
    	Ln.i("User loaded from the Main activity");
		meLoaded = true;
		setupActivityIfServiceCallsAreComplete();
    }
    
    public void onServiceCallSuccess(FetchMyCoursesService service) {
    	app.setCurrentCourseList(service.getResult());
    	Ln.i("Courses loaded from the Main activity");
    	coursesLoaded = true;
    	setupActivityIfServiceCallsAreComplete();
    }
    
    protected void setupActivityIfServiceCallsAreComplete() {
    	if (meLoaded && coursesLoaded) {
    		setupActivity();
    	}
    }
    
    protected void setupActivity() {
        addHomeTab();
        addDiscussionsTab();
        addCoursesTab();
        addProfileTab();
    }
    
	protected void addHomeTab() {
		TabHost host = getTabHost();
        Intent i= new Intent(this,HomeActivity.class);
        View v = getLayoutInflater().inflate(R.layout.home_tab_view, null);
        host.addTab(host.newTabSpec("home").setIndicator(v).setContent(i));
    }
    
    protected void addDiscussionsTab() {
        TabHost host = getTabHost();
        Intent i= new Intent(this,DiscussionsActivity.class);
        View v = getLayoutInflater().inflate(R.layout.discussions_tab_view, null);
        host.addTab(host.newTabSpec("discussions").setIndicator(v).setContent(i));
    }
    
    protected void addCoursesTab() {
        TabHost host = getTabHost();
        Intent i= new Intent(this,CoursesActivity.class);
        View v = getLayoutInflater().inflate(R.layout.courses_tab_view, null);
        host.addTab(host.newTabSpec("courses").setIndicator(v).setContent(i));
    }
    
    protected void addProfileTab() {
        TabHost host = getTabHost();
        Intent i= new Intent(this,ProfileActivity.class);
        View v = getLayoutInflater().inflate(R.layout.profile_tab_view, null);
        host.addTab(host.newTabSpec("profile").setIndicator(v).setContent(i));
    }
}