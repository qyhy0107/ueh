package com.china.alert.project.components.service;

import com.china.alert.project.components.domain.CustomComponents;
import com.china.alert.project.components.domain.VO.CustomComponentsVO;

import java.util.List;

public interface ICustomComponentsService {
    /**
     * 查询组件定制化信息
     *
     * @param componentsId 组件定制化id
     * @return 组件定制化信息
     */
    public CustomComponents selectCustomComponents(String componentsId);

    /**
     * 查询组件定制化数据
     *
     * @return 组件定制化集合
     */
    public List<CustomComponentsVO> selectCustomComponentsList();

    /**
     * 查询组件定制化数据
     *
     * @return 组件定制化集合
     */
    public List<CustomComponentsVO> selectCustomComponentsListByName(String componentsName);


    /**
     * 新增组件定制化信息
     *
     * @param bean 组件定制化信息
     * @return 结果
     */
    public int insertCustomComponents(CustomComponents bean);

    /**
     * 修改组件定制化
     *
     * @param config 组件定制化信息
     * @return 结果
     */
    public int updateCustomComponents(CustomComponents config);

    /**
     * 删除组件定制化
     *
     * @param componentsId 组件ID
     * @return 结果
     */
    public int deleteCustomComponents(String componentsId);

    /**
     * 批量删除组件定制化信息
     *
     * @param componentsIds 需要删除的组件ID
     * @return 结果
     */
    public int deleteCustomComponentsByIds(String[] componentsIds);

    /**
     * 查询组件定制化数据的统计
     *
     * @return 组件定制化集合的统计
     */
    public List<CustomComponents> selectCustomComponentsCount();

    /**
     * 根据组件的名字查询组件定制化数据的统计
     *
     * @return 组件定制化集合的统计
     */
    public List<CustomComponents> selectCustomComponentsCountBycomponentsName(String componentsName);

    /**
     * 根据组件名删除组件定制化
     *
     * @param componentsName 组件ID
     * @return 结果
     */
    public int deleteCustomComponentsBycomponentsName(String componentsName);

    /**
     * 查询组件定制化数据(数据集测试)
     *
     * @return 组件定制化集合
     */
    public List selectColumnComment();

    /**
     * 修改组件定制化的发布状态
     *
     * @param bean 组件定制化信息
     * @return 结果
     */
    public int updateCustomComponentsByComponentsName(CustomComponents bean);

}
