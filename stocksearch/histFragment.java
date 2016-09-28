package com.example.zaranadesai.stocksearch;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONObject;

public class histFragment extends Fragment {
    JSONObject hist=null;
    String temp=null;
    private OnFragmentInteractionListener mListener;

    public histFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_hist, container, false);
        hist=((ResultActivity)getActivity()).getHist();
        temp=StringEscapeUtils.escapeJava(hist.toString());
        setView(view);
        return view;
    }

    void setView(View view){
        final WebView webview =(WebView)view.findViewById(R.id.webView);
        //temp="zarana";
        try {
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new JSONdata(), temp);
            webview.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    webview.loadUrl("javascript:init('" + temp + "')");
                }
            });
            webview.loadUrl("file:///android_asset/hc.html");

        }
        catch (Exception e){
            e.printStackTrace();
        }
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    class JSONdata {

        @JavascriptInterface
        public String toString(){
            return temp;
        }
    }
}


