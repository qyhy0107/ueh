package com.china.alert.project.components.service.impl;

import com.china.alert.project.components.service.IPageComponentsService;
import com.china.alert.project.components.domain.CustomComponents;
import com.china.alert.project.components.domain.PageComponents;
import com.china.alert.project.components.mapper.CustomComponentsMapper;
import com.china.alert.project.components.mapper.PageComponentsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageComponentsServiceImpl  implements IPageComponentsService {

    @Autowired
    private PageComponentsMapper pageComponentsMapper;

    @Autowired
    private CustomComponentsMapper customComponentsMapper;

    @Override
    public PageComponents selectPageComponents(String id) {
        return pageComponentsMapper.selectPageComponents(id);
    }

    @Override
    public List<PageComponents> selectPageComponentsByComponentsName(String componentsName) {
        return pageComponentsMapper.selectPageComponentsByComponentsName(componentsName);
    }

    @Override
    public List<PageComponents> selectPageComponentsByComponentsId(String componentsId) {
        return pageComponentsMapper.selectPageComponentsByComponentsId(componentsId);
    }

    @Override
    public int insertPageComponents(PageComponents bean) {
        return pageComponentsMapper.insertPageComponents(bean);
    }

    @Override
    public int updatePageComponents(PageComponents bean) {
        if(pageComponentsMapper.selectPageComponents(bean.getId())==null){
            PageComponents textComponents = new PageComponents();
            textComponents.setId(bean.getId());
            pageComponentsMapper.insertPageComponents(textComponents);
        }
        return pageComponentsMapper.updatePageComponents(bean);
    }

    @Override
    public int deletePageComponents(String id) {
        PageComponents textComponents = pageComponentsMapper.selectPageComponents(id);
        pageComponentsMapper.deletePageComponents(id);
        CustomComponents customComponents = customComponentsMapper.selectCustomComponents(textComponents.getComponentsId());
        customComponents.setComponentsCount(customComponents.getComponentsCount()-1);
        customComponentsMapper.updateCustomComponents(customComponents);
        return 1;
    }
}
