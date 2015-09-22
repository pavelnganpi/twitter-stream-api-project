package io.github.paveytel.androidtwitterclient.ui;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.github.paveytel.androidtwitterclient.R;
import io.github.paveytel.androidtwitterclient.adapter.SearchDataAdapter;
import io.github.paveytel.androidtwitterclient.pojo.TwitterUserModel;

public class SearchResultsActivity extends AppCompatActivity {
    protected Toolbar mToolbar;
    protected static final String TWITTER_API_SEARCH_URL = "https://api.twitter.com/1.1/friends/list.json?cursor=-1&screen_name=";
    private final String consumerKey = "gfSGWmrWm3Z8gJLStD6jG1GGU";
    private final String consumerSecrete = "Q46jheV7D1DACk2yZWbAuodNGEcn423LTzxlCcvmFOzhbuyEM0";
    private final String twitterAccessToken = "996217466-MC6LOjnt0W7XmbRVvKdwDuDgre0B2QKHcaec36bZ";
    private final String accessTokenSecret = "HZe3CvCD8XTFkylSnR5BJPu2kMmBC65NGtdxsWTYs0hFP";
    private final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json?track=twitter";
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<TwitterUserModel> mfriends;
    protected SearchDataAdapter mSearchDataAdapter;

    public static String mSearchQuery;
    @InjectView(R.id.searchRecyclerView)
    RecyclerView mRecyclerView;
    @InjectView(R.id.searchSwipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.empty_view)
    TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.inject(this);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.search_app_bar);
        setSupportActionBar(mToolbar);

        handleIntent(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mSearchQuery = intent.getStringExtra(SearchManager.QUERY).trim();
        }

        else{
            mSearchQuery = "paveyN";
        }

        Thread bg = new Thread(new Runnable() {
            public void run() {
                getTwitterSearchData();
            }
        });
        bg.start();
        try {
            bg.join();
            mSearchDataAdapter = new SearchDataAdapter(SearchResultsActivity.this, mfriends);
            mRecyclerView.setAdapter(mSearchDataAdapter);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void getTwitterSearchData() {

        OAuthService service = new ServiceBuilder()
                .provider(TwitterApi.class)
                .apiKey(consumerKey)
                .apiSecret(consumerSecrete)
                .build();

        // Set your access token
        Token accessToken = new Token(twitterAccessToken, accessTokenSecret);
        OAuthRequest request = new OAuthRequest(Verb.GET, TWITTER_API_SEARCH_URL + mSearchQuery);
        request.addHeader("version", "HTTP/1.1");
        request.addHeader("host", "api.twitter.com");
        request.setConnectionKeepAlive(true);
        service.signRequest(accessToken, request);
        Response response = request.send();

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));

        String line;
        try {
            while ((line = reader.readLine()) != null) {

                JSONObject jsonObject = new JSONObject(line);
                mfriends = new ArrayList<>();

                JSONArray users = jsonObject.getJSONArray("users");
                for(int i =0 ; i < users.length();i++){
                    TwitterUserModel userModel = new TwitterUserModel();
                    String name = users.getJSONObject(i).getString("name");
                    String screenName = users.getJSONObject(i).getString("screen_name");
                    String profileImageUrl = users.getJSONObject(i).getString("profile_image_url");
                    String description = users.getJSONObject(i).getString("description");
                    userModel.setName(name);
                    userModel.setScreenName(screenName);
                    userModel.setProfileImageUrl(profileImageUrl);
                    userModel.setDescription(description);
                    mfriends.add(userModel);
                }

                if(mfriends.isEmpty()){
                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(SearchResultsActivity.this);
                    builder.setMessage("Sorry, that username does not exist, Please search another username");//creates a dialog with this message
                    builder.setTitle("Oops , no result");
                    builder.setPositiveButton(android.R.string.ok, null);//creates a button to dismiss the dialog

                    android.app.AlertDialog dialog = builder.create();//create a dialog
                    dialog.show();//show the dialog
                }


            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        inflater.inflate(R.menu.menu_search_results, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
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
}
