package com.gamerscave.acrabackend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.gamerscave.acrabackend.content.Content;
import com.gamerscave.acrabackend.utils.VolleyConnect;

import java.util.List;
import java.util.Random;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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


    }
    private RecyclerView rcv;
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        this.rcv = recyclerView;
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Content.ITEMS));
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
        startService(new Intent(this, CRService.class));
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
    public void onWindowFocusChanged(boolean focus){
        if(!focus){
            kill();
        }else{
            start();
        }
        Log.e("DEBUG", "FOCUS " + focus);
    }

    public void start(){
        if(timer == null){
            timer = new Timer();
            runnable = new UpdateInterval();
            //180000 ms = 3 minutes
            timer.scheduleAtFixedRate(runnable, 0L, 180000L);
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
            Log.e("DEBUG", "Scheduled task tick");
            if(!looper){
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

    private void tick(){
        VolleyConnect vc = new VolleyConnect();
        vc.connect(ErrorListActivity.this);
        if(rcv != null) {
            runOnUiThread(new Runnable(){
                public void run(){
                    rcv.invalidate();
                }
            });

        }
    }



}
