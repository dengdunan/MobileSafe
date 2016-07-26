package mobilesafe.dda.com.activity;

import android.content.Context;
import android.test.AndroidTestCase;

import com.dda.mobilesafe.bean.BlackNumberInfo;
import com.dda.mobilesafe.db.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by nuo on 2016/4/17.
 */
public class TestBlackNumberDao extends AndroidTestCase {

    public Context context;

    @Override
    protected void setUp() throws Exception {
        this.mContext = getContext();
        super.setUp();
    }

    public void testAdd() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            Long number = 13300000000l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));
        }
    }

    public void testDelete() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("13300000000");
        assertEquals(true, delete);
    }

    public void testFind() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String number = dao.findNumber("13300000001");
        System.out.println(number);
    }

    public void testFindAll() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackNumberInfo> blackNumberInfos = dao.findAll();
        for (BlackNumberInfo blackNumberInfo : blackNumberInfos) {
            System.out.println(blackNumberInfo.getMode() + "" + blackNumberInfo.getNumber());
        }
    }
}
