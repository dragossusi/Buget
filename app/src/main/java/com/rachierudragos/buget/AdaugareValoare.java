package com.rachierudragos.buget;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdaugareValoare extends AppCompatActivity {

    DatabaseHelper myDb;
    EditText datePicker;
    EditText bucPicker;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adaugare_valoare);
        myDb = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button btn = (Button) findViewById(R.id.btn_add);

        //nu are fab
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.hide();
        Intent intent = getIntent();
        final int op = intent.getIntExtra("op", 0);
        //tip
        final Spinner dropdown = (Spinner) findViewById(R.id.spinner);
        datePicker = (EditText) findViewById(R.id.data);
        if (op == R.id.nav_venit || op == R.id.nav_cost) {
            String[] items = new String[]{"Lunar", "Săptămânal", "Zilnic"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_dropdown_item, items);
            dropdown.setAdapter(adapter);
            dropdown.setVisibility(View.VISIBLE);
            datePicker.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //data

            calendar = Calendar.getInstance();

            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };
            datePicker.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new DatePickerDialog(AdaugareValoare.this, date, calendar
                            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
        } else {
            //change bottom alignment
            bucPicker = (EditText) findViewById(R.id.txt_input_bucati);
            bucPicker.setVisibility(View.VISIBLE);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText et1 = (EditText) findViewById(R.id.txt_input_nume);
                EditText et2 = (EditText) findViewById(R.id.txt_input_valoare);
                String nume = et1.getText().toString().replaceAll("\\s+", " ").trim();
                String valoare = et2.getText().toString();
                String data;
                if (op != R.id.nav_wish) data = datePicker.getText().toString();
                else data = new LocalDate(System.currentTimeMillis()).toString("dd/MM/yyyy");
                if (nume.matches("") || valoare.matches("") || data.matches(""))
                    Toast.makeText(getApplicationContext(), "Campuri necompletate", Toast.LENGTH_LONG).show();
                else if (eBunaData(data) == false)
                    Toast.makeText(getApplicationContext(), "Data nu este corectă", Toast.LENGTH_LONG).show();
                else {
                    boolean isInserted = false;
                    if (op == R.id.nav_venit) {
                        String tip = String.valueOf(dropdown.getSelectedItem().toString().charAt(0));
                        Log.i("MyActivity", data);
                        isInserted = myDb.insertDataV(nume, valoare, tip, data);
                        if (isInserted == false) {
                            int val = Integer.valueOf(myDb.getval(nume, 1));
                            val += Integer.valueOf(valoare);
                            myDb.updatedetails(nume, String.valueOf(val), tip, 1);
                        }
                    } else if (op == R.id.nav_cost) {
                        String tip = String.valueOf(dropdown.getSelectedItem().toString().charAt(0));
                        Log.i("MyActivity", data);
                        isInserted = myDb.insertDataC(nume, valoare, tip, data);
                        if (isInserted == false) {
                            int val = Integer.valueOf(myDb.getval(nume, 2));
                            val += Integer.valueOf(valoare);
                            myDb.updatedetails(nume, String.valueOf(val), tip, 2);

                        }
                    } else if (op == R.id.nav_wish) {
                        Log.i("MyActivity", data);
                        String bucati = bucPicker.getText().toString();
                        isInserted = myDb.insertDataW(nume, valoare, bucati, data);
                    }

                    if (isInserted == true) {
                        Toast.makeText(getApplicationContext(), "Date inserate", Toast.LENGTH_LONG).show();
                    } else {
                            Toast.makeText(getApplicationContext(), "Datele au fost modificate", Toast.LENGTH_LONG).show();
                    }
                    finish();
                }
            }
        });

    }

    public static boolean eBunaData(String value) {
        Date date = null;
        try {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }

    private void updateLabel() {

        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        datePicker.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
