package com.example.zaranadesai.stocksearch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class Stock{
    String name;
    String symb;
    String exch;

    public Stock(JSONObject object){
        try{
            symb = object.getString("Symbol");
            name = object.getString("Name");
            exch = object.getString("Exchange");
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
class stk{
    String sym,name;
    double val,change;
    double mcap;
    public stk(String s,String n, double v, double c, double m){
        sym=s;
        name=n;
        val=v;
        change=c;
        mcap=m;
    }
}

class AsyncJsonDataa extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... strings) {
        String newText = strings[0];
        JSONObject obj=new JSONObject();
        try{
            obj.put("details",getData("symbol",newText));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.pb.setVisibility(View.VISIBLE);
    }

    private static JSONObject getData(String query, String symb){
        JSONObject obj=null;
        try{
            URL url = new URL("http://stocksearchh.appspot.com/index.php/?"+query+"="+symb);
            HttpURLConnection httpc = (HttpURLConnection) url.openConnection();
            httpc.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpc.getInputStream()));
            String jsonText = readAll(in);
            jsonText = jsonText.substring(1, jsonText.length()-1);
            jsonText= StringEscapeUtils.unescapeJava(jsonText);
            obj=new JSONObject(jsonText);
            //jsonText=jsonText.replaceAll("^\"|\"$|\\\\","");
            System.out.println(query+"   "+jsonText);
            httpc.disconnect();
        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
        return obj;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while((cp = rd.read())!= -1){
            sb.append((char) cp);
        }
        return sb.toString();
    }
}


class FavAdapter extends ArrayAdapter<stk> implements Filterable {
    public ArrayList<stk> stocklist;

    public FavAdapter(Context context, ArrayList<stk> arr) {
        super(context, R.layout.fav);
        stocklist = arr;
    }
    @Override
    public int getCount() {
        return stocklist.size();
    }

    @Override
    public stk getItem(int index) {
        return stocklist.get(index);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        stk st = stocklist.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fav, parent, false);
        }
        DecimalFormat df2 = new DecimalFormat("#.##");
        TextView tSymbol = (TextView) convertView.findViewById(R.id.txtsym);
        TextView tName = (TextView) convertView.findViewById(R.id.txtname);
        TextView tChange = (TextView) convertView.findViewById(R.id.txtch);
        TextView tMcap = (TextView) convertView.findViewById(R.id.txtmcap);
        TextView tVal = (TextView) convertView.findViewById(R.id.txtcurr);
        tSymbol.setText(st.sym);
        tName.setText(st.name);
        if(st.change<0){
            tChange.setText(df2.format(st.change)+"%");
            tChange.setBackgroundColor(Color.parseColor("#FF0000"));
        }
        else{
            tChange.setText(df2.format(st.change)+"%");
            tChange.setBackgroundColor(Color.parseColor("#00FF00"));
        }
        if(st.mcap<1000000)
            tMcap.setText("Market Cap: "+st.mcap+"");
        else if(st.mcap<1000000000)
            tMcap.setText("Market Cap: "+df2.format(st.mcap/1000000)+" Million");
        else
            tMcap.setText("Market Cap: "+df2.format(st.mcap/1000000000)+" Billion");
        tVal.setText("$"+df2.format(st.val));
        return convertView;
    }

}


class AsyncJsonData extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... strings) {
        String newText = strings[0];
        JSONObject obj=new JSONObject();
        try{
            obj.put("details",getData("symbol",newText));
            obj.put("chart",getData("symbols",newText));
            obj.put("ynews",getData("symbolsss",newText));
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        return obj;
    }

    private static JSONObject getData(String query, String symb){
        JSONObject obj=null;
        try{
            URL url = new URL("http://stocksearchh.appspot.com/index.php/?"+query+"="+symb);
            HttpURLConnection httpc = (HttpURLConnection) url.openConnection();
            httpc.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(httpc.getInputStream()));
            String jsonText = readAll(in);
            jsonText = jsonText.substring(1, jsonText.length()-1);
            jsonText= StringEscapeUtils.unescapeJava(jsonText);
            obj=new JSONObject(jsonText);
            //jsonText=jsonText.replaceAll("^\"|\"$|\\\\","");
            System.out.println(query+"   "+jsonText);
            httpc.disconnect();
        }catch (JSONException | IOException e){
            e.printStackTrace();
        }
        return obj;
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while((cp = rd.read())!= -1){
            sb.append((char) cp);
        }
        return sb.toString();
    }
}


