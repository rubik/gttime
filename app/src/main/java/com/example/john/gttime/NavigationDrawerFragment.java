package com.example.john.gttime;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements NavigationDrawerCallbacks {

    /**
     * All the starred stops.
     */
    public static final String KEY_STARRED_STOPS = "key_starred_stop";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";


    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private SharedPreferences mSharedPrefs;

    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerList;
    private NavigationDrawerAdapter mNavigationDrawerAdapter;
    private View mFragmentContainerView;
    private TextView mTextNoStarred;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private TreeSet<String> mStarred = new TreeSet<>(new Comparator<String>() {
        public int compare(String first, String second) {
            Integer a = Integer.parseInt(first);
            Integer b = Integer.parseInt(second);
            return a > b ? 1 : (a.equals(b) ? 0 : -1);
        }
    });
    private final ArrayList<NavigationItem> mNavigationItems = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mSharedPrefs = sp;
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
        }
        mStarred.addAll(getStarredFromPrefs());
    }

    public TreeSet<String> getStarred() {
        return mStarred;
    }

    public Set<String> getStarredFromPrefs() {
        return mSharedPrefs.getStringSet(KEY_STARRED_STOPS, new HashSet<String>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mTextNoStarred = (TextView) view.findViewById(R.id.drawer_text_no_starred);

        mDrawerList = (RecyclerView) view.findViewById(R.id.drawer_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mDrawerList.setLayoutManager(layoutManager);
        mDrawerList.setHasFixedSize(true);
        mNavigationDrawerAdapter = new NavigationDrawerAdapter(mNavigationItems);
        mNavigationDrawerAdapter.setNavigationDrawerCallbacks(this);
        mDrawerList.setAdapter(mNavigationDrawerAdapter);
        datasetChanged();
        return view;
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     * @param toolbar      The Toolbar of the activity.
     */
    public void setup(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary));

        mActionBarDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActionBarDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
    }

    public void datasetChanged() {
        getMenu();
        if (mNavigationItems.isEmpty()) {
            Log.i("Gttime", "No starred, showing text " + mTextNoStarred.getText());
            mDrawerList.setVisibility(View.GONE);
            mTextNoStarred.setVisibility(View.VISIBLE);
        }
        mTextNoStarred.setVisibility(View.GONE);
        mDrawerList.setVisibility(View.VISIBLE);
        mNavigationDrawerAdapter.notifyDataSetChanged();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public ActionBarDrawerToggle getActionBarDrawerToggle() {
        return mActionBarDrawerToggle;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    public void getMenu() {
        mNavigationItems.clear();
        mStarred.clear();
        mStarred.addAll(getStarredFromPrefs());
        if (mStarred.isEmpty()) {
            return;
        }
        for (String stopNumber : mStarred) {
            mNavigationItems.add(new NavigationItem(stopNumber,
                    getResources().getDrawable(R.drawable.ic_label_grey, getActivity().getTheme())));
        }
    }

    public void addToFavorites(String stopNumber) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        mStarred.add(stopNumber);
        editor.putStringSet(KEY_STARRED_STOPS, new HashSet<>(mStarred));
        editor.commit();
    }

    public void removeFromFavorites(String stopNumber) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        mStarred.remove(stopNumber);
        editor.putStringSet(KEY_STARRED_STOPS, new HashSet<>(mStarred));
        editor.commit();
    }

    public Boolean isFavorite(String stopNumber) {
        return mStarred.contains(stopNumber);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

}
