package cn.qzlyhua.assistant.service.impl;

import cn.hutool.json.JSONUtil;
import cn.qzlyhua.assistant.dto.specification.Chapter;
import cn.qzlyhua.assistant.dto.specification.Parameter;
import cn.qzlyhua.assistant.dto.specification.Service;
import cn.qzlyhua.assistant.service.SpecificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanghua
 */
@org.springframework.stereotype.Service
public class SpecificationServiceImpl implements SpecificationService {
    @Override
    public List<Chapter> getSpecifications() {
        Chapter c1 = Chapter.builder()
                .headWord("家庭档案")
                .services(new ArrayList<Service>() {{
                    add(renderService());
                    add(renderService());
                }}).build();

        Chapter c2 = Chapter.builder()
                .headWord("个人档案")
                .services(new ArrayList<Service>() {{
                    add(renderService());
                    add(renderService());
                }}).build();

        return new ArrayList<Chapter>() {{
            add(c1);
            add(c2);
        }};
    }

    /**
     * TODO 获取服务传输规范文档
     *
     * @return
     */
    private Service renderService() {
        Parameter parameter1 = Parameter.builder()
                .key("IDType")
                .des("证件类别")
                .type("字符串")
                .isRequired("Y").build();

        Parameter parameter2 = Parameter.builder()
                .key("IDNO")
                .des("证件号码")
                .type("字符串")
                .isRequired("Y").build();

        Parameter parameter3 = Parameter.builder()
                .key("id")
                .des("记录主键")
                .type("字符串")
                .isRequired("Y").build();

        Service service = Service.builder()
                .serviceName("getAbcByDef")
                .serviceNick("示例方法")
                .description("根据DEF获取ABC")
                .explain("该接口为示例接口，无实际作用")
                .reqParameters(new ArrayList<Parameter>() {{
                    add(parameter1);
                    add(parameter2);
                }})
                .reqExample(JSONUtil.formatJsonStr("{\"IDNO\":\"123\"}"))
                .resParameters(new ArrayList<Parameter>() {{
                    add(parameter3);
                }})
                .resExample(JSONUtil.formatJsonStr("{\"code\":200,\"message\":\"Success\",\"result\":\"{\"id\":\"123\"}\"}")).build();

        return service;
    }
}
