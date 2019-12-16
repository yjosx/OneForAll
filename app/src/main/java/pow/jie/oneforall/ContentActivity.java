package pow.jie.oneforall;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pow.jie.oneforall.databean.EssayBean;
import pow.jie.oneforall.databean.QuestionBean;
import pow.jie.oneforall.databean.SerialContentBean;
import pow.jie.oneforall.base.BaseActivity;
import pow.jie.oneforall.util.OkHttpUtil;

public class ContentActivity extends BaseActivity {

    private static final String TAG = "ContentActivity";
    private int category;
    private List<String> serialList;
    private String itemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        Toolbar toolbar = findViewById(R.id.tb_content);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Intent intent = getIntent();
        itemId = intent.getStringExtra("ContentItemId");
        category = Integer.parseInt(intent.getStringExtra("Category"));
        if (category == 2)
            serialList = intent.getStringArrayListExtra("serialList");

        queryContent(itemId, category);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (category == 2) {
            if (serialList.get(0).equals(itemId)) {
                menu.getItem(0).setVisible(false);
            }
            if (serialList.get(serialList.size() - 1).equals(itemId)) {
                menu.getItem(1).setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (category == 2) {
            getMenuInflater().inflate(R.menu.serial_menu, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_last) {
            Log.d(TAG, "onOptionsItemSelected: " + serialList);
            Log.d(TAG, "onOptionsItemSelected: " + itemId);
            String lastID = serialList.get(serialList.indexOf(itemId) - 1);
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("Category", String.valueOf(category));
            intent.putExtra("ContentItemId", lastID);
            intent.putStringArrayListExtra("serialList", (ArrayList<String>) serialList);
            startActivity(intent);
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_next) {
            String nextID = serialList.get(serialList.indexOf(itemId) + 1);
            Intent intent = new Intent(this, ContentActivity.class);
            intent.putExtra("Category", String.valueOf(category));
            intent.putExtra("ContentItemId", nextID);
            intent.putStringArrayListExtra("serialList", (ArrayList<String>) serialList);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryContent(String itemId, int category) {
        String queryUrl;
        switch (category) {
            case 1:
                queryUrl = getResources().getString(R.string.essay_url) + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final EssayBean essayBean = new Gson().fromJson(responseText, EssayBean.class);

                            runOnUiThread(() -> {
                                TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                TextView textView = findViewById(R.id.tv_content);
                                TextView title = findViewById(R.id.tv_content_activity_title);
                                TextView author = findViewById(R.id.tv_content_activity_author);

                                textViewToolBar.setText(essayBean.getData().getHp_title());
                                title.setText(essayBean.getData().getHp_title());
                                if (!essayBean.getData().getHp_author().equals("")) {
                                    String authorText = "文/" + essayBean.getData().getHp_author() + "\n\n";
                                    author.setText(authorText);
                                }
                                textView.setText(Html.fromHtml(essayBean.getData().getHp_content()));
                            });
                        }
                    }
                });
                break;
            case 2:
                queryUrl = getResources().getString(R.string.serial_url) + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final SerialContentBean serialContentBean = new Gson().fromJson(responseText, SerialContentBean.class);

                            runOnUiThread(() -> {
                                TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                TextView textView = findViewById(R.id.tv_content);
                                TextView title = findViewById(R.id.tv_content_activity_title);
                                TextView author = findViewById(R.id.tv_content_activity_author);

                                textViewToolBar.setText("连载");
                                title.setText(serialContentBean.getData().getTitle());
                                String authorText = "文/" + serialContentBean.getData().getAuthor().getUser_name() + "\n\n";
                                author.setText(authorText);
                                textView.setText(Html.fromHtml(serialContentBean.getData().getContent()));
                            });
                        }
                    }
                });
                break;
            case 3:
                queryUrl = getResources().getString(R.string.question_url) + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(() -> Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final QuestionBean questionBean = new Gson().fromJson(responseText, QuestionBean.class);

                            runOnUiThread(() -> {
                                TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                TextView textView = findViewById(R.id.tv_content);
                                TextView title = findViewById(R.id.tv_content_activity_title);
                                TextView author = findViewById(R.id.tv_content_activity_author);

                                textViewToolBar.setText("问答");
                                title.setText(questionBean.getData().getQuestion_title());
                                textView.setText(Html.fromHtml(questionBean.getData().getAnswer_content()));
                            });
                        }
                    }
                });
                break;
        }

    }

}