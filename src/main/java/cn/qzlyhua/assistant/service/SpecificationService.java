package cn.qzlyhua.assistant.service;

import cn.qzlyhua.assistant.dto.csr.message.NoticeForChange;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.dto.specification.DictionaryTable;
import cn.qzlyhua.assistant.entity.ApiCsr;
import cn.qzlyhua.assistant.entity.ApiCsrParam;
import com.deepoove.poi.data.RowRenderData;
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

    void publishMarkDownFilesOfChangelog(NoticeForChange noticeForChange);

    void deleteAllByVersion(String version);

    List<ApiCsr> getApiCsrsByVersion(String version);

    List<ApiCsrParam> getApiCsrParamsByVersion(String version);

    NoticeForChange getNoticeForChange(String version, List<ApiCsr> origApiCsrs, List<ApiCsrParam> origApiCsrParams);

    List<DictionaryTable> getCsrDictionariesFromChapters(List<Chapter> chapters);
}
