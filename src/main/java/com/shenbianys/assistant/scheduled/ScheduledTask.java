package com.shenbianys.assistant.scheduled;

import com.shenbianys.assistant.async.LogStatisticsTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
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
    public void statisticsTask() throws InvocationTargetException, IntrospectionException, ParseException, IllegalAccessException {
        String day = getDay(-1);
        logStatisticsTask.doStatistics("pro", day);
    }

    @Scheduled(cron = "5 5 0 * * ?")
    public void clearTask() {
        String day = getDay(-7);
        logStatisticsTask.clearStatisticsData("dev", day);
    }

    private String getDay(int add) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, add);
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(date);
        return day;
    }
}
