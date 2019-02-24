package pow.jie.oneforall.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import pow.jie.oneforall.ContentActivity;
import pow.jie.oneforall.R;
import pow.jie.oneforall.db.ContentItem;

public class ContentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ContentListAdapter(List<ContentItem> mContentItem) {
        this.mContentItem = mContentItem;
    }

    private List<ContentItem> mContentItem;
    private Context mContext;
    private final int TYPE_ITEM = 1;
    private final int TYPE_FOOTER = 2;
    // 当前加载状态，默认为加载完成
    private int loadState = 1;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        if (i == TYPE_FOOTER) {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.layout_refresh_footer, viewGroup, false);
            return new FootViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext)
                    .inflate(R.layout.card_main, viewGroup, false);
            final RecyclerViewHolder holder = new RecyclerViewHolder(view);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getAdapterPosition();
                    ContentItem contentItem = mContentItem.get(position);
                    switch (contentItem.getCategory()) {
                        case "1":
                        case "2":
                        case "3":
                            Intent intent = new Intent(mContext, ContentActivity.class);
                            intent.putExtra("Category", contentItem.getCategory());
                            intent.putExtra("ContentItemId", contentItem.getItemId());
                            mContext.startActivity(intent);
                            break;
                    }
                }
            });
            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RecyclerViewHolder) {
            RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) viewHolder;
            ContentItem contentItem = mContentItem.get(i);

            recyclerViewHolder.tvCardTitle.setText(contentItem.getTagTitle());
            recyclerViewHolder.tvTitle.setText(contentItem.getTitle());
            recyclerViewHolder.guideWord.setText(contentItem.getForward());
            recyclerViewHolder.author.setText(contentItem.getAuthor());
            Glide.with(mContext).load(contentItem.getUrl()).into(recyclerViewHolder.imageView);
        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footViewHolder = (FootViewHolder) viewHolder;
            switch (loadState) {
                case 0: // 正在加载
                    footViewHolder.pbLoading.setVisibility(View.VISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.VISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case 1: // 加载完成
                    footViewHolder.pbLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.tvLoading.setVisibility(View.INVISIBLE);
                    footViewHolder.llEnd.setVisibility(View.GONE);
                    break;

                case 2: // 加载到底
                    footViewHolder.pbLoading.setVisibility(View.GONE);
                    footViewHolder.tvLoading.setVisibility(View.GONE);
                    footViewHolder.llEnd.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        // 最后一个item设置为FooterView
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mContentItem.size() + 1;
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView tvCardTitle;
        TextView tvTitle;
        TextView author;
        TextView guideWord;

        RecyclerViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = view.findViewById(R.id.iv_card_main);
            tvCardTitle = view.findViewById(R.id.tv_card_title);
            tvTitle = view.findViewById(R.id.tv_card_content_title);
            author = view.findViewById(R.id.tv_card_author);
            guideWord = view.findViewById(R.id.tv_card_guide_word);

        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        ProgressBar pbLoading;
        TextView tvLoading;
        LinearLayout llEnd;

        FootViewHolder(View itemView) {
            super(itemView);
            pbLoading = itemView.findViewById(R.id.pb_loading);
            tvLoading = itemView.findViewById(R.id.tv_loading);
            llEnd = itemView.findViewById(R.id.ll_end);
        }
    }

    public void setLoadState(int loadState) {
        this.loadState = loadState;
        notifyDataSetChanged();
    }

}
