package com.example.lab6_23120501037;

import android.content.Context;
import android.widget.BaseAdapter;
import java.util.List;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
public class ServiceAdapter_23120501037 extends BaseAdapter {
    private Context mContext;
    private List<ServiceBean_23120501037> serviceDataList;
    public ServiceAdapter_23120501037(Context context, List<ServiceBean_23120501037> list){
        this.mContext=context;
        this.serviceDataList=list;
    }
    @Override
    public int getCount(){
        return serviceDataList.size();
    }
    @Override
    public Object getItem(int position){
        return serviceDataList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView==null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_service_type_23120501037,null);
        }
        TextView tvName = convertView.findViewById(R.id.tv_item_name);
        ServiceBean_23120501037 bean = serviceDataList.get(position);
        tvName.setText(bean.getServiceName());
        return convertView;
    }
}
