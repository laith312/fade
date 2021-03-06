package com.messenger.fade.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.messenger.fade.GCMRegistrationManager;
import com.messenger.fade.MockContactsFragment;
import com.messenger.fade.MockLoginFragment;
import com.messenger.fade.R;
import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.ui.fragments.BaseFragment;
import com.messenger.fade.ui.fragments.ChatsFragment;
import com.messenger.fade.ui.fragments.ContactsFragment;
import com.messenger.fade.ui.fragments.MessagesFragment;
import com.messenger.fade.ui.fragments.NavigationDrawerFragment;
import com.messenger.fade.ui.fragments.ProfileFragment;

public class FadeNavActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private CharSequence mTitle;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_fade_nav;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarIcon(R.drawable.ic_ab_drawer);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment
                .setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (FadeApplication.me() == null) {
            final BaseFragment fragment = new MockLoginFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();

        } else {
            final BaseFragment fragment = new MockContactsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(final int position) {
        // update the main content by replacing fragments

        final FragmentManager fragmentManager = getSupportFragmentManager();
        final BaseFragment newFragment;

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
                newFragment = new ChatsFragment();
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fade_nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            final Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        } else if (id == android.R.id.home) {
            mNavigationDrawerFragment.openDrawer(Gravity.START);
            return true;
        } else if (id == R.id.action_logout) {
            new GCMRegistrationManager(this).unregisterGCM();
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
