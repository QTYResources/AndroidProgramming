package com.mycompany.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
public class OAuthActivity extends Activity {
    public static final String CLIENT_ID
            = "<Client ID from foursquare.com/developer>";
    public static final String CLIENT_SECRET
            = "<Client SECRET from foursquare.com/developer>";
    public static final Token EMPTY_TOKEN = null;
    public static final String ACCESS_TOKEN = "foursquare.access_token";
    private static final String TAG = "FoursquareOAuth2";
    private OAuthService mOAuthService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.oauth_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOAuthService = new ServiceBuilder()
                .provider(Foursquare2Api.class)
                .apiKey(CLIENT_ID)
                .apiSecret(CLIENT_SECRET)
                .callback("oauth://foursquare")
                .build();
        String authorizationUrl =
                mOAuthService.getAuthorizationUrl(EMPTY_TOKEN);
        WebView webView = (WebView) findViewById(R.id.oauth_view);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,
                                                    String url) {
                if (url.startsWith("oauth")) {
                    Uri uri = Uri.parse(url);
                    String oauthCode = uri.getQueryParameter("code");
                    Verifier verifier = new Verifier(oauthCode);
                    new GetTokenAccess().execute(verifier);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }
        };
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(authorizationUrl);
    }

    class GetTokenAccess extends AsyncTask<Verifier, Void, Token> {
        @Override
        protected Token doInBackground(Verifier... verifiers) {
            Token accessToken = mOAuthService.
                    getAccessToken(EMPTY_TOKEN, verifiers[0]);
            return accessToken;
        }

        @Override
        protected void onPostExecute(Token token) {
            if (token != null) {
                Intent intent = new Intent();
                intent.putExtra(ACCESS_TOKEN, token.getToken());
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        }
    }
}
