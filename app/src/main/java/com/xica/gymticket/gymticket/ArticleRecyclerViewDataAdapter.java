package com.xica.gymticket.gymticket;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ArticleRecyclerViewDataAdapter extends Adapter<ArticleRecyclerViewDataAdapter.ArticleRecyclerViewHolder> {

    private List<User> viewItemList;
    public Context mContext;
    public ArticleRecyclerViewDataAdapter(Context context, List<User> viewItemList) {
        this.viewItemList = viewItemList;
        this.mContext=context;
    }

    @Override
    public ArticleRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Get LayoutInflater object.
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        // Inflate the RecyclerView item layout xml.
        View itemView = layoutInflater.inflate(R.layout.article_item, parent, false);

        // Create and return our customRecycler View Holder object.
        ArticleRecyclerViewHolder ret = new ArticleRecyclerViewHolder(itemView);
        return ret;
    }

    @Override
    public void onBindViewHolder(ArticleRecyclerViewHolder holder, final int position) {
        if(viewItemList!=null) {
            // Get car item dto in list.
            User viewItem = viewItemList.get(position);

            if(viewItem != null) {
                // Set car item title.
                holder.getHeading().setText(viewItem.getHeading());
                holder.getArticleImage().setImageURI(viewItem.getPic());
                RequestOptions req= new RequestOptions();
                req.placeholder(R.drawable.chat);
                Glide.with(holder.getArticleImage()).load(viewItem.getPic()).apply(req).into(holder.getArticleImage());
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User a =viewItemList.get(position);
                String ab=a.getHeading();
                Intent intent=new Intent(mContext,ArticleActivity.class);
                intent.putExtra("article_heading",ab);
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        int ret = 0;
        if(viewItemList!=null)
        {
            ret = viewItemList.size();
        }
        return ret;
    }

    public class ArticleRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView heading;
        private ImageView articleImage;

        public ArticleRecyclerViewHolder(View itemView) {
            super(itemView);

            if(itemView != null)
            {
                heading = itemView.findViewById(R.id.article_recycler_heading);
                articleImage=itemView.findViewById(R.id.ArticleIcon);
            }
        }

        public TextView getHeading() {
            return heading;
        }

        public ImageView getArticleImage() {

            return articleImage;
        }
    }
}
