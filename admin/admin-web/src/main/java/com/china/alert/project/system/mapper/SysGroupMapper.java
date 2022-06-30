package com.china.alert.project.system.mapper;

import com.china.alert.project.system.domain.SysGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: weijingyu
 * @description: 参数分组
 * @date: create in 2019/12/5 0005 16:26
 */
@Mapper
public interface SysGroupMapper {

    /**
     * 查询参数分组
     *
     * @param group 参数分组信息
     * @return 参数分组
     */
    public SysGroup selectGroup(SysGroup group);

    /**
     * 查询参数分组配置列表
     *
     * @param group 参数配置信息
     * @return 参数配置集合
     */
    public List<SysGroup> selectGroupList(SysGroup group);

    /**
     * 根据键名查询参数分组信息
     *
     * @param groupName 分组名
     * @return 参数配置信息
     */
    public SysGroup checkGroupKeyUnique(String groupName);

    /**
     * 新增参数分组
     *
     * @param group 参数分组信息
     * @return 结果
     */
    public int insertGroup(SysGroup group);

    /**
     * 修改参数分组
     *
     * @param group 参数配置信息
     * @return 结果
     */
    public int updateGroup(SysGroup group);

    /**
     * 删除参数分组
     *
     * @param groupId 参数ID
     * @return 结果
     */
    public int deleteGroupById(Long groupId);

    /**
     * 批量删除参数分组
     *
     * @param groupIds 需要删除的参数ID
     * @return 结果
     */
    public int deleteGroupByIds(Long[] groupIds);
}
