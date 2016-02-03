package com.example.myapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;
import model.Cookie;
import model.Login;
import java.util.ArrayList;

/**
 * Created by UI on 2015/12/26.
 */
public class DashboardActivity extends Activity {
    //底部按钮集合
    private ArrayList btnsBottom = new ArrayList();
    private Fragment activeFragment;
    private ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
    private DashboardActivity self = this;
    private Fragment businessFragment;
    private Fragment accountingFragment;
    private Fragment taxFragment;
    private TextView btnService;
    private TextView btnNews;
    private LinearLayout businessBtn;
    private LinearLayout accountingBtn;
    private LinearLayout taxBtn;
    private LinearLayout menuBelow;
    private LinearLayout menuWrap;
    private GestureDetector gestureDetector;
    private MenuListener menuListener;
    private TextView company;
    private LinearLayout listEdit;
    private LinearLayout listLogout;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.dashboard);
        businessFragment = new BusinessFragment();
        accountingFragment = new AccountingFragment();
        taxFragment = new TaxFragment();
        btnService = (TextView) findViewById(R.id.btn_service);
        btnNews = (TextView) findViewById(R.id.btn_news);
        company = (TextView)findViewById(R.id.company);
        listEdit = (LinearLayout)findViewById(R.id.list_edit);
        listLogout = (LinearLayout)findViewById(R.id.list_logout);
        menuBelow = (LinearLayout)findViewById(R.id.menu_below);
        //暂时隐藏menuBelow
        menuBelow.setVisibility(View.INVISIBLE);
        menuWrap = (LinearLayout)findViewById(R.id.menu_wrap);
        businessBtn = (LinearLayout) findViewById(R.id.business_btn);
        accountingBtn = (LinearLayout) findViewById(R.id.accounting_btn);
        taxBtn = (LinearLayout) findViewById(R.id.tax_btn);
        btnsBottom.add(businessBtn);
        btnsBottom.add(accountingBtn);
        btnsBottom.add(taxBtn);
        //load fonts
        int[] icons = {R.id.header_menu,R.id.msg,R.id.icon_business,R.id.icon_accounting, R.id.icon_tax,
                R.id.menu_home,R.id.menu_edit,R.id.menu_logout,R.id.menu_company,R.id.menu_version};
        //IconFont.loadFont(this,arr);
        IconFont.loadFont(this,icons);
        //banner toggle
        final ViewFlipper vf = (ViewFlipper) findViewById(R.id.nav);
        vf.setInAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_in_left));
        vf.setOutAnimation(AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.slide_out_right));

        TextView msg = (TextView)findViewById(R.id.msg);
        msg.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                overridePendingTransition(android.R.anim.cycle_interpolator,android.R.anim.fade_out);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                //清空session中消息条数
                Session.remove("msg");
                TextView point = (TextView) findViewById(R.id.red_point);
                point.setVisibility(View.INVISIBLE);
                point.setText("");
                Intent intent = new Intent();
                intent.setClass(DashboardActivity.this,MsgActivity.class);
                startActivity(intent);
            }
        });

        bannerRegister(vf);
        ColorStateList focusColor = getResources().getColorStateList(R.color.button_text_focus);
        btnService.setTextColor(focusColor);
