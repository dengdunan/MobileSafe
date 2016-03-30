package mobilesafe.dda.com.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * 第一步设置向导页面
 * Created by nuo on 2016/3/28.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPreviousPage() {}

    @Override
    public void showNextPage() {
        startActivity(new Intent(this,Setup2Activity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in,R.anim.tran_out);//进入动画和退出动画
    }
}
