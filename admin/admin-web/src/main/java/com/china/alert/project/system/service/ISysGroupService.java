package com.china.alert.project.system.service;

import com.china.alert.project.system.domain.SysGroup;

import java.util.List;

/**
 * @author: weijingyu
 * @description: 分组
 * @date: create in 2019/12/5 0005 17:52
 */
public interface ISysGroupService {


    /**
     * 查询参数配置信息
     *分组
     * @param groupId 参数配置ID
     * @return 参数配置信息
     */
    public SysGroup selectGroupById(Long groupId);

    /**
     * 根据键名查询参数配置信息
     *
     * @param groupName 分组名
     * @return 参数键值
     */
    public Object selectGroupByKey(String groupName);

    /**
     * 查询参数配置列表
     *分组
     * @param group 参数配置信息
     * @return 参数配置集合
     */
    public List<SysGroup> selectGroupList(SysGroup group);

    /**
     * 新增参数配置
     *分组
     * @param group 参数配置信息
     * @return 结果
     */
    public int insertGroup(SysGroup group);

    /**
     * 修改参数配置
     *分组
     * @param group 参数配置信息
     * @return 结果
     */
    public int updateGroup(SysGroup group);

    /**
     * 删除参数配置信息
     *分组
     * @param groupId 参数ID
     * @return 结果
     */
    public int deleteGroupById(Long groupId);

    /**
     * 批量删除参数信息
     *分组
     * @param groupIds 需要删除的参数ID
     * @return 结果
     */
    public int deleteGroupByIds(Long[] groupIds);

    /**
     * 校验参数键名是否唯一
     *分组
     * @param group 参数信息
     * @return 结果
     */
    public String checkGroupKeyUnique(SysGroup group);

}
