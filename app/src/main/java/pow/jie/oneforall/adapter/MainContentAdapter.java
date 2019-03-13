package pow.jie.oneforall.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import pow.jie.oneforall.ContentActivity;
import pow.jie.oneforall.MainActivity;
import pow.jie.oneforall.R;
import pow.jie.oneforall.db.ContentItem;

public class MainContentAdapter extends RecyclerView.Adapter<MainContentAdapter.ViewHolder> {

    private List<ContentItem> mContentItem;
    private Context mContext;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.card_main, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.cardView.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            ContentItem contentItem = mContentItem.get(position);

            Intent intent = new Intent(mContext, ContentActivity.class);
            switch (contentItem.getCategory()) {
                case "1":
                case "3":
                    intent.putExtra("Category", contentItem.getCategory());
                    intent.putExtra("ContentItemId", contentItem.getItemId());
                    mContext.startActivity(intent);
                    break;
                case "2":
                    intent.putExtra("Category", contentItem.getCategory());
                    intent.putExtra("ContentItemId", contentItem.getItemId());
                    intent.putStringArrayListExtra("serialList", (ArrayList<String>) contentItem.getSerialList());
                    mContext.startActivity(intent);
                    break;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ContentItem contentItem = mContentItem.get(i);
        if (mContentItem.get(i).getCategory().equals("5")) {
            viewHolder.tvCardTitle.setText(contentItem.getTagTitle());
            viewHolder.tvTitle.setText(contentItem.getTitle());
            viewHolder.guideWord.setText(contentItem.getForward());
            viewHolder.author.setText(contentItem.getAuthor());
            viewHolder.date.setText(contentItem.getDate());
            //电影获取不到赞的数量，只能隐藏
            viewHolder.imageLike.setVisibility(View.GONE);
            viewHolder.likeCount.setVisibility(View.GONE);
            Glide.with(mContext).load(contentItem.getUrl()).into(viewHolder.imageView);
        } else if (i == 0) {
            String cardTitle = contentItem.getTagTitle() + " | " + contentItem.getAuthor();
            String guideWord = contentItem.getForward() + "\n——" + contentItem.getTitle();
            viewHolder.tvCardTitle.setText(cardTitle);
            viewHolder.guideWord.setText(guideWord);
            viewHolder.date.setText(contentItem.getDate());
            viewHolder.likeCount.setText(String.valueOf(contentItem.getLikeCount()));
            Glide.with(mContext).load(contentItem.getUrl()).into(viewHolder.imageView);
        } else {
            viewHolder.tvCardTitle.setText(contentItem.getTagTitle());
            viewHolder.tvTitle.setText(contentItem.getTitle());
            viewHolder.guideWord.setText(contentItem.getForward());
            viewHolder.author.setText(contentItem.getAuthor());
            viewHolder.date.setText(contentItem.getDate());
            viewHolder.likeCount.setText(String.valueOf(contentItem.getLikeCount()));
            Glide.with(mContext).load(contentItem.getUrl()).into(viewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mContentItem.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        ImageView imageLike;
        TextView tvCardTitle;
        TextView tvTitle;
        TextView author;
        TextView guideWord;
        TextView date;
        TextView likeCount;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = view.findViewById(R.id.iv_card_main);
            imageLike = view.findViewById(R.id.iv_like);
            tvCardTitle = view.findViewById(R.id.tv_card_title);
            tvTitle = view.findViewById(R.id.tv_card_content_title);
            author = view.findViewById(R.id.tv_card_author);
            guideWord = view.findViewById(R.id.tv_card_guide_word);
            date = view.findViewById(R.id.tv_card_date);
            likeCount = view.findViewById(R.id.tv_like_count);

        }
    }

    MainContentAdapter(List<ContentItem> contentItemList) {
        mContentItem = contentItemList;
    }
}
