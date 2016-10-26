package com.rachierudragos.fregmente;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.rachierudragos.buget.DatabaseHelper;
import com.rachierudragos.buget.R;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Dragos on 18.01.2016.
 */
public class CalendarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //nu are fab
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.hide();
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        final CalendarView calendarView = (CalendarView) rootView.findViewById(R.id.calendarview);

        final Long date[] = {calendarView.getDate()};
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                if (calendarView.getDate() != date[0]) {
                    date[0] = calendarView.getDate();
                    DatabaseHelper myDb = new DatabaseHelper(getActivity());
                    Cursor cursor = myDb.cautarevenituri();
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    int actual = Integer.valueOf(sp.getString("buget", ""));
                    int buget = 0;
                    String dataitem;
                    LocalDate inputDate;
                    LocalDate selectat = new LocalDate(year, month + 1, dayOfMonth);
                    LocalDate azi = new LocalDate();
                    if (cursor.moveToFirst()) {
                        do {
                            String valoare = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3));
                            String tip = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4));
                            dataitem = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_5));
                            if (tip.equals("Z")) {
                                //Zilnic
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul zile trecute si adaugare la buget
                                    int days = Days.daysBetween(inputDate, selectat).getDays();
                                    if (selectat.isAfter(inputDate))
                                        buget += Integer.valueOf(valoare) * days;
                                    else
                                        buget -= Integer.valueOf(valoare) * days;
                                }
                            } else if (tip.equals("S")) {
                                //Saptamanal
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul saptamani trecute si adaugare la buget
                                    int weeks = Weeks.weeksBetween(inputDate, selectat).getWeeks();
                                    if (selectat.isAfter(inputDate))
                                        buget += Integer.valueOf(valoare) * weeks;
                                    else
                                        buget -= Integer.valueOf(valoare) * weeks;
                                }
                            } else {
                                //Lunar
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul saptamani trecute si adaugare la buget
                                    int months = Months.monthsBetween(inputDate, selectat).getMonths();
                                    if (selectat.isAfter(inputDate))
                                        buget += months * Integer.valueOf(valoare);
                                    else
                                        buget -= months * Integer.valueOf(valoare);
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    cursor = myDb.cautarecosturi();
                    if (cursor.moveToFirst()) {
                        do {
                            String nume = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2));
                            String valoare = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3));
                            String tip = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4));
                            dataitem = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_5));
                            if (tip.equals("Z")) {
                                //Zilnic
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul zile trecute si adaugare la buget
                                    int days = Days.daysBetween(inputDate, selectat).getDays();
                                    if (selectat.isAfter(inputDate))
                                        buget -= Integer.valueOf(valoare) * days;
                                    else
                                        buget += Integer.valueOf(valoare) * days;
                                }
                            } else if (tip.equals("S")) {
                                //Saptamanal
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul saptamani trecute si adaugare la buget
                                    int weeks = Weeks.weeksBetween(inputDate, selectat).getWeeks();
                                    if (selectat.isAfter(inputDate))
                                        buget -= Integer.valueOf(valoare) * weeks;
                                    else
                                        buget += Integer.valueOf(valoare) * weeks;
                                }
                            } else {
                                //Lunar
                                inputDate = LocalDate.parse(dataitem,
                                        DateTimeFormat.forPattern("dd/MM/yyyy"));
                                //verifica daca itemul este inainte de azi pentru a putea fi prelucrata
                                if (azi.isAfter(inputDate)) {
                                    //calcul saptamani trecute si adaugare la buget
                                    int months = Months.monthsBetween(inputDate, selectat).getMonths();
                                    if (selectat.isAfter(inputDate))
                                        buget -= months * Integer.valueOf(valoare);
                                    else
                                        buget += months * Integer.valueOf(valoare);
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                    cursor.close();

                    //creeare alertdialog de afisat
                    String bugetdata = String.valueOf(actual + buget);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    //creeare listview cu istoric achizitii
                    myDb = new DatabaseHelper(getActivity());
                    //
                    if (selectat.isEqual(azi)||selectat.isBefore(azi)) {
                        Cursor cursorlista = myDb.cautarecumparat(selectat.toString("dd/MM/yyyy"));
                        if (cursorlista.moveToFirst()) {
                            ListView listView = new ListView(getActivity());
                            // Coloanele de adaugat
                            String columns[] = new String[]{
                                    DatabaseHelper.COL_2,
                                    DatabaseHelper.COL_3,
                                    DatabaseHelper.COL_44,
                                    DatabaseHelper.COL_5
                            };
                            int toviewids[] = new int[]{R.id.nume_istoric, R.id.valoare_istoric, R.id.bucati_istoric, R.id.data_istoric};
                            SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(getActivity().getBaseContext(), R.layout.item_istoric, cursorlista, columns, toviewids, 0);
                            //
                            listView.setAdapter(dataAdapter);
                            builder.setView(listView);
                        } else {
                            TextView empty = new TextView(getActivity());
                            empty.setGravity(Gravity.CENTER);
                            empty.setText("Nicio achiziţie în această dată");
                            empty.setTextSize(21);
                            builder.setView(empty);
                        }
                        builder.setMessage("Istoric achiziţii");
                    }
                    if (selectat.isBefore(azi))
                        builder.setTitle("Bugetul în data de " + selectat.toString("dd/MM/yyyy") + " a fost " + bugetdata);
                    else if (selectat.isAfter(azi))
                        builder.setTitle("Bugetul în data de " + selectat.toString("dd/MM/yyyy") + " va fi " + bugetdata);
                    //calcul buget la data aleasa
                    builder.setPositiveButton("Ok", null);
                    AlertDialog asd = builder.create();
                    asd.show();
                }
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // TODO Auto-generated method stub
        //salvare data de baze pentru observare
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//" + "com.rachierudragos.buget"
                        + "//databases//" + "venituri.db";
                String backupDBPath = "/buget/venituri.db";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                Toast.makeText(getActivity().getBaseContext(), backupDB.toString(),
                        Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {

            Toast.makeText(getActivity().getBaseContext(), e.toString(), Toast.LENGTH_LONG)
                    .show();

        }
    }
}
