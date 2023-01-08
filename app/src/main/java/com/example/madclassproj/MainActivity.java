package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;


import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    String tocken = "429720";
    public String getTocken() {
        return tocken;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);


        OkHttpClient client = new OkHttpClient();
        String urlOption;
        if (tocken.length()>=5){
            urlOption=tocken;
        }else {
            urlOption="XML";
        }
        String url = "http://mad.mywork.gr/authenticate.php?t=+" + urlOption;

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
                    
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mTextviewResult.setText(myResponse);
                            try {
                            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                    .parse(new InputSource(new StringReader(myResponse.toString())));

                            NodeList errNodes = doc.getElementsByTagName("response");
                            if(errNodes.getLength()>0){
                                Element err=(Element)errNodes.item(0);
                                String status = err.getElementsByTagName("status").item(0).getTextContent();
                                String msg  = err.getElementsByTagName("msg").item(0).getTextContent();
                                //System.out.println(status);

                                if (status.equals("0-FAIL")){
                                    startActivity( new Intent(MainActivity.this, LoginActivity.class));
                                }
                                    //System.out.println("enter email to get tocken");}
                                else if(status.equals("0-OK")){
                                    Intent intent= new Intent(MainActivity.this, MenuActivity.class);
                                    Bundle b = new Bundle();
                                    b.putString("message", msg);
                                    intent.putExtras(b);
                                    startActivity(intent);
                                    //System.out.println("something wrong");
                                }else{

                                    startActivity( new Intent(MainActivity.this, LoginActivity.class));

                                }
                            } }catch (Exception e) {
                                    System.out.println((e));
                                }


                        }
                    });
                }

            }
        });

    }
}
