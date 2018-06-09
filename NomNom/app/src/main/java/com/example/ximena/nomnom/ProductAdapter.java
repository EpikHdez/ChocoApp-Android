package com.example.ximena.nomnom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ximena.nomnom.model.Product;
import com.example.ximena.nomnom.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends ArrayAdapter<Product> implements View.OnClickListener {

    private ArrayList<Product> dataSet;
    Context mContext;

    public ProductAdapter(ArrayList<Product> dataSet, Context mContext) {
        super(mContext, R.layout.restaurant_item, dataSet);
        this.dataSet = dataSet;
        this.mContext = mContext;
    }

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        TextView txtPrice;

        ImageView info;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Product dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.product_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.name);
            viewHolder.txtPrice = (TextView) convertView.findViewById(R.id.price);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.image);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }



        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.txtPrice.setText(Float.toString(dataModel.getPrice()));
        Picasso.with(mContext).load(dataModel.getPicture()).into(viewHolder.info);
        viewHolder.info.setOnClickListener(this);
        viewHolder.info.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }
    @Override
    public void onClick(View v) {

    }
}
