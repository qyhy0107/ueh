package com.chinaalert.event.transmit.push;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.HttpClientUtil;
import com.arronlong.httpclientutil.common.HttpConfig;
import com.arronlong.httpclientutil.common.HttpHeader;
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
import com.chinaalert.event.transmit.domain.AppHtmlSendParam;
import com.chinaalert.event.transmit.domain.AppTarget;
import com.chinaalert.event.transmit.domain.Candidate;
import com.chinaalert.event.transmit.domain.MailSendParam;
import com.chinaalert.event.transmit.domain.MailTarget;
import com.chinaalert.event.transmit.domain.MediaInfo;
import com.chinaalert.event.transmit.domain.MediaType;
import com.chinaalert.event.transmit.domain.itsm.ItsmResult;
import com.chinaalert.event.transmit.domain.vo.CandidateVo;
import com.chinaalert.event.transmit.utils.HttpUtils;
import com.chinaalert.event.transmit.utils.SendmailUtil;

import cn.mcinfotech.data.service.db.ColumnDefine;
import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;
import freemarker.cache.StringTemplateLoader;
import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

/**
 * ????????????
 *
 * @author yzy
 */
@Component
public class HttpPusher implements IPusher<ProbeEventMessage> {
	private static Logger logger = LogManager.getLogger(HttpPusher.class);
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
	public void push(String handlerType, List<ProbeEventMessage> messages) {
	}

	@Override
	public void push(String handlerType, ProbeEventMessage message) {
	}

