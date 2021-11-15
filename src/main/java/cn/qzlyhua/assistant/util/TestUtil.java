package cn.qzlyhua.assistant.util;

public class TestUtil {
    public static void main(String[] args) {
        String des = "人群类型（详见附件数据字典：群发人群类型）";
        String key = "字典：";
        System.out.println(des.substring(des.indexOf(key) + key.length(), des.indexOf("）")));
    }
}
