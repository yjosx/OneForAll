package pow.jie.oneforall;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import pow.jie.oneforall.gson.EssayGson;
import pow.jie.oneforall.gson.QuestionGson;
import pow.jie.oneforall.gson.SerialContentGson;
import pow.jie.oneforall.util.BaseActivity;
import pow.jie.oneforall.util.OkHttpUtil;

public class ContentActivity extends BaseActivity {

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
        String itemId = intent.getStringExtra("ContentItemId");
        int category = Integer.parseInt(intent.getStringExtra("Category"));
        queryContent(itemId, category);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void queryContent(String itemId, int category) {
        String queryUrl;
        switch (category) {
            case 1:
                queryUrl = "http://v3.wufazhuce.com:8000/api/essay/" + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final EssayGson essayGson = new Gson().fromJson(responseText, EssayGson.class);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                    TextView textView = findViewById(R.id.tv_content);
                                    TextView title = findViewById(R.id.tv_content_activity_title);
                                    TextView author = findViewById(R.id.tv_content_activity_author);

                                    textViewToolBar.setText(essayGson.getData().getHp_title());
                                    title.setText(essayGson.getData().getHp_title());
                                    if (!essayGson.getData().getHp_author().equals("")) {
                                        String authorText = "文/" + essayGson.getData().getHp_author() + "\n\n";
                                        author.setText(authorText);
                                    }
                                    textView.setText(Html.fromHtml(essayGson.getData().getHp_content()));
                                }
                            });
                        }
                    }
                });
                break;
            case 2:
                queryUrl = "http://v3.wufazhuce.com:8000/api/serialcontent/" + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final SerialContentGson serialContentGson = new Gson().fromJson(responseText, SerialContentGson.class);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                    TextView textView = findViewById(R.id.tv_content);
                                    TextView title = findViewById(R.id.tv_content_activity_title);
                                    TextView author = findViewById(R.id.tv_content_activity_author);

                                    textViewToolBar.setText("连载");
                                    title.setText(serialContentGson.getData().getTitle());
                                    String authorText = "文/" + serialContentGson.getData().getAuthor().getUser_name() + "\n\n";
                                    author.setText(authorText);
                                    textView.setText(Html.fromHtml(serialContentGson.getData().getContent()));
                                }
                            });
                        }
                    }
                });
                break;
            case 3:
                queryUrl = "http://v3.wufazhuce.com:8000/api/question/" + itemId;
                OkHttpUtil.sendOkHttpRequest(queryUrl, new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ContentActivity.this, "加载失败了呢", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.body() != null) {
                            final String responseText = response.body().string();
                            final QuestionGson questionGson = new Gson().fromJson(responseText, QuestionGson.class);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView textViewToolBar = findViewById(R.id.tv_toolbar_title);
                                    TextView textView = findViewById(R.id.tv_content);
                                    TextView title = findViewById(R.id.tv_content_activity_title);
                                    TextView author = findViewById(R.id.tv_content_activity_author);

                                    textViewToolBar.setText("问答");
                                    title.setText(questionGson.getData().getQuestion_title());
                                    textView.setText(Html.fromHtml(questionGson.getData().getAnswer_content()));
                                }
                            });
                        }
                    }
                });
                break;
        }

    }

}