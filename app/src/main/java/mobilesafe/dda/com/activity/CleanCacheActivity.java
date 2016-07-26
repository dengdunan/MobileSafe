package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nuo on 2016/6/28.
 * Created by 15:19.
 * 描述:缓存清理
 */
public class CleanCacheActivity extends Activity {

    private PackageManager packageManager;
    private List<CacheInfo> cacheLists;
    private CacheAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_clean_cache);

        list_view = (ListView) findViewById(R.id.list_view);

        //垃圾的集合
        cacheLists = new ArrayList<>();

        packageManager = getPackageManager();

        new Thread() {
            @Override
            public void run() {
                //获取到安装在手机上面所有的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                for (PackageInfo packageInfo : installedPackages) {
                    getCaCheSize(packageInfo);
                    //System.out.println(cacheLists);
                }
                //handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            adapter = new CacheAdapter();
            list_view.setAdapter(adapter);
        }
    };

    private ListView list_view;

    private class CacheAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return cacheLists.size();
        }

        @Override
        public Object getItem(int position) {
            return cacheLists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder viewHolder;
            if (convertView == null) {
                view = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache, null);

                viewHolder = new ViewHolder();

                viewHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                viewHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                viewHolder.tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
                viewHolder.iv_clean = (ImageView) view.findViewById(R.id.iv_clean);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            viewHolder.iv_icon.setImageDrawable(cacheLists.get(position).icon);
            viewHolder.tv_name.setText(cacheLists.get(position).appName);
            viewHolder.tv_cache_size.setText("缓存大小:" + Formatter.formatFileSize(CleanCacheActivity.this, cacheLists.get(position).cacheSize));
            viewHolder.iv_clean.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent detail_intent = new Intent();
                    detail_intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    detail_intent.addCategory(Intent.CATEGORY_DEFAULT);
                    detail_intent.setData(Uri.parse("package:" + cacheLists.get(position).packageName));
                    startActivity(detail_intent);
                }
            });

            return view;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_cache_size;
        ImageView iv_clean;
    }

    //获取到缓存的大小
    private void getCaCheSize(PackageInfo packageInfo) {
        try {
            //Class<?> clazz = getClassLoader().loadClass("packageManager");
            //通过反射获取到当前的方法
            /**
             * 接收2个参数
             * 第一个参数接收一个包名
             * 第二个参数接收aidl的对象
             */
            Method method = PackageManager.class.getDeclaredMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
            /**
             * 第一个参数表示当前的这个方法由谁调用的
             *
             * 第二个参数表示包名
             */
            method.invoke(packageManager, packageInfo.applicationInfo.packageName, new MyIPackageStatsObserver(packageInfo));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

        private PackageInfo packageInfo;

        public MyIPackageStatsObserver(PackageInfo packageInfo) {
            this.packageInfo = packageInfo;
        }

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            //获取到当前手机应用的缓存大小
            long cacheSize = pStats.cacheSize;
            //如果当前的缓存大小是大于0的话，说明有缓存
            if (cacheSize > 0) {
                CacheInfo cacheInfo = new CacheInfo();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
                cacheInfo.icon = icon;
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                cacheInfo.appName = appName;
                cacheInfo.cacheSize = cacheSize;
                cacheInfo.packageName = packageInfo.packageName;
                cacheLists.add(cacheInfo);
                handler.sendEmptyMessage(0);
            }
        }
    }

    static class CacheInfo {
        Drawable icon;
        long cacheSize;
        String appName;
        String packageName;
    }

    /**
     * 全部清除
     *
     * @param view
     */
    public void cleanAll(View view) {

        //获取到当前应用程序里面所有的方法
        Method[] methods = PackageManager.class.getMethods();

        for (Method method : methods) {
            //判断当前的方法的名字
            if (method.equals("freeStorageAndNotify")) {
                try {
                    method.invoke(packageManager, Integer.MAX_VALUE, new MyIPackageDataObserver());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
       initUI();
    }

    private class MyIPackageDataObserver extends IPackageDataObserver.Stub{

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {

        }
    }
}
