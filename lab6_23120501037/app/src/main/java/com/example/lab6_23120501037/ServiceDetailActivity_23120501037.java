package com.example.lab6_23120501037;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceDetailActivity_23120501037 extends AppCompatActivity {
    private TextView tvDetailName;
    private TextView tvDetailInfo;
    private Button btnLookWorker;
    private Button btnOrderNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail_23120501037);

        tvDetailName = findViewById(R.id.tv_detail_name);
        tvDetailInfo = findViewById(R.id.tv_detail_info);

        btnOrderNow = findViewById(R.id.btn_order);

        Intent intent = getIntent();
        String serviceName = intent.getStringExtra("serviceName");
        String serviceInfo = intent.getStringExtra("serviceInfo");

        tvDetailName.setText(serviceName);
        tvDetailInfo.setText(serviceInfo);


        btnOrderNow.setOnClickListener(v -> {
            Intent intent2 = new Intent(ServiceDetailActivity_23120501037.this, OrderActivity_23120501037.class);
            intent2.putExtra("serviceN",serviceName);
            startActivity(intent2);
        });
    }
}