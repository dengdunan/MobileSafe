package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by nuo on 2016/6/29.
 * Created by 15:29.
 * 描述:
 */
public class TrafficManagerActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();

        //获取到手机的下载的流量
        //long mobileRxBytes = TrafficStats.getMobileRxBytes();
        //获取到手机的上传流量
        //long mobileTxBytes = TrafficStats.getMobileTxBytes();

    }

    private void initUI() {
        //setContentView();
    }
}
