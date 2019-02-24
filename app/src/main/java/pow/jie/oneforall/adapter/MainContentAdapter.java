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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import pow.jie.oneforall.ContentActivity;
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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ContentItem contentItem = mContentItem.get(i);
        if (i == 0) {
            String cardTitle = contentItem.getTagTitle() + " | " + contentItem.getAuthor();
            String guideWord = contentItem.getForward() + "\n——" + contentItem.getTitle();
            viewHolder.tvCardTitle.setText(cardTitle);
            viewHolder.guideWord.setText(guideWord);
            Glide.with(mContext).load(contentItem.getUrl()).into(viewHolder.imageView);
        } else {
            viewHolder.tvCardTitle.setText(contentItem.getTagTitle());
            viewHolder.tvTitle.setText(contentItem.getTitle());
            viewHolder.guideWord.setText(contentItem.getForward());
            viewHolder.author.setText(contentItem.getAuthor());
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
        TextView tvCardTitle;
        TextView tvTitle;
        TextView author;
        TextView guideWord;

        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = view.findViewById(R.id.iv_card_main);
            tvCardTitle = view.findViewById(R.id.tv_card_title);
            tvTitle = view.findViewById(R.id.tv_card_content_title);
            author = view.findViewById(R.id.tv_card_author);
            guideWord = view.findViewById(R.id.tv_card_guide_word);

        }
    }

    MainContentAdapter(List<ContentItem> contentItemList) {
        mContentItem = contentItemList;
    }
}
