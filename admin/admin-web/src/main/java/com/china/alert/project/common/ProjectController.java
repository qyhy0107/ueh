package com.china.alert.project.common;

import com.china.alert.framework.web.domain.AjaxResult;
import com.china.alert.project.operation.utils.LoginUserUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: weijingyu
 * @description: 公共controller用来获取项目名称或信息
 * @date: create in 2020/3/9 0009 21:30
 */
@RestController
public class ProjectController {

    @GetMapping("/getProjectId")
    public AjaxResult getProjectId() {
        return AjaxResult.success(LoginUserUtil.getProjectId());
    }

}
