package com.example.ximena.nomnom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximena.nomnom.model.New;
import com.example.ximena.nomnom.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends ArrayAdapter<New> implements View.OnClickListener {

    private ArrayList<New> dataSet;
    Context mContext;

    public NewsAdapter(ArrayList<New> dataSet, Context mContext) {
        super(mContext, R.layout.new_item, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtType;


    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        New dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.new_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtType = (TextView) convertView.findViewById(R.id.type);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        viewHolder.txtName.setText(dataModel.getTitle());
        viewHolder.txtType.setText(dataModel.getBody());

        // Return the completed view to render on screen
        return convertView;
    }
    @Override
    public void onClick(View v) {

    }
}
