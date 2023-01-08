package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText editText = (EditText) findViewById(R.id.editText);
        final Button btn_send = (Button) findViewById(R.id.btn_send);
        final TextView invit_text = (TextView) findViewById(R.id.invit_text);
        final TextView msg_tocken = (TextView) findViewById(R.id.msg_tocken);
        msg_tocken.setVisibility(View.INVISIBLE);
        //msg_tocken.setTextSize(15F);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpClient client = new OkHttpClient();
                String url = "http://mad.mywork.gr/generate_token.php?e=" + editText.getText().toString();
                //System.out.println(editText.getText().toString());
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

                            LoginActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //mTextviewResult.setText(myResponse);
                                    try {
                                        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                                .parse(new InputSource(new StringReader(myResponse.toString())));

                                        NodeList errNodes = doc.getElementsByTagName("response");
                                        if (errNodes.getLength() > 0) {
                                            Element err = (Element) errNodes.item(0);
                                            String status = err.getElementsByTagName("status").item(0).getTextContent();
                                            String msg = err.getElementsByTagName("msg").item(0).getTextContent();

                                            if (status.equals("1-OK")) {
                                                //msg_tocken.setVisibility(View.VISIBLE);
                                                msg_tocken.setText(msg);
                                                msg_tocken.setVisibility(View.VISIBLE);
                                                msg_tocken.setTextSize(15F);
                                                invit_text.setVisibility(View.INVISIBLE);
                                                btn_send.setVisibility(View.INVISIBLE);
                                                editText.setVisibility(View.INVISIBLE);

                                                //startActivity( new Intent(LoginActivity.this, MenuActivity.class));
                                                System.out.println(msg);
                                            } else /*(status.equals("1-FAIL"))*/ {
                                                msg_tocken.setVisibility(View.VISIBLE);
                                                msg_tocken.setTextColor(Color.RED);
                                                msg_tocken.setText("Can't identify email: " + editText.getText().toString());
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