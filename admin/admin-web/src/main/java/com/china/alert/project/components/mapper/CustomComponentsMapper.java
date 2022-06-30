package com.china.alert.project.components.mapper;


import com.china.alert.project.components.domain.CustomComponents;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 组件定制化 数据层
 *
 * @author like
 */
@Mapper
public interface CustomComponentsMapper {
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
    public List<CustomComponents> selectCustomComponentsList();

    /**
     * 查询组件定制化数据(数据集测试)
     *
     * @return 组件定制化集合
     */
    public List selectColumnComment();


    /**
     * 根据组件的名字查询组件定制化数据
     *
     * @return 组件定制化集合
     */
    public List<CustomComponents> selectCustomComponentsListByName(String componentsName);


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
    public List<CustomComponents> selectCustomComponentsCountBycomponentsName(@Param("componentsName")String componentsName);


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
     * @param bean 组件定制化信息
     * @return 结果
     */
    public int updateCustomComponents(CustomComponents bean);

    /**
     * 修改组件定制化的发布状态
     *
     * @param bean 组件定制化信息
     * @return 结果
     */
    public int updateCustomComponentsByComponentsName(CustomComponents bean);


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
     * 根据组件名删除组件定制化
     *
     * @param componentsName 组件ID
     * @return 结果
     */
    public int deleteCustomComponentsBycomponentsName(@Param("componentsName") String componentsName);

}
