package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 手机防盗页面
 * Created by nuo on 2016/3/28.
 */
public class LostFindActivity extends Activity {

    private SharedPreferences mPref;

    private TextView tv_safe_phone;
    private ImageView ivProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = mPref.getBoolean("configed", false);//判断是否进入向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);

            //根据sp更新安全号码
            tv_safe_phone = (TextView) findViewById(R.id.tv_safe_phone);

            String phone = mPref.getString("safe_phone", "");
            tv_safe_phone.setText(phone);

            //根据sp去更新保护锁
            ivProtect = (ImageView) findViewById(R.id.iv_protect);
            boolean protect = mPref.getBoolean("protect", false);

            if (protect) {
                ivProtect.setImageResource(R.drawable.lock);
            } else {
                ivProtect.setImageResource(R.drawable.unlock);
            }
        } else {
            //跳转设置向导页
            startActivity(new Intent(this, Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入设置向导
     */
    public void reEnter(View view) {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
    }
}
