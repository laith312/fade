package com.messenger.fade;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.messenger.fade.application.FadeApplication;
import com.messenger.fade.ui.fragments.BaseFragment;

/**
 * Created by kkawai on 1/5/15.
 */
public final class MockContactsFragment extends BaseFragment {

    public static final String TAG = MockContactsFragment.class.getSimpleName();

    private int mToId;
    private String mToUsername;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.mock_contacts_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (FadeApplication.me().getUsername().equals(MockLoginFragment.MOCK_USER_123)) {
            mToId = MockLoginFragment.MOCK_USER_ID_888;
            mToUsername = MockLoginFragment.MOCK_USER_888;
        } else {
            mToId = MockLoginFragment.MOCK_USER_ID_123;
            mToUsername = MockLoginFragment.MOCK_USER_123;
        }

        final ListView listView = (ListView)getView().findViewById(R.id.list);
        listView.setAdapter(new MyAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToMockChat();
            }
        });

        CloudMessagingHelper.registerIfNecessary(getActivity());
    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            final Context c = parent.getContext();
            final TextView textView = new TextView(c);
            textView.setTextSize(c.getResources().getDimension(R.dimen.contacts_font_size));
            final int p = c.getResources().getDimensionPixelSize(R.dimen.mock_user_padding);
            textView.setPadding(p,p,p,p);
            textView.setText(mToUsername);
            return textView;
        }
    }

    private void goToMockChat() {
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        final BaseFragment fragment = new MockChatFragment();
        final Bundle args = new Bundle();
        args.putInt(Constants.PROPERTY_USERID, mToId);
        args.putString(Constants.PROPERTY_USERNAME, mToUsername);
        fragment.setArguments(args);

        fragmentManager.beginTransaction().replace(R.id.container,fragment).addToBackStack(null).commit();

    }
}
