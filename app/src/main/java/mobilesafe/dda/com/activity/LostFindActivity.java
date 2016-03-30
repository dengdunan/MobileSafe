package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

/**
 * 手机防盗页面
 * Created by nuo on 2016/3/28.
 */
public class LostFindActivity extends Activity {

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPref = getSharedPreferences("config", MODE_PRIVATE);
        boolean configed = mPref.getBoolean("configed",false);//判断是否进入向导
        if (configed) {
            setContentView(R.layout.activity_lost_find);
        } else {
            //跳转设置向导页
            startActivity(new Intent(this,Setup1Activity.class));
            finish();
        }
    }

    /**
     * 重新进入设置向导
     */
    public void reEnter(View view) {
        startActivity(new Intent(this,Setup1Activity.class));
        finish();
    }
}
