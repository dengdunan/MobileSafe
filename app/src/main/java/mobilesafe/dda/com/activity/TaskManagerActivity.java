package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dda.mobilesafe.bean.TaskInfo;
import com.dda.mobilesafe.engine.TaskInfos;
import com.dda.mobilesafe.utils.SharedPreferencesUtils;
import com.dda.mobilesafe.utils.SystemInfoUtils;
import com.dda.mobilesafe.utils.UIUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author anzai
 *
 * created at 2016/6/24 13:13
 *
 * 功能描述:
 */

public class TaskManagerActivity extends Activity {

    @ViewInject(R.id.tv_task_process_count)
    private TextView tv_task_process_count;
    @ViewInject(R.id.tv_task_memory)
    private TextView tv_task_memory;
    @ViewInject(R.id.list_view)
    private ListView list_view;
    private List<TaskInfo> taskInfos;
    private ArrayList<TaskInfo> userTaskInos;
    private ArrayList<TaskInfo> systemLists;
    private TaskManagerAdapter adapter;
    private int processCount;
    private long availMem;
    private long totalMem;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //sp = getSharedPreferences("config", 0);

        initUI();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            //boolean result = sp.getBoolean("is_show_system", false);

            /**
             * 判断当前用户是否需要展示系统进程
             * 如果需要就全部展示
             * 如果不需要就展示用户进程
             */
            boolean result = SharedPreferencesUtils.getBoolean(TaskManagerActivity.this, "is_show_system", false);

