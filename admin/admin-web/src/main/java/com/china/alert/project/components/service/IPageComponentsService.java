package com.china.alert.project.components.service;

import com.china.alert.project.components.domain.PageComponents;

import java.util.List;

public interface IPageComponentsService {

    /**
     * 查询组件信息
     *
     * @param id 组件id
     * @return 组件信息
     */
    public PageComponents selectPageComponents(String id);

    /**
     * 根据组件的名字查询组件数据
     *
     * @return 组件集合
     */
    public List<PageComponents> selectPageComponentsByComponentsName(String componentsName);

    /**
     * 根据组件的ID查询组件数据
     *
     * @return 组件集合
     */
    public List<PageComponents> selectPageComponentsByComponentsId(String componentsId);

    /**
     * 新增组件信息
     *
     * @param bean 组件信息
     * @return 结果
     */
    public int insertPageComponents(PageComponents bean);

    /**
     * 修改组件
     *
     * @param bean 组件信息
     * @return 结果
     */
    public int updatePageComponents(PageComponents bean);

    /**
     * 删除组件
     *
     * @param id 组件ID
     * @return 结果
     */
    public int deletePageComponents(String id);

}
