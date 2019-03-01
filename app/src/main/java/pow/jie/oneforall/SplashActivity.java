package pow.jie.oneforall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import pow.jie.oneforall.databean.ContentItemBean;
import pow.jie.oneforall.db.ContentItem;
import pow.jie.oneforall.util.BaseActivity;
import pow.jie.oneforall.util.OkHttpUtil;
import pow.jie.oneforall.util.SaveDataToLitePal;

public class SplashActivity extends BaseActivity {
    private static final String TAG = "SplashActivity";
    private final String[] toResponse = new String[10];
    private int timesFlag = 0;//用于计数网络请求次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String idListUrl = "http://v3.wufazhuce.com:8000/api/onelist/idlist";
        queryIdFromServer(idListUrl);
    }

    private void queryIdFromServer(String address) {
        OkHttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "queryIDFromServer加载失败");
                        Toast.makeText(SplashActivity.this, "网络加载失败，尝试本地加载", Toast.LENGTH_SHORT).show();
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
                                SharedPreferences preferences = getSharedPreferences("idList", MODE_PRIVATE);
                                String idListId=idList.getString(0);

                                if (!preferences.getString("day" + 0, "0").equals(idList.getString(0))
                                        ||LitePal.where("idListId = ?", idListId).find(ContentItem.class)==null) {
                                    //如果今天的id值和本地的一样，并且数据库有数据，就不网络请求。
                                    SharedPreferences.Editor editor = getSharedPreferences("idList", MODE_PRIVATE).edit();
                                    for (int i = 0; i < idList.length(); i++) {
                                        editor.putString("day" + String.valueOf(i), idList.getString(i));
                                        toResponse[i] = idList.getString(i);
                                    }
                                    editor.apply();
                                    Log.d(TAG, toResponse[0] + "(0)");
                                    if (!toResponse[0].equals("0")) {
                                        initData(timesFlag);
                                        Log.d(TAG, "queryId" + timesFlag);
                                    } else {
                                        Log.d(TAG, "toResponse0没有值");
                                        Toast.makeText(SplashActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    initData(-1);
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
                            Toast.makeText(SplashActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
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
                    ContentItemBean contentList = new Gson().fromJson(responseText, ContentItemBean.class);
                    SaveDataToLitePal.SaveToContentList(contentList);
                    initData(++timesFlag);
                } else {
                    Log.d(TAG, "queryDataFromServer的response为空" + timesFlag);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SplashActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "queryDataFromServer加载失败" + timesFlag);
                        Toast.makeText(SplashActivity.this, "网络加载失败，尝试本地加载", Toast.LENGTH_SHORT).show();
                        if (LitePal.findFirst(ContentItem.class) != null) {
                            initData(-1);
                        }
                    }
                });
            }
        });
    }

    public void initData(int i) {
        switch (i) {
            case -1:
            case 10:
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case 0:
                LitePal.deleteAll(ContentItem.class);
                queryDataFromServer("http://v3.wufazhuce.com:8000/api/onelist/" + toResponse[0] + "/0");
                break;
            default:
                queryDataFromServer("http://v3.wufazhuce.com:8000/api/onelist/" + toResponse[i] + "/0");
        }
    }
}
