package com.china.alert.project.operation.service.impl;

import com.china.alert.project.operation.domain.ProjectInfoEntity;
import com.china.alert.project.operation.mapper.ProjectInfoMapper;
import com.china.alert.project.operation.service.ProjectInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: weijingyu
 * @description:
 * @date: create in 2020/3/9 0009 10:11
 */
@Service
public class ProjectInfoServiceImpl implements ProjectInfoService {
    @Autowired
    ProjectInfoMapper projectInfoMapper;
    @Override
    public ProjectInfoEntity selectProjectInfo(ProjectInfoEntity projectInfo) {
        return projectInfoMapper.selectProjectInfo(projectInfo);
    }
}
