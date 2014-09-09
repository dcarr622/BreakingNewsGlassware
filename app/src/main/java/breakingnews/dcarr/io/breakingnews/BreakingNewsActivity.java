package breakingnews.dcarr.io.breakingnews;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class BreakingNewsActivity extends Activity {

    private String authToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breaking_news);


        AccountManager mgr = AccountManager.get(this);
        Account[] accts = mgr.getAccountsByType("com.google");
        Account acct = accts[0];
        AccountManagerFuture<Bundle> accountManagerFuture = mgr.getAuthToken(acct, Constants.SCOPE, null, this, null, null);
        Bundle authTokenBundle = null;
        try {
            authTokenBundle = accountManagerFuture.getResult();
        } catch (OperationCanceledException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticatorException e) {
            e.printStackTrace();
        }
        authToken = authTokenBundle.get(AccountManager.KEY_AUTHTOKEN).toString();

        RelativeLayout cardOne = (RelativeLayout) findViewById(R.id.cardOne);
        TextView cardOneName = (TextView) cardOne.findViewById(R.id.list_widget_name);
        cardOneName.setText("US Open");
        TextView cardOneDesc = (TextView) cardOne.findViewById(R.id.list_widget_description);
        cardOneDesc.setText("Marin Cilic of Croatia wins US Open in straight sets over Kei Nishikori");
        cardOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    generateTimelineCard(Constants.cardThree);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        RelativeLayout cardTwo = (RelativeLayout) findViewById(R.id.cardTwo);
        TextView cardTwoName = (TextView) cardOne.findViewById(R.id.list_widget_name);
        cardTwoName.setText("Home Depot Breach");
        TextView cardTwoDesc = (TextView) cardOne.findViewById(R.id.list_widget_description);
        cardTwoDesc.setText("Home Depot confirms its payment systems were breached; no evidence PIN numbers compromised");
        cardTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    generateTimelineCard(Constants.cardTwo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        RelativeLayout cardThree = (RelativeLayout) findViewById(R.id.cardThree);
        TextView cardThreeName = (TextView) cardOne.findViewById(R.id.list_widget_name);
        cardThreeName.setText("Penn Footabll");
        TextView cardThreeDesc = (TextView) cardOne.findViewById(R.id.list_widget_description);
        cardThreeDesc.setText("NCAA restores Penn State's eligibility for football bowl games, scholarships");
        cardThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    generateTimelineCard(Constants.cardThree);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void generateTimelineCard(String cardData) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("html", cardData);
        JSONArray menuItmes = new JSONArray();

        JSONObject deleteMenu = new JSONObject();
        deleteMenu.put("action", "DELETE");

        menuItmes.put(deleteMenu);

        json.put("menuItems", menuItmes);

        JSONObject notification = new JSONObject();
        notification.put("level", "DEFAULT");

        json.put("notification", notification);

        MirrorApiClient.getInstance(BreakingNewsActivity.this).createTimelineItem(authToken, json, new MirrorApiClient.Callback() {
            @Override
            public void onSuccess(HttpResponse response) {
                Log.d("mirror", "success");
            }

            @Override
            public void onFailure(HttpResponse response, Throwable e) {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                        builder.append("\n");
                    }
                    Log.e("mirror response", builder.toString());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                Log.e("mirror", "error");
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.breaking_news, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
