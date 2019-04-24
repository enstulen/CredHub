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

    /**
     * Set up floatingActionbutton, databasehelper, recyclerview
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Databasehelper
        db = DatabaseHelper.getInstance(this);

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

    }

    /**
     * Clicking an item will go to the DetailActivity. Clicking the delete button will delete the entry from the DB.
     *
     * @param view
     * @param position
     */
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

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Gets the data from the database and updates the passwordList and notifies the adapter.
     */
    public void getDataFromDB() {
        passwordList.clear();
        Cursor res = db.getAllData();
        if (res == null || res.getCount() == 0) {
            Toast.makeText(this, "No data in DB", Toast.LENGTH_SHORT).show();
        } else {
            while (res.moveToNext()) {
                PasswordEntity entity = new PasswordEntity(res.getString(0), res.getString(1), res.getString(2), res.getString(3));
                passwordList.add(entity);
                adapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * Handle action bar item clicks. Import will go to ImportActivity. Refresh will refresh the list.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_import) {
            startActivity(new Intent(getBaseContext(), ImportActivity.class));
            return true;
        } else if (id == R.id.action_refresh) {
            getDataFromDB();
        } else if (id == R.id.action_logout) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}
