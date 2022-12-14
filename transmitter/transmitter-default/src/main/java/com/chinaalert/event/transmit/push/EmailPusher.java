package com.chinaalert.event.transmit.push;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.chinaalert.event.common.domain.EventSourceType;
import com.chinaalert.event.common.domain.ProbeEventMessage;
import com.chinaalert.event.common.domain.ProbeInfo;
import com.chinaalert.event.common.domain.ProjectInfo;
import com.chinaalert.event.common.push.IPusher;
import com.chinaalert.event.common.utils.FastJsonUtils;
import com.chinaalert.event.handler.common.config.EventHandlerRuleConfig;
import com.chinaalert.event.handler.common.config.EventIntegratedProbeConfig;
import com.chinaalert.event.handler.common.config.PlatformEventColumnConfig;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;
import com.chinaalert.event.handler.common.domain.EventHandlerRuleEffect;
import com.chinaalert.event.handler.common.domain.PlatformEventColumn;
import com.chinaalert.event.transmit.algorithm.EventNotify;
import com.chinaalert.event.transmit.config.CandidateConfig;
import com.chinaalert.event.transmit.config.MediaTypeConfig;
import com.chinaalert.event.transmit.domain.Candidate;
import com.chinaalert.event.transmit.domain.MediaInfo;
import com.chinaalert.event.transmit.domain.MediaType;

import cn.mcinfotech.data.service.db.ColumnDefine;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * ????????????
 * @author hy
 */

public class EmailPusher implements IPusher<ProbeEventMessage> {
	private static Logger logger=LogManager.getLogger(EmailPusher.class);
	@Resource
	DataSource dataSource;
	@Resource
	ProbeInfo probeInfo;
	@Resource
	ProjectInfo projectInfo;
	@Resource
	EventIntegratedProbeConfig eventIntegratedProbeConfig;
	@Resource
	EventHandlerRuleConfig eventHandlerRulesConfig;
	@Resource
	PlatformEventColumnConfig ColumnDefineConfig;
	@Resource
	CandidateConfig candidateConfig;
	@Resource
	MediaTypeConfig notifySettingsConfig;

	@Override
	public void push(ProbeEventMessage message) {
	}
	@Override
	public void push(String handlerType,List<ProbeEventMessage> messages) {
	}

	@Override
	public void push(String handlerType, ProbeEventMessage message) {
	}
	@Override
	public void push(List<ProbeEventMessage> messages) {
		Map<String,Object> conditions=new HashMap<String,Object>();
		conditions.put("isEnable", "Y");
		List<ColumnDefine> columnDefines=ColumnDefineConfig.getPlatformEventColumn(this.projectInfo.getId(), conditions);
		EventHandlerRule rule=this.eventHandlerRulesConfig.getEventHandlerRule(projectInfo.getId(),null, EventSourceType.INTERNAL,"N");
		for(ProbeEventMessage message:messages){
			Map<String,Object> mapMessage=FastJsonUtils.stringToCollect(message.getMessageBody());
			if(mapMessage.containsKey("events")){
				//if(EventNotice.excute(mapMessage.get("events"), columnMappings, rule))
				List<Map<String,Object>> events=(List<Map<String, Object>>) mapMessage.get("events");
				List<String> notifyTypes=(List<String>) mapMessage.get("notifyType");
				String remark=mapMessage.get("remark")==null?"":mapMessage.get("remark").toString();
				ProjectInfo projectInfo=message.getProject();
				for(Map<String,Object> event:events){
					if(StringUtils.isNotEmpty(remark)){
						event.put("remark", remark);
					}
					this.notify(projectInfo,notifyTypes,null, event);
				}
			}else{
				Object filterFlagObject=mapMessage.get(PlatformEventColumn.FIXED_COLUMN_FILTER_FLAG);
				String filterFlagValue=filterFlagObject==null?"":filterFlagObject.toString();
				Set<String> excludeMediaTypes=new HashSet<String>();
				if(StringUtils.isNotEmpty(filterFlagValue)&&!filterFlagValue.equalsIgnoreCase("NA")&&!filterFlagValue.equalsIgnoreCase("[]")){
					List<String> filterTypes=FastJsonUtils.toList(filterFlagValue, String.class);
					for(String filterType:filterTypes){
						if(filterType.equalsIgnoreCase("NS")){
							excludeMediaTypes.add("share");
						}else if(filterType.equalsIgnoreCase("NN")){
							excludeMediaTypes.add("email");
							excludeMediaTypes.add("wechat");
							excludeMediaTypes.add("nail");
						}else if(filterType.equalsIgnoreCase("NF")){
							excludeMediaTypes.add("flow");
						}
					}
					
				}
				if(EventNotify.excute(mapMessage,ColumnDefine.toMap(columnDefines),rule)){
					this.notify(projectInfo,rule,excludeMediaTypes,mapMessage);
				}
			}
		}
	}
	/*public void executeNotify(ProjectInfo projectInfo,List<String> notifyTypes,Map<String,Object> message){
		this.notify(projectInfo, notifyTypes, message);
	}*/
	
