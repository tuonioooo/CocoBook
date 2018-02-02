package com.copasso.cocobook.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import com.copasso.cocobook.R;
import com.copasso.cocobook.model.bean.BmobBook;
import com.copasso.cocobook.model.bean.CollBookBean;
import com.copasso.cocobook.model.local.BookRepository;
import com.copasso.cocobook.model.remote.RemoteRepository;
import com.copasso.cocobook.ui.base.BaseTabActivity;
import com.copasso.cocobook.ui.fragment.BookShelfFragment;
import com.copasso.cocobook.ui.fragment.BookDiscoverFragment;
import com.copasso.cocobook.ui.fragment.CommunityFragment;
import com.copasso.cocobook.utils.*;
import com.copasso.cocobook.ui.dialog.SexChooseDialog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主界面activity
 */
public class MainActivity extends BaseTabActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private View drawerHeader;
    private ImageView drawerIv;
    private TextView drawerTvAccount, drawerTvMail;

    private BookShelfFragment bookShelfFragment;
    /*************Constant**********/
    private static final int WAIT_INTERVAL = 2000;
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /***************Object*********************/

    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private PermissionsChecker mPermissionsChecker;
    /*****************Params*********************/
    private boolean isPrepareFinish = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_tab;
    }

    /**************init method***********************/
    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("CocoBook");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        drawerHeader = navigationView.inflateHeaderView(R.layout.drawer_header);
        drawerIv = (ImageView) drawerHeader.findViewById(R.id.drawer_iv);
        drawerTvAccount = (TextView) drawerHeader.findViewById(R.id.drawer_tv_name);
        drawerTvMail = (TextView) drawerHeader.findViewById(R.id.drawer_tv_mail);
    }

    @Override
    protected List<Fragment> createTabFragments() {
        initFragment();
        return mFragmentList;
    }

    private void initFragment() {
        bookShelfFragment=new BookShelfFragment();
        mFragmentList.add(bookShelfFragment);
        mFragmentList.add(new BookDiscoverFragment());
        mFragmentList.add(new CommunityFragment());
    }

    @Override
    protected List<String> createTabTitles() {
        String[] titles = getResources().getStringArray(R.array.nb_fragment_title);
        return Arrays.asList(titles);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        //第一：默认初始化
        Bmob.initialize(this, "3f3b7628bf00914940a6919da16b33bf");
        //实现侧滑菜单状态栏透明
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        //性别选择框
        showSexChooseDialog();

        setUpDrawerHeader();

        drawerHeader.setOnClickListener(view -> {
            if (BmobUser.getCurrentUser()==null)
                startActivity(new Intent(mContext,LandActivity.class));
        });

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpDrawerHeader() {
        if (BmobUser.getCurrentUser()!=null){
            drawerTvAccount.setText(BmobUser.getCurrentUser().getUsername());
            drawerTvMail.setText(BmobUser.getCurrentUser().getEmail());
        }
    }

    private void showSexChooseDialog() {
        String sex = SharedPreUtils.getInstance()
                .getString(Constant.SHARED_SEX);
        if (sex.equals("")) {
            mVp.postDelayed(() -> {
                Dialog dialog = new SexChooseDialog(this);
                dialog.show();
            }, 500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (menu != null && menu instanceof MenuBuilder) {
            try {
                Method method = menu.getClass().
                        getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    /**
     * 请求权限
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // 如果取消权限，则返回的值为0
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //跳转到 LocalBookActivity
                    Intent intent = new Intent(this, LocalBookActivity.class);
                    startActivity(intent);

                } else {
                    ToastUtils.show("用户拒绝开启读写权限");
                }
                return;
            }
        }
    }

    /**
     * 双击退出
     */
    @Override
    public void onBackPressed() {
        if (bookShelfFragment.isMultiSelectMode()){
            bookShelfFragment.cancelMultiSelectMode();
            return;
        }
        if (!isPrepareFinish) {
            mVp.postDelayed(
                    () -> isPrepareFinish = false, WAIT_INTERVAL
            );
            isPrepareFinish = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 标题栏点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Class<?> activityCls = null;
        switch (id) {
            case R.id.action_search:
                activityCls = SearchActivity.class;
                break;
        }
        if (activityCls != null) {
            Intent intent = new Intent(this, activityCls);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 侧滑菜单点击事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Class<?> activityCls = null;
        switch (id) {
            case R.id.action_search:
                activityCls = SearchActivity.class;
                break;
            case R.id.action_my_message:
                break;
            case R.id.action_download:
                activityCls = DownloadActivity.class;
                break;
            case R.id.action_sync_bookshelf:
                if (BmobUser.getCurrentUser()==null) break ;
                ProgressUtils.show(mContext,"正在同步");
                RemoteRepository.getInstance().syncBooks(BmobUser.getCurrentUser()
                        , new RemoteRepository.SyncBookListener() {
                            @Override
                            public void onSuccess(List<CollBookBean> list) {
                                ProgressUtils.dismiss();
                                BookRepository.getInstance()
                                        .saveCollBooks(list);
                                ToastUtils.show("同步完成");
                            }

                            @Override
                            public void onError(Throwable e) {
                                ProgressUtils.dismiss();
                            }
                        });
                break;
            case R.id.action_scan_local_book:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                    if (mPermissionsChecker == null) {
                        mPermissionsChecker = new PermissionsChecker(this);
                    }

                    //获取读取和写入SD卡的权限
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        //请求权限
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE);
                        return super.onOptionsItemSelected(item);
                    }
                }

                activityCls = LocalBookActivity.class;
                break;
//            case R.id.action_wifi_book:
//                break;
            case R.id.action_feedback:
                break;
//            case R.id.action_night_mode:
//                showUpdateThemeDialog();
//                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);//切换夜间模式
//                recreate();//重新启动当前activity
//                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }
        drawer.closeDrawer(Gravity.LEFT);
        if (activityCls != null) {
            Intent intent = new Intent(mContext, activityCls);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 显示修改主题色 Dialog
     */
    private void showUpdateThemeDialog() {
        final String[] themes = ThemeManager.getInstance().getThemes();
        new AlertDialog.Builder(mContext)
                .setTitle("选择主题")
                .setItems(themes, (dialog, which) -> {
                    ThemeManager.getInstance().setTheme(mContext, themes[which]);
                }).create().show();
    }

}