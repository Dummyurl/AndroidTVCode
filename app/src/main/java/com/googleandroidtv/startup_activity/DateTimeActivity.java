package com.googleandroidtv.startup_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.googleandroidtv.R;
import com.googleandroidtv.util.NetWorkUtils;
import com.googleandroidtv.util.Util;

public class DateTimeActivity extends Activity implements View.OnClickListener {
    Button btnNet, btnBack, btnNext;
    boolean ActionStarted = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time);

        btnNet = findViewById(R.id.btnNet);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnBack.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnNet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.btnNet:
                ActionStarted = true;
                intent = Util.getSettingLaunchIntent("com.android.tv.settings","com.android.tv.settings.system.DateTimeActivity");
                startActivity(intent);
                break;
            case R.id.btnNext:
                if (NetWorkUtils.isNetworkAvailable()) {
                    startActivity(new Intent(DateTimeActivity.this, DisplayActivity.class));
                } else {
                    Toast.makeText(DateTimeActivity.this, getResources().getString(R.string.internet_is_requiered), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnBack:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActionStarted) {
            btnNext.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
            btnNext.setTextColor(getResources().getColor(android.R.color.white));
        }
    }
}
