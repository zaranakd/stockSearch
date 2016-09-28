package com.example.zaranadesai.stocksearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

class newsVal{
    String title;
    String content;
    String pub;
    String date;
    String url;
    public newsVal(String a, String b, String c, String d, String e){
        title=a;
        content=b;
        pub=c;
        date=d;
        url=e;
    }
}


class newsAdapter extends ArrayAdapter<newsVal> {
    private ArrayList<newsVal> ne;

    public newsAdapter(Context context, ArrayList<newsVal> resource) {
        super(context, R.layout.news);
        ne = resource;
    }

    @Override
    public int getCount() {
        return ne.size();
    }

    @Override
    public newsVal getItem(int position) {
        return ne.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        newsVal nei = ne.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.news, parent, false);
        }
        TextView t1 = (TextView) convertView.findViewById(R.id.txt1);
        TextView t2 = (TextView) convertView.findViewById(R.id.txt2);
        TextView t3 = (TextView) convertView.findViewById(R.id.txt3);
        TextView t4 = (TextView) convertView.findViewById(R.id.txt4);
        t1.setText(Html.fromHtml("<a href=\""+nei.url+"\" target=\"_blank\">"+nei.title+"</a>"));
        t1.setMovementMethod(LinkMovementMethod.getInstance());
        t2.setText(nei.content);
        t3.setText(nei.pub);
        t4.setText(nei.date);
        return convertView;
    }
}


public class newsFragment extends Fragment {
    JSONObject news=null;

    private OnFragmentInteractionListener mListener;
    public newsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=  inflater.inflate(R.layout.fragment_news, container, false);
        ListView list = (ListView)view.findViewById(R.id.listView);
        news=((ResultActivity)getActivity()).getNews();
        ArrayList<newsVal> neww=new ArrayList<>();
        try {
            JSONArray array=((JSONObject)news.get("d")).getJSONArray("results");
            for(int i=0;i<array.length();i++){
                JSONObject o= (JSONObject) array.get(i);
                neww.add(getObj(o.get("Title").toString(),o.get("Description").toString(),"Publisher : "+o.get("Source").toString(),"Date : "+o.get("Date").toString(),o.get("Url").toString()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final newsAdapter aca=new newsAdapter(getContext(),neww);
        assert list != null;
        list.setAdapter(aca);
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

    private newsVal getObj(String a, String b, String c, String d,String e){
        return new newsVal(a,b,c,d,e);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
