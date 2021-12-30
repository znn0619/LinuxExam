package com.example.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.adapter.StudentAdapter;
import com.example.notepad.bean.StudentBean;
import com.example.notepad.database.SQLiteHelper;
import com.example.notepad.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class StudentActivity extends AppCompatActivity {
    ListView listView;
    //List<NotepadBean> list;
    SQLiteHelper mSQLiteHelper;
    StudentAdapter adapter;
    SQLiteDatabase db;
    MHandler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        // 用于显示记录的列表
        listView = (ListView) findViewById(R.id.listview);
        ImageView add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, com.example.notepad.RecordActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        mHandler = new MHandler();
        initData();
        adapter = new StudentAdapter(this);
        listView.setAdapter(adapter);
    }

    protected void initData() {
        //mSQLiteHelper = new SQLiteHelper(this); // 创建数据库
        getStudent();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                StudentBean studentBean = adapter.getItem(position);
                Intent intent = new Intent(StudentActivity.this, com.example.notepad.RecordActivity.class);
                intent.putExtra("id", studentBean.getId());
                intent.putExtra("time", studentBean.getAge());
                // 记录的内容
                intent.putExtra("content", studentBean.getName());
                // 跳转到修改记录页面
                StudentActivity.this.startActivityForResult(intent, 1);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //db = mSQLiteHelper.getWritableDatabase();
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this).setMessage("是否删除此记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                StudentBean studentBean = adapter.getItem(position);
                                // if (mSQLiteHelper.deleteData(notepadBean.getId(), db)) {
                                deleteStudent(studentBean.getId(), position);
                                // db.close();
                                // }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();// 关闭对话框
                            }
                        });

                dialog = builder.create();
                dialog.show();
                return true;
            }

        });
    }

    // 删除
    public void deleteStudent(String id, int position) {
        StudentBean req = new StudentBean();
        req.setId(id);

        Call call = new OkHttpClient().newCall(HttpUtils.postRequestBuilder("delete", req));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    Toast.makeText(StudentActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 线程内不能直接操作主线程的view，需要借助MQ
                Message msg = new Message();
                msg.what = HttpUtils.MSG_DELETE_OK;
                msg.obj = position;
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    // 查询
    public void getStudent() {
        Call call = new OkHttpClient().newCall(HttpUtils.getRequestBuilder("GetNotePadList"));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    Toast.makeText(StudentActivity.this, "查询失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 线程内不能直接操作主线程的view，需要借助MQ
                Message msg = new Message();
                msg.what = HttpUtils.MSG_QUERY_OK;
                msg.obj = response.body().string();
                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 2) {
            getStudent();
        }
    }

    /**
     * 事件捕获
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HttpUtils.MSG_QUERY_OK:
                    if (msg.obj != null) {
                        // 解析res
                        Gson gson = new Gson();
                        List <StudentBean> list = gson.fromJson((String) msg.obj, new TypeToken<List<StudentBean>>() {
                        }.getType());
                        // 展示列表
                        adapter.setList(list);
                    }
                    break;
                case HttpUtils.MSG_DELETE_OK:
                    if (msg.obj != null) {
                        Log.i("StudentActivity", String.format("%d", (int)msg.obj));
                        Toast.makeText(StudentActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        getStudent();
                    }
                    break;
            }
        }
    }
}
