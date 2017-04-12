package com.gamerscave.acrabackend;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.gamerscave.acrabackend.content.Content;
import com.gamerscave.acrabackend.content.HomeSCR;
import com.gamerscave.acrabackend.utils.Error;
import com.gamerscave.acrabackend.utils.SQLSaver;

import static com.gamerscave.acrabackend.content.Content.ITEMS;

/**
 * A fragment representing a single Error detail screen.
 * This fragment is either contained in a {@link ErrorListActivity}
 * in two-pane mode (on tablets) or a {@link ErrorDetailActivity}
 * on handsets.
 */
public class ErrorDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private Content.Item mItem;

    private HomeSCR homescr;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ErrorDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            if(getArguments().getString(ARG_ITEM_ID).equals("HomeScreen")){
                homescr = HomeSCR.newInstance(ITEMS);
            }else {
                mItem = Content.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));

                Activity activity = this.getActivity();
                CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(mItem.error.getTitle());
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.error_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.error_detail)).setText(mItem.error.getContent());
            Button b = (Button) rootView.findViewById(R.id.delete);
            b.setVisibility(View.VISIBLE);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SQLSaver sql = new SQLSaver(getActivity());
                    sql.delete(mItem.error.hash);
                    sql.onDestroy();


                }
            });
        }else{
            if(homescr != null){
                ((TextView) rootView.findViewById(R.id.error_detail)).setText(homescr.HOMESCREEN_CONTENT);
                Button b = (Button) rootView.findViewById(R.id.delete);
                b.setVisibility(View.GONE);
            }
        }

        return rootView;
    }
}
