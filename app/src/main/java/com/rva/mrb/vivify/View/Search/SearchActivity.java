package com.rva.mrb.vivify.View.Search;

import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.Model.Data.AccessToken;
import com.rva.mrb.vivify.Model.Data.Album;
import com.rva.mrb.vivify.Model.Data.Artist;
import com.rva.mrb.vivify.Model.Data.MediaType;
import com.rva.mrb.vivify.Model.Data.Playlist;
import com.rva.mrb.vivify.Model.Data.Search;
import com.rva.mrb.vivify.Model.Data.SimpleTrack;
import com.rva.mrb.vivify.Model.Data.Track;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.rva.mrb.vivify.Spotify.SpotifyService;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.View.Adapter.SearchAdapter;
import com.rva.mrb.vivify.View.Adapter.SimpleSectionedRecyclerViewAdapter;
import com.rva.mrb.vivify.View.Settings.SettingsActivity;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * This class allows user to search spotify
 */
public class SearchActivity extends BaseActivity implements SearchView,
        SearchInterface {

    @Inject
    SearchPresenter searchPresenter;
    @Inject
    NodeService nodeService;
    @Inject
    SpotifyService spotifyService;
    @BindView(R.id.search_recyclerview)
    RecyclerView recyclerview;
    @BindView(R.id.search_edittext)
    TextView searchEditText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private SearchAdapter searchAdapter;
//    private Playlist playlist;

    // Spotify
//    private static final int REQUEST_CODE = 5123;
//    private static final String REDIRECT_URI = "vivify://callback";
//    private Player mPlayer;
//    private Config playerConfig;
    private SearchModule searchModule = new SearchModule(this);
    private ApplicationModule applicationModule = new ApplicationModule((AlarmApplication) getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //Inject dagger and butterknife dependencies
        SearchComponent searchComponent = DaggerSearchComponent.builder()
                .applicationModule(applicationModule)
                .searchModule(searchModule)
                .applicationComponent(((AlarmApplication) getApplication()).getComponent())
                .build();
        searchComponent.inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Inititialize view and retrieve a fresh access token
        initView();
        refreshToken();
    }

    /**
     * This method initializes the view
     */
    private void initView() {
//        recyclerview.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setHasFixedSize(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    /**
     * This method sets the search interface to communicate with searchAdapter
     */
    public void setInterface() {
        searchAdapter.setSearchInterface(this);
    }

    /**
     * This method retrieves a new access token from the backend server.
     */
    private void refreshToken() {
        //Get refreshToken from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        Log.d("Node", "sharedpref refresh token: " + refreshToken);

        //Make call to node.js server to obtain a fresh access token
        nodeService.refreshToken(refreshToken).enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                AccessToken results = response.body();
                applicationModule.setAccessToken(results.getAccessToken());
            }

            @Override
            public void onFailure(Call<AccessToken> call, Throwable t) {
                Log.d("Node", "error: " + t.getMessage());
            }
        });
    }

    /**
     * Search button was clicked. This method makes a retrofit call to Spotify API with the string in
     * searchEditText to search for music.
     */
    @OnClick(R.id.fab3)
    public void onSearchClick() {
        Log.d("MyApp", "Fab Click");
        String searchQuery = searchEditText.getText().toString();
        spotifyService.getFullSearchResults(searchQuery).enqueue(new Callback<Search>() {
            @Override
            public void onResponse(Call<Search> call, Response<Search> response) {
                Log.d("Error Message", response.message());
                Search results = response.body();
                Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                Log.d("Artist name", results.getTracks().getItems().get(0).getArtists().get(0).getName());
//                Log.d("Track Name", results.getTracks().getItems().get(0).getName());
//                Log.d("Artist name", results.getTracks().getItems().get(0).getArtists().get(0).getName());
                List<MediaType> mediaTypeList = new ArrayList<MediaType>();
                for (Track t : results.getTracks().getItems())
                    mediaTypeList.add(new MediaType(t));
                for (Album a : results.getAlbums().getItems())
                    mediaTypeList.add(new MediaType(a));
                for (Playlist a : results.getPlaylists().getItems())
                    mediaTypeList.add(new MediaType(a));
                for (Artist a : results.getArtists().getItems())
                    mediaTypeList.add(new MediaType(a));

                searchAdapter = new SearchAdapter(mediaTypeList);
                List<SimpleSectionedRecyclerViewAdapter.Section> sections =
                        new ArrayList<SimpleSectionedRecyclerViewAdapter.Section>();

                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(0,"Tracks"));
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(results.getTracks().getItems().size(),"Albums"));
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(results.getAlbums().getItems().size()+
                        results.getTracks().getItems().size(),"Playlists"));
                sections.add(new SimpleSectionedRecyclerViewAdapter.Section(results.getAlbums().getItems().size()+
                        results.getTracks().getItems().size()+results.getPlaylists().getItems().size(),"Artists"));
                setInterface();

                //Add your adapter to the sectionAdapter
                SimpleSectionedRecyclerViewAdapter.Section[] dummy = new SimpleSectionedRecyclerViewAdapter.Section[sections.size()];
                SimpleSectionedRecyclerViewAdapter mSectionedAdapter = new
                        SimpleSectionedRecyclerViewAdapter(getApplicationContext(),R.layout.section,R.id.section_text,searchAdapter);
                mSectionedAdapter.setSections(sections.toArray(dummy));

                //Apply this adapter to the RecyclerView
//                mRecyclerView.setAdapter(mSectionedAdapter);
                recyclerview.setAdapter(mSectionedAdapter);
            }

            @Override
            public void onFailure(Call<Search> call, Throwable t) {
                Log.d("SpotifyService", "Call failed.");
            }
        });
