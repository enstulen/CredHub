package com.uc3m.credhub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import java.util.ArrayList;


public class ImportActivity extends AppCompatActivity implements MainRecyclerViewAdapter.ItemClickListener {

    MainRecyclerViewAdapter adapter;
    ArrayList<PasswordEntity> passwordList = new ArrayList<>();
    DatabaseHelper db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_view);
        setTitle("Import");

        SOAPSingleton soap = SOAPSingleton.getInstance();
        soap.context = this;
        passwordList = soap.importData();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.import_recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter(this, passwordList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        db = new DatabaseHelper(this);

    }


    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId()){
            case (R.id.row_button):
             Toast.makeText(this, "Can't delete from server", Toast.LENGTH_LONG).show();
        }

        boolean isinserted = db.insertData("No description", passwordList.get(position).getUsername(), passwordList.get(position).getPassword());
        if (isinserted == true) {
            Toast.makeText(this, "Data inserted in db", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
    }
}


