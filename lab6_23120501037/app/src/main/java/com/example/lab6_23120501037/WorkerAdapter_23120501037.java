package com.example.lab6_23120501037;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class WorkerAdapter_23120501037 extends BaseAdapter {
    private Context mContext;
    private List<WorkerBean_23120501037> workerList;

    public WorkerAdapter_23120501037(Context context, List<WorkerBean_23120501037> list) {
        this.mContext = context;
        this.workerList = list;
    }

    @Override
    public int getCount() {
        return workerList.size();
    }

    @Override
    public Object getItem(int position) {
        return workerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_worker_23120501037, null);
        }
        TextView name = convertView.findViewById(R.id.tv_worker_name);
        TextView type = convertView.findViewById(R.id.tv_worker_type);
        TextView phone = convertView.findViewById(R.id.tv_worker_phone);

        WorkerBean_23120501037 bean = workerList.get(position);
        name.setText("师傅姓名：" + bean.getWorkerName());
        type.setText("擅长服务：" + bean.getWorkerType());
        phone.setText("联系电话：" + bean.getWorkerPhone());
        return convertView;
    }
}