public class MainActivity extends AppCompatActivity {
    static ProgressBar pb,au;
    final Context context = this;
    ArrayList<String> arr=new ArrayList<>();
    ArrayList<stk> arrr=new ArrayList<>();
    Handler hand;
    class AutoAdapter extends ArrayAdapter<Stock> implements Filterable {
        public ArrayList<Stock> stocklist;

        public AutoAdapter(Context context, int resource) {
            super(context, resource);
            stocklist = new ArrayList<>();
        }
        @Override
        public int getCount() {
            return stocklist.size();
        }

        @Override
        public Stock getItem(int index) {
            return stocklist.get(index);
        }

        public ArrayList<Stock> getItems(){
            return stocklist;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Stock stk = stocklist.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.dd, parent, false);
            }
            TextView tSymbol = (TextView) convertView.findViewById(R.id.stk1);
            TextView tName = (TextView) convertView.findViewById(R.id.stk2);
            tSymbol.setText(stk.symb);
            String str=stk.name + "(" + stk.exch + ")";
            tName.setText(str);
            return convertView;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        try {
                            AsyncJson obj = new AsyncJson();
                            stocklist=obj.execute(constraint.toString()).get();

                            for(int i=0;i<stocklist.size();i++){
                                System.out.println(stocklist.get(i).symb);
                            }
                        } catch (Exception e) {
                            Log.e("myException", e.getMessage());
                        }
                        if(constraint.length()>=3){
                        filterResults.values = stocklist;
                        filterResults.count = stocklist.size();
                        }
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }
    class AsyncJson extends AsyncTask<String, Void, ArrayList<Stock>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread (new Thread(new Runnable() {
                public void run() {
                    MainActivity.au.setVisibility(View.VISIBLE);
                }
            }));

        }

        @Override
        protected void onPostExecute(ArrayList<Stock> stocks) {
            super.onPostExecute(stocks);
            runOnUiThread (new Thread(new Runnable() {
                public void run() {
                    MainActivity.au.setVisibility(View.GONE);
                }
            }));
        }

        @Override
        protected ArrayList<Stock> doInBackground(String... strings) {
            String newText = strings[0];
            ArrayList<Stock> arr = new ArrayList<>();
            try{
                URL url = new URL("http://stocksearchh.appspot.com/index.php/?input="+newText);
                HttpURLConnection httpc = (HttpURLConnection) url.openConnection();
                httpc.setRequestMethod("GET");
                BufferedReader in = new BufferedReader(new InputStreamReader(httpc.getInputStream()));

                String jsonText = readAll(in);
                jsonText=jsonText.replaceAll("^\"|\"$|\\\\","");
                JSONArray jsonArr = new JSONArray(jsonText);
                for(int i = 0; i<jsonArr.length(); i++)
                {
                    JSONObject jsonobj = jsonArr.getJSONObject(i);
                    Stock stock = new Stock(jsonobj);
                    stock.symb = jsonobj.getString("Symbol");
                    stock.name = jsonobj.getString("Name");
                    stock.exch = jsonobj.getString("Exchange");
                    arr.add(stock);
                }
                httpc.disconnect();

            }catch (JSONException | IOException e){
                e.printStackTrace();
            }
            return arr;
        }

        private String readAll(Reader rd) throws IOException {
            StringBuilder sb = new StringBuilder();
            int cp;
            while((cp = rd.read())!= -1){
                sb.append((char) cp);
            }
            return sb.toString();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button valid_Btn=(Button)findViewById(R.id.get_quote);
        Button clr_btn = (Button) findViewById(R.id.clear);
        final Switch switch_btn = (Switch) findViewById(R.id.switch1);
        ImageView ref_btn = (ImageView) findViewById(R.id.imageView);
        assert clr_btn != null;
        assert valid_Btn != null;
        assert switch_btn != null;
        assert ref_btn != null;
        final AutoCompleteTextView sym_btn = (AutoCompleteTextView) findViewById(R.id.symb);
        final AutoAdapter aca=new AutoAdapter(context,R.layout.dd);
        assert sym_btn != null;
        final int inter=5000;
         hand = new Handler();
        pb=(ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.GONE);
        au=(ProgressBar) findViewById(R.id.progressBar1);
        au.setVisibility(View.GONE);
        sym_btn.setAdapter(aca);
        sym_btn.setThreshold(1);

        valid_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final String symboll = sym_btn.getText().toString();
            if (symboll.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Please enter Stock Name/Symbol")
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
            else{
                if(validateInput(aca,symboll.toLowerCase())){
                    getSymbol(symboll);
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Please select Stock Name/Symbol from the given options")
                            .setCancelable(false)
                            .setPositiveButton("OK", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            }
        });
        ref_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                Map<String,?> keys = settings.getAll();
                if(settings.getAll().size()>1)
                    fav();
            }
        });
        final Runnable ref=new Runnable() {
            @Override
            public void run() {
                fav();
                hand.postDelayed(this,inter);
            }
        };
        switch_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListView list = (ListView)findViewById(R.id.listVieww);
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                Map<String,?> keys = settings.getAll();
                if(settings.getAll().size()>1) {
                    if (switch_btn.isChecked())
                        ref.run();
                    else
                        hand.removeCallbacks(ref);
                }
            }
        });

        clr_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sym_btn.setText("");
            }
        });
        sym_btn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Stock stock = (Stock) adapterView.getItemAtPosition(position);
                sym_btn.setText(stock.symb);
            }
        });

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String,?> keys = settings.getAll();
        if(settings.getAll().size()>1)
            fav();
    }
