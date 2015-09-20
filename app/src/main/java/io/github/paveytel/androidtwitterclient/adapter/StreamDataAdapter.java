package io.github.paveytel.androidtwitterclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.paveytel.androidtwitterclient.R;
import io.github.paveytel.androidtwitterclient.pojo.StreamRealmModel;
import io.realm.RealmResults;

/**
 * Created by z001hm0 on 9/3/15.
 */
public class StreamDataAdapter extends RecyclerView.Adapter<StreamDataAdapter.StreamDataViewHolder> {

    protected Context mContext;
    protected RealmResults<StreamRealmModel> mResults;

    public StreamDataAdapter(Context context, RealmResults<StreamRealmModel> results) {
        this.mContext = context;
        this.mResults = results;
    }

    @Override
    public StreamDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_item, parent, false);
        return new StreamDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StreamDataViewHolder streamDataViewHolder, int position) {
         streamDataViewHolder.bindStreamData(mResults.get(position));
    }

    @Override
    public int getItemCount() {
        return mResults.size();
    }

    public void refill(RealmResults<StreamRealmModel> results) {
        Log.d("track", "mResults size is " + mResults.size());
        mResults = results;
        Log.d("track", "mResults size is " + mResults.size());
    }

    public class StreamDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected TextView mNameLabel;
        protected TextView mScreenNameLabel;
        protected TextView mTextLabel;
        protected ImageView mProfileImageView;

        public StreamDataViewHolder(View itemView){
            super(itemView);

            mNameLabel = (TextView) itemView.findViewById(R.id.nameLabel);
            mScreenNameLabel = (TextView) itemView.findViewById(R.id.screenNameLabel);
            mTextLabel = (TextView) itemView.findViewById(R.id.textLabel);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
        }

        public void bindStreamData(StreamRealmModel result){

            mNameLabel.setText(result.getName());
            mScreenNameLabel.setText(result.getScreenName());
            mTextLabel.setText(result.getText());

            Picasso.with(mContext)
                    .load(result.getProfileImageUrl())
                    .resize(180, 180)
                    .into(mProfileImageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
