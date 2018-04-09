package com.rorsethj.expertexpense;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleIconTextRowAdapter extends BaseAdapter {

    private Context mContext;
    private String[]  Title;
    private int[] imge;

    public SimpleIconTextRowAdapter(Context context, String[] text1,int[] imageIds) {
        mContext = context;
        Title = text1;
        imge = imageIds;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return Title.length;
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row;
        row = inflater.inflate(R.layout.simple_icon_text_row, parent, false);
        TextView title;
        ImageView i1;
        i1 = (ImageView) row.findViewById(R.id.simple_row_icon);
        title = (TextView) row.findViewById(R.id.simple_row_title);
        title.setText(Title[position]);
        i1.setImageResource(imge[position]);

        return (row);
    }
}
