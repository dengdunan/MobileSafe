package mobilesafe.dda.com.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dda.mobilesafe.db.AddressDao;

/**
 * 归属地查询页面
 * Created by nuo on 2016/4/9.
 */
public class AddressActivity extends Activity {

    private EditText etNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        etNumber = (EditText) findViewById(R.id.et_number);
        tvResult = (TextView) findViewById(R.id.tv_result);

        etNumber.addTextChangedListener(new TextWatcher() {

            //文字发生变化时之前的回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //发生变化时的回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String address = AddressDao.getAddress(s.toString());
                tvResult.setText(address);
            }

            //变化之后的回调
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 开始查询
     *
     * @param view
     */
    public void query(View view) {
        String number = etNumber.getText().toString().trim();

        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        }
    }
}
