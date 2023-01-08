package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button jukebox= (Button) findViewById(R.id.btn_jukebox);
        Button btn_coffee = (Button) findViewById(R.id.btn_coffee);
        TextView menu_msg = (TextView) findViewById(R.id.menu_msg);
        menu_msg.setTextSize(15F);
        Bundle b = getIntent().getExtras();
        //String msg1 = b.getString("message");
        menu_msg.setText(b.getString("message"));
                //msg.setText();

        jukebox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, JukeboxActivity.class));
            }
        });
        btn_coffee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MenuActivity.this, TablesActivity.class));
            }
        });
    }
}
