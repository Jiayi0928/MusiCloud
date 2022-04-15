package edu.neu.madcourse.musicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AuthActivity extends AppCompatActivity {
    private static final String CLIENT_ID = "45b88b6d879642789bb87173c5e0d190";
    private static final String REDIRECT_URI = "http://example.com/callback";
    public static final int AUTH_TOKEN_REQUEST_CODE = 0x10;
    private final OkHttpClient mOkHttpClient = new OkHttpClient();
    private String mAccessToken;
    private Call mCall;
    private String keyword_i_need;
    public static ArrayList<Song> songList = new ArrayList<>();
    private RviewAdapter rviewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        mAccessToken = getIntent().getExtras().getString("token");
        keyword_i_need = getIntent().getExtras().getString("keyword I need");
        recyclerView = findViewById(R.id.rvSongs);
        getResults();
    }

    public void getResults() {
        if (mAccessToken == null) {
            return;
        }

        String query = "https://api.spotify.com/v1/search?q="+keyword_i_need+"&type=track&market=ES&limit=20&offset=5";

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
                    final JSONObject o1 = new JSONObject(response.body().string());
                    setResponse(o1.get("tracks").toString());


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
    private void createRecyclerView(ArrayList<Song> arr){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rviewAdapter = new RviewAdapter(arr);
        recyclerView.setAdapter(rviewAdapter);

    }
    private String getImg(JSONObject object)throws JSONException{
        JSONObject album_ob = new JSONObject(object.get("album").toString());
        JSONArray img_arr = new JSONArray(album_ob.get("images").toString());
        JSONObject img_ob = new JSONObject(img_arr.getJSONObject(0).toString());
        return img_ob.get("url").toString();
    }
    private String getArtist(JSONObject object) throws JSONException {
        JSONArray artist_arr = new JSONArray(object.get("artists").toString());
        JSONObject artist_ob = new JSONObject(artist_arr.getJSONObject(0).toString());
        return artist_ob.get("name").toString();
    }
    private String getURI(JSONObject object) throws JSONException {
        return object.get("uri").toString();
    }
    private String getTitle(JSONObject object) throws JSONException {
        return object.get("name").toString();
    }
    private String getPreview(JSONObject object) throws JSONException {
        return object.get("preview_url").toString();
    }


    private void setResponse(final String o1) {
        runOnUiThread(() -> {
            JSONObject o2 = null;
            try {
                o2 = new JSONObject(o1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray a1 = null;
            try {
                a1 = o2.getJSONArray("items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            int len_search_list = a1.length();
            String title = "";
            String preview_url = "";
            String track_uri = "";
            String artist = "";
            String img_url = "";
            songList = new ArrayList<>();
            for(int i = 0; i < len_search_list; i++) {
                JSONObject object = null;
                try {
                    object = a1.getJSONObject(i);
                    track_uri = getURI(object);
                    title = getTitle(object);
                    preview_url = getPreview(object);
                    artist = getArtist(object);
                    img_url = getImg(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //String op = "title: " + title + "\nuri: " + track_uri + "\nartist: " + artist + "\npreview: " + preview_url + "\nImg: " + img_url;
                //Log.i("INFO", op);
                Song songItem = new Song(title,artist,img_url,preview_url);
                songList.add(songItem);
                Log.i("Item",songItem.toString());
            }

            createRecyclerView(songList);
        });
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



}



