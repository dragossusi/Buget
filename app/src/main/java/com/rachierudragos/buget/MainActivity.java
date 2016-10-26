package com.rachierudragos.buget;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rachierudragos.fregmente.CalendarFragment;
import com.rachierudragos.fregmente.CosturiFragment;
import com.rachierudragos.fregmente.SetariFragment;
import com.rachierudragos.fregmente.TotalFragment;
import com.rachierudragos.fregmente.VenituriFragment;
import com.rachierudragos.fregmente.WishlistFragment;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Weeks;
import org.joda.time.format.DateTimeFormat;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String DEFAULT = "N/A";
    public static final int SELECTED_PICTURE = 1;
    private String name, poza, sbuget;
    private int venitL = 0, venitS = 0, venitZ = 0;
    private int costL = 0, costS = 0, costZ = 0;
    private int id, buget;
    private ImageView iv;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDb = new DatabaseHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        id = R.id.nav_venit;
        //creeare buton interactiv
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add(id);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        name = sp.getString("name", DEFAULT);
        poza = sp.getString("image_data", DEFAULT);
        sbuget = sp.getString("buget", DEFAULT);
        if (!sbuget.equals(DEFAULT)) buget = Integer.valueOf(sbuget);
        View header = navigationView.getHeaderView(0);
        TextView tv = (TextView) header.findViewById(R.id.txt_nume_persoana);
        tv.setText(name);
        if (name.equals(DEFAULT) || poza.equals(DEFAULT) || sbuget.equals(DEFAULT)) {
            ///aici sa faci sa vezi daca ai setat numele inainte, daca nu sa te forteze s-o faci
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_relative, new SetariFragment()).commit();
        } else {
            getSupportActionBar().setTitle("Lei: " + buget);
            tv.setText(name);
            byte b[] = Base64.decode(poza, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            Drawable d = new BitmapDrawable(bitmap);
            iv = (ImageView) header.findViewById(R.id.imageView);
            iv.setImageDrawable(d);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_relative, new VenituriFragment()).commit();
        }
        calcul_buget();
    }

    private void add(int iid) {
        Intent intent = new Intent(MainActivity.this, AdaugareValoare.class);
        intent.putExtra("op", iid);
        startActivity(intent);
        FragmentManager fm = getFragmentManager();
        switch (iid) {
            case R.id.nav_venit:
                fm.beginTransaction().replace(R.id.content_relative, new VenituriFragment()).commit();
                break;
            case R.id.nav_cost:
                fm.beginTransaction().replace(R.id.content_relative, new CosturiFragment()).commit();
                break;
            case R.id.nav_wish:
                fm.beginTransaction().replace(R.id.content_relative, new WishlistFragment()).commit();
        }
        calcul_buget();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_relative, new SetariFragment()).commit();
            return true;
        } else if (id == R.id.action_buget) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            //nume
            final EditText nume = new EditText(this);
            nume.setVisibility(View.GONE);
            nume.setHint("nume");

            //valoare
            final EditText valoare = new EditText(this);
            valoare.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            valoare.setHint("valoare");

            //bucati
            final EditText bucati = new EditText(this);
            bucati.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
            bucati.setVisibility(View.GONE);
            bucati.setHint("bucati");

            //checkbox
            final CheckBox input2 = new CheckBox(this);
            input2.setText("Cheltuit");
            input2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == false) {
                        nume.setVisibility(View.GONE);
                        bucati.setVisibility(View.GONE);
                    } else {
                        nume.setVisibility(View.VISIBLE);
                        bucati.setVisibility(View.VISIBLE);
                    }
                }
            });

            //creeare linear layout
            LinearLayout lay = new LinearLayout(this);
            lay.setOrientation(LinearLayout.VERTICAL);
            lay.addView(nume);
            lay.addView(valoare);
            lay.addView(bucati);
            lay.addView(input2);
            builder.setView(lay);
            builder.setMessage("Valoare nouă:")
                    .setView(lay)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            SharedPreferences.Editor edit = sp.edit();
                            if (input2.isChecked()) {
                                if(valoare.getText().toString().equals("")
                                        || nume.getText().toString().equals("")
                                        || bucati.getText().toString().equals("")){
                                    Toast.makeText(getApplicationContext(), "Nicio valoare introdusă în unele câmpuri", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    LocalDate azi = new LocalDate();
                                    myDb.insertDataCump(nume.getText().toString(), valoare.getText().toString(), bucati.getText().toString(), azi.toString("dd/MM/yyyy"));
                                    buget -= Integer.valueOf(valoare.getText().toString()) * Integer.valueOf(bucati.getText().toString());
                                    edit.putString("buget", String.valueOf(buget)).commit();
                                }
                            } else {
                                if(valoare.getText().toString().equals("")) {
                                    Toast.makeText(getApplicationContext(), "Nicio valoare introdusă", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    buget += Integer.valueOf(valoare.getText().toString());
                                    edit.putString("buget", String.valueOf(buget)).commit();
                                }
                            }
                            getSupportActionBar().setTitle("Lei: " + buget);
                        }
                    }).create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        FragmentManager fm = getFragmentManager();

        id = item.getItemId();

        if (id == R.id.nav_venit) {
            fm.beginTransaction().replace(R.id.content_relative, new VenituriFragment()).commit();
        } else if (id == R.id.nav_cost) {
            fm.beginTransaction().replace(R.id.content_relative, new CosturiFragment()).commit();
        } else if (id == R.id.nav_wish) {
            fm.beginTransaction().replace(R.id.content_relative, new WishlistFragment()).commit();
        } else if (id == R.id.nav_total) {
            fm.beginTransaction().replace(R.id.content_relative, new TotalFragment()).commit();
        } else if (id == R.id.nav_calendar) {
            fm.beginTransaction().replace(R.id.content_relative, new CalendarFragment()).commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setarenume(View view) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();

        //Adaugare nume si modificare drawer
        EditText g = (EditText) findViewById(R.id.etxt_nume);
        if (!g.getText().toString().equals(DEFAULT)) {
            editor.putString("name", g.getText().toString().replaceAll("\\s+", " ").trim());
            editor.commit();
            name = g.getText().toString().replaceAll("\\s+", " ").trim();
            TextView nnn = (TextView) findViewById(R.id.txt_nume_persoana);
            nnn.setText(name);
        }

        //Adaugare buget si modificare actionbar
        EditText et = (EditText) findViewById(R.id.buget_edit);
        if (!et.getText().toString().equals(DEFAULT)) {
            sbuget = et.getText().toString();
            buget = Integer.valueOf(et.getText().toString());
            editor.putString("buget", sbuget);
            editor.commit();
            getSupportActionBar().setTitle("Lei: " + buget);
        }
        if (!(name.equals(DEFAULT) || poza.equals(DEFAULT) || sbuget.equals(DEFAULT))) {
            getSupportActionBar().setTitle("Lei: " + buget);
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_relative, new VenituriFragment()).commit();
        }
    }

    public void phchange(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        poza = sp.getString("image_data", DEFAULT);
        if (!(name.equals(DEFAULT) || poza.equals(DEFAULT) || sbuget.equals(DEFAULT))) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.content_relative, new VenituriFragment()).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String projection[] = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap imaginea_selectata = BitmapFactory.decodeFile(filePath);
                    Bitmap croppedImage;
                    if (imaginea_selectata.getWidth() >= imaginea_selectata.getHeight()) {

                        croppedImage = Bitmap.createBitmap(
                                imaginea_selectata,
                                imaginea_selectata.getWidth() / 2 - imaginea_selectata.getHeight() / 2,
                                0,
                                imaginea_selectata.getHeight(),
                                imaginea_selectata.getHeight()
                        );

                    } else {

                        croppedImage = Bitmap.createBitmap(
                                imaginea_selectata,
                                0,
                                imaginea_selectata.getHeight() / 2 - imaginea_selectata.getWidth() / 2,
                                imaginea_selectata.getWidth(),
                                imaginea_selectata.getWidth()
                        );
                    }
                    Drawable d = new BitmapDrawable(croppedImage);
                    iv = (ImageView) findViewById(R.id.imageView);
                    iv.setImageDrawable(d);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    croppedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte b[] = baos.toByteArray();

                    String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("image_data", encodedImage);
                    editor.commit();
                }
                break;
        }
    }

    //calcul buget in functie de timp

    public void calcul_buget() {
        Cursor cursor = myDb.cautarevenituri();
        String dataitem;
        LocalDate inputDate;
        if (cursor.moveToFirst()) {
            do {
                String nume = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_2));
                String valoare = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_3));
                String tip = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_4));
                dataitem = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_5));
                if (tip.equals("Z")) {
                    //Zilnic
                    venitZ += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));

                    //calcul zile trecute si adaugare la buget

                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul zile
                        int days = Days.daysBetween(inputDate, azi).getDays();
                        buget += Integer.valueOf(valoare) * days;

                        //schimbare data item cu data actuala
                        dataitem = azi.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 1);
                    }
                } else if (tip.equals("S")) {
                    //Saptamanal
                    venitS += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));

                    //calcul saptamani trecute si adaugare la buget

                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul saptamani
                        int weeks = Weeks.weeksBetween(inputDate, azi).getWeeks();
                        buget += Integer.valueOf(valoare) * weeks;

                        //adaugare saptamani la data
                        inputDate = inputDate.plusWeeks(weeks);

                        //schimbare data item cu data noua
                        dataitem = inputDate.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 1);
                    }
                } else {
                    //Lunar
                    venitL += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));


                    //calcul saptamani trecute si adaugare la buget

                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul luni
                        int months = Months.monthsBetween(inputDate, azi).getMonths();
                        buget += months * Integer.valueOf(valoare);

                        //adaugare luni la data

                        inputDate = inputDate.plusMonths(months);

                        //schimbare data item cu data noua
                        dataitem = inputDate.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 1);
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
                    costZ += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));

                    //calcul zile trecute si adaugare la buget
                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul zile
                        int days = Days.daysBetween(inputDate, azi).getDays();
                        buget -= Integer.valueOf(valoare) * days;

                        //schimbare data item cu data actuala
                        dataitem = azi.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 2);
                    }
                } else if (tip.equals("S")) {
                    //Saptamanal
                    costS += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));

                    //calcul saptamani trecute si adaugare la buget

                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul saptamani
                        int weeks = Weeks.weeksBetween(inputDate, azi).getWeeks();
                        buget -= Integer.valueOf(valoare) * weeks;

                        //adaugare saptamani la data
                        inputDate = inputDate.plusWeeks(weeks);

                        //schimbare data item cu data noua
                        dataitem = inputDate.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 2);
                    }
                } else {
                    //Lunar
                    costL += Integer.valueOf(valoare);
                    inputDate = LocalDate.parse(dataitem,
                            DateTimeFormat.forPattern("dd/MM/yyyy"));

                    //calcul saptamani trecute si adaugare la buget

                    LocalDate azi = new LocalDate(System.currentTimeMillis());
                    if (azi.isAfter(inputDate)) {
                        //calcul luni
                        int months = Months.monthsBetween(inputDate, azi).getMonths();
                        buget -= months * Integer.valueOf(valoare);

                        //adaugare luni la data
                        inputDate = inputDate.plusMonths(months);

                        //schimbare data item cu data noua
                        dataitem = inputDate.toString("dd/MM/yyyy");
                        myDb.updatedata(nume, dataitem, 2);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        //salvare buget
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("buget", String.valueOf(buget))
                .putInt("venitz", venitZ)
                .putInt("venits", venitS)
                .putInt("venitl", venitL)
                .putInt("costz", costZ)
                .putInt("costs", costS)
                .putInt("costl", costL)
                .commit();
        getSupportActionBar().setTitle("Lei: " + buget);

    }
}
