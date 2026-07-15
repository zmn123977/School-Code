package com.example.lab6_23120501037;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.List;

public class OrderAdapter_23120501037 extends BaseAdapter {
    private Context mCtx;
    private List<OrderBean_23120501037> list;
    private OnDelClickListener listener;

    public interface OnDelClickListener{
        void onDelete(int pos);
    }

    public void setOnDelClickListener(OnDelClickListener listener){
        this.listener = listener;
    }

    public OrderAdapter_23120501037(Context context, List<OrderBean_23120501037> data){
        this.mCtx = context;
        this.list = data;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_order_23120501037,null);
            holder = new ViewHolder();
            holder.tvSer = convertView.findViewById(R.id.tv_ord_service);
            holder.tvName = convertView.findViewById(R.id.tv_ord_name);
            holder.tvPhone = convertView.findViewById(R.id.tv_ord_phone);
            holder.tvAddr = convertView.findViewById(R.id.tv_ord_addr);
            holder.tvTime = convertView.findViewById(R.id.tv_ord_time);
            holder.tvWorker = convertView.findViewById(R.id.tv_ord_worker);
            holder.btnDel = convertView.findViewById(R.id.btn_del_order);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        OrderBean_23120501037 bean = list.get(position);
        holder.tvSer.setText("服务类型：" + bean.getServiceName());
        holder.tvName.setText("预约人：" + bean.getUserName());
        holder.tvPhone.setText("联系电话：" + bean.getUserPhone());
        holder.tvAddr.setText("服务地址：" + bean.getAddress());
        holder.tvTime.setText("预约时间：" + bean.getOrderTime());
        holder.tvWorker.setText("指派师傅：" + bean.getWorkerName() + " 电话：" + bean.getWorkerPhone());

        final int index = position;
        holder.btnDel.setOnClickListener(v -> {
            if(listener != null){
                listener.onDelete(index);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        TextView tvSer,tvName,tvPhone,tvAddr,tvTime,tvWorker;
        Button btnDel;
    }
}