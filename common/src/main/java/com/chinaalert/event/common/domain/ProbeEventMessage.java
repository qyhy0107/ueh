package com.chinaalert.event.common.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.chinaalert.event.common.utils.FastJsonUtils;

/**
 * Probe收到监控工具的告警事件之后，对传来的消息进行包装，添加Probe信息
 * @author q
 *
 */
public class ProbeEventMessage implements Serializable{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8274208743768055006L;
	private ProbeInfo probe;
	private ProjectInfo project;
	private String messageBody;
	
	public ProbeEventMessage(ProjectInfo project,ProbeInfo probe,String messageBody){
		this.probe=probe;
		this.project=project;
		this.messageBody=messageBody;
	}
	public ProbeInfo getProbe() {
		return probe;
	}
	public void setProbe(ProbeInfo probe) {
		this.probe = probe;
	}
	public String getMessageBody() {
		return messageBody;
	}
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
	public ProjectInfo getProject() {
		return project;
	}
	public void setProject(ProjectInfo project) {
		this.project = project;
	}
	@Override
	public String toString() {
		return "ProbeEventMessage [probe=" + probe + ", project=" + project + ", messageBody=" + messageBody + "]";
	}
	/**
	 * 从List<MAP>转换为List<ProbeEventMessage>,如果存在过滤条件则执行过滤条件
	 * @param projectInfo 项目信息
	 * @param probeInfo Probe信息
	 * @param rawData List<MAP>
	 * @param excludes 过滤规则,单项多个过滤时，过滤值以#分割
	 * @return
	 */
	public static List<ProbeEventMessage> collectionToProbeEventMessage(ProjectInfo projectInfo,ProbeInfo probeInfo,Collection<Map<String, Object>> rawData,Map<String,String> excludes){		
		List<ProbeEventMessage> result=new ArrayList<ProbeEventMessage>();
		if(rawData==null||projectInfo==null||probeInfo==null){
			return result;
		}
		for(Map<String,Object> data:rawData){
			boolean isExclude=false;
			if(excludes!=null){
				for(String key:excludes.keySet()){
					String value=data.get(key)==null?"":data.get(key).toString();
					if(StringUtils.isEmpty(value)||value.equalsIgnoreCase("NA")||value.equalsIgnoreCase("[]")){
						break;
					}else{
						if(StringUtils.isEmpty(excludes.get(key))){
							break;
						}else{
							List<String> excludeTargetValues=FastJsonUtils.toList(value,String.class);
							String[] excludeValues=excludes.get(key).split("#");
							isExclude=true;
							for(String excludeValue:excludeValues){
								boolean isMatched=false;
								for(String rawValue:excludeTargetValues){
									if(rawValue.equalsIgnoreCase(excludeValue)){
										isMatched=true;
										break;
									}
								}
								if(!isMatched){
									isExclude=false;
									break;
								}else{
									isExclude&=true;
								}
							}
						}
					}
				}
			}
			if(!isExclude){
				result.add(new ProbeEventMessage(projectInfo,probeInfo,FastJsonUtils.convertObjectToJSON(data)));
			}
		}
		return result;
	}
}
