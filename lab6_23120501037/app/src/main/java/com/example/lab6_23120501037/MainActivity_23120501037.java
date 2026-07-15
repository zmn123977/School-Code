package com.example.lab6_23120501037;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity_23120501037 extends AppCompatActivity {
    private GridView gvService;
    private List<ServiceBean_23120501037> serviceBeanList;
    private Button btnMine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_23120501037);

        gvService = findViewById(R.id.gv_service);
        btnMine = findViewById(R.id.btn_mine);
        serviceBeanList = new ArrayList<>();

        serviceBeanList.add(new ServiceBean_23120501037("日常保洁","全屋日常清扫整理"));
        serviceBeanList.add(new ServiceBean_23120501037("家电清洗","空调油烟机深度清洁"));
        serviceBeanList.add(new ServiceBean_23120501037("保姆陪护","老人日常居家照料"));
        serviceBeanList.add(new ServiceBean_23120501037("搬家服务","家庭物品搬运拆装"));
        serviceBeanList.add(new ServiceBean_23120501037("家电维修","家用电器故障维修"));
        serviceBeanList.add(new ServiceBean_23120501037("育儿看护","幼儿陪伴日常照看"));

        ServiceAdapter_23120501037 adapter = new ServiceAdapter_23120501037(this, serviceBeanList);
        gvService.setAdapter(adapter);

        gvService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ServiceBean_23120501037 bean = serviceBeanList.get(position);
                Intent intent = new Intent(MainActivity_23120501037.this, ServiceDetailActivity_23120501037.class);
                intent.putExtra("serviceName", bean.getServiceName());
                intent.putExtra("serviceInfo", bean.getServiceInfo());
                startActivity(intent);
            }
        });

        btnMine.setOnClickListener(v->startActivity(new Intent(this,MineActivity_23120501037.class)));
    }
}