//    public void favt(boolean chk){
//
//        System.out.println(chk);
//    }
    public void fav(){
        arrr.clear();
        arr.clear();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Map<String,?> keys = settings.getAll();
        if(settings.getAll().size()>0)
            for(Map.Entry<String,?> entry : keys.entrySet()){
                if(!(entry.getValue().toString().replaceAll("[^0-9]","")).equals(""))
                    arr.add(entry.getValue().toString());
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
            String str=arr.get(i).replaceAll("[0-9]", "");
            System.out.println(str);
            arr.set(i,str);
        }



        x=0;
        favtemp(arr.get(0));
        final FavAdapter aca=new FavAdapter(this,arrr);
        DynamicListView list = (DynamicListView)findViewById(R.id.listVieww);
        list.setDivider(null);
        assert list != null;
        list.enableSwipeToDismiss(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull ViewGroup listView, @NonNull int[] reverseSortedPositions) {
                for(int position:reverseSortedPositions){

                    deleteFavoriteItemAlert(arrr.get(position).sym,position,aca);
                }
                aca.notifyDataSetChanged();
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String sym=arrr.get(i).sym;
                getSymbol(sym);
            }
        });
        assert list != null;
        list.setAdapter(aca);
//        aca.setNotifyOnChange(true);
//        aca.notifyDataSetChanged();
    }
    private void deleteFavoriteItemAlert(final String name, final int position, final FavAdapter adapter){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage("Want to delete "+name+" from favorites?");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        arrr.remove(position);
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove(name);
                        editor.commit();
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    int x=0;
    public void favtemp(String symb){
        AsyncJsonDataa obj = new AsyncJsonDataa();
        JSONObject jobj=null;
        try {
            jobj=obj.execute(symb).get().getJSONObject("details");
            stk st=new stk(jobj.getString("Symbol"),jobj.getString("Name"),jobj.getDouble("LastPrice"),jobj.getDouble("ChangePercent"),jobj.getDouble("MarketCap"));
            arrr.add(st);
        } catch (InterruptedException | JSONException | ExecutionException e) {
            e.printStackTrace();
        }
        if(x<arr.size()-1){
            x++;
            favtemp(arr.get(x));
        }
        else{
            hand.postDelayed(new Runnable() {
                @Override
                public void run() {

                    pb.setVisibility(View.GONE);
                }
            },1000);
        }
    }


    public boolean validateInput(AutoAdapter aca,String str){
        ArrayList<Stock> arr=aca.getItems();
        for(int i=0;i<arr.size();i++){
            if(str.equals(arr.get(i).symb.toLowerCase())){
                return true;
            }
        }
        return false;
    }
    public void getSymbol(String str){
        System.out.println(str);
        try{
            AsyncJsonData obj = new AsyncJsonData();
            JSONObject jobj=obj.execute(str).get();
            Intent myIntent = new Intent(MainActivity.this, ResultActivity.class);
            myIntent.putExtra("details", jobj.get("details").toString());
            myIntent.putExtra("chart", jobj.get("chart").toString());
            myIntent.putExtra("ynews", jobj.get("ynews").toString());
            if(((JSONObject)jobj.get("details")).get("Status").toString().equals("SUCCESS"))
                MainActivity.this.startActivity(myIntent);
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Data not available")
                        .setCancelable(false)
                        .setPositiveButton("OK", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        catch (Exception e) {
                Log.e("myException", e.getMessage());
        }
    }
}
