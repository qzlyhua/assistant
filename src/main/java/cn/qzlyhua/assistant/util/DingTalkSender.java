package cn.qzlyhua.assistant.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author yanghua
 */
@Slf4j
public class DingTalkSender {
    private static final ThreadLocal<ExecutorService> executorService = ThreadLocal.withInitial(Executors::newSingleThreadExecutor);

    public static void sendDingAlarm(String webhookUrl, String title, String markDownText) {
        executorService.get().execute(() -> {
            AlarmDto.AtBean atBean = new AlarmDto.AtBean();
            atBean.setIsAtAll(true);

            AlarmDto.MarkdownBean markdownBean = new AlarmDto.MarkdownBean();
            markdownBean.setTitle(title);
            markdownBean.setText(markDownText);

            AlarmDto alarmDto = new AlarmDto();
            alarmDto.setAt(atBean);
            alarmDto.setMsgtype("markdown");
            alarmDto.setMarkdown(markdownBean);

            HttpRequest httpRequest = HttpRequest.post(webhookUrl);
            httpRequest.body(JSONUtil.toJsonStr(alarmDto));
            HttpResponse httpResponse = httpRequest.timeout(3000).execute();
            log.info("发送钉钉机器人消息完成：【{}】{}", httpResponse.getStatus(), httpResponse.body());
        });
    }

    @NoArgsConstructor
    @Data
    private static class AlarmDto {
        /**
         * msgtype : markdown
         * markdown : {"title":"杭州天气","text":"#### 杭州天气 @150XXXXXXXX \n> 9度，西北风1级，空气良89，相对温度73%\n> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n> ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"}
         * at : {"atMobiles":["150XXXXXXXX"],"isAtAll":false}
         */

        private String msgtype;
        private MarkdownBean markdown;
        private AtBean at;

        @NoArgsConstructor
        @Data
        public static class MarkdownBean {
            /**
             * title : 杭州天气
             * text : #### 杭州天气 @150XXXXXXXX
             * > 9度，西北风1级，空气良89，相对温度73%
             * > ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)
             * > ###### 10点20分发布 [天气](https://www.dingtalk.com)
             */
            private String title;
            private String text;
        }

        @NoArgsConstructor
        @Data
        public static class AtBean {
            /**
             * atMobiles : ["150XXXXXXXX"]
             * isAtAll : false
             */
            private Boolean isAtAll;
            private List<String> atMobiles;
        }
    }
}
