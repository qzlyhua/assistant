package cn.qzlyhua.assistant.util.word;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 传输规范文档-基于按版本迭代传输规范word文件
 *
 * @author yanghua
 */
@Data
@AllArgsConstructor
public class CsrBook {
    List<TransmissionSpecification> transmissionSpecifications;
    List<Dictionary> dictionaries;
}
