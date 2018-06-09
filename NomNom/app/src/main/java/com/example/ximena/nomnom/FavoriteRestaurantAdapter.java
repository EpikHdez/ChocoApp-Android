package com.example.ximena.nomnom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximena.nomnom.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class FavoriteRestaurantAdapter extends ArrayAdapter<Restaurant> implements View.OnClickListener {

    private ArrayList<Restaurant> dataSet;
    Context mContext;

    public FavoriteRestaurantAdapter(ArrayList<Restaurant> dataSet, Context mContext) {
        super(mContext, R.layout.restaurant_fav_item, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;

        ImageView info;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Restaurant dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.restaurant_fav_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtType.setText(dataModel.getType());
        Picasso.with(mContext).load("http://i.imgur.com/DvpvklR.png").into(viewHolder.info);
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
    @Override
    public void onClick(View v) {

    }
}
