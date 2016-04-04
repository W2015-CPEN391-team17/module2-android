package com.example.justin.bluetoothmodule;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCustomArrayAdaptor extends ArrayAdapter<String> {
    // my new class variables, copies of constructor params, but add more if required
    private Context context ;
    private ArrayList<String> theStringArray;

    public final int numRows = 500 ;
    private boolean [] RowValidity = new boolean [numRows];

    // constructor
    public MyCustomArrayAdaptor ( Context _context,
                                  int textViewResourceId,
                                  ArrayList<String> _theStringArray
    )
    {
        // call base class constructor
        super(_context, textViewResourceId, _theStringArray);
        // save the context and the array of strings we were given
        context = _context;
        theStringArray = _theStringArray;
        clearValidity ();
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE );
        View row = inflater.inflate ( R.layout.row, parent, false );
        ImageView icon = (ImageView) row.findViewById (R.id.BTicon);
        icon.setImageResource (R.drawable.bluetooth);
        icon.setVisibility (View.VISIBLE);
        TextView label = (TextView) row.findViewById( R.id.BTdeviceText);
        label.setText (theStringArray.get(position));
        icon = (ImageView) row.findViewById (R.id.Selected);
        icon.setImageResource (R.drawable.redcross);
        icon.setVisibility (View.VISIBLE);

        if(RowValidity [position] == false)
            icon.setImageResource (R.drawable.redcross);
        else
            icon.setImageResource (R.drawable.greentick);


        return row;
    }

    public void setValid (int position) { RowValidity [position] = true ; }
    public void setInValid (int position) { RowValidity [position] = false ; }
    public void clearValidity () {
        for(int i = 0; i < numRows; i ++)
            RowValidity[i] = false ;
    }
}
