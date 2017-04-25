package com.gamerscave.acrabackend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gamerscave.acrabackend.background.CRService;
import com.gamerscave.acrabackend.content.Content;
import com.gamerscave.acrabackend.utils.SQLSaver;
import com.gamerscave.acrabackend.utils.VolleyConnect;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An activity representing a list of Errors. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ErrorDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ErrorListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    Button home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Settings.DOMAIN == null) new Settings(this);
        setContentView(R.layout.activity_error_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        View recyclerView = findViewById(R.id.error_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.error_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
        home = (Button) findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ErrorDetailFragment.ARG_ITEM_ID, "HomeScreen");
                    ErrorDetailFragment fragment = new ErrorDetailFragment();
                    fragment.setArguments(arguments);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.error_detail_container, fragment)
                            .commit();
                } else {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ErrorDetailActivity.class);
                    intent.putExtra(ErrorDetailFragment.ARG_ITEM_ID, "HomeScreen");

                    context.startActivity(intent);
                }
            }
        });


    }
    private RecyclerView rcv;
    SimpleItemRecyclerViewAdapter adapter;
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        this.rcv = recyclerView;
        adapter = new SimpleItemRecyclerViewAdapter(Content.ITEMS);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Content.Item> mValues;

        public SimpleItemRecyclerViewAdapter(List<Content.Item> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.error_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            if(holder.mItem != null) {
                holder.mIdView.setText(mValues.get(position).error.getTitle());
                holder.mContentView.setText(mValues.get(position).error.getDesc());

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mTwoPane) {
                            Bundle arguments = new Bundle();
                            arguments.putString(ErrorDetailFragment.ARG_ITEM_ID, Long.toString(holder.mItem.error.getId()));
                            ErrorDetailFragment fragment = new ErrorDetailFragment();
                            fragment.setArguments(arguments);
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.error_detail_container, fragment)
                                    .commit();
                        } else {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, ErrorDetailActivity.class);
                            intent.putExtra(ErrorDetailFragment.ARG_ITEM_ID, Long.toString(holder.mItem.error.getId()));

                            context.startActivity(intent);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public Content.Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
            }

            public void inval(){
                mView.invalidate();
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();

        kill();
    }

    @Override
    public void onPause(){
        super.onPause();
        //If we want the service to run, we start it
        if(Settings.RUN_BACKGROUND)
            startService(new Intent(this, CRService.class));
        kill();
    }


    @Override
    public void onResume(){
        super.onResume();
        //We don't need to see if it should run in the background to stop it
        //If the user doesn't want it active, it has to be able to stop
        //And it is active when the user is in settings,  but the service stops
        //when this activity is reopened
        stopService(new Intent(this, CRService.class));
    }

    Timer timer = null;
    UpdateInterval runnable;
    boolean looper = false;
    /*
    This runnable simply refreshes the content while in the app. Every 5 minutes.
     */


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate your main_menu into the menu
        getMenuInflater().inflate(R.menu.coremenu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settngs:
                Intent i = new Intent(this, Setup.class);
                i.putExtra("exist", true);
                startActivity(i);
                finish();
                return true;
            case R.id.wipedp:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface iface, int id){
                                SQLSaver sql = new SQLSaver(ErrorListActivity.this);
                                sql.wipe();
                                sql.onDestroy();
                            }})
                        .setNegativeButton("No", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface iface, int id){

                            }
                        }).show();

                return true;
            case R.id.crash:
                throw new RuntimeException("Test error");
            case R.id.help:
                AlertDialog.Builder b2 = new AlertDialog.Builder(this);

                b2.setTitle("Help")
                        .setMessage("This is a backend for ACRA that runs" +
                                " primarily on Android. There is a server-component involved, which" +
                                " is important to ensure the security of your device. YOu connect to the server," +
                                " which then does whatever action is necessary. You need the server to be able to" +
                                " get the logs, but the logs are then stored and viewed on your Android-device. The background service" +
                                " will (if active) alert you of any new crashes using a Notification. It stores the logs in a local" +
                                " database(using SQLite) and you can delete a single entry or wipe the entire database. The issues" +
                                " are given hashed issue ID's based on their stacktrace. The stacktrace is taken from the log file." +
                                " This app is configured for a specific input, and is not designed to handle all input types. It is something" +
                                " that needs to be updated in the GitHub repository. You will not get the full log, because any changes in" +
                                " output before the stacktrace itself will create a new hash for each time. For big apps with many users that" +
                                " may mean 1 unique report from every user for every time the crash happens. More details about this app" +
                                " and the server component can be found on the GitHub repository.")
                        .setPositiveButton("Got it!", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface iface, int id){
                                iface.dismiss();
                            }
                        }).setNegativeButton("Open Github in the browser", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface iface, int id){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/GamersCave/ACRA-Android-PHP-backend"));
                    startActivity(browserIntent);
                }
            })
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onWindowFocusChanged(boolean focus){
        if(!focus){
            kill();
        }else{
            start();
        }
        Log.e("DEBUG", "FOCUS " + focus);
    }

    public void start(){
        if(timer == null && Settings.AUTO_REFRESH_WHILE_OPEN){
            timer = new Timer();
            runnable = new UpdateInterval();
            //30000 ms = 10 seconds
            //This is at a short rate because it should update a lot. Can be adjusted by editing source code
            //By having it at 30 seconds, it will update to handle whatever new errors show up
            timer.scheduleAtFixedRate(runnable, 0L, 30000);
        }
    }

    public void kill(){
        if(timer != null) {
            timer.cancel();
            boolean rep = runnable.cancel();
            Log.e("DEBUG", "REP = " + rep);
            runnable = null;
            timer = null;
            looper = false;//redeclare looper as false. Once we recreate the thread, we need this to be false
            //to re-prepare it
        }
    }

    public class UpdateInterval extends TimerTask {
        @Override
        public void run() {
            if(Settings.AUTO_REFRESH_WHILE_OPEN) {
                Log.e("DEBUG", "Scheduled task tick");
                if (!looper) {
                    //Call Looper.prepare before creating calls to the internet.
                    //This only needs to be done once per thread, and is only allowed
                    //to do once per thread(hence the boolean). The thread is recalled,
                    //but is still once instance
                    Looper.prepare();
                    looper = true;
                }
                tick();
            }
        }
    }

    private void tick(){
        VolleyConnect vc = new VolleyConnect();
        vc.connect(ErrorListActivity.this);
        if(rcv != null) {
            runOnUiThread(new Runnable(){
                public void run(){
                    adapter.notifyDataSetChanged();
                    rcv.invalidate();
                }
            });
            Log.e("DEBUG", Content.ITEMS.size() + "");
        }
    }



}
