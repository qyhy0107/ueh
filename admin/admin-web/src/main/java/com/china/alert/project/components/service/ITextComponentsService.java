package com.china.alert.project.components.service;

import com.china.alert.project.components.domain.TextComponents;

import java.util.List;

public interface ITextComponentsService {

    /**
     * 查询文字组件信息
     *
     * @param id 文字组件id
     * @return 文字组件信息
     */
    public TextComponents selectTextComponents(String id);

    /**
     * 根据组件的名字查询文字组件数据
     *
     * @return 文字组件集合
     */
    public List<TextComponents> selectTextComponentsByComponentsName(String componentsName);

    /**
     * 新增文字组件信息
     *
     * @param bean 文字组件信息
     * @return 结果
     */
    public int insertTextComponents(TextComponents bean);

    /**
     * 修改文字组件
     *
     * @param bean 文字组件信息
     * @return 结果
     */
    public int updateTextComponents(TextComponents bean);

    /**
     * 删除文字组件
     *
     * @param id 组件ID
     * @return 结果
     */
    public int deleteTextComponents(String id);
}
