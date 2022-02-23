package com.example.todowithrecycleview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.todowithrecycleview.Adapter.MyAdapter;
import com.example.todowithrecycleview.Adapter.MyCustomRecycleAdapter;
import com.example.todowithrecycleview.Model.PersonDetails;
import com.example.todowithrecycleview.databinding.ActivityMainBinding;
import com.example.todowithrecycleview.databinding.InsertDataBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickInterface {

    ActivityMainBinding binding;

    MyAdapter myAdapter;
    private Cursor cursor;

    private InsertDataBinding bindingInsert;
    final Calendar myCalendar= Calendar.getInstance();
    private int clickedPosition;

    Context context;
    private PersonDetails personDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;


        myAdapter = new MyAdapter(context);
        myAdapter.openDatabase();

        loadData();
        registerForContextMenu(binding.recycleView);


        LinearLayoutManager manager = new LinearLayoutManager(context);
        binding.recycleView.setLayoutManager(manager);

        MyCustomRecycleAdapter adapter = new MyCustomRecycleAdapter(context, getPersonList(),this);
        binding.recycleView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bindingInsert = InsertDataBinding.inflate(getLayoutInflater());
                Dialog dialog = new Dialog(context);
                dialog.setContentView(bindingInsert.getRoot());
                dialog.setCancelable(false);
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);

                        updateLabel();

                    }
                };
                bindingInsert.etDatepik.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        new DatePickerDialog(context,date,myCalendar.get(Calendar.YEAR),
                                myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });


                bindingInsert.cencle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                bindingInsert.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myAdapter.insertRecord(context,bindingInsert.etTitle.getText().toString(),
                                bindingInsert.Description.getText().toString(),bindingInsert.etDatepik
                                        .getText().toString());

                        dialog.dismiss();
                        loadData();

                    }

                });

                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);

            }

            private void updateLabel() {
                String myFormat = "EEE / MMM / d /yyyy";
                SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                bindingInsert.etDatepik.setText(dateFormat.format(myCalendar.getTime()));

            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.delete_decord,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.delete_Alldata:

                // TODO : Delete related task
                myAdapter.deleteAllRecords(context);
                loadData();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int position) {

      
        PersonDetails personDetails = (PersonDetails)parent.getItemAtPosition(position);
        Log.d("PersonDetails", ""+personDetails);

        Intent intent = new Intent(MainActivity.this,Show_data.class);

        intent.putExtra("title",personDetails.getTitle());
        intent.putExtra("description",personDetails.getDescription());
        intent.putExtra("datepicker",personDetails.getDatepicker());
        startActivity(intent);
        finish();
    }

    private List<PersonDetails> getPersonList(){

        cursor = myAdapter.getAllRecords();
        List<PersonDetails> finalList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                String serNo = cursor.getString(0);// serialNo
                String title = cursor.getString(1);// photo
                String description = cursor.getString(2);// fname
                String datepicker = cursor.getString(3);// lname
                PersonDetails personDetails = new PersonDetails(title, description, datepicker);
                finalList.add(personDetails);
            } while (cursor.moveToNext());
        }
        return finalList;
    }

    private void loadData() {

        if (getPersonList().size() > 0) {
            binding.recycleView.setVisibility(View.VISIBLE);
            MyCustomRecycleAdapter myCustomListAdapter = new MyCustomRecycleAdapter(context, getPersonList(),this);
            binding.recycleView.setAdapter(myCustomListAdapter);
        }else {
            Toast.makeText(context, "No Data found.", Toast.LENGTH_SHORT).show();
            binding.recycleView.setVisibility(View.GONE);

        }

    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu_option, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_one:

                // TODO : DElete record....
                cursor.moveToPosition(clickedPosition);
                String colRow = cursor.getString(0);
                myAdapter.deleteRecord(colRow, context);
                loadData();
                break;

            case R.id.update:

                // TODO : Update record......
                InsertDataBinding updateProfileBinding = InsertDataBinding.inflate(getLayoutInflater());
                Dialog dialog = new Dialog(context);
                dialog.setContentView(updateProfileBinding.getRoot());
                dialog.setCancelable(false);
                updateProfileBinding.save.setText("Update");

                updateProfileBinding.cencle.setText("Updation cancle");

                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

                cursor.moveToPosition(clickedPosition);

                String rowId = cursor.getString(0);
                String title = cursor.getString(1);// title
                String description = cursor.getString(2);// description
                String datepicker = cursor.getString(3);// datepicker

                updateProfileBinding.etTitle.setText(title);
                updateProfileBinding.Description.setText(description);
                updateProfileBinding.etDatepik.setText(datepicker);
                dialog.show();

                //TODO Data Saved.......
                updateProfileBinding.save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        myAdapter.updateRecord(context, rowId, updateProfileBinding.etTitle.getText().toString(),
                                updateProfileBinding.Description.getText().toString(),updateProfileBinding.etDatepik.getText().toString());
                        dialog.dismiss();
                        loadData();

                    }
                });

                updateProfileBinding.cencle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onLongItemClick(int position) {

        clickedPosition = position;
        return false;

    }

    //TODO Backpressed Method.......

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Exit ToDo");
        builder.setMessage("Sure you want to exit");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();

    }

}