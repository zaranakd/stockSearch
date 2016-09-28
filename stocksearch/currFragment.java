package com.example.zaranadesai.stocksearch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;


class currVal{
    String key;
    String value;
    public currVal(String k, String v){
        key=k;
        value=v;
    }
}
class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;
    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    protected Bitmap doInBackground(String...urls){
        String urlOfImage = urls[0];
        Bitmap logo = null;
        try{
            URL urlConnection = new URL(urlOfImage);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream is = connection.getInputStream();

            logo = BitmapFactory.decodeStream(is);
        }catch(Exception e){
            e.printStackTrace();
        }
        return logo;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        imageView.setImageBitmap(result);
    }
}

public class currFragment extends Fragment {


    class currAdapter extends ArrayAdapter<currVal> {

        class ImageGetter implements Html.ImageGetter {

            public Drawable getDrawable(String source) {
                int id;
                if (source.equals("down.png")) {
                    id = R.drawable.down;
                }
                else if (source.equals("up.png")) {
                    id = R.drawable.up;
                }
                else {
                    return null;
                }
                Drawable d = getContext().getResources().getDrawable(id);
                d.setBounds(0,0,d.getIntrinsicWidth(),d.getIntrinsicHeight());
                return d;
            }
        }
        public ArrayList<currVal> curr;

        public currAdapter(Context context, ArrayList<currVal> resource) {
            super(context, R.layout.text);
            curr = resource;
        }

        @Override
        public int getCount() {
            return curr.size();
        }

        @Override
        public currVal getItem(int position) {
            return curr.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            currVal curri = curr.get(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.text, parent, false);
            }
            TextView tSymbol = (TextView) convertView.findViewById(R.id.txt1);
            TextView tName = (TextView) convertView.findViewById(R.id.txt2);
            ImageView iv = (ImageView)convertView.findViewById(R.id.stock_img);
            //if(curri.key.equals("Today's Stock Activity")){
            if(position==curr.size()-1){
                iv.setVisibility(View.VISIBLE);
                tSymbol.setText(curri.key);
                String imgURL = null;
                imgURL = "http://chart.finance.yahoo.com/t?s="+curri.value+"&lang=en-US";
                new DownLoadImageTask(iv).execute(imgURL+"&width=3000&height=2000");
                tName.setVisibility(View.GONE);
                assert iv != null;
                iv.setOnClickListener(new ImageView.OnClickListener(){

                    @Override
                    public void onClick(View arg0) {

                        // custom dialog
                        final Dialog dialog = new Dialog(view.getContext());
                        dialog.setContentView(R.layout.opup);

                        final ImageView img = (ImageView)dialog.findViewById(R.id.charts1);
                        PhotoViewAttacher mAttacher = new PhotoViewAttacher(img);
                        new DownLoadImageTask(img).execute("http://chart.finance.yahoo.com/t?s="+curr.get(1).value+"&lang=en-US&width=4000&height=3000");


                        LinearLayout btnDismiss = (LinearLayout)view.findViewById(R.id.idddd);
                        btnDismiss.setOnClickListener(new LinearLayout.OnClickListener(){

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                dialog.dismiss();
                            }});

                        dialog.show();
                    }});
            }
            else{
                iv.setVisibility(View.GONE);
                tName.setVisibility(View.VISIBLE);
                tSymbol.setText(curri.key);
                tName.setText(Html.fromHtml(curri.value, new ImageGetter(), null));
            }
            return convertView;
        }
    }

    JSONObject details=null;
    private OnFragmentInteractionListener mListener;
    public currFragment() {
    }
