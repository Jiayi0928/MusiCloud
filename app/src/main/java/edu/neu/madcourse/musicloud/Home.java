package edu.neu.madcourse.musicloud;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import okhttp3.Call;


public class Home extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText keyword;

    private static final String CLIENT_ID = "45b88b6d879642789bb87173c5e0d190";
    private static final String REDIRECT_URI = "http://myspotify.com/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private String mAccessToken;
    private Call mCall;
    TextView nav_title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        nav_title =findViewById(R.id.navTitle);
        nav_title.setText("HOME");

        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);


        keyword = findViewById(R.id.keyword_input);
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> searchResult(keyword.getText().toString()));
    }


    /**
     *  Ask users to login their Spotify account, then we can use their token
     *  to fetch data through Spotify Api
     */

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);

        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            //Toast.makeText(getApplication(),mAccessToken, Toast.LENGTH_SHORT).show();
        }
    }


    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }


    public void test(View view){
        Intent intent = new Intent(Home.this, SearchResults.class);
        intent.putExtra("token",mAccessToken);
        intent.putExtra("keyword I need",keyword.getText().toString());
        startActivity(intent);
    }


    /**
     * redirect to the search result page
     * @param s - keyword user entered
     */

    private void searchResult(String s){
        Intent intent = new Intent(Home.this, SearchResults.class);
        intent.putExtra("token",mAccessToken);
        intent.putExtra("keyword I need",s);
        startActivity(intent);
    }
}