package pow.jie.oneforall;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
import pow.jie.oneforall.adapter.MainPageAdapter;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.gson.ContentItemGson;
import pow.jie.oneforall.util.ActivityCollector;
import pow.jie.oneforall.util.BaseActivity;
import pow.jie.oneforall.util.EndlessRecyclerOnScrollListener;
import pow.jie.oneforall.util.OkHttpUtil;
import pow.jie.oneforall.util.SaveDataToLitePal;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private long mExitTime = 0;//记录按键时间，用于双击退出。
    private final String[] toResponse = new String[10];
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

        String idlistUrl = "http://v3.wufazhuce.com:8000/api/onelist/idlist";
        queryIdFromServer(idlistUrl);
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
            RecyclerView recyclerView = findViewById(R.id.rv_main);
            recyclerView.scrollToPosition(0);
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
            dialog.setMessage("复杂的世界里 一个就够了\nOneForAll" + " 0.1alpha");
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

    private void queryIdFromServer(String address) {
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "queryIDFromServer加载失败");
                        Toast.makeText(MainActivity.this, "网络加载失败，尝试本地加载", Toast.LENGTH_SHORT).show();
                        if (LitePal.findFirst(ContentItem.class) != null) {
                            initData(-1);
                        }
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    final String responseText = response.body().string();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(responseText);
                                JSONArray idList = jsonObject.getJSONArray("data");

                                SharedPreferences.Editor editor = getSharedPreferences("idList", MODE_PRIVATE).edit();
                                for (int i = 0; i < idList.length(); i++) {
                                    editor.putString("day" + String.valueOf(i), idList.getString(i));
                                    toResponse[i] = idList.getString(i);
                                }
                                editor.apply();
                                Log.d(TAG, toResponse[0] + "(0)");
                                if (!toResponse[0].equals("0")) {
                                    initData(0);
                                } else {
                                    Log.d(TAG, "toResponse0没有值");
                                    Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "queryIDFromServer的response为空");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
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
                    ContentItemGson contentList = new Gson().fromJson(responseText, ContentItemGson.class);
                    LitePal.deleteAll(ContentItem.class);
                    SaveDataToLitePal.SaveToContentList(contentList);
                    final String idListId = contentList.getData().getId();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<ContentItem> contentItems = LitePal
                                    .where("idListId = ?", idListId)
                                    .find(ContentItem.class);
                            pages.add(contentItems);
                            if (pages.size() != 0) {
                                initData(pages.size());
                            } else {
                                Log.d(TAG, "queryDataFromServer获取content为空");
                                Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "queryDataFromServer的response为空");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "queryDataFromServer加载失败");
                        Toast.makeText(MainActivity.this, "网络加载失败，尝试本地加载", Toast.LENGTH_SHORT).show();
                        if (LitePal.findFirst(ContentItem.class) != null) {
                            initData(-1);
                        }
                    }
                });
            }
        });
    }

    private void loadDbContentMain() {
        SharedPreferences sharedPreferences = getSharedPreferences("idList", MODE_PRIVATE);
        //没有网络，一次加载完。
        for (int i = 0; i < 10; i++) {
            String idTheDay = sharedPreferences.getString("day" + i, "0");
            if (!idTheDay.equals("0")) {
                List<ContentItem> contentItems = LitePal
                        .where("idListId = ?", idTheDay)
                        .find(ContentItem.class);
                pages.add(contentItems);
            }
            if (pages != null) {
                initCards(0);
            } else {
                Log.d(TAG, "loadDbContentMain获取content为空");
                Toast.makeText(MainActivity.this, "本地加载也失败了(っ °Д °;)っ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void initCards(int i) {
        RecyclerView recyclerView = findViewById(R.id.rv_main);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        final MainPageAdapter adapter = new MainPageAdapter(pages, this);
        if (i == 0) {
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);
            recyclerView.setAdapter(adapter);
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            recyclerView.setOnFlingListener(null);
            snapHelper.attachToRecyclerView(recyclerView);

            recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
                @Override
                public void onLoadMore() {
                    adapter.notifyDataSetChanged();
                    if (pages.size() == 5)
                        queryDataFromServer("http://v3.wufazhuce.com:8000/api/onelist/" + toResponse[5] + "/0");
                    else
                        adapter.notifyDataSetChanged();
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    public void initData(int i) {
        switch (i) {
            case -1:
                loadDbContentMain();
                break;
            case 0:
                queryDataFromServer("http://v3.wufazhuce.com:8000/api/onelist/" + toResponse[0] + "/0");
                break;
            case 5:
                initCards(0);
                break;
            case 10:
                initCards(1);
                break;
            default:
                queryDataFromServer("http://v3.wufazhuce.com:8000/api/onelist/" + toResponse[i] + "/0");
        }
    }

}
