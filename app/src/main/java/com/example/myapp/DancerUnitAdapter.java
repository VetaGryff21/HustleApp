package com.example.myapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapp.model.Dancer;

import java.util.List;

public class DancerUnitAdapter extends BaseAdapter {

    private List<Dancer> dancers;
    private LayoutInflater dancerInf;
    private Context c;

    public DancerUnitAdapter(Context c, List<Dancer> theDancers){
        dancers = theDancers;
        dancerInf = LayoutInflater.from(c);
    }

    @Override
    public int getCount() {
        return dancers.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout dancerLay = (LinearLayout)dancerInf.inflate
                (R.layout.dancer_unit, parent, false);
        //get title and artist views
        TextView dancerView = (TextView)dancerLay.findViewById(R.id.dancer_fullname);
        TextView ratingClubView = (TextView)dancerLay.findViewById(R.id.dancer_club_and_rating);
        //get song using position
        Dancer dancer = dancers.get(position);
        //get title and artist strings
        dancerView.setText(dancer.getFullname());
        ratingClubView.setText(dancer.getRatingc() + "/" +dancer.getRatingd() + ", "+ dancer.getCode());
        //set position as tag
        dancerLay.setTag(position);

        return dancerLay;
    }
}
