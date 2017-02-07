package com.example.trevonharvey.nutritioninformation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Trevon Harvey on 4/26/2016.
 */
public class CategoryItemAdapter extends ArrayAdapter<CategoryItem> {

    private static class ViewHolder{
        private TextView nameItemTextView;
        private TextView categoryItemTextView;
    }

    public CategoryItemAdapter(Context context, List<CategoryItem> info){
        super(context, -1, info);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        CategoryItem categoryItem = getItem(position);

        ViewHolder viewHolder;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView =
                    inflater.inflate(R.layout.list_item, parent, false);
            viewHolder.nameItemTextView =
                    (TextView)convertView.findViewById(R.id.nameItemTextView);
            viewHolder.categoryItemTextView =
                    (TextView)convertView.findViewById(R.id.categoryItemTextView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Context context = getContext();
        viewHolder.nameItemTextView.setText(context.getString(R.string.categoryItemName, categoryItem.getName()));
        viewHolder.categoryItemTextView.setText(context.getString(R.string.categoryName, categoryItem.getCategoryName()));

        return convertView;
    }
}
