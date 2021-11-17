package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.specification.Chapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author yanghua
 */
public interface SpecificationService {
    List<Chapter> getSpecificationsByVersion(String version);

    List<Chapter> getSpecificationsByBusinessArea(String areaName);

    List<String> getSpecificationsBusinessAreaByUpdateTime(Date time);

    List<String> getSpecificationsBusinessAreaByVersion(String version);

    int importSpecificationsFromWord(MultipartFile file, String version) throws IOException;

    void publishMarkDownFilesByVersion(List<Chapter> chapters, String version);

    void publishMarkDownFilesByBusinessArea(List<Chapter> chapters, String businessArea);
}
