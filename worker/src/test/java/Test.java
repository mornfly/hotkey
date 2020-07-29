import com.jd.platform.hotkey.common.model.HotKeyModel;
import com.jd.platform.hotkey.common.model.HotKeyMsg;
import com.jd.platform.hotkey.common.tool.FastJsonUtils;
import com.jd.platform.hotkey.worker.tool.ProtostuffUtils;

/**
 * @author wuweifeng
 * @version 1.0
 * @date 2020-07-28
 */
public class Test {
    public static void main(String[] args) {
        HotKeyMsg hotKeyMsg = new HotKeyMsg();
        hotKeyMsg.setAppName("cartsoa");
        HotKeyModel hotKeyModel = new HotKeyModel();
        hotKeyModel.setCount(1);
        hotKeyModel.setKey("pin_xx");
        hotKeyModel.setAppName("cartsoa");
        hotKeyMsg.setBody(FastJsonUtils.convertObjectToJSON(hotKeyModel));

        byte[] serialize = ProtostuffUtils.serialize(hotKeyMsg);
        String msg = FastJsonUtils.convertObjectToJSON(hotKeyMsg);

        long time1 = System.currentTimeMillis();
        for (int i = 0; i < 300000; i++) {
            HotKeyMsg hhh = ProtostuffUtils.deserialize(serialize, HotKeyMsg.class);
        }
        System.out.println(System.currentTimeMillis() - time1);

        long time = System.currentTimeMillis();
        for (int i = 0; i < 300000; i++) {
            HotKeyMsg hhh = FastJsonUtils.toBean(msg, HotKeyMsg.class);
        }
        System.out.println(System.currentTimeMillis() - time);


    }
}
