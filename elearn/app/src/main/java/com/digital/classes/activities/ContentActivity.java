package com.digital.classes.activities;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.digital.classes.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ContentActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        String name = getIntent().getExtras().getString("name");
        String fileName = getIntent().getExtras().getString("file_name");
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView txtView = (TextView) findViewById(R.id.text);
        txtView.setMovementMethod(new ScrollingMovementMethod());

        InputStream inputStream = null;
        try {
            assert fileName != null;
            inputStream = getAssets().open(fileName);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            int i;
            try {
                i = inputStream.read();
                while (i != -1) {
                    byteArrayOutputStream.write(i);
                    i = inputStream.read();
                }
                inputStream.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            txtView.setText(byteArrayOutputStream.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

