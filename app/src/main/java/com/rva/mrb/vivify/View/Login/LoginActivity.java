package com.rva.mrb.vivify.View.Login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.rva.mrb.vivify.AlarmApplication;
import com.rva.mrb.vivify.ApplicationModule;
import com.rva.mrb.vivify.BaseActivity;
import com.rva.mrb.vivify.BuildConfig;
import com.rva.mrb.vivify.Model.Data.Tokens;
import com.rva.mrb.vivify.R;
import com.rva.mrb.vivify.Spotify.NodeService;
import com.rva.mrb.vivify.Spotify.SpotifyService;
import com.rva.mrb.vivify.View.Alarm.AlarmActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bao on 9/28/16.
 */
public class LoginActivity extends BaseActivity {

    @Inject
    NodeService nodeService;
    @Inject
    SpotifyService spotifyService;
    @BindView(R.id.login_button)
    Button loginButton;
//    private static final String CLIENT_ID = BuildConfig.SPOTIFY_CLIENT_ID;
    private static final String REDIRECT_URI = "vivify://callback";
    private static final int REQUEST_CODE = 5123;
    private ApplicationModule applicationModule = new ApplicationModule((AlarmApplication) getApplication());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //inject dagger and butterknife dependencies
        LoginComponent loginComponent = DaggerLoginComponent.builder()
                .applicationModule(applicationModule)
                .applicationComponent(((AlarmApplication) getApplication()).getComponent())
                .build();
        loginComponent.inject(this);
        ButterKnife.bind(this);

    }

    /**
     * OnClick for the login button. This method will launch the Spotify Android SDK to allow user
     * to login and retrieve authorization code for authorization-code-flow.
     */
    @OnClick(R.id.login_button)
    public void onLoginButtonClick() {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(getString(R.string.spotify_client_id),
                AuthenticationResponse.Type.CODE, REDIRECT_URI);

        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    /**
     * This method is called as a result of Spotify Android SDK authentication. If response is good,
     * then a call to the node.js backend will be made exchange authorization code for access and
     * refresh tokens.This method will then start a new AlarmActivity
     * @param requestCode code sent with spotify call to verify app
     * @param resultCode
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                case CODE:

                    Log.d("Spotify", "Response Code: " + response.getCode());
                    //Make retrofit call to node.js server
                    nodeService.getTokens(response.getCode()).enqueue(new Callback<Tokens>() {
                        @Override
                        public void onResponse(Call<Tokens> call, Response<Tokens> response) {
                            Log.d("Node", "Response Message: " + response.message());
                            //get the response body
                            Tokens results = response.body();
                            Log.d("Node", "Response Body: " + response.body().toString());
                            Log.d("Node", "Code: " + response.code());
                            Log.d("Node", "AccessToken: " + results.getAccessToken());
                            Log.d("Node", "Refresh Token: " + results.getRefreshToken());

                            Calendar cal = Calendar.getInstance();
                            cal.add(Calendar.SECOND, results.getExpiresIn());
                            long expires = cal.getTimeInMillis();
                            //save the access token and refesh token in Shared Preferences
                            SharedPreferences sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString("access_token", results.getAccessToken());
                            editor.putString("refresh_token", results.getRefreshToken());
                            editor.putBoolean("isLoggedIn", true);
                            editor.putLong("expires", expires);
                            editor.commit();

                            //Start alarm activity
                            startAlarmActivity();
                        }

                        @Override
                        public void onFailure(Call<Tokens> call, Throwable t) {
                            Log.d("Node", "Call failed: " + t.getMessage());
                            t.printStackTrace();
                        }
                    });
                    Log.d("Node", "Made call to node");
                    break;
                case ERROR:
                    break;
                default:
            }
        }
    }

    /**
     * This method starts the alarm activity class
     */
    public void startAlarmActivity() {
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void closeRealm() {

    }
}
