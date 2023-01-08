package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class PaymentActivity extends AppCompatActivity {
    TextView tableNum;
    public Context ct;
    private SQLiteDatabase myDB;
    public RecyclerView mRecyclerView;
    private LinkedList<Product> ProductList;
    TextView tv_cost;
    TextView tv_paid;
    TextView tv_balance;
    EditText et_am;
    ImageButton send_order;
    String balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        tv_cost =(TextView) findViewById(R.id.tv_cost);
        tv_paid =(TextView) findViewById(R.id.tv_paid);
        tv_balance =(TextView) findViewById(R.id.tv_balance);
        tableNum = (TextView) findViewById(R.id.tableNum);
        et_am = (EditText) findViewById(R.id.et_am);
        send_order = (ImageButton) findViewById(R.id.send_order);
        mRecyclerView = (RecyclerView) findViewById(R.id.payment_recycler);
        ProductList = new LinkedList<Product>();
        myDB = this.openOrCreateDatabase("CoffeeDB", MODE_PRIVATE, null);
        final MainActivity mainActivity =new MainActivity();


        final Bundle bundle = getIntent().getExtras();
        tableNum.setText(bundle.getString("table"));
        final String tid = bundle.getString("table");

        OkHttpClient client = new OkHttpClient();
        String url = "http://mad.mywork.gr/get_order.php?t="+mainActivity.getTocken()+"&tid="+ tid;

        Request request = new Request.Builder()
                .url(url)
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

                    PaymentActivity.this.runOnUiThread(new Runnable() {
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
                                    if (status.equals("0-FAIL")) {
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(PaymentActivity.this, LoginActivity.class));
                                    }else if(status.equals("5-FAIL")){
                                         Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                                         startActivity(new Intent(PaymentActivity.this, TablesActivity.class));
                                    } else if(status.equals("5-OK")){
                                        //Toast.makeText(getApplicationContext(), "Enter AMOUNT of payment to see a list of order", Toast.LENGTH_LONG).show();
                                        String cost = err.getElementsByTagName("cost").item(0).getTextContent();
                                        String payment = err.getElementsByTagName("payment").item(0).getTextContent();
                                        balance = err.getElementsByTagName("balance").item(0).getTextContent();
                                        tv_cost.setText(cost);
                                        tv_paid.setText(payment);
                                        tv_balance.setText(balance);
                                    }
                                }
                                NodeList products = doc.getElementsByTagName("products");
                                if(products.getLength()>0){
                                    NodeList ids = doc.getElementsByTagName("id");
                                    Element err2=(Element)products.item(0);
                                   for (int p = 0; p<ids.getLength(); p++){
                                        int product_id1 = Integer.parseInt(err2.getElementsByTagName("id").item(p).getTextContent());
                                        String product_title1 = err2.getElementsByTagName("title").item(p).getTextContent();
                                        float product_price1 = Float.parseFloat(err2.getElementsByTagName("price").item(p).getTextContent());
                                        int quantity1 = Integer.parseInt(err2.getElementsByTagName("quantity").item(p).getTextContent());
                                            ProductList.add(new Product(product_id1, product_title1, product_price1, quantity1));
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
        ProductListAdapter mAdapter = new ProductListAdapter(this, ProductList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter.notifyDataSetChanged();

        send_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if( Float.parseFloat(et_am.getText().toString())<=0){
                    Toast.makeText(getApplicationContext(), "Wrong amount. Negative value", Toast.LENGTH_LONG).show();
               }else if(Float.parseFloat(et_am.getText().toString())>Float.parseFloat(balance)){
                   Toast.makeText(getApplicationContext(), "Wrong amount. Too much", Toast.LENGTH_LONG).show();
                }else if(Float.parseFloat(et_am.getText().toString())<=Float.parseFloat(balance)){
                    OkHttpClient client = new OkHttpClient();
                   String url = "http://mad.mywork.gr/send_payment.php?t="+"429720"+"&tid="+tid+"&a="+et_am.getText().toString();

                    Request request = new Request.Builder()
                            .url(url)
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

                                PaymentActivity.this.runOnUiThread(new Runnable() {
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
                                                String new_balance = err.getElementsByTagName("new_balance").item(0).getTextContent();
                                                System.out.println(msg);
                                                tv_balance.setText(new_balance);
                                                if (status.equals("6-FAIL")) {
                                                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                                }
                                                if(status.equals("6-OK")){
                                                    if (new_balance.equals("0")){
                                                        Toast.makeText(getApplicationContext(), "Order fully paid!", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(PaymentActivity.this, TablesActivity.class));
                                                        myDB.execSQL("UPDATE tableStatus SET " +
                                                                "table_status = 0" +
                                                                "WHERE table_id = " + tid
                                                        );
                                                    }else{
                                                        tv_paid.setText(Integer.parseInt(tv_cost.getText().toString())-Integer.parseInt(new_balance));
                                                        tv_balance.setText(new_balance);
                                                    }
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
            }
        });
    }
}