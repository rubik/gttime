package com.example.john.gttime;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.john.gttime.model.ApiResult;
import com.example.john.gttime.model.StopResult;
import com.example.john.gttime.provider.FiveT;
import com.example.john.gttime.utils.RecyclerViewAdapter;
import com.example.john.gttime.utils.Utils;
import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;

import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInRightAnimator;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private RequestQueue mRequestQueue;
    private EditText mStopEdit;
    private TextView mResultNoStop;
    private TextView mStopName;
    private ProgressBarCircularIndeterminate mProgressCircle;
    private RelativeLayout mResults;

    private ArrayList<StopResult> mDataset;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        ButtonFloat mButtonFloat = (ButtonFloat) findViewById(R.id.fab_search);
        mButtonFloat.setBackgroundColor(getResources().getColor(R.color.primary));
        mButtonFloat.setRippleColor(getResources().getColor(R.color.primaryDark));
        mButtonFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });

        mStopEdit = (EditText) findViewById(R.id.stop_edit);
        mResultNoStop = (TextView) findViewById(R.id.result_no_stop);
        mStopName = (TextView) findViewById(R.id.results_stop_name);
        mProgressCircle = (ProgressBarCircularIndeterminate) findViewById(R.id.progress_circle);
        mResults = (RelativeLayout) findViewById(R.id.results);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer,
                (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        mDataset = new ArrayList<StopResult>();
        mRecyclerView = (RecyclerView) findViewById(R.id.results_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewAdapter(mDataset, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new SlideInRightAnimator());
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }

    private void refreshData() {
        Utils.hideSoftKeyboard(this);
        if (!Utils.isOnline(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), R.string.toast_no_conn, Toast.LENGTH_SHORT).show();
            return;
        }
        String stopNumber = mStopEdit.getText().toString();
        if (stopNumber.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.toast_no_stop, Toast.LENGTH_SHORT).show();
            return;
        }
        mResultNoStop.setVisibility(View.GONE);
        mResults.setVisibility(View.GONE);
        mProgressCircle.setVisibility(View.VISIBLE);
        if (!mDataset.isEmpty()) {
            int size = mDataset.size();
            mDataset.clear();
            mAdapter.notifyItemRangeRemoved(0, size);
        }
        FiveT.getStopData(stopNumber, mRequestQueue, new FiveT.StopDataClientListener() {
            @Override
            public void onResponse(ApiResult result) {
                mProgressCircle.setVisibility(View.GONE);
                if (result.getStopResults().size() == 0) {
                    mResultNoStop.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), R.string.toast_no_data, Toast.LENGTH_SHORT).show();
                    return;
                }
                int i = 0;
                mStopName.setText(result.getStopName());
                for (StopResult res : result.getStopResults()) {
                    mDataset.add(res);
                    mAdapter.notifyItemInserted(i++);
                }
                mResults.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
