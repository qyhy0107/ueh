package com.china.alert.project.components.mapper;

import com.china.alert.project.components.domain.TextComponents;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 文字组件 数据层
 *
 * @author like
 */
@Mapper
public interface TextComponentsMapper {
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
     * 根据组件的ID查询文字组件数据
     *
     * @return 文字组件集合
     */
    public List<TextComponents> selectTextComponentsByComponentsId(String componentsId);

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

//    /**
//     * 批量删除文字组件信息
//     *
//     * @param componentsIds 需要删除的组件ID
//     * @return 结果
//     */
//    public int deleteCustomComponentsByIds(String[] componentsIds);
//
//
//    /**
//     * 根据组件名删除文字组件
//     *
//     * @param componentsName 组件ID
//     * @return 结果
//     */
//    public int deleteCustomComponentsBycomponentsName(@Param("componentsName") String componentsName);

}
