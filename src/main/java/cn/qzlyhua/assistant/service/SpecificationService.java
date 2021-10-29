package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.specification.Chapter;

import java.io.File;
import java.util.List;

/**
 * @author yanghua
 */
public interface SpecificationService {
    List<Chapter> getSpecifications();

    void importSpecificationsFromWord(File word, String version);
}
