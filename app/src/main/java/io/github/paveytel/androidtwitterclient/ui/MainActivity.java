package io.github.paveytel.androidtwitterclient.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.github.paveytel.androidtwitterclient.R;
import io.github.paveytel.androidtwitterclient.adapter.StreamDataAdapter;
import io.github.paveytel.androidtwitterclient.pojo.StreamRealmModel;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private final String consumerKey = "gfSGWmrWm3Z8gJLStD6jG1GGU";
    private final String consumerSecrete = "Q46jheV7D1DACk2yZWbAuodNGEcn423LTzxlCcvmFOzhbuyEM0";
    private final String twitterAccessToken = "996217466-MC6LOjnt0W7XmbRVvKdwDuDgre0B2QKHcaec36bZ";
    private final String accessTokenSecret = "HZe3CvCD8XTFkylSnR5BJPu2kMmBC65NGtdxsWTYs0hFP";
    private final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json?track=kim";

    protected Toolbar mToolbar;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected StreamDataAdapter mStreamDataAdapter;
    private RealmChangeListener mRealmListener;
    private Realm mRealm;

    RealmResults<StreamRealmModel> mQuery;
    @InjectView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view)
    TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mRealm = Realm.getInstance(this);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);

        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary,
                R.color.accent,
                R.color.primary_light,
                R.color.primary);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        TwitterStreamBackgroundTask twitterStreamBackgroundTask = new TwitterStreamBackgroundTask();
        twitterStreamBackgroundTask.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class TwitterStreamBackgroundTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {

            getTwitterStreamData();
            return null;
        }
    }

    public void loadData() {

        //mRealm = Realm.getInstance(this);
        mQuery = mRealm.where(StreamRealmModel.class).findAll();
        mQuery.sort("createdAt", RealmResults.SORT_ORDER_DESCENDING);

        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
            Log.d("track", "swiping stoped");
        }
        Log.d("track", "loadData was called");
        Log.d("track", "query size is " + mQuery.size());
        setStreamDataAdapter(mQuery);
        Log.d("querying", "restarted!!!!!!");
        for (StreamRealmModel streamRealmModel : mQuery) {
            Log.d("querying", "size: " + mQuery.size());

        }

    }

    protected SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {

            //refreshes the activity
            finish();
            startActivity(getIntent());
        }
    };

    public void setStreamDataAdapter(RealmResults<StreamRealmModel> mQuery){
        if (mRecyclerView.getAdapter() == null) {
            mStreamDataAdapter =
                    new StreamDataAdapter(this, mQuery);
            if (mStreamDataAdapter.getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
            }
            mRecyclerView.setAdapter(mStreamDataAdapter);
        } else {
            //if it exists, no need to recreate it,
            //just set the data on the recyclerView
            if (mRecyclerView.getAdapter().getItemCount() == 0) {
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mEmptyView.setVisibility(View.GONE);
                ((StreamDataAdapter) mRecyclerView.getAdapter()).refill(mQuery);
                //mRecyclerView.notify();
            }

        }
    }

    public void getTwitterStreamData() {

        OAuthService service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecrete)
                .build();

        // Set your access token
        Token accessToken = new Token(twitterAccessToken, accessTokenSecret);
        OAuthRequest request = new OAuthRequest(Verb.GET, STREAM_URI);
        request.addHeader("version", "HTTP/1.1");
        request.addHeader("host", "stream.twitter.com");
        request.setConnectionKeepAlive(true);
        request.addHeader("user-agent", "Twitter Stream Reader");
        service.signRequest(accessToken, request);
        Response response = request.send();

        // reads Twitter's Streams
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));

        String line;
        try {
            while ((line = reader.readLine()) != null) {

                JSONObject jsonObject = new JSONObject(line);

                if (jsonObject.getJSONObject("user") != null && jsonObject.getString("text") != null) {
                    Log.d("testing", "text:" + jsonObject.getString("text"));

                    Realm realm = Realm.getInstance(getApplicationContext());

                    realm.beginTransaction();
                    StreamRealmModel streamRealmModel = realm.createObject(StreamRealmModel.class);

                    String name = jsonObject.getJSONObject("user").getString("name");
                    String screenName = jsonObject.getJSONObject("user").getString("screen_name");
                    String profileImageUrl = jsonObject.getJSONObject("user").getString("profile_image_url");
                    String text = jsonObject.getString("text");

                    streamRealmModel.setName(name);
                    streamRealmModel.setScreenName(screenName);
                    streamRealmModel.setProfileImageUrl(profileImageUrl);
                    streamRealmModel.setText(text);
                    streamRealmModel.setCreatedAt(new Date().getTime());

                    realm.commitTransaction();
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
