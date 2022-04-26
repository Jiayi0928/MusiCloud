package edu.neu.madcourse.musicloud;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.ListItems;
import com.spotify.protocol.types.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import com.squareup.picasso.Picasso;
public class HomeScreenActivity extends AppCompatActivity {
    //private static final String CLIENT_ID = "d137c858782648f7b8b9e8c87d4de56d";
    private static final String CLIENT_ID = "45b88b6d879642789bb87173c5e0d190";
    private static final String REDIRECT_URI = "spotify-sdk://auth";
    private SpotifyAppRemote mSpotifyAppRemote;
    private final ErrorCallback mErrorCallback = this::logError;

    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    public static final int AUTH_CODE_REQUEST_CODE = 0x11;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private String mAccessToken2;
    private Call mCall;


    User currentUser;

    Button searchBtn;
    ImageButton imageButton1;
    ImageButton imageButton2;
    ImageButton imageButton3;

    String playlistID ="";

    TextView songName1;
    TextView songName2;
    TextView songName3;
    TextView artist1;
    TextView artist2;
    TextView artist3;

    TextView test;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        searchBtn = findViewById(R.id.buttonTest);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                Intent i = new Intent(HomeScreenActivity.this, Home.class);
                i.putExtra("token",mAccessToken);
                i.putExtra("currentUser", currentUser);
                startActivity(i);
            }
        });
        imageButton1 = findViewById(R.id.HSimageButton1);
        imageButton2 = findViewById(R.id.HSimageButton2);
        imageButton3 = findViewById(R.id.HSimageButton3);
        songName1 = findViewById(R.id.HSsongName1);
        songName2 = findViewById(R.id.HSSongName2);
        songName3 = findViewById(R.id.HSSongname3);
        artist1 = findViewById(R.id.HSArtist1);
        artist2 = findViewById(R.id.HSArtist2);
        artist3 = findViewById(R.id.HSArtist3);

        currentUser = getIntent().getExtras().getParcelable("currentUser");

        test = findViewById(R.id.textViewTest001);
        test.setText(currentUser.username);

        final AuthorizationRequest request = getAuthenticationRequest(AuthorizationResponse.Type.TOKEN);
        AuthorizationClient.openLoginActivity(this, AUTH_TOKEN_REQUEST_CODE, request);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
        if (requestCode == AUTH_TOKEN_REQUEST_CODE) {
            mAccessToken = response.getAccessToken();
            getFeaturedList();
        }
    }
    private void getFeaturedList(){
        if(mAccessToken ==null) {
            return;
        }
        String query = "https://api.spotify.com/v1/browse/featured-playlists?limit=1";
        final Request request = new Request.Builder()
                .url(query)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request);

        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setResponse(jsonObject.get("playlists").toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void getPlayList(){
        if(mAccessToken == null){
            return;
        }
        String query2 = "https://api.spotify.com/v1/playlists/"+playlistID;
        final Request request2 = new Request.Builder()
                .url(query2)
                .addHeader("Authorization","Bearer " + mAccessToken)
                .build();
        cancelCall();
        mCall = mOkHttpClient.newCall(request2);
        mCall.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    setResponse2(jsonObject.get("tracks").toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setResponse(final String text) {
        runOnUiThread(() -> {
            JSONObject object1 = null;
            JSONArray array1 =null;
            JSONObject object2 =null;
            String test1="";
            try {
                object1 = new JSONObject(text);
                //object2 = object1.getString("items");
                test1 = object1.get("items").toString();
                array1 = new JSONArray(test1);
                object2 =array1.getJSONObject(0);
                playlistID = object2.get("id").toString();
                getPlayList();
            }catch (JSONException e){
                e.printStackTrace();
            }
            //tt101.setText(playlistID);
        });
    }
    private void setResponse2(final String text){
        runOnUiThread(()->{
            JSONObject object1 =null;
            JSONArray array1 = null;
            JSONObject object2 = null;
            JSONObject object3 = null;
            String test  ="";
            try {
                object1 = new JSONObject(text);
                array1 = object1.getJSONArray("items");
                object2 = array1.getJSONObject(1);
                object3 = object2.getJSONObject("track");

                String imageCover1 = getImageURL(object3);
                Picasso.get().load(imageCover1).into(imageButton1);
                String title1 = getSongName(object3);
                String artistName1 = getArtistName(object3);
                songName1.setText(title1);
                artist1.setText(artistName1);
                String preview1 = getPreviewURL(object3);
                String track_uri1 = getURI(object3);

                JSONObject object4 = array1.getJSONObject(2);
                String imageCover2 = getImageURL(object4.getJSONObject("track"));
                String title2 = getSongName(object4.getJSONObject("track"));
                String artistName2 = getArtistName(object4.getJSONObject("track"));
                String preview2 = getPreviewURL(object4.getJSONObject("track"));
                String track_uri2 = getURI(object4.getJSONObject("track"));
                Picasso.get().load(imageCover2).into(imageButton2);
                songName2.setText(title2);
                artist2.setText(artistName2);

                JSONObject object5 = array1.getJSONObject(3);
                String imageCover3 = getImageURL(object5.getJSONObject("track"));
                String title3 = getSongName(object5.getJSONObject("track"));
                String artistName3 = getArtistName(object5.getJSONObject("track"));
                String preview3 = getPreviewURL(object5.getJSONObject("track"));
                String track_uri3 = getURI(object5.getJSONObject("track"));
                Picasso.get().load(imageCover3).into(imageButton3);
                songName3.setText(title3);
                artist3.setText(artistName3);

                imageButton1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(HomeScreenActivity.this,PostActivity.class);
                        i.putExtra("title",title1);
                        i.putExtra("artist",artistName1);
                        i.putExtra("img",imageCover1);
                        i.putExtra("preview",preview1);
                        i.putExtra("track_uri",track_uri1);
                        i.putExtra("currentUser", currentUser);
                        startActivity(i);
                    }
                });
                imageButton2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(HomeScreenActivity.this,PostActivity.class);
                        i.putExtra("title",title2);
                        i.putExtra("artist",artistName2);
                        i.putExtra("img",imageCover2);
                        i.putExtra("preview",preview2);
                        i.putExtra("track_uri",track_uri2);
                        i.putExtra("currentUser", currentUser);
                        startActivity(i);
                    }
                });

                imageButton3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(HomeScreenActivity.this,PostActivity.class);
                        i.putExtra("title",title3);
                        i.putExtra("artist",artistName3);
                        i.putExtra("img",imageCover3);
                        i.putExtra("preview",preview3);
                        i.putExtra("track_uri",track_uri3);
                        i.putExtra("currentUser", currentUser);
                        startActivity(i);
                    }
                });
            }catch (JSONException e){
                e.printStackTrace();
            }
            // tt1.setText(test);

        });
    }

    private String getSongName(JSONObject obj1) throws JSONException {
        String test = obj1.get("name").toString();
        return test;
    }
    private  String getURI(JSONObject obj1) throws JSONException{
        return obj1.get("uri").toString();
    }
    private String getPreviewURL(JSONObject obj1) throws JSONException{
        return obj1.get("preview_url").toString();
    }

    private String getArtistName(JSONObject obj1) throws JSONException{
        JSONObject  obj2= obj1.getJSONObject("album");
        JSONArray array1 = obj2.getJSONArray("artists");
        JSONObject obj3 = array1.getJSONObject(0);
        String test = obj3.get("name").toString();
        return test;
    }

    private String getImageURL(JSONObject obj1) throws  JSONException{
        JSONObject object1 = obj1.getJSONObject("album");
        JSONArray array1 = object1.getJSONArray("images");
        JSONObject obj2 = array1.getJSONObject(0);
        String test = obj2.get("url").toString();
        return test;
    }

    @Override
    protected void onDestroy() {
        cancelCall();
        super.onDestroy();
    }

    private AuthorizationRequest getAuthenticationRequest(AuthorizationResponse.Type type) {
        return new AuthorizationRequest.Builder(CLIENT_ID, type, getRedirectUri().toString())
                .setShowDialog(false)
                .setScopes(new String[]{"user-read-email"})
                .setCampaign("your-campaign-token")
                .build();
    }
    private void cancelCall() {
        if (mCall != null) {
            mCall.cancel();
        }
    }
    private void logError(Throwable throwable) {
        Toast.makeText(this, "R.string.err_generic_toast", Toast.LENGTH_SHORT).show();
        Log.e(MainActivity.class.getSimpleName(), "", throwable);
    }
    private Uri getRedirectUri() {
        return Uri.parse(REDIRECT_URI);
    }

}
