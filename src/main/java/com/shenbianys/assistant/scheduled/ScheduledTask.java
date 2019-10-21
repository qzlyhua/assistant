package com.shenbianys.assistant.scheduled;

import com.shenbianys.assistant.async.LogStatisticsTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Yang Hua
 */
@Component
@Slf4j
public class ScheduledTask {
    @Autowired
    LogStatisticsTask logStatisticsTask;

    @Scheduled(cron = "6 6 0 * * ?")
    public void inhabitantDoctorWxTask() {
        log.info("定时统计日志任务开始...");

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -1);
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(date);
        try {
            logStatisticsTask.doStatistics("pro", day);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void main(String[] args) {
        inhabitantDoctorWxTask();
    }
}
