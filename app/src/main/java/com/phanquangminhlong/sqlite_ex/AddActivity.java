package com.phanquangminhlong.sqlite_ex;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.phanquangminhlong.sqlite_ex.databinding.ActivityAddBinding;

public class AddActivity extends AppCompatActivity {

    ActivityAddBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_add);

        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addEvents();
    }

    private void addEvents() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Insert data into Db
                String name = binding.edtName.getText().toString();
                double price = Double.parseDouble(binding.edtPrice.getText().toString());

                ContentValues values = new ContentValues();
                values.put(Utils.COL_NAME, name);
                values.put(Utils.COL_PRICE, price);

                long numbOfRows = MainActivity.db.insert("Product", null, values);

                if(numbOfRows >0) {
                    Toast.makeText(AddActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                }
                finish();
            }
        });
    }
}