package com.hlxx.climber.services;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.hlxx.climber.R;
import com.hlxx.climber.dataTables.Comments;

public class CommentItemAdapter  extends ArrayAdapter<Comments>{
    Context mContext;
    int mLayoutResourceId;

    public CommentItemAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        final Comments currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(R.layout.row_list_comment, parent, false);
        }
        row.setTag(currentItem);
        return row;
    }
}
