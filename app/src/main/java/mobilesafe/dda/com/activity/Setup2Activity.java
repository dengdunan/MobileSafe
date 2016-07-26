package mobilesafe.dda.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.dda.mobilesafe.utils.ToastUtils;
import com.dda.mobilesafe.view.SettingItemView;

/**
 * 第二步设置向导页面
 * Created by nuo on 2016/3/28.
 */
public class Setup2Activity extends BaseSetupActivity {

    private SettingItemView sivSim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);

        sivSim = (SettingItemView) findViewById(R.id.siv_sim);

        String sim = mPref.getString("sim", null);
        if (!TextUtils.isEmpty(sim)) {
            sivSim.setChecked(true);
        } else {
            sivSim.setChecked(false);
        }

        sivSim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivSim.isChecked()) {
                    sivSim.setChecked(false);
                    mPref.edit().remove("sim").commit();//删除已绑定的sim卡
                } else {
                    sivSim.setChecked(true);
                    //保存sim卡的信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialMember = tm.getSimSerialNumber();//获取sim卡序列号
                    System.out.println("sim卡序列号：" + simSerialMember);

                    mPref.edit().putString("sim", simSerialMember).commit();//将sim卡序列号保存在sp中。
                }
            }
        });
    }

    /**
     * 展示上一页
     */
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入动画和退出动画
    }

    /**
     * 展示下一页
     */
    public void showNextPage() {
        //如果sim卡没有绑定，就不允许进入下一个页面。
        String sim = mPref.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            ToastUtils.showToast(this, "必须绑定sim卡!");
            return;
        }

        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }
}
