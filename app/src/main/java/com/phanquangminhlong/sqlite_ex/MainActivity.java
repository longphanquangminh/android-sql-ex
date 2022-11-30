package com.phanquangminhlong.sqlite_ex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.phanquangminhlong.models.Product;
import com.phanquangminhlong.sqlite_ex.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    ArrayList<Product> products;
    ArrayAdapter<Product> adapter;

    public static SQLiteDatabase db = null;

    Product selectedProduct = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        copyDB();
        // loadData();

        addEvents();
        registerForContextMenu(binding.lvProduct);
    }

    private void addEvents() {
        binding.lvProduct.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedProduct = adapter.getItem(i);

                return false;
            }
        });
    }

    private void loadData() {
        products = new ArrayList<>();
        // Init sample data
        // products.add(new Product(1, "Tiger", 19000));
        // products.add(new Product(2, "Sapporo", 21000));

        // Loading data from DB
        db = openOrCreateDatabase(Utils.DB_NAME, MODE_PRIVATE, null);

        //Method 1
        // Cursor cursor = db.rawQuery("SELECT * FROM " + Utils.TBL_NAME, null);
        // dòng dưới là có điều kiện cụ thể của method 1
        // Cursor cursor = db.rawQuery("SELECT * FROM " + Utils.TBL_NAME + " WHERE ProductId=? OR ProductId=?", new String[]{"1", "3"});
        // Cursor cursor = db.rawQuery("SELECT * FROM " + Utils.TBL_NAME + " WHERE ProductId>?", new String[]{"2"});

        //Method 2
        // Cursor cursor = db.query(Utils.TBL_NAME, null, null, null, null, null, null);
        // dòng dưới là có điều kiện cụ thể của method 2
        Cursor cursor = db.query(Utils.TBL_NAME, null, "ProductId>?", new String[]{"2"}, null, null, null);


//        int id;
//        String name;
//        double price;
//        while (cursor.moveToNext()){
//            id = cursor.getInt(0);
//            name = cursor.getString(1);
//            price = cursor.getDouble(2);
//
//            products.add(new Product(id, name, price));
//        }

        while (cursor.moveToNext()){
            products.add(new Product(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2)));
        }

        adapter = new ArrayAdapter<Product>(MainActivity.this, android.R.layout.simple_list_item_1, products);

        binding.lvProduct.setAdapter(adapter);
    }

    private void copyDB() {
        File dbPath = getDatabasePath(Utils.DB_NAME);
        if (!dbPath.exists()) {
            // Thực hiện copy dữ liệu
            if (copyDBFromAssets()) {
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Fail!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean copyDBFromAssets() {
        String dbPath = getApplicationInfo().dataDir + Utils.DB_PATH_SUFFIX +
                Utils.DB_NAME;
        try {
            InputStream inputStream = getAssets().open(Utils.DB_NAME);
            File f = new File(getApplicationInfo().dataDir + Utils.DB_PATH_SUFFIX);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(dbPath);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }

    //=====MENU=====
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mn_Add){
            Intent intent = new Intent(MainActivity.this, AddActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mn_Edit){
            Intent intent = new Intent(MainActivity.this, EditActivity.class);

            //Attach data
            if(selectedProduct != null){
                intent.putExtra("productInf", selectedProduct);
                startActivity(intent);
            }
        }
        if(item.getItemId() == R.id.mn_Delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Xác nhận xóa!");
            builder.setMessage("Bạn có chắc muốn xóa sp: " + selectedProduct.getProductName() + "?");
            builder.setIcon(android.R.drawable.ic_delete);

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int deletedRows = db.delete(Utils.TBL_NAME, Utils.COL_ID + "=?", new String[]{String.valueOf(selectedProduct.getProductId())});
                    if(deletedRows > 0)
                        Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();

                    else
                        Toast.makeText(MainActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                    loadData();
                }
            });

            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            builder.create().show();
        }
        return super.onContextItemSelected(item);
    }
}