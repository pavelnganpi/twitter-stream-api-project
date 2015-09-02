package io.github.paveytel.androidtwitterclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity {

    private final String consumerKey = "gfSGWmrWm3Z8gJLStD6jG1GGU";
    private final String consumerSecrete = "Q46jheV7D1DACk2yZWbAuodNGEcn423LTzxlCcvmFOzhbuyEM0";
    private final String twitterAccessToken = "996217466-MC6LOjnt0W7XmbRVvKdwDuDgre0B2QKHcaec36bZ";
    private final String accessTokenSecret = "HZe3CvCD8XTFkylSnR5BJPu2kMmBC65NGtdxsWTYs0hFP";
    private final String STREAM_URI = "https://stream.twitter.com/1.1/statuses/filter.json?track=taylorswift";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TwitterStreamBackgroundTask twitterStreamBackgroundTask = new TwitterStreamBackgroundTask();
        twitterStreamBackgroundTask.execute();
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

    public void getTwitterStreamData(){

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
        //request.addBodyParameter("track", "java,heroku,twitter"); // Set keywords you'd like to track here
        service.signRequest(accessToken, request);
        Response response = request.send();

        // Create a reader to read Twitter's stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getStream()));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
//                latestTweet = line;
//                tweetCount++;
                Log.d("twitterData", "reading twitter data");
                Log.d("twitterData", line + " \n");
                //System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
