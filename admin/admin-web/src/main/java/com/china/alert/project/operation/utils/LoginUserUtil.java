package com.china.alert.project.operation.utils;

import com.china.alert.project.operation.domain.ProjectInfoEntity;
import com.china.alert.project.system.domain.SysDept;
import com.china.alert.project.system.service.ISysDeptService;
import com.china.alert.common.utils.ServletUtils;
import com.china.alert.common.utils.spring.SpringUtils;
import com.china.alert.framework.security.LoginUser;
import com.china.alert.framework.security.service.TokenService;
import com.china.alert.project.operation.service.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author: weijingyu
 * @description:
 * @date: create in 2020/3/3 0003 21:58
 */
@Component
public class LoginUserUtil {

    @Autowired
    ISysDeptService sysDeptService;

    @Autowired
    ProjectInfoService projectInfoService;

    static LoginUserUtil loginUserUtil;

    @PostConstruct
    public void init() {
        loginUserUtil = this;
    }
    public static int getProjectId(){
        try{
            // 获取当前的用户
            LoginUser loginUser = SpringUtils.getBean(TokenService.class).getLoginUser(ServletUtils.getRequest());
            SysDept dept = loginUser.getUser().getDept();
            SysDept sysDept = loginUserUtil.sysDeptService.selectDeptById(dept.getDeptId());
            if(sysDept.getParentId()==0){
                int projectInfoId = getProjectInfoId(Integer.parseInt(String.valueOf(sysDept.getDeptId())));
                return projectInfoId;
            } else {
                String[] split = sysDept.getAncestors().split(",");
                int projectInfoId = getProjectInfoId(Integer.parseInt(split[1]));
                return projectInfoId;
            }
        }catch (Exception e){
            return 10;//配置默认项目ID
        }
    }

    public static int getProjectInfoId(int deptId){
        ProjectInfoEntity projectInfo = new ProjectInfoEntity();
        projectInfo.setSysDeptId(deptId);
        ProjectInfoEntity info = loginUserUtil.projectInfoService.selectProjectInfo(projectInfo);
        return info.getId();
    }

}
