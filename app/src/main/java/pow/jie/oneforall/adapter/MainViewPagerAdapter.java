package pow.jie.oneforall.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

import pow.jie.oneforall.R;
import pow.jie.oneforall.db.ContentItem;

public class MainViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<List<ContentItem>> mList;

    public MainViewPagerAdapter(Context context, List<List<ContentItem>> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.recycler_vertical, null);
        RecyclerView recyclerView = view.findViewById(R.id.rv_vertical);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        recyclerView.setLayoutManager(layoutManager);
        MainContentAdapter adapter = new MainContentAdapter(mList.get(position));
        recyclerView.setAdapter(adapter);
        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
