package pow.jie.oneforall;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pow.jie.oneforall.adapter.ContentListAdapter;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.databean.ContentListBean;
import pow.jie.oneforall.util.BaseActivity;
import pow.jie.oneforall.util.EndlessRecyclerOnScrollListener;
import pow.jie.oneforall.util.OkHttpUtil;

public class ContentListActivity extends BaseActivity {
    private static final String TAG = "ContentListActivity";
    private List<ContentItem> contentItems = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private ContentListAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_list);
        final String readingListUrl = getResources().getString(R.string.reading_url) + "0";

        Toolbar toolbar = findViewById(R.id.tb_content_list);
        toolbar.setTitle("阅读");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.rv_content_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_content);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(() -> reQueryFromServer(readingListUrl));

        queryFromServer(readingListUrl);
    }

    private void queryFromServer(String address) {
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseText = response.body().string();
                    ContentListBean bean = new Gson().fromJson(responseText, ContentListBean.class);
                    readBean(bean);
                    runOnUiThread(() -> {
                        if (contentItems.size() == 10) {
                            initCards();
                        } else if (contentItems.size() > 10) {
                            adapter.setLoadState(1);
                        } else {
                            Log.d(TAG, "contentItems为空");
                            Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "queryDataFromServer的response为空");
                    runOnUiThread(() -> Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.d(TAG, "queryDataFromServer加载失败");
                    Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    public void initCards() {
        adapter = new ContentListAdapter(contentItems);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                int id = contentItems.get(contentItems.size() - 1).getIdListId();
                Log.d(TAG, "onLoadMore: " + id);
                String readingListUrl = getResources().getString(R.string.reading_url) + id;
                adapter.setLoadState(0);
                reQueryFromServer(readingListUrl);
            }
        });
    }

    public void readBean(ContentListBean bean) {
        List<ContentListBean.DataBean> list = bean.getData();
        for (int i = 0; i < 10; i++) {
            ContentItem contentItem = new ContentItem();

            contentItem.setItemId(list.get(i).getItem_id());
            contentItem.setCategory(list.get(i).getCategory());
            contentItem.setTagTitle(list.get(i).getTitle());//栏目标题
            contentItem.setForward(list.get(i).getForward());
            contentItem.setUrl(list.get(i).getImg_url());
            contentItem.setContent(list.get(i).getContent_id());
            contentItem.setTitle(list.get(i).getTitle());
            contentItem.setIdListId(Integer.parseInt(list.get(i).getId()));
            contentItem.setLikeCount(list.get(i).getLike_count());

            switch (list.get(i).getCategory()) {
                case "1": //文章
                    if (!String.valueOf(list.get(i).getTag_list()).equals("[]")) {
                        Log.d("SaveDataToLitePal", String.valueOf(list.get(i).getTag_list()));
                        contentItem.setTagTitle(list.get(i).getTag_list().get(0).getTitle());
                    } else {
                        Log.d("SaveDataToLitePal", String.valueOf(list.get(i).getTag_list()));
                        contentItem.setTagTitle("阅读");
                    }
                    contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                    contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                    break;
                case "2": //连载
                    contentItem.setSerialList(list.get(i).getSerial_list());
                    contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                    contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                    break;
                case "3": //问答
                    contentItem.setAuthor(list.get(i).getAuthor().getUser_name());
                    contentItem.setAuthorPicUrl(list.get(i).getAuthor().getWeb_url());
                    break;
            }
            contentItems.add(contentItem);
        }
    }

    private void reQueryFromServer(final String address) {
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.body() != null) {
                    String responseText = response.body().string();
                    ContentListBean bean = new Gson().fromJson(responseText, ContentListBean.class);
                    readBean(bean);
                    runOnUiThread(() -> {
                        if (contentItems.size() > 10) {
                            adapter.setLoadState(1);
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            Log.d(TAG, "reQuery: contentItems.size<=10");
                            Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d(TAG, "queryDataFromServer的response为空");
                    runOnUiThread(() -> Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d(TAG, "queryDataFromServer加载失败");
                runOnUiThread(() -> Toast.makeText(ContentListActivity.this, "加载失败", Toast.LENGTH_SHORT).show());
            }
        });
    }

}
