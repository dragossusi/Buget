package com.rachierudragos.fregmente;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rachierudragos.Expandable;
import com.rachierudragos.buget.DatabaseHelper;
import com.rachierudragos.buget.R;

import org.joda.time.LocalDate;

/**
 * Created by Dragos on 18.01.2016.
 */
public class TotalFragment extends Fragment {
    final String DEFAULT = "0";
    DatabaseHelper myDb;
    ExpandableListView expandableListView;
    String sbuget;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();
        View rootView = inflater.inflate(R.layout.fragment_total, container, false);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        final int venitZ = sp.getInt("venitz", 0);
        final int venitS = sp.getInt("venits", 0);
        final int venitL = sp.getInt("venitl", 0);
        final int costZ = sp.getInt("costz", 0);
        final int costS = sp.getInt("costs", 0);
        final int costL = sp.getInt("costl", 0);
        sbuget = sp.getString("buget", DEFAULT);

        myDb = new DatabaseHelper(getActivity());
        //
        Cursor cursor = myDb.cautarewishlistlowerthan(sbuget);
        final int size = cursor.getCount();
        String wishlist[][] = new String[size][3];

        if (cursor.moveToFirst()) {
            int i = 0;
            do {
                wishlist[i][0] = cursor.getString(1);
                wishlist[i][1] = cursor.getString(2);
                wishlist[i][2] = cursor.getString(3);
                i++;
            } while (cursor.moveToNext());
        }
        //
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.exp_lv);
        String groups[] = new String[]{"Total Venituri", "Total Costuri", "Total Economii", "Listă iteme cumpărabile"};
        String children[][] = new String[][]{
                {"Lunare", "Săptămânale", "Zilnice"},
                {"Lunare", "Săptămânale", "Zilnice"},
                {"Lunare", "Săptămânale", "Zilnice"},
        };
        String valori[][] = new String[][]{
                {String.valueOf(venitL), String.valueOf(venitS), String.valueOf(venitZ)},
                {String.valueOf(costL), String.valueOf(costS), String.valueOf(costZ)},
                {String.valueOf(venitL - costL), String.valueOf(venitS - costS), String.valueOf(venitZ - costZ)},
        };
        Expandable adapter = new Expandable(getActivity(), children, groups, valori, wishlist);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

      /* You must make use of the View v, find the view by id and extract the text as below*/
                if (groupPosition == 3) {
                    TextView Text_nume = (TextView) v.findViewById(R.id.nume_item_grup);
                    TextView Text_valoare = (TextView) v.findViewById(R.id.valoare_item_grup);
                    TextView Text_bucati = (TextView) v.findViewById(R.id.bucati_item_grup);
                    final String s = Text_nume.getText().toString();
                    final String s1 = Text_valoare.getText().toString();
                    final String s2 = Text_bucati.getText().toString();

                    //textview pentru alertdialog
                    final TextView textView = new TextView(getActivity());
                    textView.setText(" Câte bucăţi?");
                    textView.setTextSize(16);
                    textView.setPadding(3, 10, 3, 5);

                    //edittext pentru alertdialog
                    final EditText editText = new EditText(getActivity());
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                    editText.setText(String.valueOf(Math.min(Integer.valueOf(sbuget)/Integer.valueOf(s1),Integer.valueOf(s2))));

                    //view pentru alertdialog
                    LinearLayout ll = new LinearLayout(getActivity());
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.addView(textView);
                    ll.addView(editText);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Cumpără " + s + ", preţ: " + s1 + "\n")
                            .setView(ll)
                            .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    int diferenta = Integer.valueOf(s1) * Integer.valueOf(editText.getText().toString());
                                    //Cumpara valoarea
                                    Toast.makeText(getActivity(), "Am cumparat " + editText.getText().toString() + " bucăţi cu " + diferenta + " lei", Toast.LENGTH_LONG).show();
                                    myDb.scadebucatiW(s, editText.getText().toString());
                                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                    String sbuget = sp.getString("buget", "N/A");
                                    String azi = new LocalDate().toString("dd/MM/yyyy");
                                    myDb.insertDataCump(s, s1, editText.getText().toString(), azi);
                                    int buget = Integer.valueOf(sbuget);

                                    buget -= diferenta;


                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putString("buget", String.valueOf(buget)).commit();
                                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Lei: " + buget);

                                    //refresh
                                    Cursor cursor = myDb.cautarewishlistlowerthan(sbuget);
                                    final int size = cursor.getCount();
                                    String wishlist[][] = new String[size][3];

                                    if (cursor.moveToFirst()) {
                                        int i = 0;
                                        do {
                                            wishlist[i][0] = cursor.getString(1);
                                            wishlist[i][1] = cursor.getString(2);
                                            wishlist[i][2] = cursor.getString(3);
                                            i++;
                                        } while (cursor.moveToNext());
                                    }
                                    String groups[] = new String[]{"Total Venituri", "Total Costuri", "Total Economii", "Listă iteme cumpărabile"};
                                    String children[][] = new String[][]{
                                            {"Lunare", "Săptămânale", "Zilnice"},
                                            {"Lunare", "Săptămânale", "Zilnice"},
                                            {"Lunare", "Săptămânale", "Zilnice"},
                                    };
                                    String valori[][] = new String[][]{
                                            {String.valueOf(venitL), String.valueOf(venitS), String.valueOf(venitZ)},
                                            {String.valueOf(costL), String.valueOf(costS), String.valueOf(costZ)},
                                            {String.valueOf(venitL - costL), String.valueOf(venitS - costS), String.valueOf(venitZ - costZ)},
                                    };
                                    Expandable adapter = new Expandable(getActivity(), children, groups, valori, wishlist);
                                    expandableListView.setAdapter(adapter);
                                    expandableListView.deferNotifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("Nu", null);
                    AlertDialog asd = builder.create();
                    asd.show();
                }
                return true;
            }
        });
        return rootView;

    }

}
