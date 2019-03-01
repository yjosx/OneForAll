package pow.jie.oneforall;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import pow.jie.oneforall.adapter.MainViewPagerAdapter;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.util.ActivityCollector;
import pow.jie.oneforall.util.BaseActivity;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private long mExitTime = 0;//记录按键时间，用于双击退出。
    private List<List<ContentItem>> pages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initCards();
    }

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if ((currentTime - mExitTime) >= 2000) {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            mExitTime = currentTime;
        } else {
            ActivityCollector.finishAll();
        }
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        //全面屏适配
//        LinearLayout linearLayout = findViewById(R.id.nav_header_linear_layout);
//        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
//        if (Build.VERSION.SDK_INT >= 28) {
//            View decorView = getWindow().getDecorView();
//            DisplayCutout displayCutout = decorView.getRootWindowInsets().getDisplayCutout();
//            if (displayCutout != null)
//                if (displayCutout.getSafeInsetTop() != 0) {
//                    Log.d(TAG, "刘海屏手机，api28");
//                    params.height = linearLayout.getHeight() + displayCutout.getSafeInsetTop();
//                    linearLayout.setLayoutParams(params);
//                }
//        } else {
//            int statusBarHeight = 66;
//            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//            if (resourceId > 0)
//                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
//            Log.d(TAG, "statusBarHeight=" + statusBarHeight);
//            if (statusBarHeight > 66) {
//                params.height = statusBarHeight + linearLayout.getHeight();
//                linearLayout.setLayoutParams(params);
//            }
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_return) {
            ViewPager viewPager = findViewById(R.id.vp_main);
            viewPager.setCurrentItem(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_essay) {
            Intent intent = new Intent(this, ContentListActivity.class);
            startActivity(intent);
//        } else if (id == R.id.nav_serial) {
//        } else if (id == R.id.nav_question) {
//        } else if (id == R.id.nav_setting) {
        } else if (id == R.id.nav_about) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setMessage("复杂的世界里 一个就够了\nOneForAll" + BuildConfig.VERSION_NAME);
            dialog.setNegativeButton("了解了", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void initCards() {
        Log.d(TAG, "initCards");

        SharedPreferences sharedPreferences = getSharedPreferences("idList", MODE_PRIVATE);
        for (int i = 0; i < 10; i++) {
            String idListId = sharedPreferences.getString("day" + i, "0");
            Log.d(TAG, "initCards: "+idListId);
            List<ContentItem> contentItems = LitePal
                    .where("idListId = ?", idListId)
                    .find(ContentItem.class);
            pages.add(contentItems);
        }
        if (pages != null) {
            Log.d(TAG, "initCards: "+pages);
            ViewPager vp = findViewById(R.id.vp_main);
            vp.setAdapter(new MainViewPagerAdapter(MainActivity.this, pages));
        } else {
            Log.d(TAG, "initCards: pages没有内容。");
            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
        }
    }

}
