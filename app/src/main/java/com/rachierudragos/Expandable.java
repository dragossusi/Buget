package com.rachierudragos;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.rachierudragos.buget.R;

/**
 * Created by Dragos on 21.04.2016.
 */
public class Expandable extends BaseExpandableListAdapter {
    public Context context;
    public String elemente[][];
    public String lista[];
    public String valori[][];
    public String wishlist[][];

    public Expandable(Context context, String elemente[][], String lista[],String valori[][],String wishlist[][]){
        this.context = context;
        this.elemente = elemente;
        this.lista = lista;
        this.valori = valori;
        this.wishlist = wishlist;
    }

    @Override
    public int getGroupCount() {
        return lista.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(groupPosition<3)
            return elemente[groupPosition].length;
        else
            return wishlist.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return lista[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(groupPosition<3)
            return elemente[groupPosition][childPosition];
        else
            return wishlist[childPosition][0];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String nume = (String) getGroup(groupPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.total_grup, parent, false);
        }
        TextView textView_grup = (TextView) convertView.findViewById(R.id.nume_grup);
        textView_grup.setTypeface(null, Typeface.BOLD);
        textView_grup.setText(nume);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String nume = getChild(groupPosition,childPosition).toString();
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_grup, parent, false);
        }
        TextView textView_item = (TextView) convertView.findViewById(R.id.nume_item_grup);
        TextView textView_valoare = (TextView) convertView.findViewById(R.id.valoare_item_grup);
        textView_item.setText(nume);
        if(groupPosition<3)
            textView_valoare.setText(valori[groupPosition][childPosition]);
        else {
            textView_valoare.setText(wishlist[childPosition][1]);
            TextView textView_bucati = (TextView) convertView.findViewById(R.id.bucati_item_grup);
            textView_bucati.setText(wishlist[childPosition][2]);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
