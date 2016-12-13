package com.android.potatofly.ffmpegdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    TextView FFshow;
    //load library
    static {
        System.loadLibrary("avcodec-57");
        System.loadLibrary("avfilter-6");
        System.loadLibrary("avformat-57");
        System.loadLibrary("avutil-55");
        System.loadLibrary("swresample-2");
        System.loadLibrary("swscale-4");
        System.loadLibrary("ffmpegdemo");
    }
    //JNI method
    public native String avformatiofo();
    public native String avcodecinfo();
    public native String avfilterinfo();
    public native String configurationinfo();
    public void format(View view){

        FFshow.setText(avformatiofo());
        Toast.makeText(this,"format",Toast.LENGTH_SHORT).show();
    }

    public void codec(View view){
        FFshow.setText(avcodecinfo());
        Toast.makeText(this,"codec",Toast.LENGTH_SHORT).show();
    }

    public void filter(View view){

        FFshow.setText(avfilterinfo());
        Toast.makeText(this,"filter",Toast.LENGTH_SHORT).show();
    }

    public void config(View view){

        FFshow.setText(configurationinfo());
        Toast.makeText(this,"config",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FFshow = (TextView)findViewById(R.id.show);
    }

}
