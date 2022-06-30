package com.china.alert.project.operation.service.impl;

import com.china.alert.project.operation.domain.SysNetWorkType;
import com.china.alert.project.operation.mapper.SysNetWorkTypeMapper;
import com.china.alert.project.operation.service.ISysNetWorkTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysNetWorkTypeServiceImpl implements ISysNetWorkTypeService {


    @Autowired
    private SysNetWorkTypeMapper sysNetWorkTypeMapper;

    @Override
    public List<SysNetWorkType> selectNetWorkList(String id) {

        return sysNetWorkTypeMapper.selectNetWorkList(id);
    }
}
