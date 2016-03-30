package mobilesafe.dda.com.activity;

import android.content.Intent;
import android.os.Bundle;

/**
 * 第二步设置向导页面
 * Created by nuo on 2016/3/28.
 */
public class Setup2Activity extends BaseSetupActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
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
        startActivity(new Intent(this, Setup3Activity.class));
        finish();

        //两个界面切换的动画
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);//进入动画和退出动画
    }
}
