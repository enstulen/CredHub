package com.uc3m.credhub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainRecyclerViewAdapter.ItemClickListener {

    MainRecyclerViewAdapter adapter;
    ArrayList<PasswordEntity> passwordList = new ArrayList<>();

    DatabaseHelper db;


    @Override
    protected void onResume() {
        super.onResume();
        getDataFromDB();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Databasehelper
        db = new DatabaseHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.rvAnimals);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MainRecyclerViewAdapter(this, passwordList);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        //Divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        //Sharedprefs webservice_url
        SharedPreferences.Editor editor = getSharedPreferences("webservice_url", MODE_PRIVATE).edit();
        editor.putString("webservice_url", "http://10.0.2.2/SDM/WebRepo?wsdl");
        editor.apply();
    }

    @Override
    public void onItemClick(View view, int position) {

        switch (view.getId()) {

            case R.id.row_button:
                db.deleteData(passwordList.get(position).getID());
                passwordList.remove(position);
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "Deleted", Toast.LENGTH_LONG).show();
                break;

            default:
                Intent intent = new Intent(getBaseContext(), DetailActivity.class);
                intent.putExtra("description", passwordList.get(position).getDescription());
                intent.putExtra("username", passwordList.get(position).getUsername());
                intent.putExtra("password", passwordList.get(position).getPassword());
                startActivity(intent);
                break;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void getDataFromDB() {
        passwordList.clear();
        Cursor res = db.getAllData();
        if (res.getCount() == 0) {
            Toast.makeText(this, "No data in DB", Toast.LENGTH_SHORT).show();
        }

        while (res.moveToNext()) {
            PasswordEntity entity = new PasswordEntity(res.getString(0), res.getString(1), res.getString(2), res.getString(3));
            passwordList.add(entity);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_import) {
            startActivity(new Intent(getBaseContext(), ImportActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            getDataFromDB();
        }

        return super.onOptionsItemSelected(item);
    }
}
