package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TablesActivity extends AppCompatActivity {
    private Hashtable<Integer, Integer> tablesHash = new Hashtable<>();
    private ArrayList<Integer> table_id_arr=new ArrayList<>();
    public Hashtable<Integer, Integer> getTablesHash() {
        return tablesHash;
    }
    public ArrayList<Integer> getTable_id_arr() {
        return table_id_arr;
    }

    private SQLiteDatabase myDB;
    private ContentValues contentValues = new ContentValues();
    private ContentValues contentValuesProducts = new ContentValues();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tables);
        myDB = this.openOrCreateDatabase("CoffeeDB", MODE_PRIVATE, null);

        final Button btn_table1 = (Button) findViewById(R.id.btn_table1);
        final Button btn_table2 = (Button) findViewById(R.id.btn_table2);
        final Button btn_table3 = (Button) findViewById(R.id.btn_table3);
        final Button btn_table4 = (Button) findViewById(R.id.btn_table4);
        final Button btn_table5 = (Button) findViewById(R.id.btn_table5);
        final Button btn_table6 = (Button) findViewById(R.id.btn_table6);
        final Button btn_table7 = (Button) findViewById(R.id.btn_table7);
        final Button btn_table8 = (Button) findViewById(R.id.btn_table8);
        final Button btn_table9 = (Button) findViewById(R.id.btn_table9);

        OkHttpClient client = new OkHttpClient();
        MainActivity mainActivity =new MainActivity();

        String url = "http://mad.mywork.gr/get_coffee_data.php?t=" + mainActivity.getTocken();

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
                if (response.isSuccessful()){
                    final String myResponse = response.body().string();

                    TablesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //create_product_t();
                            //create_tables_t();
                            try {
                                Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                        .parse(new InputSource(new StringReader(myResponse.toString())));

                                NodeList errNodes = doc.getElementsByTagName("response");
                                if(errNodes.getLength()>0){
                                    Element err=(Element)errNodes.item(0);
                                    String status = err.getElementsByTagName("status").item(0).getTextContent();
                                    String msg  = err.getElementsByTagName("msg").item(0).getTextContent();
                                    System.out.println(msg);
                                    if (status.equals("0-FAIL")){
                                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                                        startActivity( new Intent(TablesActivity.this, LoginActivity.class));
                                    }
                                }
                                NodeList tables = doc.getElementsByTagName("tables");
                                if(tables.getLength()>0){
                                    myDB.execSQL("DELETE FROM " + "tableStatus");
                                    Element err1=(Element)tables.item(0);
                                    for (int i = 0; i<9; i++){
                                        int table_id1 = Integer.parseInt(err1.getElementsByTagName("id").item(i).getTextContent());
                                        int table_status1 = Integer.parseInt(err1.getElementsByTagName("status").item(i).getTextContent());
                                        contentValues.put("table_id",table_id1);
                                        contentValues.put("table_status",table_status1);
                                        myDB.insert("tableStatus", null, contentValues);
                                        //System.out.println("this is " + table_id + " status "  + table_status); //for checking
                                    }
                                    Cursor c = myDB.rawQuery(
                                            "SELECT * FROM tableStatus ", null);
                                    int Column1 = c.getColumnIndex("table_id");
                                    int Column2 = c.getColumnIndex("table_status");
                                    c.moveToFirst();
                                    if (c != null) {
                                        // Loop through all Results
                                        do {
                                            int table_id2 = c.getInt(Column1);
                                            int table_status2 = c.getInt(Column2);
                                            tablesHash.put(table_id2,table_status2);
                                            table_id_arr.add(table_id2);
                                        } while (c.moveToNext());
                                    }
                                    btn_table1.setText(table_id_arr.get(0).toString());
                                    btn_table2.setText(table_id_arr.get(1).toString());
                                    btn_table3.setText(table_id_arr.get(2).toString());
                                    btn_table4.setText(table_id_arr.get(3).toString());
                                    btn_table5.setText(table_id_arr.get(4).toString());
                                    btn_table6.setText(table_id_arr.get(5).toString());
                                    btn_table7.setText(table_id_arr.get(6).toString());
                                    btn_table8.setText(table_id_arr.get(7).toString());
                                    btn_table9.setText(table_id_arr.get(8).toString());
                                    if (tablesHash.get(table_id_arr.get(0))==1){
                                        btn_table1.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table1.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(1))==1){
                                        btn_table2.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table2.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(2))==1){
                                        btn_table3.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table3.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(3))==1){
                                        btn_table4.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table4.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(4))==1){
                                        btn_table5.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table5.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(5))==1){
                                        btn_table6.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table6.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(6))==1){
                                        btn_table7.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table7.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(7))==1){
                                        btn_table8.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table8.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                    if (tablesHash.get(table_id_arr.get(8))==1){
                                        btn_table9.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }else{
                                        btn_table9.setBackgroundColor(Color.parseColor("#4CAF50"));
                                    }
                                }
                                NodeList products = doc.getElementsByTagName("products");
                                if(products.getLength()>0){
                                    myDB.execSQL("DELETE FROM "+ "products");
                                    Element err2=(Element)products.item(0);
                                    for (int p = 0; p<16; p++){
                                        //DbActivity Db = new DbActivity();
                                        int product_id1 = Integer.parseInt(err2.getElementsByTagName("id").item(p).getTextContent());
                                        String product_title1 = err2.getElementsByTagName("title").item(p).getTextContent();
                                        float product_price1 = Float.parseFloat(err2.getElementsByTagName("price").item(p).getTextContent());
                                        contentValuesProducts.put("product_id",product_id1);
                                        contentValuesProducts.put("product_title",product_title1);
                                        contentValuesProducts.put("product_price",product_price1);
                                        myDB.insert("products", null,contentValuesProducts);
                                        /*myDB.execSQL("INSERT INTO products " +
                                                        "(product_id, product_title, product_price) VALUES " +
                                                        "(product_id1, product_title1, product_price1)"
                                        );*/
                                        //System.out.println("product id  " + product_id  + " title "+ product_title+" price " + product_price); //for checking
                                    }
                                }
                            }catch (Exception e) {
                                System.out.println((e));
                            }
                        }
                    });
                }
            }
        });
        final Intent intent = new Intent(TablesActivity.this, OrderActivity.class);
        btn_table1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(TablesActivity.this, OrderActivity.class);
                Bundle bundle1= new Bundle();
                bundle1.putString("table",btn_table1.getText().toString());
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
        btn_table2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table2.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table3.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Bundle bundle= new Bundle();
                bundle.putString("table",btn_table4.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table5.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table6.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table7.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table8.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        btn_table9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle= new Bundle();
                bundle.putString("table",btn_table9.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        final Intent intentPay = new Intent(TablesActivity.this, PaymentActivity.class);
        btn_table1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table1.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table2.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table3.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table4.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table5.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table6.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table7.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table8.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
        btn_table9.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Bundle bundlePay= new Bundle();
                bundlePay.putString("table",btn_table9.getText().toString());
                intentPay.putExtras(bundlePay);
                startActivity(intentPay);
                return true;
            }
        });
    }
    protected void create_tables_t() {
        /* Create a Table in the Database. */
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " +
                "tableStatus " +
                "(table_id integer primary key, " +
                "table_status integer)");
    }
    protected void create_product_t() {
        myDB.execSQL("CREATE TABLE IF NOT EXISTS " +
                "products " +
                "(product_id integer primary key," +
                "product_title text not null, " +
                "product_price real)");
    }
}
