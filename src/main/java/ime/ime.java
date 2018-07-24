package ime;

import java.util.LinkedList;

public class ime {
    public static final char[] PY_mb_a = "啊阿吖嗄腌锕呵安按爱暗埃".toCharArray();

    public static void main(String[] args) throws Exception {

        LinkedList<String> list = new LinkedList();
        long l = 10000000;
        try {
            while (l > 0) {
                list.add(l+"");
                l--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println(list.size());
        }

    }
}
