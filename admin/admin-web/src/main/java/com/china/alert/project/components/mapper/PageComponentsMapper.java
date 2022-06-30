package com.china.alert.project.components.mapper;

import com.china.alert.project.components.domain.PageComponents;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 *  组件 数据层
 *
 * @author like
 */
@Mapper
public interface PageComponentsMapper {
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

//    /**
//     * 批量删除组件信息
//     *
//     * @param componentsIds 需要删除的组件ID
//     * @return 结果
//     */
//    public int deleteCustomComponentsByIds(String[] componentsIds);
//
//
//    /**
//     * 根据组件名删除组件
//     *
//     * @param componentsName 组件ID
//     * @return 结果
//     */
//    public int deleteCustomComponentsBycomponentsName(@Param("componentsName") String componentsName);

}
