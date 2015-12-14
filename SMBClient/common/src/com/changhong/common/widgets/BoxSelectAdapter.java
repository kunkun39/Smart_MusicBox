package com.changhong.common.widgets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.changhong.common.service.ClientSendCommandService;

import java.util.List;

/**
 * Created by Administrator on 15-5-15.
 */
public class BoxSelectAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<String> ipList;

    public BoxSelectAdapter(Context context, List<String> ipList) {
        this.minflater = LayoutInflater.from(context);
        this.ipList = ipList;
    }

    public void setData(List<String> list){
    	this.ipList=list;
    	notifyDataSetChanged();
    }
    
    public void clearData(){
    	this.ipList=null;
    	notifyDataSetInvalidated();
    }
    
    @Override
    public int getCount() {
        return ipList!=null ? ipList.size():0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * VIEW HOLDER的配置
         */
        final ViewHolder vh;
        String serverIP=null;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = minflater.inflate(android.R.layout.simple_list_item_1, null);
            vh.boxInfo = (TextView) convertView.findViewById(android.R.id.text1);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        if(ipList!=null){
        serverIP = ipList.get(position);
        }
        vh.boxInfo.setText(ClientSendCommandService.getConnectBoxName(serverIP) +  " [" + serverIP + "]");

        return convertView;
    }

    public final class ViewHolder {
        public TextView boxInfo;
    }
}
