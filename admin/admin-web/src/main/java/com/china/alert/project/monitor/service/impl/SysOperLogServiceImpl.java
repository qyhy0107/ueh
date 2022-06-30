package com.china.alert.project.monitor.service.impl;

import java.util.List;

import com.china.alert.project.monitor.domain.SysOperLog;
import com.china.alert.project.monitor.mapper.SysOperLogMapper;
import com.china.alert.project.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.china.alert.project.monitor.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 *
 * @author jz
 */
@Service
public class SysOperLogServiceImpl implements ISysOperLogService {
    @Autowired
    private SysOperLogMapper operLogMapper;

    @Autowired
    private ISysUserService iSysUserService;
    /**
     * 新增操作日志
     *
     * @param operLog 操作日志对象
     */
    @Override
    public void insertOperlog(SysOperLog operLog) {
//        String idCard=operLog.getOperName();
//        int optionType=operLog.getOperatorType();
//        int userDepartID=2;
//        String userDepartName="研发部";
//        String params=operLog.getOperParam();;
//        int number=blackBoxService.BlackBoxSendData(idCard,optionType,userDepartID,userDepartName,params);
//        String idCard, int optionType, int userDepartID, String userDepartName, String params
        int flag=0;
        flag= operLogMapper.insertOperlog(operLog);
    }

    /**
     * 查询系统操作日志集合
     *
     * @param operLog 操作日志对象
     * @return 操作日志集合
     */
    @Override
    public List<SysOperLog> selectOperLogList(SysOperLog operLog) {
        return operLogMapper.selectOperLogList(operLog);
    }

    /**
     * 批量删除系统操作日志
     *
     * @param operIds 需要删除的操作日志ID
     * @return 结果
     */
    public int deleteOperLogByIds(Long[] operIds) {
        return operLogMapper.deleteOperLogByIds(operIds);
    }

    /**
     * 查询操作日志详细
     *
     * @param operId 操作ID
     * @return 操作日志对象
     */
    @Override
    public SysOperLog selectOperLogById(Long operId) {
        return operLogMapper.selectOperLogById(operId);
    }

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog() {
        operLogMapper.cleanOperLog();
    }
}
