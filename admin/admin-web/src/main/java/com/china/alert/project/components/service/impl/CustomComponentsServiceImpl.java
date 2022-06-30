package com.china.alert.project.components.service.impl;

import com.china.alert.project.components.service.ICustomComponentsService;
import com.china.alert.common.utils.IdUtils;
import com.china.alert.common.utils.StringUtils;
import com.china.alert.project.components.domain.CustomComponents;
import com.china.alert.project.components.domain.PageComponents;
import com.china.alert.project.components.domain.VO.CustomComponentsVO;
import com.china.alert.project.components.mapper.CustomComponentsMapper;
import com.china.alert.project.components.mapper.PageComponentsMapper;
import com.china.alert.project.components.mapper.TextComponentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomComponentsServiceImpl implements ICustomComponentsService {

    @Autowired
    private CustomComponentsMapper customComponentsMapper;

    @Autowired
    private TextComponentsMapper textComponentsMapper;

    @Autowired
    private PageComponentsMapper pageComponentsMapper;

    @Override
    public CustomComponents selectCustomComponents(String componentsId) {
        return customComponentsMapper.selectCustomComponents(componentsId);
    }

    @Override
    public List<CustomComponentsVO> selectCustomComponentsList() {
        List<CustomComponentsVO> CustomComponentsVOList = new ArrayList<CustomComponentsVO>();
        List<CustomComponents> customComponentsList = customComponentsMapper.selectCustomComponentsList();
        for (CustomComponents customComponents : customComponentsList) {//其内部实质上还是调用了迭代器遍历方式，这种循环方式还有其他限制，不建议使用。
            CustomComponentsVO customComponentsVO = new CustomComponentsVO();
            customComponentsVO.setComponentsId(customComponents.getComponentsId());
            customComponentsVO.setComponentsName(customComponents.getComponentsName());
            customComponentsVO.setComponentsSize(customComponents.getComponentsSize());
            if(StringUtils.isEmpty(pageComponentsMapper.selectPageComponentsByComponentsId(customComponents.getComponentsId()))){
                int componentsCout = customComponents.getComponentsCount();
                if (!StringUtils.isEmpty(componentsCout + "")) {
                    for (int i = 0; i < componentsCout; i++) {//内部不锁定，效率最高，但在多线程要考虑并发操作的问题。
                        PageComponents pageView = new PageComponents();
                        pageView.setId(IdUtils.randomUUID());
                        pageView.setComponentsId(customComponents.getComponentsId());
                        pageView.setComponentsName(customComponents.getComponentsName());
                        pageComponentsMapper.insertPageComponents(pageView);
                    }
                }
            }
            customComponentsVO.setData(pageComponentsMapper.selectPageComponentsByComponentsId(customComponents.getComponentsId()));
            CustomComponentsVOList.add(customComponentsVO);
        }
        return CustomComponentsVOList;
    }

    @Override
    public List<CustomComponentsVO> selectCustomComponentsListByName(String componentsName) {
        List<CustomComponentsVO> CustomComponentsVOList = new ArrayList<CustomComponentsVO>();
        List<CustomComponents> customComponentsList = customComponentsMapper.selectCustomComponentsListByName(componentsName);
        for (CustomComponents customComponents : customComponentsList) {//其内部实质上还是调用了迭代器遍历方式，这种循环方式还有其他限制，不建议使用。
            CustomComponentsVO customComponentsVO = new CustomComponentsVO();
            customComponentsVO.setComponentsId(customComponents.getComponentsId());
            customComponentsVO.setComponentsName(customComponents.getComponentsName());
            customComponentsVO.setComponentsSize(customComponents.getComponentsSize());
            if(StringUtils.isEmpty(pageComponentsMapper.selectPageComponentsByComponentsId(customComponents.getComponentsId()))){
                int componentsCout = customComponents.getComponentsCount();
                if (!StringUtils.isEmpty(componentsCout + "")) {
                    for (int i = 0; i < componentsCout; i++) {//内部不锁定，效率最高，但在多线程要考虑并发操作的问题。
                        PageComponents pageView = new PageComponents();
                        pageView.setId(IdUtils.randomUUID());
                        pageView.setComponentsId(customComponents.getComponentsId());
                        pageView.setComponentsName(customComponents.getComponentsName());
                        pageComponentsMapper.insertPageComponents(pageView);
                    }
                }
            }
            customComponentsVO.setData(pageComponentsMapper.selectPageComponentsByComponentsId(customComponents.getComponentsId()));
            CustomComponentsVOList.add(customComponentsVO);
        }
        return CustomComponentsVOList;
    }

    @Override
    public int insertCustomComponents(CustomComponents bean) {
        bean.setComponentsId(IdUtils.randomUUID());
        return customComponentsMapper.insertCustomComponents(bean);
    }

    @Override
    public int updateCustomComponents(CustomComponents bean) {
        return customComponentsMapper.updateCustomComponents(bean);
    }

    @Override
    public int deleteCustomComponents(String componentsId) {
        return customComponentsMapper.deleteCustomComponents(componentsId);
    }

    @Override
    public int deleteCustomComponentsByIds(String[] componentsIds) {
        return customComponentsMapper.deleteCustomComponentsByIds(componentsIds);
    }

    @Override
    public List<CustomComponents> selectCustomComponentsCount() {
        return customComponentsMapper.selectCustomComponentsCount();
    }

    @Override
    public List<CustomComponents> selectCustomComponentsCountBycomponentsName(String componentsName) {
        return customComponentsMapper.selectCustomComponentsCountBycomponentsName(componentsName);
    }

    @Override
    public int deleteCustomComponentsBycomponentsName(String componentsName) {
        return customComponentsMapper.deleteCustomComponentsBycomponentsName(componentsName);
    }

    @Override
    public List selectColumnComment() {
        return customComponentsMapper.selectColumnComment();
    }

    @Override
    public int updateCustomComponentsByComponentsName(CustomComponents bean) {
        return customComponentsMapper.updateCustomComponentsByComponentsName(bean);
    }

}
