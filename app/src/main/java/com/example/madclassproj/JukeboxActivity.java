package com.example.madclassproj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.content.Context;

import android.media.MediaPlayer;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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

public class JukeboxActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jukebox);

        //TextView capt_artist=(TextView) findViewById(R.id.capt_artist);
        //capt_artist.setFontFeatureSettings(Font.BOLD);
        mContext = getApplicationContext();
        final TextView val_artist = (TextView) findViewById(R.id.val_artist);
        final TextView val_title = (TextView) findViewById(R.id.val_title);
        final TextView val_url = (TextView) findViewById(R.id.val_url);
        final TextView tv_status = (TextView) findViewById(R.id.tv_status);


        final ImageButton btn_play = (ImageButton) findViewById(R.id.btn_play);
        final ImageButton btn_pause= (ImageButton) findViewById(R.id.btn_pause);
        final ImageButton btn_request = (ImageButton) findViewById(R.id.btn_request);

        btn_play.setEnabled(false);
        btn_pause.setEnabled(false);
        btn_play.setBackgroundColor(Color.parseColor("#888888"));
        btn_pause.setBackgroundColor(Color.parseColor("#888888"));

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tv_status.setText("Requesting a song from CTower");
                OkHttpClient client = new OkHttpClient();
                String url = "http://mad.mywork.gr/get_song.php?t=429720";
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
                            System.out.println(myResponse);

                            JukeboxActivity.this.runOnUiThread(new Runnable() {
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
                                            if (status.equals("0-FAIL")){
                                                Toast.makeText(getApplicationContext(), "Not valid token, please get new", Toast.LENGTH_SHORT).show();
                                                startActivity( new Intent(JukeboxActivity.this, LoginActivity.class));
                                            }
                                            String title = err.getElementsByTagName("title").item(0).getTextContent();
                                            String artist = err.getElementsByTagName("artist").item(0).getTextContent();
                                            String url = err.getElementsByTagName("url").item(0).getTextContent();

                                                val_artist.setText(artist);
                                                val_title.setText(title);
                                                val_url.setText(url);
                                                stopPlaying();

                                                mediaPlayer = new MediaPlayer();

                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                                mediaPlayer.setDataSource(url);
                                                mediaPlayer.prepare();
                                                mediaPlayer.start();

                                                tv_status.setText("Playing");

                                                btn_play.setEnabled(false);
                                                btn_pause.setEnabled(true);
                                                btn_pause.setBackgroundColor(Color.parseColor("#F44336"));
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
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText("Playing");
                mediaPlayer.start();
                //boolean playing = true;
                btn_play.setBackgroundColor(Color.parseColor("#888888"));
                btn_pause.setBackgroundColor(Color.parseColor("#F44336"));
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_status.setText("Stopped");
               mediaPlayer.pause();
               btn_play.setEnabled(true);

                btn_pause.setBackgroundColor(Color.parseColor("#888888"));

            }
        });
    }
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }
    protected void stopPlaying() {
        // If media player is not null then try to stop it
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(mContext, "Playing starts", Toast.LENGTH_SHORT).show();
        }
    }
}
