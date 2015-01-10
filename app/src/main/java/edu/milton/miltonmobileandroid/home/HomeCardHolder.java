package edu.milton.miltonmobileandroid.home;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import edu.milton.miltonmobileandroid.R;

public class HomeCardHolder extends RecyclerView.ViewHolder {
    protected TextView vTitle;
    protected TextView vContent;
    public HomeCardHolder(View itemView) {
        super(itemView);
        vTitle =  (TextView) itemView.findViewById(R.id.home_cardview_title);
        vContent =  (TextView) itemView.findViewById(R.id.home_cardview_content);
    }
}
