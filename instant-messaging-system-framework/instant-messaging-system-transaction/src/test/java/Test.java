import com.youmin.imsystem.common.utils.JsonUtils;
import javafx.util.Pair;

import java.util.ArrayList;

public class Test {
    public static void main(String[] args) {
        ArrayList<Pair<Integer,String>> integers = new ArrayList<>();
        integers.add(new Pair<>(1,"s"));
        System.out.println(JsonUtils.toStr(integers));
    }
}