	@Override
	public void push(List<ProbeEventMessage> messages) {
		long projectId = projectInfo.getId();
		Map<String, Object> conditions = new HashMap<>();
		conditions.put("isEnable", "Y");
		List<ColumnDefine> columnDefines = ColumnDefineConfig.getPlatformEventColumn(this.projectInfo.getId(),conditions);
		// ????????????
		List<EventHandlerRule> rules = eventHandlerRulesConfig.getEventHandlerRules(projectInfo.getId(), null,
				EventSourceType.INTERNAL, "N");
		// ????????????????????????
		for (ProbeEventMessage message : messages) {
			Map<String, Object> mapMessage = FastJsonUtils.stringToCollect(message.getMessageBody());
			// 1.????????????????????????
			if (mapMessage.containsKey("events")) {
				// ????????????
				List<Map<String, Object>> events = (List<Map<String, Object>>) mapMessage.get("events");
				// ??????????????????
				List<String> notifyTypes = (List<String>) mapMessage.get("notifyType");
				// ?????????
				Object acknoledger = mapMessage.get("acknoledger");
				// ???????????????
				List<Object> candidates = null;
				Object person = mapMessage.get("person");
				if (null != person && !"".equals(person)) {
					String[] split = String.valueOf(person).split("#");
					candidates = Arrays.asList(split);
				}
				String remark = mapMessage.get("remark") == null ? "" : mapMessage.get("remark").toString();
				ProjectInfo projectInfo = message.getProject();
				for (Map<String, Object> event : events) {
					if (StringUtils.isNotEmpty(remark)) {
						event.put("remark", remark);
					}
					if (CollectionUtils.isEmpty(candidates)) {
						// ???????????????????????????/?????????????????????
						Map<String, Object> filter = new HashMap<>();
						filter.put("categoryName", event.get("EventCategory"));
						filter.put("deviceKey", event.get("Node"));
						filter.put("status", '0');
						filter.put("isEnable", 'Y');
						candidates = candidateConfig.getCandidatesByNode(projectId, filter);
						if (CollectionUtils.isEmpty(candidates)) {
							candidates = candidateConfig.getCandidatesByGroup(projectId, filter);
						}
					}
					// ??????????????????
					ArrayList<CandidateVo> candidateList = new ArrayList<>();
					if (candidates != null) {
						for (Object candidate : candidates) {
							CandidateVo candidateVo = new CandidateVo();
							candidateVo.setNotification_types(notifyTypes);
							candidateVo.setCandidate(candidate.toString());
							candidateList.add(candidateVo);
						}
					}
					Object eventID = event.get("EventID");
					DataLoadParams dataLoadParams = new DataLoadParams();
					HashMap<Object, Object> filter = new HashMap<>();
					filter.put("event_id", eventID);
					filter.put("candidate", JSON.toJSONString(candidateList));
					dataLoadParams.setFilter(JSON.toJSONString(filter));
					dataLoadParams.setDcName("updateNotificationLog");
					dataLoadParams.setProjectId(10L);
					dataLoadParams.setStart(1);
					dataLoadParams.setLimit(-10);
					DataServiceUtils.dataLoad(dataSource, dataLoadParams);
					// ??????
					if (notifyTypes != null) {
						for (String notifyType : notifyTypes) {
							if (CollectionUtils.isEmpty(candidates) && !"flow".equalsIgnoreCase(notifyType))
								continue;
							notify(notifyType, candidates, event);
						}
					}
				}
			} else {
				// 2.????????????????????????
				// ??????????????????FilterFlag ????????????????????????????????????????????????????????????
				Object filterFlagObject = mapMessage.get(PlatformEventColumn.FIXED_COLUMN_FILTER_FLAG);
				String filterFlagValue = filterFlagObject == null ? "" : filterFlagObject.toString();
				Set<String> excludeMediaTypes = new HashSet<String>();
				if (StringUtils.isNotEmpty(filterFlagValue) && !filterFlagValue.equalsIgnoreCase("NA")
						&& !filterFlagValue.equalsIgnoreCase("[]")) {
					List<String> filterTypes = FastJsonUtils.toList(filterFlagValue, String.class);
					for (String filterType : filterTypes) {
						if (filterType.equalsIgnoreCase("NS")) {// ??????
							excludeMediaTypes.add("share");
						} else if (filterType.equalsIgnoreCase("NN")) {// ??????
							excludeMediaTypes.add("email");
							excludeMediaTypes.add("Wechat");
							excludeMediaTypes.add("nail");
						} else if (filterType.equalsIgnoreCase("NF")) {// ??????
							excludeMediaTypes.add("flow");
						}
					}
				}
				// ????????????????????????????????????
				if (CollectionUtils.isNotEmpty(rules)) {
					Map<String, List<Object>> noticeList = new HashMap<>();
					Map<Object, List> candidateVo = new HashMap<>();
					StringBuffer filterNames = new StringBuffer();
					boolean flag = false;// ??????????????????????????????
					for (EventHandlerRule rule : rules) {
						boolean excute = EventNotify.excute(mapMessage, ColumnDefine.toMap(columnDefines), rule);
						logger.info("{}=====?????????{}======?????????{}=======?????????????????????{}======???????????????{}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), message, rule, excute,excludeMediaTypes);
						if (excute) {
							// ????????????
							if (filterNames.length() > 0) {
								filterNames.append("#");
							}
							filterNames.append(rule.getName());
							flag = flag || excute;
							String effectStr = rule.getEffect();
							EventHandlerRuleEffect effectObj = JSON.parseArray(effectStr, EventHandlerRuleEffect.class)
									.get(0);
							// ????????????
							String effectTypeStr = effectObj.getEffectType();
							if (StringUtils.isBlank(effectTypeStr))
								continue;
							List<String> effectTypeList = new ArrayList<>(JSON.parseArray(effectTypeStr, String.class));
							if (CollectionUtils.isNotEmpty(excludeMediaTypes)) {
								for (String effectType : effectTypeList) {
									if (excludeMediaTypes.contains(effectType)) {
										effectTypeList.remove(effectType);
									}
								}
							}
							// ???????????????
							String effectValue = effectObj.getEffectValue();
							List<Object> candidates = null;
							if (StringUtils.isNotBlank(effectValue)) {
								// ???????????????????????????
								String[] split = effectValue.split("#");
								candidates = Arrays.asList(split);
							} else {
								// ???????????????????????????/?????????????????????
								String deviceCategory = (String) mapMessage.get("EventCategory");
								String node = (String) mapMessage.get("Node");
								Map<String, Object> filter = new HashMap<>();
								filter.put("categoryName", deviceCategory);
								filter.put("deviceKey", node);
								filter.put("status", '0');
								filter.put("isEnable", 'Y');
								candidates = candidateConfig.getCandidatesByNode(projectId, filter);
								if (CollectionUtils.isEmpty(candidates)) {
									candidates = candidateConfig.getCandidatesByGroup(projectId, filter);
								}
							}

							if (CollectionUtils.isNotEmpty(candidates)) {
								for (Object candidate : candidates) {
									List<Object> effectTypes = candidateVo.get(candidate);
									if (CollectionUtils.isNotEmpty(effectTypes)&& CollectionUtils.isNotEmpty(effectTypeList)) {
										List<Object> union = new ArrayList<>(CollectionUtils.union(effectTypes, effectTypeList));
										candidateVo.put(candidate, union);
									} else if (CollectionUtils.isEmpty(effectTypes)&& CollectionUtils.isNotEmpty(effectTypeList)) {
										candidateVo.put(candidate, effectTypeList);
									} else if (CollectionUtils.isNotEmpty(effectTypes)&& CollectionUtils.isEmpty(effectTypeList)) {
										candidateVo.put(candidate, effectTypes);
									}
								}
							}

							if (CollectionUtils.isNotEmpty(effectTypeList)) {
								for (String effectType : effectTypeList) {
									List<Object> candidateList = noticeList.get(effectType);
									if (CollectionUtils.isNotEmpty(candidateList)&& CollectionUtils.isNotEmpty(candidates)) {
										List<Object> union = new ArrayList<>(CollectionUtils.union(candidateList, candidates));
										noticeList.put(effectType, union);
									} else if (CollectionUtils.isEmpty(candidateList)&& CollectionUtils.isNotEmpty(candidates)) {
										noticeList.put(effectType, candidates);
									} else if (CollectionUtils.isNotEmpty(candidateList)&& CollectionUtils.isEmpty(candidates)) {
										noticeList.put(effectType, candidateList);
									}
								}
							}

						}
					}

					// ??????????????????????????????
					if (flag) {
						// ??????????????????????????????????????????
						mapMessage.put("RefNotifyRules", filterNames.toString());
						DataLoadParams notifyParams = new DataLoadParams();
						Map<Object, Object> notifyfilter = new HashMap<>();
						notifyfilter.put("mapMessage", mapMessage);
						notifyParams.setFilter(JSON.toJSONString(notifyfilter));
						notifyParams.setDcName("updateRefNotifyRules");
						notifyParams.setProjectId(projectId);
						notifyParams.setStart(1);
						notifyParams.setLimit(-10);
						DataServiceUtils.dataLoad(dataSource, notifyParams);
						// ????????????
						List<CandidateVo> CandidateVoList = new ArrayList<>();
						Set<Map.Entry<Object, List>> entries = candidateVo.entrySet();
						for (Map.Entry<Object, List> entry : entries) {
							String key = (String) entry.getKey();
							List value = entry.getValue();
							CandidateVo candidateVo1 = new CandidateVo();
							candidateVo1.setCandidate(key);
							candidateVo1.setNotification_types(value);
							CandidateVoList.add(candidateVo1);
						}

						DataLoadParams dataLoadParams = new DataLoadParams();
						HashMap<Object, Object> filter = new HashMap<>();
						filter.put("event_id", mapMessage.get("EventID"));
						filter.put("acknoledger", "SYSTEM");
						filter.put("transmit_date", Instant.now().toEpochMilli());
						filter.put("candidate", JSON.toJSONString(CandidateVoList));
						filter.put("project_id", projectId);
						dataLoadParams.setFilter(JSON.toJSONString(filter));
						dataLoadParams.setDcName("addNotificationLog");
						dataLoadParams.setProjectId(projectId);
						dataLoadParams.setStart(1);
						dataLoadParams.setLimit(-10);
						ResultPattern res = DataServiceUtils.dataLoad(dataSource, dataLoadParams);
						// ????????????
						for (Map.Entry<String, List<Object>> entry : noticeList.entrySet()) {
							String key = entry.getKey();
							List<Object> value = entry.getValue();
							if (CollectionUtils.isEmpty(value) && !"flow".equalsIgnoreCase(key))
								continue;
							notify(key, value, mapMessage);
						}
						// ?????????????????????????????????????????????????????????
						DataLoadParams dataLoadParams1 = new DataLoadParams();
						HashMap<Object, Object> filter1 = new HashMap<>();
						filter1.put("projectId", projectId);
						filter1.put("ids", mapMessage.get("EventID"));
						dataLoadParams1.setFilter(JSON.toJSONString(filter1));
						dataLoadParams1.setDcName("batchCloseEvents");
						dataLoadParams1.setProjectId(projectId);
						dataLoadParams1.setStart(1);
						dataLoadParams1.setLimit(-10);
						DataServiceUtils.dataLoad(dataSource, dataLoadParams1);
					}
				}
			}
		}
	}

	/**
	 * @param mediaTypes
	 *            ????????????
	 * @param candidates
	 *            ?????????
	 * @param message
	 *            ????????????
	 */
	public void notify(String mediaTypes, List<Object> candidates, Map<String, Object> message) {
		if (StringUtils.isBlank(mediaTypes))
			return;
		if (CollectionUtils.isEmpty(candidates) && !"flow".equalsIgnoreCase(mediaTypes))
			return;
		if (MapUtils.isEmpty(message))
			return;
		Map<String, Object> newMessage = new HashMap<>();
		newMessage.putAll(message);
		MediaType mediaType = Enum.valueOf(MediaType.class, mediaTypes.toUpperCase());
		if (mediaType == MediaType.FLOW) {// ????????????
		} else if (mediaType == MediaType.EMAIL) {
			// ??????SMTP????????????
			MediaInfo sendServiceInfo = this.notifySettingsConfig.getSendServiceInfo(projectInfo.getId(), "email");
			// ????????????????????????
			JSONObject mediaSettings = JSONObject.parseObject(sendServiceInfo.getMediaSettings());
			// ????????????SMTP??????
			boolean isSSL = StringUtils.isEmpty(mediaSettings.getString("is_ssl")) ? false
					: mediaSettings.getString("is_ssl").equalsIgnoreCase("Y") ? true : false;
			String mailserver = mediaSettings.getString("mail_server");// ????????? SMTP??????????????????:smtp.qq.com
			int mailserverPort = new Integer(mediaSettings.getString("mail_server_port"));
			String fromEmail = mediaSettings.getString("from_mail");// ???????????????
			String fromEmailPassword = mediaSettings.getString("from_mail_password");// ????????????????????????????????????
			String mailSubject = mediaSettings.getString("mailSubject");// ??????
			List<String> toMails = new ArrayList<String>();
			for (Object candidate : candidates) {
				toMails.add(candidate.toString());
			}
			// ??????????????????
			// ??????????????????
			// ??????
			try {
				Map<String, Object> newMessageSubject = new HashMap<>();
				newMessageSubject.putAll(message);
				String s = this.buildBody(mailSubject, newMessageSubject);
				String s1 = this.buildBody(sendServiceInfo.getMediaTemplet(), newMessage);
				SendmailUtil.sendEmail(toMails, null, s, s1, mailserver, mailserverPort, isSSL, fromEmail, "????????????",
						fromEmailPassword);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// ??????????????????
		}
	}

	private String buildBody(String templet, Map<String, Object> params) throws TemplateNotFoundException,
			MalformedTemplateNameException, ParseException, IOException, TemplateException {
		String firstOccurrenceStr = String.valueOf(params.get("FirstOccurrence"));
		String LastOccurrenceStr = String.valueOf(params.get("LastOccurrence"));
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (StringUtils.isNotEmpty(firstOccurrenceStr)) {
			long firstOccurrence = Long.parseLong(firstOccurrenceStr);
			params.put("FirstOccurrence", simpleDateFormat.format(new Date(firstOccurrence)));
		}
		if (StringUtils.isNotEmpty(LastOccurrenceStr)) {
			long lastOccurrence = Long.parseLong(LastOccurrenceStr);
			params.put("LastOccurrence", simpleDateFormat.format(new Date(lastOccurrence)));
		}
		StringTemplateLoader stringLoader = new StringTemplateLoader();
		stringLoader.putTemplate("emailContents", templet);
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
		cfg.setNumberFormat("#");
		cfg.setTemplateLoader(stringLoader);
		Writer out = new StringWriter(4096);
		Template template = cfg.getTemplate("emailContents");
		template.process(params, out);
		return out.toString();
	}
}