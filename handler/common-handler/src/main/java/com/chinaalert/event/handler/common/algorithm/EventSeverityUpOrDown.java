package com.chinaalert.event.handler.common.algorithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.chinaalert.event.common.utils.DateTimeUtils;
import com.chinaalert.event.common.utils.FastJsonUtils;
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
 * 事件级别升降级
 * 针对单台设备中一个或几个字段重复出现时，事件级别进行升降级
 * 条件设置：[{"conditionColumn":"Node","conditionDataType":"string","operator":"=","conditionValue":"173.21.10.18"}]
 * 生效设置：[{"effectColumn":"Severity","effectDataType":"int","effectType":"plus","effectValue":"1"}]
 * @author q
 *
 */
public class EventSeverityUpOrDown {
	private static Logger logger=LogManager.getLogger(EventSeverityUpOrDown.class);
	/**
	 * 按照指定的条件进行升级或者降级。
	 * @param rawMessages 原始消息
	 * @param columnMappings 事件源与平台映射关系
	 * @param rules 生效规则配置
	 * @return 升降级过的事件
	 */
	public static Collection<Map<String, Object>> excute(Collection<Map<String, Object>> rawMessages, Map<String,ColumnDefine> columnMappings, List<EventHandlerRule> rules){
		if(CollectionUtils.isEmpty(rawMessages))return null;
		if(rules==null)return rawMessages;
		try {
			for (Map<String, Object> rawMessage : rawMessages) {//每条消息 与每个策略里得每个条件进行对比
				for (EventHandlerRule rule : rules) {//每条策略
					boolean notExpired=DateTimeUtils.isValid(rule.getExecType(), rule.getIntervalType(), rule.getDayOfWeekAt(), rule.getDayOfWeekUtil(), rule.getExecuteAt(), rule.getExecuteUtil());
					if(!notExpired) continue;
					String expressionStr = rule.getExpression();
					String effectStr = rule.getEffect();
					List<EventHandlerRuleExpression> expressions = JSON.parseArray(expressionStr, EventHandlerRuleExpression.class);
					List<EventHandlerRuleEffect> effects = JSON.parseArray(effectStr, EventHandlerRuleEffect.class);
					if (CollectionUtils.isEmpty(expressions)) continue;
					if (CollectionUtils.isEmpty(effects)) continue;
					String effectValue = effects.get(0).getEffectValue();
					String effectColumn = effects.get(0).getEffectColumn();
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
						ColumnDefine columnDefine = columnMappings.get(conditionColumn);//列定义
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
								isValid2=conditionValueList.indexOf(String.valueOf(rawValue))!=-1;
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
								if (StringUtils.isNotBlank(conditionValueVo)) {
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
								conditionValueList= JSON.parseArray(conditionValueVo,String.class);
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
						rawMessage.put(effectColumn,effectValue);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			return rawMessages;
		}
	}
	/**
	 * 按照指定的条件进行升级或者降级。
	 * @param rawMessages 原始消息
	 * @param columnMappings 事件源与平台映射关系
	 * @param rule 生效规则配置
	 * @return 升降级过的事件
	 */
	public static Collection<Map<String, Object>> excute20220120(Collection<Map<String, Object>> rawMessages,Map<String,ColumnDefine> columnMappings,EventHandlerRule rule){
		if(CollectionUtils.isEmpty(rawMessages))return null;
		if(rule==null)return rawMessages;
		boolean notExpired=DateTimeUtils.isValid(rule.getExecType(), rule.getIntervalType(), rule.getDayOfWeekAt(), rule.getDayOfWeekUtil(), rule.getExecuteAt(), rule.getExecuteUtil());
		if(!notExpired){
			return rawMessages;
		}
		String expressionStr=rule.getExpression();
		if(StringUtils.isEmpty(expressionStr))return rawMessages;

		List<EventHandlerRuleExpression> expressions=FastJsonUtils.toList(expressionStr, EventHandlerRuleExpression.class);
		Map<String,EventHandlerRuleExpression> conditionColumns=new HashMap<String,EventHandlerRuleExpression>();
		for(EventHandlerRuleExpression expression:expressions){
			conditionColumns.put(expression.getConditionColumn(),expression);
		}
		List<EventHandlerRuleEffect> effects=FastJsonUtils.toList(rule.getEffect(), EventHandlerRuleEffect.class);

		if(MapUtils.isEmpty(conditionColumns))return rawMessages;
		if(CollectionUtils.isEmpty(effects))return rawMessages;

		List<Map<String,Object>> newMessages=new ArrayList<Map<String,Object>>();
		for(Map<String,Object> message:rawMessages){
			/**
			 * 规则匹配
			 */
			//缺少多个条件的与、或、非、异或操作
			boolean isValid=true;
			boolean currentValid=true;
			for(String key:conditionColumns.keySet()){
				currentValid=true;
				Object messageValue=message.get(key);
				EventHandlerRuleExpression condition=conditionColumns.get(key);
				ColumnDefine columnDefine=columnMappings.get(key);
				if(columnDefine.getDataType()==DataType.STRING){
					String messageStrValue=(String) messageValue;
					if(condition.getOperator().equalsIgnoreCase("=")||condition.getOperator().equalsIgnoreCase("in")){
						if(!messageStrValue.contains(condition.getConditionValue())){
							currentValid&=false;
							//break;
						}
					}else if (condition.getOperator().equalsIgnoreCase("regex")){//匹配正则
						Pattern pattern = Pattern.compile(condition.getConditionValue());
						Matcher matcher = pattern.matcher(messageStrValue);
						while (!matcher.find()) {
							currentValid&=false;
//							break;
						}
					}
				}else if(columnDefine.getDataType()==DataType.INT){
					int messageIntValue=Integer.parseInt(messageValue.toString());
					int conditionIntValue=Integer.parseInt(condition.getConditionValue());
					if(condition.getOperator().equalsIgnoreCase(">")){
						if(!(messageIntValue>conditionIntValue)){
							currentValid&=false;
							//break;
						}
					}else if(condition.getOperator().equalsIgnoreCase("<")){
						if(!(messageIntValue<conditionIntValue)){
							currentValid&=false;
							//break;
						}
					}else if(condition.getOperator().equalsIgnoreCase("=")){
						if(!(messageIntValue==conditionIntValue)){
							currentValid&=false;
							//break;
						}
					}else if (condition.getOperator().equalsIgnoreCase("regex")){//匹配正则
						Pattern pattern = Pattern.compile(condition.getConditionValue());
						Matcher matcher = pattern.matcher(messageValue.toString());
						while (!matcher.find()) {
							currentValid&=false;
//							break;
						}
					}
				}else if(columnDefine.getDataType()==DataType.FLOAT){
					float messageIntValue=Float.parseFloat(messageValue.toString());
					float conditionIntValue=Float.parseFloat(condition.getConditionValue());
					if(condition.getOperator().equalsIgnoreCase(">")){
						if(!(messageIntValue>conditionIntValue)){
							currentValid&=false;
							//break;
						}
					}else if(condition.getOperator().equalsIgnoreCase("<")){
						if(!(messageIntValue<conditionIntValue)){
							currentValid&=false;
							//break;
						}
					}
				}else if(columnDefine.getDataType()==DataType.LONGTIMESTAMP){
					long messageLongValue=Long.parseLong(messageValue.toString());
					long conditionLongValue=Long.parseLong(condition.getConditionValue());
					if(condition.getOperator().equalsIgnoreCase(">")){
						if(!(messageLongValue>conditionLongValue)){
							currentValid&=false;
							//break;
						}
					}else if(condition.getOperator().equalsIgnoreCase("<")){
						if(!(messageLongValue<conditionLongValue)){
							currentValid&=false;
							//break;
						}
					}else if(condition.getOperator().equalsIgnoreCase("=")){
						if(!(messageLongValue==conditionLongValue)){
							currentValid&=false;
							//break;
						}
					}
				}
				if(StringUtils.isNotEmpty(condition.getOperatorLogic())){
					if(condition.getOperatorLogic().equalsIgnoreCase("&&")){
						isValid=isValid&&currentValid;
					}else if(condition.getOperatorLogic().equalsIgnoreCase("||")){
						isValid=isValid||currentValid;
					}
				}
			}
			/**
			 * 进行生效
			 */
			if(isValid){
				Map<String,Object> newMessage=message;
				for(EventHandlerRuleEffect effect:effects){
					if(effect.getEffectType().equalsIgnoreCase("minus")){
						//降级
						Object lastValue=message.get(effect.getEffectColumn());
						if(newMessage.containsKey(effect.getEffectColumn())){
							newMessage.remove(effect.getEffectColumn());
						}
						ColumnDefine columnMapping=columnMappings.get(effect.getEffectColumn());
						if(columnMapping.getDataType()==DataType.INT){
							newMessage.put(effect.getEffectColumn(), new BigDecimal(lastValue.toString()).subtract(new BigDecimal(effect.getEffectValue())).intValue()<0?0:new BigDecimal(lastValue.toString()).subtract(new BigDecimal(effect.getEffectValue())).intValue());
							continue;
						}
					}else if(effect.getEffectType().equalsIgnoreCase("plus")){
						//升级
						Object lastValue=message.get(effect.getEffectColumn());
						if(newMessage.containsKey(effect.getEffectColumn())){
							newMessage.remove(effect.getEffectColumn());
						}
						ColumnDefine columnMapping=columnMappings.get(effect.getEffectColumn());
						if(columnMapping.getDataType()==DataType.INT){
							newMessage.put(effect.getEffectColumn(), new BigDecimal(lastValue.toString()).add(new BigDecimal(effect.getEffectValue())).intValue()>5?5:new BigDecimal(lastValue.toString()).add(new BigDecimal(effect.getEffectValue())).intValue());
							continue;
						}
					}
				}
				newMessages.add(newMessage);
			}else{
				newMessages.add(message);
			}
		}
		return newMessages;
	}
	public static void main(String[] args){
		//eventQuery
		String url="jdbc:postgresql://127.0.0.1:5432/ueh?currentSchema=ueh_ctrl&TimeZone=PRC&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT";
		String driverClassName="org.postgresql.Driver";
		Properties prop=new Properties();
		prop.put("initial.size", "5");
		prop.put("max.total", "10");
		prop.put("min.idle", "2");
		prop.put("max.wait.seconds", "20");
		DataSource dataSource = DataSourceUtils.setupDataSource(url,driverClassName,"postgresql","root","123456",prop);
		//pgInsert(dataSource);
		//ObjectMapper mapper=new ObjectMapper();
		//Map<String,Object> filter=new HashMap<String,Object>();
		//filter.put("name", "20311842-8231-48af-b064-5ef726b3534d");
		DataLoadParams params=new DataLoadParams();
		params.setDcName("eventQuery");
		params.setProjectId(10l);
		/*try {
			params.setFilter(mapper.writeValueAsString(filter));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}*/
		params.setStart(1);
		params.setLimit(0);
		//System.out.println(new Date());
		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
		System.out.println(FastJsonUtils.convertObjectToJSON(result));
		/*try {
			System.out.println(mapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}*/
		//result.getMapData();
		List<Map<String,Object>> rawMessages=result.getDatas();;
		//rawMessages.add(FastJsonUtils.stringToCollect(""));
		List<String> rules=new ArrayList<String>();
		rules.add("Node");
		rules.add("NodeAlias");
		rules.add("AlertGroup");
		rules.add("AlertKey");
		rules.add("Summary");
		//EventCompress.excute(rawMessages, rules, "Tally", "LastOccurence");
		//for(Map)
	}
}
