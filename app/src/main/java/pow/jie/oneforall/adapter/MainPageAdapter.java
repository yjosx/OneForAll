package pow.jie.oneforall.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import pow.jie.oneforall.R;
import pow.jie.oneforall.db.ContentItem;

public class MainPageAdapter extends RecyclerView.Adapter<MainPageAdapter.ViewHolder> {

    private List<List<ContentItem>> pages;
    private Context mContext;

    public MainPageAdapter(List<List<ContentItem>> pages, Context context) {
        this.pages = pages;
        mContext = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        ViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.rv_vertical);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(mContext==null){
            mContext=viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_vertical,viewGroup,false);
        return new MainPageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 1);
        viewHolder.recyclerView.setLayoutManager(layoutManager);
        MainContentAdapter adapter = new MainContentAdapter(pages.get(i));
        viewHolder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

}