//          LATEST
//        Log.d("MyApp", "Fab Click");
//        String searchQuery = searchEditText.getText().toString();
//        spotifyService.getSearchResults(searchQuery).enqueue(new Callback<Search>() {
//            @Override
//            public void onResponse(Call<Search> call, Response<Search> response) {
//                Log.d("Error Message", response.message());
//                Search results = response.body();
//                Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                Log.d("Track Name", results.getTracks().getItems().get(0).getName());
////                Log.d("Artist name", results.getTracks().getItems().get(0).getArtists().get(0).getName());
//                Log.d("Track Name", results.getTracks().getItems().get(0).getName());
//                Log.d("Artist name", results.getTracks().getItems().get(0).getArtists().get(0).getName());
//                searchAdapter = new SearchAdapter(results);
//                setInterface();
//
//                recyclerview.setAdapter(searchAdapter);
//            }
//
//            @Override
//            public void onFailure(Call<Search> call, Throwable t) {
//                Log.d("SpotifyService", "Call failed.");
//            }
//        });
//            spotifyService.getFeaturedPlaylists().enqueue(new Callback<Playlist>() {
//                @Override
//                public void onResponse(Call<Playlist> call, Response<Playlist> response) {
//                    if (response.isSuccessful()) {
//                        Playlist featured = response.body();
//                        Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                        Log.d("SpotifyService", "Code:" + response.code());
//                        Log.d("SpotifyService", "Message:" + response.message());
//                        Log.d("SpotifyService", "Body:" + response.body());
//                        Log.d("SpotifyService", "Featured Message:" + featured.getMessage());
//                        Log.d("SpotifyService", "Playlists #: " + featured.getPlaylists().getTotal());
//                        Log.d("SpotifyService", "First Playlist Name: " + featured.getPlaylists().getItems().get(1).getTracks().getTotal());
//                        searchAdapter = new SearchAdapter(featured);
//                        recyclerview.setAdapter(searchAdapter);
//                    }
//                    else {
//                        Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                        Log.d("SpotifyService", "Code:" + response.code());
//                        Log.d("SpotifyService", "Message:" + response.message());
//                        Log.d("SpotifyService", "Body:" + response.body());
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Playlist> call, Throwable t) {
//                }
//            });


//        spotifyService.getSearchResults(searchQuery).enqueue(new Callback<Playlist.Tracks>() {
//            @Override
//            public void onResponse(Call<Playlist.Tracks> call, Response<Playlist.Tracks> response) {
//                Playlist.Tracks results = response.body();
//                Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                searchAdapter = new SearchAdapter(results);
//            }
//
//            @Override
//            public void onFailure(Call<Playlist.Tracks> call, Throwable t) {
//
//            }
//        });
//        spotifyService.getFeaturedPlaylists().enqueue(new Callback<Playlist>() {
//            @Override
//            public void onResponse(Call<Playlist> call, Response<Playlist> response) {
//                if (response.isSuccessful()) {
//                    Playlist featured = response.body();
//                    Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                    Log.d("SpotifyService", "Code:" + response.code());
//                    Log.d("SpotifyService", "Message:" + response.message());
//                    Log.d("SpotifyService", "Body:" + response.body());
//                    Log.d("SpotifyService", "Featured Message:" + featured.getMessage());
//                    Log.d("SpotifyService", "Playlists #: " + featured.getPlaylists().getTotal());
//                    Log.d("SpotifyService", "First Playlist Name: " + featured.getPlaylists().getItems().get(1).getTracks().getTotal());
//                    searchAdapter = new SearchAdapter(featured);
//                    recyclerview.setAdapter(searchAdapter);
//                }
//                else {
//                    Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                    Log.d("SpotifyService", "Code:" + response.code());
//                    Log.d("SpotifyService", "Message:" + response.message());
//                    Log.d("SpotifyService", "Body:" + response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Playlist> call, Throwable t) {
//            }
//        });

//        spotifyService.getUser("rmoran92").enqueue(new Callback<User>() {
//            @Override
//            public void onResponse(Call<User> call, Response<User> response) {
//                if (response.isSuccessful()) {
//                    User user = response.body();
//                    Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                    Log.d("SpotifyService", "Code:" + response.code());
//                    Log.d("SpotifyService", "Message:" + response.message());
//                    Log.d("SpotifyService", "Body:" + response.body());
//                    Log.d("SpotifyService", "Username:" + user.getDisplayName());
//                    Log.d("SpotifyService", "UserId:" + user.getId());
//                }
//                else {
//                    Log.d("SpotifyService", "Successful: " + response.isSuccessful());
//                    Log.d("SpotifyService", "Code:" + response.code());
//                    Log.d("SpotifyService", "Message:" + response.message());
//                    Log.d("SpotifyService", "Body:" + response.body());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<User> call, Throwable t) {
//
//            }
//        });

//        alarmPresenter.onSearch();
//        mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
//            @Override
//            public void onInitialized(Player player) {
//                mPlayer.addConnectionStateCallback(SearchActivity.this);
//                mPlayer.addPlayerNotificationCallback(SearchActivity.this);
//                mPlayer.play("spotify:track:53XV0VrMvBcMBUWv5xSERU");
//            }
//
//            @Override
//            public void onError(Throwable throwable) {
//                Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
//            }
//        });
    }

    @Override
    protected void closeRealm() {

    }


    @Override
    public void onStart() {
        super.onStart();
        searchPresenter.setView(this);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
    // Spotify methods
//    @Override
//    public void onLoggedIn() {
//        Log.d("Spotify", "User logged in");
//    }
//
//    @Override
//    public void onLoggedOut() {
//        Log.d("Spotify", "User logged out");
//
//    }
//
//    @Override
//    public void onLoginFailed(int i) {
//        Log.d("Spotify", "LoginActivity failed");
//
//    }
//
//    @Override
//    public void onTemporaryError() {
//        Log.d("Spotify", "Temporary error occurred");
//
//    }
//
//    @Override
//    public void onConnectionMessage(String s) {
//        Log.d("Spotify", "Received connection message");
//
//    }

//    /**
//     * This method is called when a track has been selected and returns the track.
//     * @param track The track that is selected
//     */
//    @Override
//    public void onTrackSelected(Track track) {
//        Log.d("Search Activity", "At onTrackSelected");
//
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("track", Parcel.);
//        Intent intent = new Intent();
//        intent.putExtra("track", Parcels.wrap(track));
//        setResult(Activity.RESULT_OK, intent);
//        finish();
//    }

    @Override
    public void onMediaSelected(MediaType mediaType) {
        Log.d("Search Activity", "At onMediaSelected");
        Intent intent = new Intent();
        intent.putExtra("track", Parcels.wrap(mediaType));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