//        btnService.performClick();
        btnsRegister();
        businessBtn.performClick();
        //menu
        TextView header_menu = (TextView) findViewById(R.id.header_menu);
        header_menu.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                menuListener.callout();
            }
        });
        menuRegister();
        //初始化公司名称
        String company = Session.getData("company");
        if(!company.equals("")){
            this.company.setText(company);
        }
        //修改密码和登出
        modifyRegister();
        logoutRegister();
        //检查版本更新
        VersionUpdate versionUpdate = new VersionUpdate(self);
        versionUpdate.checkVersion();
        //定时检查消息更新
        startService();
    }

    private void addFragmentListener(Fragment ftl){
        fragmentList.add(ftl);
    }

    private void clearFragmentListener(){
        fragmentList.clear();
    }

    private void resetBtn(LinearLayout focusBtn){
        for(int i = 0;i < btnsBottom.size();i++){
            LinearLayout btn = (LinearLayout)btnsBottom.get(i);
            TextView c1 = (TextView)btn.getChildAt(0);
            TextView c2 = (TextView)btn.getChildAt(1);
            c1.setTextColor(getResources().getColor(R.color.gray));
            c2.setTextColor(getResources().getColor(R.color.gray));
        }
        //focus color change
        TextView c1 = (TextView)focusBtn.getChildAt(0);
        TextView c2 = (TextView)focusBtn.getChildAt(1);
        c1.setTextColor(getResources().getColor(R.color.blue));
        c2.setTextColor(getResources().getColor(R.color.blue));
    }

    private void menuRegister(){
        menuListener = new MenuListener();
        menuListener.setMenuBelow(menuBelow);
        menuListener.setMenuWrap(menuWrap);
        gestureDetector = new GestureDetector(this,menuListener);
        //点击menuBelow恢复
        menuBelow.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                menuReset();
            }
        });
    }

    private void menuReset(){
        menuBelow.setAlpha(0.0F);
        menuBelow.setVisibility(View.INVISIBLE);
        ObjectAnimator.ofFloat(menuWrap,"X",-menuWrap.getWidth())
                .setDuration(100)
                .start();
        ObjectAnimator.ofFloat(menuBelow,"alpha",0.0F)
                .setDuration(100)
                .start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        menuListener.onTouch(event);
        return true;
    }

    public void switchContent(Fragment from, Fragment to, FragmentManager mFragmentMan) {
        if (from != to) {
            FragmentTransaction transaction = mFragmentMan.beginTransaction();
//                    .setCustomAnimations(
//                    android.R.anim.fade_in, android.R.anim.fade_out);
            if(from == null){
                transaction.add(R.id.content,to).commit();
                fragmentList.add(to);
            }else {
                clearFragmentListener();
                addFragmentListener(to);
                if (!to.isAdded()) {
                    transaction.hide(from).add(R.id.content, to).commit();
                } else {
                    transaction.hide(from).show(to).commit();
                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for(Fragment listener : fragmentList){
            RichFragment ftl = (RichFragment)listener;
            ftl.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void btnsRegister(){
        businessBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchContent(activeFragment,businessFragment,getFragmentManager());
                activeFragment = businessFragment;
                resetBtn(businessBtn);
            }
        });
        accountingBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchContent(activeFragment,accountingFragment,getFragmentManager());
                activeFragment = accountingFragment;
                resetBtn(accountingBtn);
            }
        });
        taxBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                switchContent(activeFragment,taxFragment,getFragmentManager());
                activeFragment = taxFragment;
                resetBtn(taxBtn);
            }
        });
    }

    private void bannerRegister(final ViewFlipper vf){
        Resources resource = getBaseContext().getResources();
        final ColorStateList focusColor = resource.getColorStateList(R.color.button_text_focus);
        final ColorStateList baseColor = resource.getColorStateList(R.color.button_text_base);

        btnService.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(vf.getDisplayedChild() == 1){
                    btnService.setTextColor(focusColor);
                    btnNews.setTextColor(baseColor);
                    vf.showPrevious();
                }
            }
        });
        btnNews.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(vf.getDisplayedChild() == 0){
                    btnNews.setTextColor(focusColor);
                    btnService.setTextColor(baseColor);
                    vf.showNext();
                }
            }
        });
    }

    private void modifyRegister(){
        listEdit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(self,EditPassActivity.class);
                startActivity(intent);
            }
        });
    }

    private void logoutRegister(){
        listLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //delete phone and password
                Login login = new Login(self);
                login.deleteAll();
                //delete cookie
                Cookie cookie = new Cookie(self);
                cookie.delete();
                Session.setData("cookie","");
                //toggle activity
                Intent intent = new Intent();
                intent.setClass(self,LoginActivity.class);
                startActivity(intent);
                self.finish();
            }
        });
    }

    public void startService(){
        startService(new Intent(getBaseContext(), MyService.class));
    }
    public void onStart(){
        super.onStart();
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(activeFragment != null){
//            WebView webView = activeFragment.getWebView();
//            if(webView != null){
//                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
//                    webView.goBack();
//                    return true;
//                }
//            }else{
////                return super.onKeyDown(keyCode, event);
//                return false;
//            }
//
//        }
//        return super.onKeyDown(keyCode, event);
        return false;
    }
    public void onResume(){
        final Activity self = this;
        //每次恢复都检查消息更新，更新相应UI
        String count = Session.getData("msg");
        if(!count.equals("")){
            TextView point = (TextView)findViewById(R.id.red_point);
            point.setVisibility(View.VISIBLE);
            point.setText(count);
        }
        super.onResume();
    }
}