View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_curr, container, false);
        ListView list = (ListView)view.findViewById(R.id.listView);
        DecimalFormat df2 = new DecimalFormat("#.##");
        ArrayList<currVal> curr=new ArrayList<>();
        ImageView iv = (ImageView)view.findViewById(R.id.stock_img);
        details=((ResultActivity)getActivity()).getDetail();
        try {
            curr.add(getObj("NAME", (String) details.get("Name")));
            curr.add(getObj("SYMBOL", (String) details.get("Symbol")));
            curr.add(getObj("LASTPRICE", ""+ details.get("LastPrice")));
            if(Double.parseDouble(details.get("Change").toString())>0)
                curr.add(getObj("CHANGE", df2.format(details.get("Change"))+"( +"+df2.format(details.get("ChangePercent"))+"%)"+"<img src=\"up.png\">"));
            else
                curr.add(getObj("CHANGE", df2.format(details.get("Change"))+"( "+df2.format(details.get("ChangePercent"))+"%)"+"<img src=\"down.png\">"));
//            DateFormat df = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
//            Date parsedDate = (Date) df.parse(details.get("Timestamp").toString());
//            System.out.println("hiiiiiiiiiii..............");
//            System.out.println(parsedDate);
            SimpleDateFormat date_formatter = new SimpleDateFormat("EEE MMMM dd HH:mm:ss zZ yyyy");
            Date date_string = date_formatter.parse(details.get("Timestamp").toString());
            date_formatter = new SimpleDateFormat("dd MMMM yyyy, HH:mm:ss");
            String formatted_time = date_formatter.format(date_string);
            curr.add(getObj("TIMESTAMP",  formatted_time));
            if(Long.parseLong(details.get("MarketCap").toString())<1000000)
                curr.add(getObj("MARKETCAP",  ""+details.get("MarketCap")));
            else if(Long.parseLong(details.get("MarketCap").toString())<1000000000)
                curr.add(getObj("MARKETCAP",  ""+df2.format(Double.parseDouble(details.get("MarketCap").toString())/1000000)+" Million"));
            else
                curr.add(getObj("MARKETCAP",  ""+df2.format(Double.parseDouble(details.get("MarketCap").toString())/1000000000)+" Billion"));
            if(Long.parseLong(details.get("Volume").toString())<1000000)
                curr.add(getObj("VOLUME",  ""+details.get("Volume")));
            else if(Long.parseLong(details.get("Volume").toString())<1000000000)
                curr.add(getObj("VOLUME",  ""+df2.format(Double.parseDouble(details.get("Volume").toString())/1000000)+" Million"));
            else
                curr.add(getObj("VOLUME",  ""+df2.format(Double.parseDouble(details.get("Volume").toString())/1000000000)+" Billion"));
            double dr=Double.parseDouble(details.get("ChangeYTD").toString());
            if(Double.parseDouble(details.get("ChangePercentYTD").toString())>0)
                curr.add(getObj("CHANGE YTD", df2.format(dr)+"( +"+df2.format(details.get("ChangePercentYTD"))+"%)"+"<img src=\"up.png\">"));
            else
                curr.add(getObj("CHANGE YTD", df2.format(dr)+"( "+df2.format(details.get("ChangePercentYTD"))+"%)"+"<img src=\"down.png\">"));
            curr.add(getObj("HIGH",  ""+details.get("High")));
            curr.add(getObj("LOW",  ""+details.get("Low")));
            curr.add(getObj("OPEN",  ""+ details.get("Open")));
            curr.add(getObj("Today's Stock Activity",details.get("Symbol")+""));

        }
        catch (JSONException e){
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final currAdapter aca=new currAdapter(getContext(),curr);
        assert list != null;
        list.setAdapter(aca);
//        String imgURL = null;
//        try {
//            imgURL = "http://chart.finance.yahoo.com/t?s="+details.get("Symbol")+"&lang=en-US";
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        new DownLoadImageTask(iv).execute(imgURL+"&width=400&height=250");
//        final String finalImgURL = imgURL;
//        iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Dialog d=new Dialog(view.getContext());
//                d.setContentView(R.layout.opup);
//                final ImageView img=(ImageView)d.findViewById(R.id.charts1);
//                PhotoViewAttacher mAttacher =new PhotoViewAttacher(img);
//                new DownLoadImageTask(img).execute(finalImgURL +"&width=3000&height=4000");
//                LinearLayout btnDismiss=(LinearLayout)view.findViewById(R.id.LL);
//                //btnDismiss.setOnClickListener((v){d.dismiss();});
//            }
//        });
//        iv.setOnClickListener((arg0){
//                final Dialog d=new Dialog(view.getContext());
//                d.setContentView(R.layout.opup);
//                final ImageView img=(ImageView)d.findViewById(R.id.charts1);
//                mAttacher =new PhotoViewAttacher(img);
//                new DownLoadImageTask(img).execute(imgURL+"&width=3000&height=4000");
//        LinearLayout btnDismiss=(LinearLayout)view.findViewById(R.id.LL);
//        btnDismiss.setOnClickListener((v){d.dismiss();});
//
//                }
//
//        );
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private currVal getObj(String k, String v){
        return new currVal(k,v);
    }
    public interface OnFragmentInteractionListener {
       void onFragmentInteraction(Uri uri);
    }
}
