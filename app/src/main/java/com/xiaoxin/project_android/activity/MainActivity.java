package com.xiaoxin.project_android.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.login.LoginActivity;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.fragment.main.CourseListFragment;
import com.xiaoxin.project_android.fragment.main.InfoFragment;
import com.xiaoxin.project_android.fragment.main.ModifyPasswordFragment;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.Static;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 列出所有课程页面
 */
@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity {

    @BindView(R.id.action_bar_title1)
    TextView titleText;
    @BindView(R.id.drawer_layout_teacher)
    DrawerLayout drawerLayout;
    @BindView(R.id.action_bar_avatar)
    ImageView imageView;

    private ImageView headerImage;
    private TextView userName;

    private NavController controller;
    private SharedPreferences preferences;

    private FragmentManager fragmentManager;
    private InfoFragment infoFragment;
    private CourseListFragment courseListFragment;
    private ModifyPasswordFragment modifyPasswordFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        titleText.setText("课程列表");
        preferences = getSharedPreferences("localRecord",MODE_PRIVATE);
        fragmentManager = getSupportFragmentManager();
        initNavigation();
        showCourseList();
    }

    @SuppressLint("SetTextI18n")
    private void initNavigation(){
        Log.d("主界面头像路径",Static.SERVICE_PATH + preferences.getString("avatar",null));
        GlideUtil.load(this,Static.SERVICE_PATH + preferences.getString("avatar",null),imageView,new RequestOptions().circleCrop());

        NavigationView navigationView = findViewById(R.id.navigation_view);

        View view = navigationView.inflateHeaderView(R.layout.nav_header);
        userName = view.findViewById(R.id.drawer_header_userName);
        headerImage = view.findViewById(R.id.drawer_header_avatar);

        Log.d("欢迎栏头像路径",Static.SERVICE_PATH + preferences.getString("avatar",null));
        GlideUtil.load(this,Static.SERVICE_PATH + preferences.getString("avatar",null), headerImage, new RequestOptions().circleCrop());

        userName.setText("欢迎您！" + preferences.getString("name", null));
        if("2".equals(preferences.getString("userType",null))){
            userName.setText("欢迎您！" + preferences.getString("name",null) + "同学");
        }

        navigationView.setCheckedItem(R.id.main_menu_course);
        navigationView.setNavigationItemSelectedListener(item -> {
            drawerLayout.closeDrawers();
            //执行具体跳转fragment的操作
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            hideAllFragment();
            switch (item.getItemId()){
                case R.id.main_menu_course:
                    titleText.setText("课程列表");
                    if (courseListFragment == null){
                        courseListFragment = new CourseListFragment(imageView,headerImage);
                        transaction.add(R.id.fragment_main,courseListFragment);
                    } else {
                        transaction.show(courseListFragment);
                    }
                    transaction.commit();
                    break;
                case R.id.main_menu_info:
                    titleText.setText("我的信息");
                    //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    if (infoFragment == null){
                        infoFragment = new InfoFragment();
                        transaction.add(R.id.fragment_main,infoFragment);
                    } else {
                        transaction.show(infoFragment);
                    }
                    transaction.commit();
                    break;
                case R.id.main_menu_password:
                    titleText.setText("修改密码");
                    //Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                    if (modifyPasswordFragment == null){
                        modifyPasswordFragment = new ModifyPasswordFragment();
                        modifyPasswordFragment.setSuccessListener(() -> {
                            hideAllFragment();
                            showCourseList();
                            navigationView.setCheckedItem(R.id.main_menu_course);
                            titleText.setText("课程列表");
                        });
                        transaction.add(R.id.fragment_main,modifyPasswordFragment);
                    } else {
                        transaction.show(modifyPasswordFragment);
                    }
                    transaction.commit();
                    break;
                case R.id.main_menu_unregister:
                    Intent intent = new Intent(this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
            return true;
        });
    }

    public void showCourseList(){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        courseListFragment = new CourseListFragment(imageView,headerImage);
        transaction.add(R.id.fragment_main, courseListFragment);
        transaction.commit();
    }

    @Override
    public boolean onSupportNavigateUp() {
        controller = Navigation.findNavController(this, R.id.fragment_main);
        return controller.navigateUp();
    }

    @OnClick({R.id.action_bar_avatar,R.id.drawer_layout_teacher})
    public void onClick(View v){
        if (!CommonUtil.isFastClick()){
            return;
        }
        if (v.getId() == R.id.action_bar_avatar) {
            //设置标题栏左部小头像的点击事件
            drawerLayout.openDrawer(GravityCompat.START);
        } else if(v.getId() == R.id.drawer_layout_teacher){
            hideSoftInput();
        }
    }

    public void hideAllFragment(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (infoFragment != null){
            fragmentTransaction.hide(infoFragment);
        }
        if (courseListFragment != null){
            fragmentTransaction.hide(courseListFragment);
        }
        if (modifyPasswordFragment != null){
            fragmentTransaction.hide(modifyPasswordFragment);
        }
        fragmentTransaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        GlideUtil.load(this,Static.SERVICE_PATH + preferences.getString("avatar",null),imageView,new RequestOptions().circleCrop());
        GlideUtil.load(this,Static.SERVICE_PATH + preferences.getString("avatar",null), headerImage, new RequestOptions().circleCrop());
        userName.setText("欢迎您！" + preferences.getString("name",null));
    }

}