package com.example.myapp;

import android.app.Fragment;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import org.apache.http.util.EncodingUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by UI on 2015/12/29.
 */
public class CommonFragment extends Fragment {

    private WebView webView = null;
    public WebView getWebView(){
        return this.webView;
    }
    public void setWebView(WebView webView){
        this.webView = webView;
    }
    protected WebView initWebview(View view, int progress_bar_id,int webview_id,String uri){
        return this.initWebview(view,progress_bar_id,webview_id,uri,new HashMap());
    }
    protected WebView initWebview(View view, int progress_bar_id, int webview_id, String uri,HashMap params){
        final ProgressBar bar = (ProgressBar)view.findViewById(progress_bar_id);
        final WebView webView = (WebView)view.findViewById(webview_id);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    bar.setProgress(100);
//                    bar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == bar.getVisibility()) {
                        bar.setVisibility(View.VISIBLE);
                    }
                    bar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });
        HashMap pmap = new HashMap();
        pmap.put("phone",Session.getData("phone"));
        pmap.put("password",Session.getData("password"));
        Iterator iter = params.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            pmap.put(entry.getKey().toString(),entry.getValue().toString());
        }
        iter = pmap.entrySet().iterator();
        String strParams = "";
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            strParams = strParams.concat(entry.getKey().toString()+"="+entry.getValue().toString()).concat("&");
        }
        webView.postUrl(uri, EncodingUtils.getBytes(strParams,"base64"));
        setWebView(webView);
        return webView;
    }
}
