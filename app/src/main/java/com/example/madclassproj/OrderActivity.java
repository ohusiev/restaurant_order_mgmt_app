package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OrderActivity extends AppCompatActivity {
    public static Activity prod;

    public Context ct;
    public RecyclerView mRecyclerView;
    private SQLiteDatabase myDB;
    private LinkedList<Product> ProductList;
    TextView tableNum;
    TextView total;
    ProductListAdapter mAdapter;

    ImageButton btn_send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ct = this;
        prod = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.order_recycler);
        ProductList = new LinkedList<Product>();
        myDB = this.openOrCreateDatabase("CoffeeDB", MODE_PRIVATE, null);
        total = (TextView) findViewById(R.id.total);
        tableNum = (TextView) findViewById(R.id.tableNum);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        final Bundle bundle = getIntent().getExtras();
        tableNum.setText(bundle.getString("table"));
        final MainActivity mainActivity = new MainActivity();

        Cursor c = myDB.rawQuery("SELECT * FROM products", null);
        int Column1 = c.getColumnIndex("product_id");
        int Column2 = c.getColumnIndex("product_title");
        int Column3 = c.getColumnIndex("product_price");
        // Check if our result was valid.
        c.moveToFirst();
        if (c != null) {
            // Loop through all Results
            do {
                int product_id = c.getInt(Column1);
                String product_title = c.getString(Column2);
                float product_price = Float.parseFloat(c.getString(Column3));
                int quantity = 0;
                ProductList.add(
                        new Product(product_id, product_title, product_price, quantity)
                );
            } while (c.moveToNext());
        }
        mAdapter= new ProductListAdapter(this, ProductList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tid = bundle.getString("table");
                String urlOrder = "http://mad.mywork.gr/send_order.php?t=" + mainActivity.getTocken() + "&tid=" + tid;
                boolean hasItems = false;
                String oc = "&oc=";
                for (Product product : mAdapter.mProductList) {
                    int id = product.getId();
                    int upd_qty = product.getQuantity();
                    if (upd_qty > 0) {
                        hasItems = true;
                        oc += String.valueOf(id) + "," + String.valueOf(upd_qty) + ";";
                    }
                }
                if (hasItems) {
                    oc = oc.substring(0, oc.length() - 1);
                    urlOrder += oc;
                } else {
                    Toast.makeText(getApplicationContext(), "There are no products in the order", Toast.LENGTH_SHORT).show();
                }
                System.out.println(urlOrder); //for testing

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(urlOrder)
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            final String myResponse = response.body().string();
                            OrderActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                                .parse(new InputSource(new StringReader(myResponse.toString())));
                                        NodeList errNodes = doc.getElementsByTagName("response");
                                        if (errNodes.getLength() > 0) {
                                            Element err = (Element) errNodes.item(0);
                                            String status = err.getElementsByTagName("status").item(0).getTextContent();
                                            String msg = err.getElementsByTagName("msg").item(0).getTextContent();
                                            System.out.println(msg);
                                            if (status.equals("4-FAIL")) {
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                            } else if (status.equals("4-OK")) {
                                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(OrderActivity.this, TablesActivity.class));
                                                myDB.execSQL("UPDATE tableStatus SET " +
                                                        "table_status = 1" +
                                                        "WHERE table_id = " + tid
                                                );
                                            }
                                        }
                                    } catch (Exception e) {
                                        System.out.println((e));
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
    }
    }