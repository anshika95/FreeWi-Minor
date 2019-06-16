package com.example.kamal.minor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;



public class FileShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_share);
    }



    public void browse(View view) {
        Intent intent1 = new Intent(getApplicationContext(), FileBrowserActivity.class);
        startActivity(intent1);
    }





}
