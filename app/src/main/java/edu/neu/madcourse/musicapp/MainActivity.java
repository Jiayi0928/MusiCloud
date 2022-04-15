package edu.neu.madcourse.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private EditText keyword;

    private static final String CLIENT_ID = "45b88b6d879642789bb87173c5e0d190";
    private static final String REDIRECT_URI = "http://myspotify.com/callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private String mAccessToken;
    private Call mCall;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);


        keyword = findViewById(R.id.keyword_input);
        Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(view -> searchResult(keyword.getText().toString()));
    }

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
        Intent intent = new Intent(MainActivity.this,AuthActivity.class);
        intent.putExtra("token",mAccessToken);
        intent.putExtra("keyword I need",keyword.getText().toString());
        startActivity(intent);
    }

    private void searchResult(String s){
        Intent intent = new Intent(MainActivity.this, AuthActivity.class);
        intent.putExtra("token",mAccessToken);
        intent.putExtra("keyword I need",s);
        startActivity(intent);
    }
}