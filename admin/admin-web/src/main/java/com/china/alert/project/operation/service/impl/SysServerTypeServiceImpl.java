package com.china.alert.project.operation.service.impl;

import com.china.alert.project.operation.domain.SysServerType;
import com.china.alert.project.operation.mapper.SysServerTypeMapper;
import com.china.alert.project.operation.service.ISysServerTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 状态监控-服务器
 *
 * @author yht
 */
@Service
public class SysServerTypeServiceImpl implements ISysServerTypeService {

    @Autowired
    private SysServerTypeMapper sysServerTypeMapper;

    @Override
    public List<SysServerType> selectServerList(String id) {
        return sysServerTypeMapper.selectServerList(id);
    }
}