            if (result) {
                return userTaskInos.size() + 1 + systemLists.size() + 1;
            } else {
                return userTaskInos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {

            if (position == 0) {
                return null;
            } else if (position == userTaskInos.size() + 1) {
                return null;
            }

            TaskInfo taskInfo = taskInfos.get(position);

            if (position < userTaskInos.size() + 1) {
                //把多出来的特殊的条目减掉
                taskInfo = userTaskInos.get(position - 1);

            } else if (position > userTaskInos.size() + 1) {
                int location = userTaskInos.size() + 2;
                taskInfo = systemLists.get(position - location);
            }

            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                //表示用户程序
                TextView textView = new TextView(getApplicationContext());

                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("用户程序(" + userTaskInos.size() + ")");

                return textView;
            } else if (position == userTaskInos.size() + 1) {
                //表示系统程序
                TextView textView = new TextView(getApplicationContext());

                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                textView.setText("系统程序(" + systemLists.size() + ")");

                return textView;

            }

            ViewHolder holder;
            View view;

            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {

                view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);

                holder = new ViewHolder();

                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);

                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);

                holder.tv_app_memory_size = (TextView) view.findViewById(R.id.tv_app_memory_size);

                holder.tv_app_status = (CheckBox) view.findViewById(R.id.tv_app_status);

                view.setTag(holder);
            }
            TaskInfo taskInfo = taskInfos.get(position);

            if (position < userTaskInos.size() + 1) {
                //把多出来的特殊的条目减掉
                taskInfo = userTaskInos.get(position - 1);

            } else if (position > userTaskInos.size() + 1) {
                int location = userTaskInos.size() + 2;
                taskInfo = systemLists.get(position - location);
            }

            holder.iv_app_icon.setImageDrawable(taskInfo.getIcon());

            holder.tv_app_name.setText(taskInfo.getAppName());

            holder.tv_app_memory_size.setText("内存占用:" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));

            if (taskInfo.isChecked()) {
                holder.tv_app_status.setChecked(true);
            } else {
                holder.tv_app_status.setChecked(false);
            }

            //判断当前展示的item是否是自己的程序，如果是，就把程序给隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                //隐藏
                holder.tv_app_status.setVisibility(View.INVISIBLE);
            } else {
                //显示
                holder.tv_app_status.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_app_icon;
        TextView tv_app_name;
        TextView tv_app_memory_size;
        CheckBox tv_app_status;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter = new TaskManagerAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                taskInfos = TaskInfos.getTaskInfos(TaskManagerActivity.this);

                userTaskInos = new ArrayList<>();

                systemLists = new ArrayList<>();

                for (TaskInfo taskInfo : taskInfos) {

                    if (taskInfo.isUserApp()) {
                        userTaskInos.add(taskInfo);
                    } else {
                        systemLists.add(taskInfo);
                    }
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 区别，
     * ActivityManager
     * 活动管理器(进程管理器)
     * packageManager
     * 包管理器
     */
    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);

        processCount = SystemInfoUtils.getProcessCount(this);
        tv_task_process_count.setText("运行中的进程:" + processCount + "个");

        availMem = SystemInfoUtils.getAvailMem(this);
        //获取到总的内存
        totalMem = SystemInfoUtils.getTotalMem(this);

        tv_task_memory.setText("剩余/总内存:" + android.text.format.Formatter.formatFileSize(TaskManagerActivity.this, availMem) + "/" + android.text.format.Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //得到当前点击listView的对象
                Object object = list_view.getItemAtPosition(position);

                if (object != null && object instanceof TaskInfo) {

                    TaskInfo taskInfo = (TaskInfo) object;
                    ViewHolder holder = (ViewHolder) view.getTag();

                    if(taskInfo.getPackageName().equals(getPackageName())) {
                        return;
                    }

                    //判断当前的item是否被勾选上
                    /**
                     * 如果被勾选上了，那么就改成没有勾选
                     * 如果没有勾选，就改成已经勾选
                     */
                    if (taskInfo.isChecked()) {
                        taskInfo.setChecked(false);
                        holder.tv_app_status.setChecked(false);
                    } else {
                        taskInfo.setChecked(true);
                        holder.tv_app_status.setChecked(true);
                    }
                }
            }
        });

    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {

        for (TaskInfo taskInfo : userTaskInos) {

            //判断当前的用户程序是不是自己的程序，如果是自己的程序，那么就把文本隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }

            taskInfo.setChecked(true);
        }

        for (TaskInfo taskInfo : systemLists) {
            taskInfo.setChecked(true);
        }
        //一定要注意，一旦数据发生改变，一定要刷新
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectOppsite(View view) {
        for (TaskInfo taskInfo : userTaskInos) {

            //判断当前的用户程序是不是自己的程序，如果是自己的程序，那么就把文本隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                continue;
            }

            taskInfo.setChecked(!(taskInfo.isChecked()));
        }

        for (TaskInfo taskInfo : systemLists) {
            taskInfo.setChecked(!(taskInfo.isChecked()));

        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理进程
     *
     * @param view
     */
    public void killProcess(View view) {

        //想杀死进程，首先必须得得到进程管理器

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        //清理进程的集合
        List<TaskInfo> killLists = new ArrayList<>();

        //清理的总共的进程个数
        int totalCount = 0;
        //清理的进程的大小
        int killMem = 0;
        for (TaskInfo taskInfo : userTaskInos) {

            if (taskInfo.isChecked()) {
                killLists.add(taskInfo);
                //userTaskInos.remove(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
                //杀死进程，参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        for (TaskInfo taskInfo : systemLists) {
            if (taskInfo.isChecked()) {
                killLists.add(taskInfo);
                //systemLists.remove(taskInfo);
                totalCount++;
                killMem += taskInfo.getMemorySize();
                //杀死进程，参数表示包名
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        /**
         * 注意，当集合在迭代的时候，不能修改集合的大小
         */
        for (TaskInfo taskInfo : killLists) {
            //判断是否是用户app
            if (taskInfo.isUserApp()) {
                userTaskInos.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            } else {
                systemLists.remove(taskInfo);
                activityManager.killBackgroundProcesses(taskInfo.getPackageName());
            }
        }

        UIUtils.showToast(TaskManagerActivity.this, "共清理了" + totalCount + "个进程,释放" + Formatter.formatFileSize(TaskManagerActivity.this, killMem) + "内存");

        //processCount 表示总共有多少个进程
        //totalCount 当前清理了多少个进程
        processCount -= totalCount;
        tv_task_process_count.setText("运行中的进程:" + processCount + "个");

        //
        tv_task_memory.setText("剩余/总内存:" + android.text.format.Formatter.formatFileSize(TaskManagerActivity.this, availMem + killMem) + "/" + android.text.format.Formatter.formatFileSize(TaskManagerActivity.this, totalMem));

        //刷新界面
        adapter.notifyDataSetChanged();
    }

    public void openSetting(View view) {
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivity(intent);
    }
}
