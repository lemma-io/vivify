package com.rva.mrb.vivify.Spotify;

import com.rva.mrb.vivify.Model.Data.Playlist;
import com.rva.mrb.vivify.Model.Data.PlaylistPager;
import com.rva.mrb.vivify.Model.Data.Search;
import com.rva.mrb.vivify.Model.Data.SimpleTrack;
import com.rva.mrb.vivify.Model.Data.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rigo on 7/8/16.
 */
public interface SpotifyService {

    /**
     * Maximum number of objects to return
     */
    String LIMIT = "limit";

    /**
     * Index of the first item to return (Default = 0)
     * Use with limit to get the next set of objects
     */
    String OFFSET = "offset";

    /**
     * Comma-seperated list of keywords that will be used to filter
     * the response
     * Values are: {@code album},{@code single}
     */
    String ALBUM_TYPE = "album_type";

    /**
     * Country code. Limit response to one country code
     */
    String MARKET = "market";

    /**
     * Language code, consists of language code_country code
     * Example: es_MX meaning "Spanish (Mexico)"
     */
    String LOCALE = "locale";

    /**
     * Filters for the query
     */
    String FIELDS = "fields";

    /***********
     *Me
     ***********/
    @GET("me/playlists")
    Call<PlaylistPager> getMyPlaylists();
    /***********
     * Playlists
     ***********/
    @GET("browse/featured-playlists")
    Call<Playlist> getFeaturedPlaylists();
    /***********
     * Tracks
     ***********/

    @GET("/tracks/{id}")
    void getTrack(@Path("id") String trackId);

    /***********
     * User
     ***********/

    @GET("users/{user_id}")
    Call<User> getUser(@Path("user_id") String userId);

    @GET("users/{user_id}/playlists")
    Call<PlaylistPager> getUserPlaylists(@Path("user_id") String userId);
    /***********
     * Search
     ***********/

    @GET("search?type=track")
    Call<Search> getSearchResults(@Query("q") String searchQuery);

    @GET("search?type=playlist,album,track,artist&limit=5")
    Call<Search> getFullSearchResults(@Query("q") String searchQuery);
}
