package com.example.lab6_23120501037;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MineActivity_23120501037 extends AppCompatActivity {
    private ListView lvOrder;
    private OrderAdapter_23120501037 adapter;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine_23120501037);

        lvOrder = findViewById(R.id.lv_order_list);
        btnBack = findViewById(R.id.btn_back_home);

        adapter = new OrderAdapter_23120501037(this, OrderDataUtil_23120501037.allOrder);
        lvOrder.setAdapter(adapter);

        adapter.setOnDelClickListener(pos -> {
            OrderDataUtil_23120501037.allOrder.remove(pos);
            adapter.notifyDataSetChanged();
            Toast.makeText(MineActivity_23120501037.this, "订单已删除", Toast.LENGTH_SHORT).show();
        });

        lvOrder.setOnItemClickListener((parent, view, position, id) -> {
            OrderBean_23120501037 data = OrderDataUtil_23120501037.allOrder.get(position);
            Intent intent = new Intent(MineActivity_23120501037.this, OrderActivity_23120501037.class);

            intent.putExtra("editName", data.getUserName());
            intent.putExtra("editPhone", data.getUserPhone());
            intent.putExtra("editAddr", data.getAddress());
            intent.putExtra("editTime", data.getOrderTime());
            intent.putExtra("editSer", data.getServiceName());
            intent.putExtra("pos", position);
            intent.putExtra("editWorkerName", data.getWorkerName());
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}