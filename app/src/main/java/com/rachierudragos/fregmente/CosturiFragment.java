package com.rachierudragos.fregmente;

import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rachierudragos.buget.DatabaseHelper;
import com.rachierudragos.buget.R;

/**
 * Created by Dragos on 18.01.2016.
 */
public class CosturiFragment extends Fragment {

    DatabaseHelper myDb;
    private SimpleCursorAdapter dataAdapter;
    ListView listView;
    FloatingActionButton fab;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        // are fab
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.show();

        View rootView = inflater.inflate(R.layout.fragment_costuri,container,false);

        listView = (ListView)rootView.findViewById(R.id.listView_costuri);
        return rootView;

    }
    @Override
    public void onResume() {
        super.onResume();
        myDb = new DatabaseHelper(getActivity());
        Cursor cursor = myDb.cautarecosturi();

        // Coloanele de pus in lista
        final String columns[] = new String[] {
                DatabaseHelper.COL_2,
                DatabaseHelper.COL_3,
                DatabaseHelper.COL_4
        };

        int toviewids[] = new int[] { R.id.nume_item,R.id.valoare_item,R.id.tip_item};
        dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(),R.layout.item_layout_conditionat,cursor,columns,toviewids,0);
        //
        listView.setAdapter(dataAdapter);
        //
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long arg3) {
                TextView tv = (TextView) view.findViewById(R.id.nume_item);
                TextView tv2 = (TextView) view.findViewById(R.id.valoare_item);
                TextView tv3 = (TextView) view.findViewById(R.id.tip_item);
                final String s = tv.getText().toString();
                final String s1 = tv2.getText().toString();
                String data = tv3.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                // creeare edittext pt Alert Dialog //

                final EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                editText.setText(s1);

                // creeare spinner pt Alert Dialog //

                String ss[] = { "Lunar","Săptămânal","Zilnic" };
                final ArrayAdapter<String> adp = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_dropdown_item, ss);
                final Spinner spinner = new Spinner(getActivity());
                spinner.setLayoutParams(new LinearLayout.LayoutParams(AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT));
                spinner.setAdapter(adp);
                spinner.setPadding(1,4,1,10);
                if(data.equals("L"))spinner.setSelection(0);
                else if(data.equals("S"))spinner.setSelection(1);
                else spinner.setSelection(2);

                // creeare adapter cu pentru Alert Dialog //

                LinearLayout ll=new LinearLayout(getActivity());
                ll.setOrientation(LinearLayout.VERTICAL);
                ll.addView(spinner);
                ll.addView(editText);

                // creare Alert Dialog

                builder.setMessage("Valoare nouă:\n")
                        .setTitle(s + " " + s1)
                        .setView(ll)
                        .setPositiveButton("Schimbă", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(editText.getText().toString().equals(""))
                                    Toast.makeText(getActivity(), "Introdu valoare înainte", Toast.LENGTH_LONG).show();
                                else {
                                    //Schimba valoarea

                                    myDb.updatedetails(s, editText.getText().toString(), String.valueOf(spinner.getSelectedItem().toString().charAt(0)), 2);

                                    // update listview

                                    dataAdapter.notifyDataSetChanged();
                                    Cursor cursor = myDb.cautarecosturi();
                                    final String columns[] = new String[]{
                                            DatabaseHelper.COL_2,
                                            DatabaseHelper.COL_3,
                                            DatabaseHelper.COL_4
                                    };

                                    int toviewids[] = new int[]{R.id.nume_item, R.id.valoare_item, R.id.tip_item};
                                    dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_layout_conditionat, cursor, columns, toviewids, 0);
                                    listView.setAdapter(dataAdapter);
                                }
                            }
                        })
                        .setNegativeButton("Elimină", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Elimina valoarea
                                Toast.makeText(getActivity(), "Am sters " + s, Toast.LENGTH_LONG).show();
                                myDb.deleteNumeC(s);
                                dataAdapter.notifyDataSetChanged();
                                Cursor cursor = myDb.cautarecosturi();
                                final String columns[] = new String[] {
                                        DatabaseHelper.COL_2,
                                        DatabaseHelper.COL_3,
                                        DatabaseHelper.COL_4
                                };

                                int toviewids[] = new int[] { R.id.nume_item,R.id.valoare_item,R.id.tip_item};
                                dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(),R.layout.item_layout_conditionat,cursor,columns,toviewids,0);
                                listView.setAdapter(dataAdapter);
                            }
                        });
                    AlertDialog asd = builder.create();
                    asd.show();

                    return false;
                }

            }

            );

            fab=(FloatingActionButton) getActivity().findViewById(R.id.fab);

            listView.setOnScrollListener(new AbsListView.OnScrollListener()

                                         {
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
                                             public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                                                  int totalItemCount) {

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
                                         }

            );
        }

    }
