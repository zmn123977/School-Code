package com.example.lab6_23120501037;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity_23120501037 extends AppCompatActivity {
    private EditText etName,etPhone,etAddr,etOrderTime;
    private Button btnSubmit;
    private ListView lvChooseWorker;

    private String serName;
    private int editPos = -1;

    private WorkerBean_23120501037 selectedWorker = null;
    private List<WorkerBean_23120501037> workerList;
    private WorkerAdapter_23120501037 workerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_23120501037);

        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAddr = findViewById(R.id.et_address);
        etOrderTime = findViewById(R.id.et_time);
        btnSubmit = findViewById(R.id.btn_submit_order);
        lvChooseWorker = findViewById(R.id.lv_choose_worker);

        Intent intent = getIntent();
        editPos = intent.getIntExtra("pos", -1);

        if(editPos != -1){
            serName = intent.getStringExtra("editSer");
        }else{
            serName = intent.getStringExtra("serviceN");
        }

        loadAllServiceWorker();


        lvChooseWorker.setOnItemClickListener((parent, view, position, id) -> {
            selectedWorker = workerList.get(position);
            Toast.makeText(this, "已选中：" + selectedWorker.getWorkerName(), Toast.LENGTH_SHORT).show();
        });

        if (editPos != -1) {
            etName.setText(intent.getStringExtra("editName"));
            etPhone.setText(intent.getStringExtra("editPhone"));
            etAddr.setText(intent.getStringExtra("editAddr"));
            etOrderTime.setText(intent.getStringExtra("editTime"));
            btnSubmit.setText("修改保存");

            String oldWorkerName = intent.getStringExtra("editWorkerName");
            if(oldWorkerName != null && workerList != null){
                for(WorkerBean_23120501037 w : workerList){
                    if(w.getWorkerName().equals(oldWorkerName)){
                        selectedWorker = w;
                        break;
                    }
                }
            }
        } else {
            btnSubmit.setText("提交预约");
        }

        btnSubmit.setOnClickListener(v -> {
            String uname = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String addr = etAddr.getText().toString().trim();
            String oTime = etOrderTime.getText().toString().trim();

            if (uname.isEmpty() || phone.isEmpty() || addr.isEmpty() || oTime.isEmpty()) {
                Toast.makeText(this, "请填写完整预约信息", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedWorker == null) {
                Toast.makeText(this, "请选择一位服务师傅", Toast.LENGTH_SHORT).show();
                return;
            }

            if (editPos != -1) {
                OrderBean_23120501037 newOrder = new OrderBean_23120501037(
                        uname, phone, addr, oTime, serName,
                        selectedWorker.getWorkerName(),
                        selectedWorker.getWorkerPhone()
                );
                OrderDataUtil_23120501037.allOrder.set(editPos, newOrder);
                Toast.makeText(this, "修改成功，师傅已更换", Toast.LENGTH_SHORT).show();
                finish();
            } else {

                OrderBean_23120501037 order = new OrderBean_23120501037(
                        uname, phone, addr, oTime, serName,
                        selectedWorker.getWorkerName(),
                        selectedWorker.getWorkerPhone()
                );
                OrderDataUtil_23120501037.allOrder.add(order);
                Toast.makeText(this, "预约成功！", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadAllServiceWorker() {
        workerList = new ArrayList<>();
        if(serName.contains("日常保洁")){
            workerList.add(new WorkerBean_23120501037("张大姐","全屋清扫/玻璃擦拭","13511110001"));
            workerList.add(new WorkerBean_23120501037("李阿姨","厨房深度保洁","13511110002"));
            workerList.add(new WorkerBean_23120501037("王姐","卧室整理收纳","13511110003"));
            workerList.add(new WorkerBean_23120501037("刘姨","卫生间清洁除味","13511110004"));
            workerList.add(new WorkerBean_23120501037("陈嫂","全屋除尘消杀","13511110005"));
        } else if(serName.contains("家电清洗")){
            workerList.add(new WorkerBean_23120501037("赵师傅","空调深度清洗","13622220001"));
            workerList.add(new WorkerBean_23120501037("孙师傅","油烟机拆洗","13622220002"));
            workerList.add(new WorkerBean_23120501037("周师傅","洗衣机清洗杀菌","13622220003"));
            workerList.add(new WorkerBean_23120501037("吴师傅","冰箱除味清洁","13622220004"));
            workerList.add(new WorkerBean_23120501037("郑师傅","热水器除垢清洗","13622220005"));
        } else if(serName.contains("保姆陪护")){
            workerList.add(new WorkerBean_23120501037("秦阿姨","老人日常陪护","13733330001"));
            workerList.add(new WorkerBean_23120501037("冯阿姨","老年膳食照料","13733330002"));
            workerList.add(new WorkerBean_23120501037("韩阿姨","行动不便看护","13733330003"));
            workerList.add(new WorkerBean_23120501037("朱阿姨","居家陪伴聊天","13733330004"));
            workerList.add(new WorkerBean_23120501037("林阿姨","术后康复照料","13733330005"));
        } else if(serName.contains("搬家服务")){
            workerList.add(new WorkerBean_23120501037("马师傅","大件家具搬运","13844440001"));
            workerList.add(new WorkerBean_23120501037("胡师傅","小件物品打包","13844440002"));
            workerList.add(new WorkerBean_23120501037("郭师傅","家具拆装组装","13844440003"));
            workerList.add(new WorkerBean_23120501037("何师傅","同城短途搬家","13844440004"));
            workerList.add(new WorkerBean_23120501037("高师傅","贵重物品护送","13844440005"));
        } else if(serName.contains("家电维修")){
            workerList.add(new WorkerBean_23120501037("罗师傅","冰箱冰柜维修","13955550001"));
            workerList.add(new WorkerBean_23120501037("梁师傅","空调故障检修","13955550002"));
            workerList.add(new WorkerBean_23120501037("宋师傅","洗衣机维修","13955550003"));
            workerList.add(new WorkerBean_23120501037("唐师傅","热水器维修","13955550004"));
            workerList.add(new WorkerBean_23120501037("许师傅","厨房小家电维修","13955550005"));
        } else if(serName.contains("育儿看护")){
            workerList.add(new WorkerBean_23120501037("温阿姨","低龄幼儿看护","14066660001"));
            workerList.add(new WorkerBean_23120501037("薛阿姨","幼儿辅食制作","14066660002"));
            workerList.add(new WorkerBean_23120501037("彭阿姨","早教启蒙陪伴","14066660003"));
            workerList.add(new WorkerBean_23120501037("戴阿姨","幼儿作息照料","14066660004"));
            workerList.add(new WorkerBean_23120501037("严阿姨","儿童户外活动陪伴","14066660005"));
        }

        workerAdapter = new WorkerAdapter_23120501037(this, workerList);
        lvChooseWorker.setAdapter(workerAdapter);
    }
}