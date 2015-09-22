package io.github.paveytel.androidtwitterclient.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import io.github.paveytel.androidtwitterclient.R;
import io.github.paveytel.androidtwitterclient.pojo.StreamRealmModel;
import io.github.paveytel.androidtwitterclient.pojo.TwitterUserModel;
import io.realm.RealmResults;

/**
 * Created by z001hm0 on 9/20/15.
 */
public class SearchDataAdapter extends RecyclerView.Adapter<SearchDataAdapter.SearchDataViewHolder> {

    protected Context mContext;
    protected List<TwitterUserModel> mFriends;

    public SearchDataAdapter(Context context, List<TwitterUserModel> friends) {
        this.mContext = context;
        this.mFriends = friends;
    }

    @Override
    public SearchDataAdapter.SearchDataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_item, parent, false);
        return new SearchDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchDataViewHolder searchDataViewHolder, int position) {
        searchDataViewHolder.bindStreamData(mFriends.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class SearchDataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        protected TextView mNameLabel;
        protected TextView mScreenNameLabel;
        protected TextView mTextLabel;
        protected ImageView mProfileImageView;

        public SearchDataViewHolder(View itemView) {
            super(itemView);

            mNameLabel = (TextView) itemView.findViewById(R.id.nameLabel);
            mScreenNameLabel = (TextView) itemView.findViewById(R.id.screenNameLabel);
            mTextLabel = (TextView) itemView.findViewById(R.id.textLabel);
            mProfileImageView = (ImageView) itemView.findViewById(R.id.profileImageView);
        }

        public void bindStreamData(TwitterUserModel friend) {

            mNameLabel.setText(friend.getName());
            mScreenNameLabel.setText(friend.getScreenName());
            mTextLabel.setText(friend.getDescription());

            Picasso.with(mContext)
                    .load(friend.getProfileImageUrl())
                    .resize(180, 180)
                    .into(mProfileImageView);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
