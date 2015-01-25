package edu.milton.miltonmobileandroid;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ravi on 12/14/14.
 */
public class NavigationListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<NavigationGroup> navigationGroups;
    private HashMap<NavigationGroup, ArrayList<NavigationItem>> navigationItems;

    public NavigationListAdapter(Context context, ArrayList<NavigationGroup> navigationGroups,
                                 HashMap<NavigationGroup, ArrayList<NavigationItem>> navigationItems) {
        this.context = context;
        this.navigationGroups = navigationGroups;
        this.navigationItems = navigationItems;

    }
    @Override
    public int getGroupCount() {
        return navigationGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return navigationItems.get(getGroup(groupPosition)).size();
    }

    @Override
    public NavigationGroup getGroup(int groupPosition) {
        return navigationGroups.get(groupPosition);
    }

    @Override
    public NavigationItem getChild(int groupPosition, int childPosition) {
        return navigationItems.get(getGroup(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return ((NavigationGroup) getGroup(groupPosition)).id;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return ((NavigationItem) getChild(groupPosition,childPosition)).id;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String headerTitle = ((NavigationGroup) getGroup(groupPosition)).title;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this.context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.navigation_fragment_listview_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.navigation_fragment_listview_group_desc);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.navigation_fragment_listview_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.navigation_fragment_listview_item_desc);

        txtListChild.setText(((NavigationItem) getChild(groupPosition,childPosition)).title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
