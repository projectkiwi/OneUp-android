package com.purduecs.kiwi.oneup.views;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.purduecs.kiwi.oneup.R;
import com.purduecs.kiwi.oneup.helpers.TimeFormatHelper;
import com.purduecs.kiwi.oneup.models.Challenge;
import com.purduecs.kiwi.oneup.models.Notification;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Adam on 4/7/16.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    ArrayList<Notification> data;
    Activity mActivity;
    View.OnClickListener mListener;

    public NotificationsAdapter(Activity act, ArrayList<Notification> data, View.OnClickListener listener) {
        this.data = data;
        mActivity = act;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mActivity)
                .load(data.get(position).image)
                .error(R.drawable.doge_with_sunglasses)
                .into(holder.img);

        holder.desc.setText(data.get(position).desc);
        holder.user.setText(data.get(position).user);
        holder.time.setText(TimeFormatHelper.timeSince(data.get(position).time));

        holder.img.setTag(data.get(position).challenge_id);

        holder.notifView.setOnClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View notifView;
        TextView desc;
        ImageView img;
        TextView user;
        TextView time;

        public ViewHolder(View itemView) {
            super(itemView);

            notifView = itemView;

            desc = (TextView) itemView.findViewById(R.id.notif_desc);
            img = (ImageView) itemView.findViewById(R.id.notif_image);
            user = (TextView) itemView.findViewById(R.id.notif_user);
            time = (TextView) itemView.findViewById(R.id.notif_time);
        }
    }
}
