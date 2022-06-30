package com.chinaalert.event.transmit.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.chinaalert.event.common.utils.FastJsonUtils;
import com.chinaalert.event.transmit.domain.MediaInfo;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;

@Component
public class MediaTypeConfig {
	@Autowired
	DataSource dataSource;
	
	public MediaInfo getSendServiceInfo(long projectId, String type){
		MediaInfo column=null;
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("mediaType", type);
		filter.put("isEnable", "Y");
		DataLoadParams params=new DataLoadParams();
		params.setProjectId(projectId);
		params.setDcName("notifySettingSelectOne");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(0);
		//System.out.println(new Date());
		ResultPattern result= DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			column= (MediaInfo) FastJsonUtils.convertJSONToObject(FastJsonUtils.convertObjectToJSON(result.getMapData()),MediaInfo.class);
		}
		return column;
	}
	public List<MediaInfo> getSendServiceInfo(long projectId, Map<String,Object> conditions){
		List<MediaInfo> columns=null;
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("mediaTypes", conditions.get("mediaTypes"));
		filter.put("isEnable", "Y");
		DataLoadParams params=new DataLoadParams();
		params.setProjectId(projectId);
		params.setDcName("notifySettingSelect");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(0);
		//System.out.println(new Date());
		ResultPattern result= DataServiceUtils.dataLoad(dataSource, params);
		if(result.isSuccess()&&!result.isEmpty()){
			columns=(List<MediaInfo>) FastJsonUtils.toList(FastJsonUtils.convertObjectToJSON(result.getDatas()),MediaInfo.class);
		}
		return columns;
	}
}
