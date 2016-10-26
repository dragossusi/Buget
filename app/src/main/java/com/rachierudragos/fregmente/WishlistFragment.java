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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rachierudragos.buget.DatabaseHelper;
import com.rachierudragos.buget.R;

import org.joda.time.LocalDate;

/**
 * Created by Dragos on 18.01.2016.
 */
public class WishlistFragment extends Fragment {

    DatabaseHelper myDb;
    private SimpleCursorAdapter dataAdapter;
    ListView listView;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //are fab
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();

        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        listView = (ListView) rootView.findViewById(R.id.listView_wishlist);
        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        myDb = new DatabaseHelper(getActivity());
        //
        Cursor cursor = myDb.cautarewishlist();

        // Coloanele de adaugat
        String columns[] = new String[]{
                DatabaseHelper.COL_1,
                DatabaseHelper.COL_2,
                DatabaseHelper.COL_3,
                DatabaseHelper.COL_44
        };

        int toviewids[] = new int[]{R.id.id_item, R.id.nume_item, R.id.valoare_item, R.id.bucati_item};
        dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_layout, cursor, columns, toviewids, 0);
        //
        listView.setAdapter(dataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv = (TextView) view.findViewById(R.id.nume_item);
                TextView tv1 = (TextView) view.findViewById(R.id.valoare_item);
                TextView tv2 = (TextView) view.findViewById(R.id.bucati_item);
                TextView tvid = (TextView) view.findViewById(R.id.id_item);
                final String s = tv.getText().toString();
                final String s1 = tv1.getText().toString();
                final String s2 = tv2.getText().toString();
                final String idd = tvid.getText().toString();
                //textview pentru alertdialog
                final TextView textView = new TextView(getActivity());
                textView.setText(" Câte bucăţi?");
                textView.setTextSize(16);
                textView.setPadding(3,10,3,5);
                //edittext pentru alertdialog
                final EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                editText.setText(s2);
                //view pentru alertdialog
                LinearLayout ll = new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(textView);
                ll.addView(editText);
                //creeare alertdialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Cumpără " + s + ", preţ: " + s1 + "\n")
                        .setView(ll)
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                int diferenta = Integer.valueOf(s1)*Integer.valueOf(editText.getText().toString());
                                //Cumpara valoarea
                                Toast.makeText(getActivity(), "Am cumparat " + editText.getText().toString() +" bucăţi cu " + diferenta+" lei", Toast.LENGTH_LONG).show();
                                myDb.scadebucatiW(idd,editText.getText().toString());
                                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                                String sbuget = sp.getString("buget", "N/A");
                                String azi = new LocalDate().toString("dd/MM/yyyy");
                                myDb.insertDataCump(s,s1,editText.getText().toString(),azi);
                                int buget = Integer.valueOf(sbuget);

                                buget -= diferenta;
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putString("buget", String.valueOf(buget)).commit();
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Lei: " + buget);

                                //eliminare din SQLite
                                dataAdapter.notifyDataSetChanged();
                                Cursor cursor = myDb.cautarewishlist();
                                final String columns[] = new String[]{
                                        DatabaseHelper.COL_1,
                                        DatabaseHelper.COL_2,
                                        DatabaseHelper.COL_3,
                                        DatabaseHelper.COL_44
                                };
                                int toviewids[] = new int[]{R.id.id_item, R.id.nume_item, R.id.valoare_item, R.id.bucati_item};
                                dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_layout, cursor, columns, toviewids, 0);
                                listView.setAdapter(dataAdapter);
                            }
                        })
                        .setNegativeButton("Nu", null);
                AlertDialog asd = builder.create();
                asd.show();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                TextView tv = (TextView) view.findViewById(R.id.nume_item);
                TextView tv1 = (TextView) view.findViewById(R.id.valoare_item);
                TextView tv2 = (TextView) view.findViewById(R.id.bucati_item);
                TextView tvid = (TextView) view.findViewById(R.id.id_item);
                final String s = tv.getText().toString();
                final String s1 = tv1.getText().toString();
                final String s2 = tv2.getText().toString();
                final String idd = tvid.getText().toString();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // creeare adapter cu pentru Alert Dialog //
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogview = inflater.inflate(R.layout.dialogwish, null);
                final EditText val = (EditText) dialogview.findViewById(R.id.valoare_edit);
                val.setText(s1);
                final EditText buc = (EditText) dialogview.findViewById(R.id.bucati_edit);
                buc.setText(s2);

                //construire alertdialog
                builder.setMessage("Valoare nouă:")
                        .setTitle(s + " " + s1)
                        .setView(dialogview);
                builder.setPositiveButton("Schimbă", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (val.getText().toString().equals("") || buc.getText().toString().equals(""))
                            Toast.makeText(getActivity(), "Introdu valoare înainte", Toast.LENGTH_LONG).show();
                        else {
                            //Schimba valoarea
                            myDb.updatedetailsW(idd, val.getText().toString(), buc.getText().toString());
                            dataAdapter.notifyDataSetChanged();
                            Cursor cursor = myDb.cautarewishlist();
                            final String columns[] = new String[]{
                                    DatabaseHelper.COL_1,
                                    DatabaseHelper.COL_2,
                                    DatabaseHelper.COL_3,
                                    DatabaseHelper.COL_44
                            };
                            int toviewids[] = new int[]{R.id.id_item, R.id.nume_item, R.id.valoare_item, R.id.bucati_item};
                            dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_layout, cursor, columns, toviewids, 0);
                            listView.setAdapter(dataAdapter);
                        }
                    }
                })
                        .setNegativeButton("Elimină", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Elimina valoarea
                                Toast.makeText(getActivity(), "Am sters " + s, Toast.LENGTH_LONG).show();
                                myDb.deleteNumeW(idd);
                                dataAdapter.notifyDataSetChanged();
                                Cursor cursor = myDb.cautarewishlist();
                                final String columns[] = new String[]{
                                        DatabaseHelper.COL_1,
                                        DatabaseHelper.COL_2,
                                        DatabaseHelper.COL_3,
                                        DatabaseHelper.COL_44
                                };
                                int toviewids[] = new int[]{R.id.id_item, R.id.nume_item, R.id.valoare_item, R.id.bucati_item};
                                dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_layout, cursor, columns, toviewids, 0);
                                listView.setAdapter(dataAdapter);
                            }
                        });
                AlertDialog asd = builder.create();
                asd.show();

                return false;
            }

        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem;
            int afiseaza = 1;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    if (afiseaza == 1)
                        fab.hide();
                    else
                        fab.show();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (mLastFirstVisibleItem < firstVisibleItem) {
                    //Log.i("SCROLLING DOWN","TRUE");
                    afiseaza = 1;
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    //Log.i("SCROLLING UP","TRUE");
                    afiseaza = 0;
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });
    }
}
