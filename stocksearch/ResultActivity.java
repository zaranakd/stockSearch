package com.example.zaranadesai.stocksearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutionException;


class PagerAdapter extends FragmentPagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag=null;
        switch (position){
            case 0:
                frag=new currFragment();
                break;
            case 1:
                frag=new histFragment();
                break;
            case 2:
                frag=new newsFragment();
                break;
        }
        return frag;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title=" ";
        switch (position){
            case 0:
                title="CURRENT";
                break;
            case 1:
                title="HISTORICAL";
                break;
            case 2:
                title="NEWS";
                break;
        }

        return title;
    }
}

class myViewPager extends ViewPager{


    public myViewPager(Context context) {
        super(context);
    }

    public myViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}

public class ResultActivity extends AppCompatActivity implements currFragment.OnFragmentInteractionListener,histFragment.OnFragmentInteractionListener,newsFragment.OnFragmentInteractionListener{
    ViewPager pager;
    TabLayout tabLayout;
    JSONObject details=null;
    JSONObject chart=null;
    JSONObject ynews=null;
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    boolean fav=false;
    ArrayList<String> arr;
    ArrayList<stk> arrr=new ArrayList<>();
    String sym=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        try {
            details = new JSONObject(intent.getStringExtra("details"));
            chart = new JSONObject(intent.getStringExtra("chart"));
            ynews = new JSONObject(intent.getStringExtra("ynews"));
            sym=details.getString("Name");
            setTitle(sym);
            sym=details.getString("Symbol");
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        arr=new ArrayList<>();
        setContentView(R.layout.activity_result);
        pager= (myViewPager) findViewById(R.id.pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);
        FragmentManager manager=getSupportFragmentManager();
        PagerAdapter adapter=new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setupWithViewPager(pager);
        fav();
        }
    public void fav(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String,?> keys = settings.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            arr.add(entry.getValue().toString());
            if(((String)entry.getKey()).equals(sym)){
                fav=true;
            }
        }
        if(arr.size()>0)
        {
            Collections.sort(arr,new Comparator<String>() {
                @Override
                public int compare(String a, String b) {
                    if(!(a.replaceAll("[^0-9]","")).equals("") && !(b.replaceAll("[^0-9]","")).equals("")) {
                        int aa = Integer.parseInt(a.replaceAll("[^0-9]", ""));
                        int bb = Integer.parseInt(b.replaceAll("[^0-9]", ""));
                        return aa - bb;
                    }
                    return 0;
                }
            });
        }
        for(int i=0;i<arr.size();i++){
            arr.set(i,arr.get(i).replaceAll("[0-9]", ""));
        }
        View view=  LayoutInflater.from(this).inflate(R.layout.activity_main,(ViewGroup)getWindow().getDecorView(),false);
        ListView list = (ListView)view.findViewById(R.id.listVieww);
        if(arr.size()>0)
            favtemp(arr.get(0));
        final FavAdapter aca=new FavAdapter(this,arrr);
        assert list != null;
        list.setAdapter(aca);
    }
    int x=0;
    public void favtemp(String symb){
        AsyncJsonDataa obj = new AsyncJsonDataa();
        JSONObject jobj=null;
        try {
            jobj=obj.execute(symb).get();
            stk st=new stk(jobj.getString("Symbol"),jobj.getString("Name"),jobj.getDouble("LastPrice"),jobj.getDouble("ChangePercent"),jobj.getInt("MarketCap"));
            arrr.add(st);
        } catch (InterruptedException | JSONException | ExecutionException e) {
            e.printStackTrace();
        }
        if(x<arr.size()-1){
            x++;
            favtemp(arr.get(x));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String,?> keys = settings.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            if((entry.getKey()).equals(sym)){
                fav=true;
                menu.getItem(0).setIcon(R.drawable.yellow_star);
            }
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){

            case R.id.fb:
                try {
                    facebook_post("Sharing "+details.get("Name").toString()+"!!");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.fstar:
                if(this.fav == true)
                {
                    item.setIcon(R.drawable.empty_star);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove(sym);
                    editor.commit();
                    fav=false;
                }
                else
                {
                    item.setIcon(R.drawable.yellow_star);
                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = settings.edit();
                    System.out.println(settings.getAll().size()+" iiiiiiiiiiiiiiiiiiiiiiiiiiii");
                    if(settings.getAll().size()==1) {
                        editor.putString(sym, "0" + sym);
                        editor.commit();
                        System.out.println(settings.getAll().size()+" iiiiiiiiiiiiiiiiiiiiiiiiiiii");
                    }
                    else{
                        Map<String,?> keys = settings.getAll();
                        int no=0;
                        for(Map.Entry<String,?> entry : keys.entrySet()){
                            //System.out.println(((String)entry.getValue()).replaceAll("[^0-9]","")+"hiiiiiiiii");
                            if(!((String)entry.getValue()).replaceAll("[^0-9]","").equals(""))
                            if(no<Integer.parseInt(((String)entry.getValue()).replaceAll("[^0-9]","")))
                                no=Integer.parseInt(((String)entry.getValue()).replaceAll("[^0-9]",""));
                        }
                        no++;
                        editor.putString(sym,no+sym);
                        editor.commit();
                    }
                    fav = true;
                }
                return true;

            case android.R.id.home:
                x=0;
                if(arrr.size()>0)
                    favtemp(arrr.get(0).sym);
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void facebook_post(String message) {

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = null;
            try {
                linkContent = new ShareLinkContent.Builder()
                        .setContentTitle("Current Stock Price of "+details.get("Name").toString()+", "+details.get("LastPrice"))
                        .setContentDescription(
                                "Stock Information of "+details.get("Name").toString())
                        .setContentUrl(Uri.parse("http://chart.finance.yahoo.com/t?s="+details.get("Symbol").toString()+"&lang=en-US&width=4000&height=3000"))
                        .build();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            shareDialog.show(linkContent);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if(resultCode==-1)
        {
            Toast.makeText(this, "You shared this post", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "You did not share this post", Toast.LENGTH_SHORT).show();
        }
    }

    public JSONObject getDetail() {
        return details;
    }
    public JSONObject getHist() {
        return chart;
    }
    public JSONObject getNews() { return ynews;}

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
