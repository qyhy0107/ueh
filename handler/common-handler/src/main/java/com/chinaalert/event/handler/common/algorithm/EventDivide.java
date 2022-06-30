package com.chinaalert.event.handler.common.algorithm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.chinaalert.event.common.domain.EventSourceType;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;
import com.chinaalert.event.handler.common.domain.EventHandlerRuleEffect;
import com.chinaalert.event.handler.common.domain.EventHandlerRuleExpression;

import cn.mcinfotech.data.service.db.ColumnDefine;
import cn.mcinfotech.data.service.db.DataType;
import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;
import cn.mcinfotech.data.service.util.DataSourceUtils;

/**
 * 事件分组
 * 针对单台设备中一个或几个字段符合条件时，对事件所属进行填充，所属字段可以自由定义
 * 条件设置：[{"conditionColumn":"Node","conditionDataType":"string","operator":"in","conditionValue":"173.21.10.18"}]
 * 生效设置：[{"effectColumn":"Customer","effectDataType":"string","effectType":"fill","effectValue":"Network"}]
 * @author q
 *
 */
public class EventDivide {

	public static Collection<Map<String, Object>> excute(Collection<Map<String, Object>> rawMessages, Map<String, ColumnDefine> columnDefineMap, List<EventHandlerRule> rules, DataSource dataSource, EventSourceType eventSourceType) {
		try {
			if(CollectionUtils.isEmpty(rawMessages))return null;
//			if(rules==null)return rawMessages;
			//当前probe策略与公共策略分开存放
			List<EventHandlerRule> probeRules = new ArrayList<>();//当前probe的策略
			if(rules!=null){
				Iterator<EventHandlerRule> ruleIterator = rules.iterator();
				while (ruleIterator.hasNext()){
					EventHandlerRule rule = ruleIterator.next();
					if (!StringUtils.isEmpty(rule.getEventSource()) ){
						probeRules.add(rule);
						ruleIterator.remove();
					}
				}
			}
			//依次从资源表（排除CMDB设备）、当前probe的策略、公共策略、CMDB设备进行匹配
			boolean flag=false;
			for (Map<String, Object> rawMessage : rawMessages) {
				//从资源表匹配
				String node = String.valueOf(rawMessage.get("Node"));
				if (!"null".equals(node)) {
					DataLoadParams dataLoadParams = new DataLoadParams();
					if (EventSourceType.CUSTOM.name().equals(String.valueOf(eventSourceType))) {//自定义监控
						dataLoadParams.setDcName("getCustomClassName");
					}else {
						dataLoadParams.setDcName("getClassNameExcludeCMDB");
					}
					ResultPattern rs = getGroupName(dataSource, node, dataLoadParams);
					List<Map<String, Object>> datas = rs.getDatas();
					if (rs.isSuccess()&&rs.getTotalCount()==1&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(datas)) {
						String name = String.valueOf(datas.get(0).get("name"));
						String labelText = String.valueOf(datas.get(0).get("labelText"));
						if (!"null".equals(name)) {
							rawMessage.put("EventCategory",name);
							rawMessage.put("EventCategoryLabelText",labelText);
							flag=true;
						}
					}
				}
				//资源表中没有当前设备，按照当前probe的策略进行匹配
				if (!flag &&!CollectionUtils.isEmpty(probeRules)) {
					flag = groupByRules(rawMessage, probeRules, columnDefineMap);
				}
				//当前probe的策略没有匹配上，使用公共策略进行匹配
				if (!flag && !CollectionUtils.isEmpty(rules)) {
					flag = groupByRules(rawMessage, rules, columnDefineMap);
				}
				//公共策略没有匹配上，使用CMDB进行匹配
				if (!flag) {
					if (!EventSourceType.CUSTOM.name().equals(String.valueOf(eventSourceType))) {//自定义监控
						DataLoadParams dataLoadParams = new DataLoadParams();
						dataLoadParams.setDcName("getClassNameFromCMDB");
						ResultPattern rs = getGroupName(dataSource, node, dataLoadParams);
						List<Map<String, Object>> datas = rs.getDatas();
						if (rs.isSuccess()&&rs.getTotalCount()>0&& org.apache.commons.collections4.CollectionUtils.isNotEmpty(datas)) {
							String name = String.valueOf(datas.get(0).get("name"));
							String labelText = String.valueOf(datas.get(0).get("labelText"));
							if (!"null".equals(name)) {
								rawMessage.put("EventCategory",name);
								rawMessage.put("EventCategoryLabelText",labelText);
							}
						}
					}
				}
				flag = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rawMessages;
	}

	private static ResultPattern getGroupName(DataSource dataSource, String node, DataLoadParams dataLoadParams) {
		Map<String, Object> filter = new HashMap<>();
		filter.put("key_", node);
		dataLoadParams.setFilter(JSON.toJSONString(filter));
		dataLoadParams.setProjectId(10L);
		dataLoadParams.setStart(1);
		dataLoadParams.setLimit(-10);
		return DataServiceUtils.dataLoad(dataSource, dataLoadParams);
	}

	/**
	 * 按照指定字段的内容进行匹配，然后添加事件所属字段
	 * @param rawMessages 原始消息
	 * @param columnDefineMap 事件源与平台映射关系
	 * @param rules 生效规则
	 * @param dataSource
	 * @return 分组过的事件
	 */
	public static Collection<Map<String, Object>> excute(Collection<Map<String, Object>> rawMessages, Map<String, ColumnDefine> columnDefineMap, List<EventHandlerRule> rules, DataSource dataSource){
		return excute(rawMessages, columnDefineMap, rules, dataSource,null);
	}
	//每条消息 与每个策略里得每个条件进行对比
	private static boolean groupByRules(Map<String, Object> rawMessage, List<EventHandlerRule> rules, Map<String, ColumnDefine> columnDefineMap) {
			for (EventHandlerRule rule : rules) {//每条策略
				String expressionStr = rule.getExpression();
				List<EventHandlerRuleExpression> expressions = JSON.parseArray(expressionStr, EventHandlerRuleExpression.class);
				String effectStr = rule.getEffect();
				List<EventHandlerRuleEffect> effects = JSON.parseArray(effectStr, EventHandlerRuleEffect.class);
				String effectValue = effects.get(0).getEffectValue();
				String effectType = effects.get(0).getEffectType();
				boolean isValid1 = false;//是否符合多条规则  规则之间得关系为与 或
				String operatorLogic=null;//上一条规则的与下一条规则的关系，从前一条规则中取出
				for (EventHandlerRuleExpression expression : expressions) {//每条规则
					boolean isValid2=false;//是否符合某条规则
					String conditionColumn = expression.getConditionColumn();
					String operator = expression.getOperator();
					String conditionValue = expression.getConditionValue();
					List<String> conditionValueList = JSON.parseArray(conditionValue,String.class);
					Object rawValue = rawMessage.get(conditionColumn);
					ColumnDefine columnDefine = columnDefineMap.get(conditionColumn);//列定义
					DataType dataType = columnDefine.getDataType();
					String conditionValueVo="";
					String conditionValueVo2="";
					if(conditionValueList.size()==1){
						conditionValueVo=conditionValueList.get(0);
					}else if(conditionValueList.size()==2){
						conditionValueVo=conditionValueList.get(0);
						conditionValueVo2=conditionValueList.get(1);
					}
					if (dataType.equals(DataType.INT)){
						int rawValueInt = Integer.parseInt(String.valueOf(rawValue));
						int conditionValueInt=-1;
						if(!StringUtils.isEmpty(conditionValueVo)) {
							conditionValueInt = Integer.parseInt(conditionValueVo);
						}
						if("=".equals(operator)){
							isValid2= rawValueInt == conditionValueInt;
						}else if("!=".equals(operator)){
							isValid2= rawValueInt != conditionValueInt;
						}else if("<".equals(operator)){
							isValid2= rawValueInt < conditionValueInt;
						}else if("<=".equals(operator)){
							isValid2= rawValueInt <= conditionValueInt;
						}else if(">".equals(operator)){
							isValid2= rawValueInt > conditionValueInt;
						}else if(">=".equals(operator)){
							isValid2= rawValueInt >= conditionValueInt;
						}else if("in".equals(operator)){
							isValid2= conditionValueList.contains(String.valueOf(rawValue));
						}else if("between".equals(operator)){
							int conditionValueInt2 = Integer.parseInt(conditionValueVo2);
							if(conditionValueInt>conditionValueInt2){
								isValid2=rawValueInt>=conditionValueInt2&&rawValueInt<=conditionValueInt;
							}else {
								isValid2=rawValueInt>=conditionValueInt&&rawValueInt<=conditionValueInt2;
							}
						}else if ("regex".equalsIgnoreCase(operator)){//匹配正则
							String rawValueStr = String.valueOf(rawValue).trim();
							Pattern pattern = Pattern.compile(conditionValueVo);
							Matcher matcher = pattern.matcher(rawValueStr);
							isValid2=matcher.find();
						}
					}else if (dataType.equals(DataType.STRING)){
						String rawValueStr = String.valueOf(rawValue).trim();
						if("=".equals(operator)){
							isValid2=rawValueStr.equalsIgnoreCase(conditionValueVo);
						}else if("!=".equals(operator)){
							isValid2=!rawValueStr.equalsIgnoreCase(conditionValueVo);
						}else if("like".equals(operator)){
							isValid2= rawValueStr.contains(conditionValueVo);
						}else if ("regex".equalsIgnoreCase(operator)){//匹配正则
							Pattern pattern = Pattern.compile(conditionValueVo);
							Matcher matcher = pattern.matcher(rawValueStr);
							isValid2=matcher.find();
						}else if ("ip".equalsIgnoreCase(operator)){//匹配ip地址段 192.168.1.1;192.168.1.3;192.168.1.1-254
							if (org.apache.commons.lang3.StringUtils.isNotBlank(conditionValueVo)) {
								List<String> ipList = new ArrayList<>();
								StringTokenizer tokenizer = new StringTokenizer(conditionValueVo, ";；");
								while (tokenizer.hasMoreTokens()) {
									String token = tokenizer.nextToken();
									String[] tokenArr = token.split("-");
									if (tokenArr.length == 1) {
										ipList.add(token);
									} else if (tokenArr.length == 2) {
										try {
											String tokenStart = tokenArr[0];
											int tokenEnd = Integer.valueOf(tokenArr[1]);
											ipList.add(tokenStart);
											String tokenCommon = tokenStart.substring(0, tokenStart.lastIndexOf(".")+1);
											int tokenDiff = Integer.valueOf(tokenStart.substring(tokenStart.lastIndexOf(".")+1));
											for (int i = tokenDiff; i < tokenEnd; i++) {
												ipList.add(tokenCommon+Integer.sum(1,i));
											}
										}catch (Exception e){
											e.printStackTrace();
										}
									}
								}
								isValid2=ipList.contains(rawValueStr);
							}
						}
					}else if (dataType.equals(DataType.LONGTIMESTAMP)){
						if("between".equals(operator)){
							conditionValueList=JSON.parseArray(conditionValueVo,String.class);
							long conditionValueVol = Long.parseLong(conditionValueList.get(0));
							long conditionValueVo2l = Long.parseLong(conditionValueList.get(1));
							long rawValuel = Long.parseLong(String.valueOf(rawValue) );
							isValid2=rawValuel>=conditionValueVol&&rawValuel<=conditionValueVo2l;
						}
					}
					if(StringUtils.isEmpty(operatorLogic)){
						isValid1=isValid2;
					}else if("&&".equals(operatorLogic)){
						isValid1=isValid1&&isValid2;
					}else if("||".equals(operatorLogic)){
						isValid1=isValid1||isValid2;
					}
					operatorLogic = expression.getOperatorLogic();
				}
				if (isValid1) {
					rawMessage.put("EventCategory",effectValue);
					rawMessage.put("EventCategoryLabelText",effectType);
					return true;
				}
			}
		return false;
	}

	public static void main(String[] args) {

		String url="jdbc:postgresql://10.128.68.151:5432/ueh?currentSchema=ueh_admin&TimeZone=PRC&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT";
		String driverClassName="org.postgresql.Driver";
		Properties prop=new Properties();
		prop.put("initial.size", "5");
		prop.put("max.total", "10");
		prop.put("min.idle", "2");
		prop.put("max.wait.seconds", "20");
		DataSource dataSource = DataSourceUtils.setupDataSource(url,driverClassName,"postgresql","root","Pass@123",prop);

		DataLoadParams dataLoadParams = new DataLoadParams();
		dataLoadParams.setDcName("getClassName");
		Map<String,Object> filter= new HashMap<>();
		filter.put("key_","10.2.125.3");
		dataLoadParams.setFilter(JSON.toJSONString(filter));
		dataLoadParams.setProjectId(10L);
		dataLoadParams.setStart(1);
		dataLoadParams.setLimit(-10);
		ResultPattern resultPattern = DataServiceUtils.dataLoad(dataSource, dataLoadParams);
		resultPattern.getDatas();
	}
}
