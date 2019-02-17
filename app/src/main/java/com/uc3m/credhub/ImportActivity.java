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

import org.ksoap2.HeaderProperty;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        boolean isinserted = db.insertData("No description", passwordList.get(position).getUsername(), passwordList.get(position).getPassword());
        if (isinserted == true) {
            Toast.makeText(this, "Data inserted in db", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Data not inserted", Toast.LENGTH_LONG).show();
    }
}


