package com.messenger.fade.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.fade.R;
import com.messenger.fade.ui.fragments.ChatFragment;
import com.messenger.fade.ui.fragments.ContactsFragment;
import com.messenger.fade.ui.fragments.MessagesFragment;
import com.messenger.fade.ui.fragments.NavigationDrawerFragment;
import com.messenger.fade.ui.fragments.ProfileFragment;
import com.messenger.fade.ui.fragments.SettingsFragment;

public class FadeNavActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fade_nav);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment
                .setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // update the main content by replacing fragments
        final FragmentManager fragmentManager = getFragmentManager();
        final Fragment newFragment;

        Log.e("zzz", "position:" + position);

        switch (position) {
            case 0:
                newFragment = new ProfileFragment();
                break;
            case 1:
                newFragment = new MessagesFragment();
                break;
            case 2:
                newFragment = new ContactsFragment();
                break;
            case 3:
                newFragment = new ChatFragment();
                break;
            case 4:
                newFragment = new SettingsFragment();
                break;

            default:
                newFragment = new ProfileFragment();
                break;
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, newFragment).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.fade_nav, menu);
            restoreActionBar();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
