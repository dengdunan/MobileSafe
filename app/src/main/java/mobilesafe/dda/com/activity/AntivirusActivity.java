package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dda.mobilesafe.db.AntivirusDao;
import com.dda.mobilesafe.utils.MD5Utils;

import java.util.List;

public class AntivirusActivity extends Activity {

    //扫描开始
    private static final int BEGING = 1;
    //扫描中
    private static final int SCANING = 2;
    //扫描结束
    private static final int FINISH = 3;
    private Message message;
    private TextView tv_init_virus;
    private ProgressBar pb;
    private ImageView iv_scanning;
    private LinearLayout ll_content;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();

        initData();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BEGING:
                    tv_init_virus.setText("初始化双核引擎");
                    break;
                case SCANING:
                    //病毒扫描中。。。
                    TextView child = new TextView(AntivirusActivity.this);

                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    //如果为true表示有病毒
                    if (scanInfo.desc) {

                        child.setTextColor(Color.RED);

                        child.setText(scanInfo.appName + "有病毒");

                    } else {
                        //为false表示没有病毒
                        child.setTextColor(Color.BLACK);

                        child.setText(scanInfo.appName + "扫描安全");

                    }

                    ll_content.addView(child, 0);

                    //自动滚动
                    scrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            //一直在先秒进行滚动
                            scrollView.fullScroll(scrollView.FOCUS_DOWN);

                        }
                    });

                    break;
                case FINISH:
                    //当扫描结束的时候，停止动画
                    iv_scanning.clearAnimation();
                    break;
            }
        }
    };

    private void initData() {

        new Thread() {
            @Override
            public void run() {

                message = Message.obtain();

                message.what = BEGING;

                PackageManager packageManager = getPackageManager();
                //获取到所有安装的应用程序
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                //返回手机上面安装了多少应用程序
                int size = installedPackages.size();
                //设置进度条的最大值
                pb.setMax(size);

                int progress = 0;

                for (PackageInfo packageInfo : installedPackages) {

                    ScanInfo scanInfo = new ScanInfo();

                    //获取到当前手机上面的app的名字
                    String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                    scanInfo.appName = appName;

                    String appPackageName = packageInfo.applicationInfo.packageName;

                    scanInfo.packageName = appPackageName;

                    //首先需要获取到每个应用程序的目录
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    //获取到文件的md5值
                    String md5 = MD5Utils.getFileMd5(sourceDir);
                    //判断当前文件是否是在病毒数据库里面
                    String desc = AntivirusDao.checkFileVirus(md5);

                    //如果当前的描述信息等于null说明没有病毒
                    if (desc == null) {
                        scanInfo.desc = false;
                    } else {
                        scanInfo.desc = true;
                    }

                    progress++;

                    SystemClock.sleep(100);

                    pb.setProgress(progress);

                    message = Message.obtain();

                    message.what = SCANING;

                    message.obj = scanInfo;

                    handler.sendMessage(message);
                }

                message = Message.obtain();

                message.what = FINISH;

                handler.sendMessage(message);

            }
        }.start();

    }

    static class ScanInfo {
        boolean desc;
        String appName;
        String packageName;
    }

    private void initUI() {
        setContentView(R.layout.activity_antivirus);
        iv_scanning = (ImageView) findViewById(R.id.iv_scanning);

        tv_init_virus = (TextView) findViewById(R.id.tv_init_virus);

        ll_content = (LinearLayout) findViewById(R.id.ll_content);

        pb = (ProgressBar) findViewById(R.id.progressBar1);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        /**
         * 第一个参数表示开始的角度
         * 第二个参数表示结束的角度
         * 第三个参数表示参照自己
         * 初始化旋转动画
         */
        RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //设置动画的时间
        rotateAnimation.setDuration(5000);
        //rotateAnimation.setRepeatMode(-1);
        //设置无限循环
        rotateAnimation.setRepeatCount(-1);
        //开始动画
        iv_scanning.startAnimation(rotateAnimation);
    }
}
