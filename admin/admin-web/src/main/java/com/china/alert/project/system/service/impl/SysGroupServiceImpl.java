package com.china.alert.project.system.service.impl;

import com.china.alert.common.constant.UserConstants;
import com.china.alert.common.utils.StringUtils;
import com.china.alert.project.system.domain.SysGroup;
import com.china.alert.project.system.mapper.SysGroupMapper;
import com.china.alert.project.system.service.ISysGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: weijingyu
 * @description:
 * @date: create in 2019/12/5 0005 17:58
 */
@Service
public class SysGroupServiceImpl implements ISysGroupService {

    @Autowired
    SysGroupMapper sysGroupMapper;

    @Override
    public SysGroup selectGroupById(Long groupId) {
        SysGroup sysGroup = new SysGroup();
        sysGroup.setGroupId(groupId);
        return sysGroupMapper.selectGroup(sysGroup);
    }

    @Override
    public Object selectGroupByKey(String groupName) {
        SysGroup sysGroup = new SysGroup();
        sysGroup.setGroupName(groupName);
        SysGroup sysGroup1 = sysGroupMapper.selectGroup(sysGroup);
        return StringUtils.isNotNull(sysGroup1) ? sysGroup1.getGroupName() : "";
    }

    @Override
    public List<SysGroup> selectGroupList(SysGroup group) {
        return sysGroupMapper.selectGroupList(group);
    }

    @Override
    public int insertGroup(SysGroup group) {
        return sysGroupMapper.insertGroup(group);
    }

    @Override
    public int updateGroup(SysGroup group) {
        return sysGroupMapper.updateGroup(group);
    }

    @Override
    public int deleteGroupById(Long groupId) {
        return sysGroupMapper.deleteGroupById(groupId);
    }

    @Override
    public int deleteGroupByIds(Long[] groupIds) {
        return sysGroupMapper.deleteGroupByIds(groupIds);
    }

    @Override
    public String checkGroupKeyUnique(SysGroup group) {
        Long groupId = StringUtils.isNull(group.getGroupId()) ? -1L : group.getGroupId();
        SysGroup sysGroup = sysGroupMapper.checkGroupKeyUnique(group.getGroupName());
        if (StringUtils.isNotNull(sysGroup) && sysGroup.getGroupId().longValue() != groupId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }
}
