package com.china.alert.project.components.service.impl;

import com.china.alert.project.components.service.ITextComponentsService;
import com.china.alert.project.components.domain.CustomComponents;
import com.china.alert.project.components.domain.TextComponents;
import com.china.alert.project.components.mapper.CustomComponentsMapper;
import com.china.alert.project.components.mapper.TextComponentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ITextComponentsServiceImpl implements ITextComponentsService {


    @Autowired
    private TextComponentsMapper textComponentsMapper;

    @Autowired
    private CustomComponentsMapper customComponentsMapper;

    @Override
    public TextComponents selectTextComponents(String id) {
        return textComponentsMapper.selectTextComponents(id);
    }

    @Override
    public List<TextComponents> selectTextComponentsByComponentsName(String componentsName) {
        return textComponentsMapper.selectTextComponentsByComponentsName(componentsName);
    }

    @Override
    public int insertTextComponents(TextComponents bean) {
        return textComponentsMapper.insertTextComponents(bean);
    }

    @Override
    public int updateTextComponents(TextComponents bean) {
        if(textComponentsMapper.selectTextComponents(bean.getId())==null){
            TextComponents textComponents = new TextComponents();
            textComponents.setId(bean.getId());
            textComponentsMapper.insertTextComponents(textComponents);
        }
        return textComponentsMapper.updateTextComponents(bean);
    }

    @Override
    public int deleteTextComponents(String id) {
        TextComponents textComponents = textComponentsMapper.selectTextComponents(id);
        textComponentsMapper.deleteTextComponents(id);
        CustomComponents customComponents = customComponentsMapper.selectCustomComponents(textComponents.getComponentsId());
        customComponents.setComponentsCount(customComponents.getComponentsCount()-1);
        customComponentsMapper.updateCustomComponents(customComponents);
        return 1;
    }
}
