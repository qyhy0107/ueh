package com.chinaalert.event.transmit.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.chinaalert.event.common.utils.DateTimeUtils;
import com.chinaalert.event.handler.common.domain.EventHandlerRule;
import com.chinaalert.event.handler.common.domain.EventHandlerRuleExpression;

import cn.mcinfotech.data.service.db.ColumnDefine;
import cn.mcinfotech.data.service.db.DataType;

/**
 * 判断事件是否符合发送条件
 * 针对单台设备中一个或几个字段符合条件时，对事件进行通知条件判断
 * 条件设置：[{"conditionColumn":"Node","conditionDataType":"string","operator":"=","conditionValue":"173.21.10.18"}]
 *
 * @author q
 */
public class EventNotify {
    private static Logger logger = LogManager.getLogger(EventNotify.class);

    /**
     * @param message        原始消息
     * @param columnMappings 事件源与平台映射关系
     * @param rule           规则
     * @return
     */
    public static boolean excute(Map<String, Object> message, Map<String, ColumnDefine> columnMappings, EventHandlerRule rule) {
        if (message == null) return false;
        if (rule == null) return false;
        boolean isValid1 = false;//是否符合多条规则  规则之间得关系为与 或

        //try {
            //策略是否过期
            boolean notExpired = DateTimeUtils.isValid(rule.getExecType(), rule.getIntervalType(), rule.getDayOfWeekAt(), rule.getDayOfWeekUtil(), rule.getExecuteAt(), rule.getExecuteUtil());
            if (!notExpired) {
                return false;
            }
            String expressionStr = rule.getExpression();
            if (StringUtils.isEmpty(expressionStr)) return false;

            List<EventHandlerRuleExpression> expressions = JSON.parseArray(expressionStr, EventHandlerRuleExpression.class);
            isValid1 = false;
            String operatorLogic = null;//上一条规则的与下一条规则的关系，从前一条规则中取出
            for (EventHandlerRuleExpression expression : expressions) {//每条规则
                boolean isValid2 = false;//是否符合某条规则
                String conditionColumn = expression.getConditionColumn();
                String operator = expression.getOperator();
                String conditionValue = expression.getConditionValue();
                List<String> conditionValueList = JSON.parseArray(conditionValue, String.class);
                Object rawValue = message.get(conditionColumn);
                ColumnDefine columnDefine = columnMappings.get(conditionColumn);//列定义
                DataType dataType = columnDefine.getDataType();
                String conditionValueVo = "";
                String conditionValueVo2 = "";
                if (conditionValueList.size() == 1) {
                    conditionValueVo = conditionValueList.get(0);
                } else if (conditionValueList.size() == 2) {
                    conditionValueVo = conditionValueList.get(0);
                    conditionValueVo2 = conditionValueList.get(1);
                }
                if (dataType.equals(DataType.INT)) {
                    int rawValueInt = Integer.parseInt(String.valueOf(rawValue));
                    int conditionValueInt = -1;
                    if (!org.springframework.util.StringUtils.isEmpty(conditionValueVo)) {
                        conditionValueInt = Integer.parseInt(conditionValueVo);
                    }
                    if ("=".equals(operator)) {
                        isValid2 = rawValueInt == conditionValueInt;
                    } else if ("!=".equals(operator)) {
                        isValid2 = rawValueInt != conditionValueInt;
                    } else if ("<".equals(operator)) {
                        isValid2 = rawValueInt < conditionValueInt;
                    } else if ("<=".equals(operator)) {
                        isValid2 = rawValueInt <= conditionValueInt;
                    } else if (">".equals(operator)) {
                        isValid2 = rawValueInt > conditionValueInt;
                    } else if (">=".equals(operator)) {
                        isValid2 = rawValueInt >= conditionValueInt;
                    } else if ("in".equals(operator)) {
                        isValid2 = conditionValueList.contains(String.valueOf(rawValue));
                    } else if ("between".equals(operator)) {
                        int conditionValueInt2 = Integer.parseInt(conditionValueVo2);
                        if (conditionValueInt > conditionValueInt2) {
                            isValid2 = rawValueInt >= conditionValueInt2 && rawValueInt <= conditionValueInt;
                        } else {
                            isValid2 = rawValueInt >= conditionValueInt && rawValueInt <= conditionValueInt2;
                        }
                    } else if ("regex".equalsIgnoreCase(operator)) {//匹配正则
                        String rawValueStr = String.valueOf(rawValue).trim();
                        Pattern pattern = Pattern.compile(conditionValueVo);
                        Matcher matcher = pattern.matcher(rawValueStr);
                        isValid2 = matcher.find();
                    }
                } else if (dataType.equals(DataType.STRING)) {
                    String rawValueStr = String.valueOf(rawValue).trim();
                    if ("=".equals(operator)) {
                        isValid2 = rawValueStr.equalsIgnoreCase(conditionValueVo);
                    } else if ("!=".equals(operator)) {
                        isValid2 = !rawValueStr.equalsIgnoreCase(conditionValueVo);
                    } else if ("like".equals(operator)) {
                        isValid2 = rawValueStr.contains(conditionValueVo);
                    } else if ("regex".equalsIgnoreCase(operator)) {//匹配正则
                        Pattern pattern = Pattern.compile(conditionValueVo);
                        Matcher matcher = pattern.matcher(rawValueStr);
                        isValid2 = matcher.find();
                    } else if ("ip".equalsIgnoreCase(operator)) {//匹配ip地址段 192.168.1.1;192.168.1.3;192.168.1.1-254
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
                                        String tokenCommon = tokenStart.substring(0, tokenStart.lastIndexOf(".") + 1);
                                        int tokenDiff = Integer.valueOf(tokenStart.substring(tokenStart.lastIndexOf(".") + 1));
                                        for (int i = tokenDiff; i < tokenEnd; i++) {
                                            ipList.add(tokenCommon + Integer.sum(1, i));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            isValid2 = ipList.contains(rawValueStr);
                        }
                    }
                } else if (dataType.equals(DataType.LONGTIMESTAMP)) {
                    if ("between".equals(operator)) {
                        conditionValueList = JSON.parseArray(conditionValueVo, String.class);
                        long conditionValueVol = Long.parseLong(conditionValueList.get(0));
                        long conditionValueVo2l = Long.parseLong(conditionValueList.get(1));
                        long rawValuel = Long.parseLong(String.valueOf(rawValue));
                        isValid2 = rawValuel >= conditionValueVol && rawValuel <= conditionValueVo2l;
                    }
                }
                if (StringUtils.isEmpty(operatorLogic)) {
                    isValid1 = isValid2;
                } else if ("&&".equals(operatorLogic)) {
                    isValid1 = isValid1 && isValid2;
                } else if ("||".equals(operatorLogic)) {
                    isValid1 = isValid1 || isValid2;
                }
                operatorLogic = expression.getOperatorLogic();
            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
            return isValid1;
//        }
    }

    //	public static void main(String[] args){
//		//eventQuery
//		String url="jdbc:postgresql://127.0.0.1:5432/ueh?currentSchema=ueh_ctrl&TimeZone=PRC&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT";
//		String driverClassName="org.postgresql.Driver";
//		Properties prop=new Properties();
//		prop.put("initial.size", "5");
//		prop.put("max.total", "10");
//		prop.put("min.idle", "2");
//		prop.put("max.wait.seconds", "20");
//		DataSource dataSource = DataSourceUtils.setupDataSource(url,driverClassName,"postgresql","root","123456",prop);
//		//pgInsert(dataSource);
//		//ObjectMapper mapper=new ObjectMapper();
//		//Map<String,Object> filter=new HashMap<String,Object>();
//		//filter.put("name", "20311842-8231-48af-b064-5ef726b3534d");
//		DataLoadParams params=new DataLoadParams();
//		params.setDcName("eventQuery");
//		params.setProjectId(10l);
//		/*try {
//			params.setFilter(mapper.writeValueAsString(filter));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}*/
//		params.setStart(1);
//		params.setLimit(0);
//		//System.out.println(new Date());
//		ResultPattern result=DataServiceUtils.dataLoad(dataSource, params);
//		System.out.println(FastJsonUtils.convertObjectToJSON(result));
//		/*try {
//			System.out.println(mapper.writeValueAsString(result));
//		} catch (JsonProcessingException e) {
//			e.printStackTrace();
//		}*/
//		//result.getMapData();
//		List<Map<String,Object>> rawMessages=result.getDatas();;
//		//rawMessages.add(FastJsonUtils.stringToCollect(""));
//		List<String> rules=new ArrayList<String>();
//		rules.add("Node");
//		rules.add("NodeAlias");
//		rules.add("AlertGroup");
//		rules.add("AlertKey");
//		rules.add("Summary");
//		//EventCompress.excute(rawMessages, rules, "Tally", "LastOccurence");
//		//for(Map)
//	}
}
