package com.example.notepad;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.bean.StudentBean;
import com.example.notepad.database.SQLiteHelper;
import com.example.notepad.utils.DBUtils;
import com.example.notepad.utils.HttpUtils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView note_back;
    TextView note_time;
    EditText content;
    ImageView delete;
    ImageView note_save;
    SQLiteHelper mSQLiteHelper;
    TextView noteName;
    String id;
    SQLiteDatabase db;
    RecordActivity.MHandler mHandler;
    static Boolean addFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        note_back = (ImageView) findViewById(R.id.note_back);
        content = (EditText) findViewById(R.id.note_content);
        delete = (ImageView) findViewById(R.id.delete);
        note_save = (ImageView) findViewById(R.id.note_save);
        noteName = (TextView) findViewById(R.id.note_name);

        note_back.setOnClickListener(this);
        delete.setOnClickListener(this);
        note_save.setOnClickListener(this);
        mHandler = new RecordActivity.MHandler();
        initData();
    }

    protected void initData() {
        mSQLiteHelper = new SQLiteHelper(this);
        noteName.setText("添加记录");

        Intent intent = getIntent();
        if (intent != null) {
            id = intent.getStringExtra("id");
            if (id != null) {
                noteName.setText("修改记录");
                content.setText(intent.getStringExtra("name"));
                note_time.setText(intent.getStringExtra("age"));
                note_time.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.note_back:// 后退按钮
            finish();
            break;
        case R.id.delete:// 清空按钮
            content.setText("");
            break;
        case R.id.note_save:
            // db = mSQLiteHelper.getWritableDatabase();
            // 获取输入内容
            String name = content.getText().toString().trim();
            StudentBean studentBean = new StudentBean();
            studentBean.setId(id);
            studentBean.setName(name);
            studentBean.setAge(studentBean.getAge());

            // 向数据库中添加内容
            if (id != null) {
                if (name.length() > 0) {
                    updateStudent(studentBean);
                } else {
                    showToast("修改内容不能为空");
                }
            } else { // 添加记录界面的保存操作
                // 向数据库中添加数据
                if (name.length() > 0) {
                    insertStudent(studentBean);
                } else {
                    showToast("填写内容不能为空");
                }
            }
            // db.close();
            break;
        }
    }

    // 增加
    public void insertStudent(StudentBean req) {
        addOrUpdate("add", req);
    }

    // 更新
    public void updateStudent(StudentBean req) {
        addOrUpdate("update", req);
    }

    private void addOrUpdate(String action, StudentBean req) {
        Call call = new OkHttpClient().newCall(HttpUtils.postRequestBuilder(action, req));

        // andriod不能使用同步调用
        // 开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 错误处理
                    showToast("更新失败");
                    return;
                }

                // 线程内不能直接操作主线程的view，需要借助MQ
                Message msg = new Message();
                msg.obj = response.body().string();

                switch (action) {
                    case "add":
                        msg.what = HttpUtils.MSG_CREATE_OK;
                        msg.obj = req;
                        break;
                    case "update":
                        msg.what = HttpUtils.MSG_UPDATE_OK;
                        msg.obj = req;
                        break;
                }

                mHandler.sendMessage(msg);
            }

            @Override
            public void onFailure(Call call, IOException e) {

            }
        });
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 事件捕获
     */
    class MHandler extends Handler {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HttpUtils.MSG_CREATE_OK:
                case HttpUtils.MSG_UPDATE_OK:
                    if (msg.obj != null) {
                        showToast("更新成功");
                        setResult(2);
                        finish();
                    }
                    break;
                default:
                break;
            }
        }
    }
}
