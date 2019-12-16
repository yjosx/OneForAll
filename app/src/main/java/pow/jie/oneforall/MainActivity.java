package pow.jie.oneforall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pow.jie.oneforall.adapter.MainViewPagerAdapter;
import pow.jie.oneforall.databean.ContentItemBean;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.util.ActivityCollector;
import pow.jie.oneforall.base.BaseActivity;
import pow.jie.oneforall.util.OkHttpUtil;
import pow.jie.oneforall.util.SaveDataToLitePal;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private long mExitTime = 0;//记录按键时间，用于双击退出。
    private final String[] toResponse = new String[10];
    private int timesFlag = 0;//用于计数网络请求次数
    private List<List<ContentItem>> pages = new ArrayList<>();
    private ViewPager vp;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        vp = findViewById(R.id.vp_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_main);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(this::reQueryDate);

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
            dialog.setNegativeButton("了解了", (dialog1, which) -> {
            });
            dialog.show();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void reQueryDate() {
        String address = getResources().getString(R.string.id_list_url);
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.d(TAG, "reQueryDate(): queryIDFromServer加载失败");
                    Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    final String responseText = response.body().string();
                    runOnUiThread(() -> {
                        try {
                            JSONObject jsonObject = new JSONObject(responseText);
                            JSONArray idList = jsonObject.getJSONArray("data");

                            SharedPreferences.Editor editor = getSharedPreferences("idList", MODE_PRIVATE).edit();
                            for (int i = 0; i < idList.length(); i++) {
                                editor.putString("day" + i, idList.getString(i));
                                toResponse[i] = idList.getString(i);
                            }
                            editor.apply();
                            Log.d(TAG, toResponse[0] + "(0)");
                            if (!toResponse[0].equals("0")) {
                                initData(timesFlag);
                                Log.d(TAG, "queryId" + timesFlag);
                            } else {
                                Log.d(TAG, "toResponse0没有值");
                                Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Log.d(TAG, "queryIDFromServer的response为空");
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void queryDataFromServer(String address) {
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseText = response.body().string();
                    ContentItemBean contentList = new Gson().fromJson(responseText, ContentItemBean.class);
                    SaveDataToLitePal.SaveToContentList(contentList);
                    initData(timesFlag++);
                } else {
                    Log.d(TAG, "queryDataFromServer的response为空" + timesFlag);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.d(TAG, "queryDataFromServer加载失败" + timesFlag);
                    Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void initData(int i) {
        String oneListUrl = getResources().getString(R.string.one_list_url);
        switch (i) {
            case -1:
                runOnUiThread(() -> swipeRefreshLayout.setRefreshing(false));
                break;
            case 0:
                LitePal.deleteAll(ContentItem.class);
                pages.clear();
                queryDataFromServer(oneListUrl + toResponse[0] + "/0");
                break;
            case 10:
                runOnUiThread(() -> {
                    initCards();
                    swipeRefreshLayout.setRefreshing(false);
                });
                break;
            default:
                queryDataFromServer(oneListUrl + toResponse[i] + "/0");
        }
    }

    public void initCards() {
        Log.d(TAG, "initCards");

        SharedPreferences sharedPreferences = getSharedPreferences("idList", MODE_PRIVATE);
        for (int i = 0; i < 10; i++) {
            String idListId = sharedPreferences.getString("day" + i, "0");
            List<ContentItem> contentItems = LitePal
                    .where("idListId = ?", idListId)
                    .find(ContentItem.class);
            if (contentItems != null) {
                Log.d(TAG, "initCards: " + idListId);
                pages.add(contentItems);
            }
        }
        Log.d(TAG, "initCards: " + pages);
        final MainViewPagerAdapter adapter = new MainViewPagerAdapter(MainActivity.this, pages);
//        vp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
//            @Override
//            public void onPageSelected(int position) {
//                if (position > pages.size() - 2) {
//                    loadMore();
//                    vp.getAdapter().notifyDataSetChanged();
//                }
//            }
//        });
        vp.setAdapter(adapter);
    }

    public void loadMore() {
        String oneListUrl = getResources().getString(R.string.one_list_url);
        queryDataFromServer(oneListUrl + toResponse[pages.size()] + "/0");
        SharedPreferences sharedPreferences = getSharedPreferences("idList", MODE_PRIVATE);
        String idListId = sharedPreferences.getString("day" + pages.size(), "0");
        List<ContentItem> contentItems = LitePal
                .where("idListId = ?", idListId)
                .find(ContentItem.class);
        if (contentItems != null) {
            Log.d(TAG, "loadMore: " + idListId);
            pages.add(contentItems);
        }
    }
}
