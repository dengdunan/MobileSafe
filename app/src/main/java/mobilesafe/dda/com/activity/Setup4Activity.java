package mobilesafe.dda.com.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * 第二步设置向导页面
 * Created by nuo on 2016/3/28.
 */
public class Setup4Activity extends BaseSetupActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
    }

    @Override
    public void showPreviousPage() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);//进入动画和退出动画
    }

    @Override
    public void showNextPage() {
        mPref.edit().putBoolean("configed", true).commit();//更新sp，表示已经展示过设置向导了，西祠进来，不需要展示

        startActivity(new Intent(this, LostFindActivity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }
}
