package com.china.alert.project.operation.mapper;

import com.china.alert.project.operation.domain.ProjectInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: weijingyu
 * @description: 项目信息
 * @date: create in 2019/9/25 0025 10:18
 */
@Mapper
public interface ProjectInfoMapper {

    /**
     * 查询项目信息
     */
    public ProjectInfoEntity selectProjectInfo(ProjectInfoEntity projectInfo);
}
