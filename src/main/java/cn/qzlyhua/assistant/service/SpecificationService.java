package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.specification.Chapter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author yanghua
 */
public interface SpecificationService {
    List<Chapter> getSpecificationsByVersion(String version);

    List<Chapter> getSpecificationsByBusinessArea(String areaName);

    void importSpecificationsFromWord(File file, String version) throws IOException;

    void importSpecificationsFromWord(MultipartFile file, String version) throws IOException;
}
