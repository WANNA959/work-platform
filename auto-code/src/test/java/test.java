import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class test {

    @Test
    public void test1(){
        List<String> names=new ArrayList<>();
        names.add("user");
        names.add("test");
        String o = JSON.toJSON(names).toString();
        log.info(names.toString());
        log.info(o);
    }
}
