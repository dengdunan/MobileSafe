package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.dda.mobilesafe.utils.SmsUtils;
import com.dda.mobilesafe.utils.UIUtils;

/**
 * 高级工具
 * Created by nuo on 2016/4/8.
 */
public class AToolsActivity extends Activity {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_atools);
    }

    /**
     * 归属地查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        Intent intent = new Intent(AToolsActivity.this, AddressActivity.class);
        startActivity(intent);
    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void backUpsms(View view) {
        //初始化有一个进度条的对话框
        pd = new ProgressDialog(AToolsActivity.this);
        pd.setTitle("提示");
        pd.setMessage("稍安勿躁，正在备份，等一等吧。。。");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();

        new Thread(){
            @Override
            public void run() {
                boolean result = SmsUtils.backUp(AToolsActivity.this, new SmsUtils.BackUpSms() {
                    @Override
                    public void before(int count) {
                        pd.setMax(count);
                    }

                    @Override
                    public void onBackUpsms(int process) {
                        pd.setProgress(process);
                    }
                });
                if (result) {
                   //安全弹吐司的方法
                    UIUtils.showToast(AToolsActivity.this,"备份成功!");
                } else {
                    UIUtils.showToast(AToolsActivity.this,"备份失败!");
                }
                pd.dismiss();
            }
        }.start();
    }

    /**
     * 软件程序锁
     * @param view
     */
    public void applock(View view) {
        Intent intent = new Intent(this,AppLockActivity.class);
        startActivity(intent);
    }

    /**
     * 软件推荐
     * @param view
     */
//    public void appRecomment(View view) {
//
//    }
}
