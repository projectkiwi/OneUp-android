package com.purduecs.kiwi.oneup.views;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.web.LikeWebRequest;
import com.purduecs.kiwi.oneup.web.RequestHandler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChallengesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VISIBLE_THRESHOLD = 4;

    private final int ITEM_VIEW_TYPE_BASIC = 0;
    private final int ITEM_VIEW_TYPE_FOOTER = 1;

    List<Challenge> list = new ArrayList<>();

    private OnLoadMoreListener mLoadListener;
    LinearLayoutManager mLayout;

    private int firstVisibleItem, visibleItemCount, totalItemCount, previousTotal = 0;
    private boolean loading = true;

    private View.OnClickListener clickListener;

    private Activity mActivity;

    public ChallengesAdapter(Activity holdingActivity, RecyclerView recyclerView, List<Challenge> list, View.OnClickListener cardListener, final OnLoadMoreListener onLoadMoreListener) {
        this.list = list;
        this.mLoadListener = onLoadMoreListener;
        this.clickListener = cardListener;
        this.mActivity = holdingActivity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            mLayout = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    scrollCheck();
                }
            });
        }
    }

    private void scrollCheck() {
        if (mLayout != null) {
            totalItemCount = mLayout.getItemCount();
            visibleItemCount = mLayout.getChildCount();
            firstVisibleItem = mLayout.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            }
            if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
                // End has been reached

                addItem(null);
                if (mLoadListener != null) {
                    mLoadListener.onLoadMore(new FinishedLoadingListener() {
                        @Override
                        public void finishedLoading() {
                            removeItem(null);
                        }
                    });
                }
                loading = true;
            }
        }
    }

    public void resetItems(@NonNull List<Challenge> list) {
        firstVisibleItem = 0;
        visibleItemCount = 0;
        totalItemCount = 0;
        previousTotal = 0;

        this.list.clear();
        addItems(list);
        scrollCheck();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_BASIC) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.newsfeed_item, parent, false);
            return new ViewHolder(itemView);
        } else if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        } else {
            throw new IllegalStateException("Invalid type, this type ot items " + viewType + " can't be handled");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == ITEM_VIEW_TYPE_BASIC) {
            ViewHolder h = (ViewHolder) holder;
            h.cardid.setText(getItem(position).id);
            h.attemptid.setText(getItem(position).attempt_id);
            h.challenge=getItem(position);
            h.cardtitle.setText(list.get(position).name);
            Glide.with(mActivity)
                    .load(list.get(position).previewImage)
                    .error(R.drawable.doge_with_sunglasses)
                    .into(h.cardimage);
            h.cardowner.setText("by " + list.get(position).owner);
            String categories = "";
            for (int i = 0; i < list.get(position).categories.length; i++) {
                categories += list.get(position).categories[i];
                categories += ", ";
            }
            categories = categories.substring(0, categories.length()-2);
            h.cardcategories.setText(categories);
            h.cardlikes.setText(Integer.toString(list.get(position).likes));
            h.cardlikes.setTextOff(Integer.toString(list.get(position).likes));
            h.cardlikes.setTextOn(Integer.toString(list.get(position).likes + 1));
            h.cardlikes.setPastLiked(false);
            h.cardlikes.setOnCheckedChangeListener(null);
            h.cardlikes.setChecked(false);
            switch (list.get(position).liked) {
                case 0:
                    break;
                case 1:
                    h.cardlikes.toggle();
                    break;
                case 2:
                    h.cardlikes.setPastLiked(true);
                    break;
            }
            h.cardlikes.setOnCheckedChangeListener(likeListener);

            //h.cardtime.setText(list.get(position).time);
            long time = (new Date()).getTime() - list.get(position).time.getTime();
            String tim = "s";
            time /= 1000; // At seconds
            if (time >= 60) {
                time /= 60;
                tim = "m";
                if (time >= 60) {
                    time /= 60;
                    tim = "h";
                    if (time >= 24) {
                        time /= 24;
                        tim = "d";
                        if (time >= 365) {
                            time /= 365;
                            tim = "y";
                        }// At years
                        else if (time >= 12) {
                            time /= 12;
                            tim = "mo";
                        }// Else do months
                    } // At days
                }// At hours
            }// At minutes
            h.cardtime.setText(time + " " + tim);

            h.carddesc.setText(list.get(position).desc);

            h.cardview.setOnClickListener(clickListener);
        } else {
            onBindFooterView(holder, position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private void addItem(Challenge item) {
        if (!list.contains(item)) {
            list.add(item);
            notifyItemInserted(list.size() - 1);
        }
    }

    private void removeItem(Challenge item) {
        int indexOfItem = list.indexOf(item);
        if (indexOfItem != -1) {
            this.list.remove(indexOfItem);
            notifyItemRemoved(indexOfItem);
        }
    }

    public void addItems(@NonNull List<Challenge> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public Challenge getItem(int i) {
        return list.get(i);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) != null ? ITEM_VIEW_TYPE_BASIC : ITEM_VIEW_TYPE_FOOTER;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View cardview;

        TextView cardid;
        TextView attemptid;
        ImageView cardimage;
        TextView cardtitle;
        TextView cardowner;
        TextView cardcategories;
        CenterIconButton cardlikes;
        TextView cardtime;
        TextView carddesc;
        Challenge challenge;
        public ViewHolder(View itemView) {
            super(itemView);

            cardview = itemView;

            cardid = (TextView) itemView.findViewById(R.id.card_id);
            attemptid = (TextView) itemView.findViewById(R.id.attempt_id);
            cardimage = (ImageView) itemView.findViewById(R.id.card_image);
            cardtitle = (TextView) itemView.findViewById(R.id.card_title);
            cardowner = (TextView) itemView.findViewById(R.id.card_winner);
            cardcategories = (TextView) itemView.findViewById(R.id.card_categories);
            cardlikes = (CenterIconButton) itemView.findViewById(R.id.card_like_button);
            cardtime = (TextView) itemView.findViewById(R.id.card_time);
            carddesc = (TextView) itemView.findViewById(R.id.card_desc);
        }
    }

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        //noinspection ConstantConditions
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeed_loading_bar, parent, false);
        return new ProgressViewHolder(v);
    }

    public void onBindFooterView(RecyclerView.ViewHolder genericHolder, int position) {
        ((ProgressViewHolder) genericHolder).progressBar.setIndeterminate(true);
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar)v.findViewById(R.id.loading_bar);
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(FinishedLoadingListener listener);
    }

    public interface FinishedLoadingListener {
        void finishedLoading();
    }

    // Need this so the oncheckedchange listener doesn't loop when it fails
    private boolean failed = false;

    private CompoundButton.OnCheckedChangeListener likeListener =
            new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                    if (failed) { failed = false; return; }
                    String challengeId  = ((TextView)(
                            (RelativeLayout)(
                                    (RelativeLayout)buttonView.getParent())
                                        .getParent())
                                        .findViewById(R.id.attempt_id))
                                        .getText()
                                        .toString();
                    new LikeWebRequest(challengeId, isChecked, new RequestHandler<Boolean>() {
                        @Override
                        public void onSuccess(Boolean response) {

                        }

                        @Override
                        public void onFailure() {
                            Log.d("HEY", "we failed to like the post :(");
                            failed = true;
                            buttonView.toggle();
                        }
                    });
                }
            };
}
