package com.example.notepad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.notepad.R;
import com.example.notepad.bean.StudentBean;

import java.util.List;

public class StudentAdapter extends BaseAdapter {

    private Context context;
    private List<StudentBean> list;


    public StudentAdapter(Context context){
        this.context = context;
    }

    public void setList(List<StudentBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 :list.size();
    }

    @Override
    public StudentBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.notepd_item_layout,null);
            viewHolder  = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        StudentBean noteInfo = getItem(position);
        viewHolder.tvName.setText(noteInfo.getName());
        viewHolder.tcAge.setText(noteInfo.getAge());

        return convertView;
    }



    class ViewHolder{
        TextView tvName;
        TextView tcAge;
        public ViewHolder(View view){
            tvName=(TextView) view.findViewById(R.id.item_content);
        }
    }
}
