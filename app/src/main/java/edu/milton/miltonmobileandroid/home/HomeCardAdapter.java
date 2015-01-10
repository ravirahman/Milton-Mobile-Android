package edu.milton.miltonmobileandroid.home;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import edu.milton.miltonmobileandroid.R;

public class HomeCardAdapter extends RecyclerView.Adapter<HomeCardHolder> {

    private ArrayList<HomeCard> cards;

    public HomeCardAdapter(ArrayList<HomeCard> cards) {
        this.cards = cards;

    }

    @Override
    public HomeCardHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.home_cardview, viewGroup, false);

        return new HomeCardHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HomeCardHolder homeCardHolder, int i) {
        HomeCard ci = cards.get(i);
        homeCardHolder.vTitle.setText(ci.title);
        homeCardHolder.vContent.setText(ci.description);
    }


    @Override
    public int getItemCount() {
        return cards.size();
    }
}