	/**
	 * ???????????????????????????????????????????????????
	 * @param message
	 * @return
	 */
	public void notify(ProjectInfo projectInfo,EventHandlerRule rule,Set<String> excludeMediaTypes,Map<String, Object> message) {
		if(message==null) return;
		//1.assemble data
		//????????????,???????????????????????????????????????
		List<EventHandlerRuleEffect> effects=FastJsonUtils.toList(rule.getEffect(), EventHandlerRuleEffect.class);
		List<String> mediaTypes=new ArrayList<String>();
		for(EventHandlerRuleEffect effect:effects){
			mediaTypes.addAll(FastJsonUtils.toList(effect.getEffectValue(), String.class));
		}
		notify(projectInfo,mediaTypes,excludeMediaTypes,message);
	}
	public void notify(ProjectInfo projectInfo,List<String> mediaTypes,Set<String> excludeMediaTypes,Map<String, Object> message) {
		if(message==null) return;
		if(CollectionUtils.isEmpty(mediaTypes)) {
			logger.warn("no candidated media type exist.");
			return;
		}
		Set<String> mergedMediaTypes=new HashSet<String>();
		mergedMediaTypes.addAll(mediaTypes);
		if(CollectionUtils.isNotEmpty(excludeMediaTypes)){
			for(String mediaType:mediaTypes){
				if(excludeMediaTypes.contains(mediaType)){
					mergedMediaTypes.remove(mediaType);
				}
			}
		}
		if(CollectionUtils.isEmpty(mergedMediaTypes)){
			logger.warn("waiting to send message :");
			logger.warn(message);
			logger.warn("no media matched ...");
			return;
		}
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("mediaTypes", mergedMediaTypes);
		List<MediaInfo> medias=this.notifySettingsConfig.getSendServiceInfo(projectInfo.getId(), filter);
		if(medias!=null){
			for(MediaInfo media:medias){
				try {
					//??????????????????
					//String emailBody=media.getMediaTemplet();
					//????????????????????????????????????EventCategory?????????????????????????????????
					//???????????????EventCategory??????????????????????????????????????????????????????????????????
					String deviceCategory=(String) message.get("EventCategory");
					String node=(String) message.get("Node");
					Map<String,Object> params=new HashMap<String,Object>();
					params.put("categoryName", deviceCategory);
					params.put("deviceKey", node);
					params.put("status", '0');
					params.put("isEnable", 'Y');
					//2.????????????????????????
					List<Candidate> candidates=this.candidateConfig.getCandidates(this.projectInfo.getId(), params);
					//??????
					if(CollectionUtils.isNotEmpty(candidates)){
						if(media.getMediaType()==MediaType.WECHAT){
							System.out.println("wechat not implements...........................................");
						}else if(media.getMediaType()==MediaType.FLOW){
							System.out.println("flow not implements.............................................");
						}else if(media.getMediaType()==MediaType.NAIL){
							System.out.println("nail not implements.............................................");
						}else{
							//??????SMTP????????????
							MediaInfo sendServiceInfo=this.notifySettingsConfig.getSendServiceInfo(projectInfo.getId(),"email");
							//????????????????????????
							JSONObject mediaSettings=JSONObject.parseObject(sendServiceInfo.getMediaSettings());
							//????????????SMTP??????
							boolean isSSL=StringUtils.isEmpty(mediaSettings.getString("is_ssl"))?false:mediaSettings.getString("is_ssl").equalsIgnoreCase("Y")?true:false;
							String mailserver = mediaSettings.getString("mail_server");//????????? SMTP ??????????????????: smtp.qq.com
							int mailserverPort=new Integer(mediaSettings.getString("mail_server_port"));
							String fromEmail = mediaSettings.getString("from_mail");//???????????????
							String fromEmailPassword = mediaSettings.getString("from_mail_password");//????????????????????????????????????
							List<String> toMails=new ArrayList<String>();
							for(Candidate candidate:candidates){
								toMails.add(candidate.getEmail());
							}
							//??????????????????
							//??????????????????
							//??????
							//SendmailUtil.sendEmail(toMails,null,"??????????????????",this.buildBody(sendServiceInfo.getMediaTemplet(),message),mailserver,mailserverPort,isSSL,fromEmail,"????????????",fromEmailPassword);
						}
					}else{
						logger.warn("no candidate !");
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	private String buildBody(String templet,Map<String,Object> params) throws TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException, TemplateException{
		String firstOccurrenceStr = String.valueOf(params.get("FirstOccurrence"));
		String LastOccurrenceStr = String.valueOf(params.get("LastOccurrence"));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if(StringUtils.isNotEmpty(firstOccurrenceStr)) {
			long firstOccurrence = Long.parseLong(firstOccurrenceStr);
			params.put("FirstOccurrence",simpleDateFormat.format(new Date(firstOccurrence)));
		}
		if(StringUtils.isNotEmpty(LastOccurrenceStr)) {
			long lastOccurrence = Long.parseLong(LastOccurrenceStr);
			params.put("LastOccurrence",simpleDateFormat.format(new Date(lastOccurrence)));
		}
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate("emailContents",templet);
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
		cfg.setNumberFormat("#");
		cfg.setTemplateLoader(stringLoader);
		Writer out=new StringWriter(4096);
		Template template = cfg.getTemplate("emailContents");
		template.process(params, out);
		return out.toString();
	}
}
