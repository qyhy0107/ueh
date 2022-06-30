package com.chinaalert.event.handler.common.algorithm;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;

import com.chinaalert.event.common.utils.FastJsonUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import cn.mcinfotech.core.json.JSONUtils;
import cn.mcinfotech.core.lang.JavaDataType;
import cn.mcinfotech.data.service.db.ColumnDefine;
import cn.mcinfotech.data.service.db.pg.PostgreSQLUtils;
import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.PageDataSetEntity;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.domain.SQLEngine;
import cn.mcinfotech.data.service.service.PageDataSetService;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * 实现事件管理功能：
 * 1.事件管理
 * 1.1事件查询：丰富字段、合并查询
 * 1.2确认
 * 1.3开工单
 * 1.4事件删除
 * 1.5事件同步功能：从实时表同步到历史表
 * 2.维护事件表
 * 2.1动态生成建表语句
 * 2.2动态生成修改表语句
 * 2.3动态生成删除表语句
 * @author q
 *
 */
public class EventManage {
	private static String eventTableName="ueh_events\".\"t_event_status2";
	
	/**
	 * 创建事件表
	 * @projectId 工程ID
	 * @param dataSource 数据集所在的DataSource
	 * @param columnDefines 事件字段列表
	 * @return
	 */
	public static ResultPattern createTable(long projectId,DataSource dataSource,List<ColumnDefine> columnDefines){
		//1.查询事件字段
		//2.生成查询SQL
		String querySQL=PostgreSQLUtils.buildCreateTableSql(EventManage.eventTableName, columnDefines);
		DataLoadParams params=new DataLoadParams();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("mainSQL", querySQL);
		params.setProjectId(projectId);
		params.setDcName("simpleSQL");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		return DataServiceUtils.dataLoad(dataSource, params);
	}
	/**
	 * 删除事件表
	 * @param projectId 工程ID
	 * @param dataSource 数据集所在的DataSource
	 * @return
	 */
	public static ResultPattern dropTable(long projectId,DataSource dataSource){
		//1.查询事件字段
		//2.生成查询SQL
		String querySQL=PostgreSQLUtils.buildDropTableSql(EventManage.eventTableName);
		DataLoadParams params=new DataLoadParams();
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("mainSQL", querySQL);
		params.setProjectId(projectId);
		params.setDcName("simpleSQL");
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		return DataServiceUtils.dataLoad(dataSource, params);
	}
	/**
	 * 查询事件表
	 * @param dataSource 数据集所在的DataSource
	 * @param columnDefines 事件字段列表
	 * @param params 事件查询参数
	 * @return 返回的字段与columnDefines中相对应
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public static ResultPattern query(DataSource dataSource,List<ColumnDefine> columnDefines,DataLoadParams params) throws IOException, ParseException{
		//1.查询事件字段
		//2.生成查询SQL
		String querySQL=PostgreSQLUtils.buildQuerySql(EventManage.eventTableName, columnDefines);
		Map<String,Object> filter=(Map<String, Object>) FastJsonUtils.convertJsonToObject(params.getFilter(), Map.class);
		if(filter==null){
			filter=new HashMap<String,Object>();
		}
		PageDataSetEntity dataset=PageDataSetService.selectOneWithDataSource(dataSource,params.getProjectId(), null, "eventSelect");
		filter.put("mainSQL", querySQL+" where \"ProjectId\" ="+params.getProjectId()+EventManage.buildEventWhere(params.getFilter()));
		params.setDcName("eventSelect");
		params.setEngine(SQLEngine.Freemarker);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		Map<String,String> returnDescription=PostgreSQLUtils.buildReturnColumnDescription(columnDefines);
		dataset.setColumns(returnDescription.get("columns"));
		dataset.setLabelTexts(returnDescription.get("labelTexts"));
		dataset.setDataTypes(returnDescription.get("dataTypes"));
		return DataServiceUtils.dataLoad(dataSource, params,dataset);
	}
	/**
	 * 将事件插入到数据库中:实时表
	 * @param dataSource 数据集所在的DataSource
	 * @param columnDefines 事件字段列表
	 * @param projectId 项目ID
	 * @param rawDatas 待插入数据，key与columnDefine中的columnInDB相对应
	 */
	public static ResultPattern createEvent(DataSource dataSource,List<ColumnDefine> columnDefines,long projectId,Iterable<Map<String,Object>> rawDatas){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("data", rawDatas);
		PageDataSetEntity dataset=PageDataSetService.selectOneWithDataSource(dataSource,projectId, null, "createMassEvent");
		dataset.setExecSql(PostgreSQLUtils.buildBatchInsertSql(EventManage.eventTableName, columnDefines));
		filter.put("mainSQL", dataset.getExecSql());
		DataLoadParams params=new DataLoadParams();
		params.setDcName("createMassEvent");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		params.setEngine(SQLEngine.Freemarker);
		return DataServiceUtils.dataLoad(dataSource, params,dataset);
	}
	/**
	 * 将事件插入到数据库中:实时表和历史表
	 * @param dataSource 数据集所在的DataSource
	 * @param projectId 项目ID
	 * @param rawDatas 待插入数据，key与columnDefine中的columnInDB相对应
	 * @return
	 */
	public static ResultPattern createEvent(DataSource dataSource,long projectId,Iterable<Map<String,Object>> rawDatas){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("data", rawDatas);
		filter.put("ProjectID", projectId);
		DataLoadParams params=new DataLoadParams();
		params.setDcName("createMassEvents");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		params.setEngine(SQLEngine.Freemarker);
		return DataServiceUtils.dataLoad(dataSource, params);
	}
	/**
	 * 将恢复事件插入到数据库中:历史表
	 * @param dataSource 数据集所在的DataSource
	 * @param projectId 项目ID
	 * @param rawDatas 待插入数据，key与columnDefine中的columnInDB相对应
	 * @return
	 */
	public static ResultPattern createRecoveryEvent(DataSource dataSource,long projectId,Iterable<Map<String,Object>> rawDatas){
		Map<String,Object> filter=new HashMap<String,Object>();
		filter.put("data", rawDatas);
		filter.put("ProjectID", projectId);
		DataLoadParams params=new DataLoadParams();
		params.setDcName("createRecoveryEvent");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		params.setEngine(SQLEngine.Freemarker);
		return DataServiceUtils.dataLoad(dataSource, params);
	}
	/**
	 * 将事件插入到数据库中
	 * @param dataSource 数据集所在的DataSource
	 * @param columnDefines 事件字段列表
	 * @param values 待修改字段
	 * @param conditions 条件参数
	 */
	public static ResultPattern updateEvent(DataSource dataSource,List<ColumnDefine> columnDefines,long projectId,Map<String,Object> values,Map<String,Object> conditions){
		Map<String,Object> filter=new HashMap<String,Object>();
		if(values==null) return new ResultPattern();
		
		PageDataSetEntity dataset=PageDataSetService.selectOneWithDataSource(dataSource,projectId, null, "updateEvent");
		dataset.setExecSql(PostgreSQLUtils.buildBatchUpdateSql(EventManage.eventTableName,columnDefines, values,conditions));
		//if(conditions!=null)values.putAll(conditions);
		filter.put("value", values);
		filter.put("condition", conditions);
		filter.put("mainSQL", dataset.getExecSql());
		DataLoadParams params=new DataLoadParams();
		params.setDcName("updateEvent");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		params.setEngine(SQLEngine.Freemarker);
		return DataServiceUtils.dataLoad(dataSource, params,dataset);
	}
	/**
	 * 按照指定条件，删除事件
	 * @param dataSource 数据集所在事件源
	 * @param columnDefines 事件字段列表
	 * @param projectId 项目ID
	 * @param conditions 条件参数及参数值
	 * 
	 *	conditions.put("FirstOccurrence", 1622102072128l);
	 *	conditions.put("Tally", -99999);
	 *	conditions.put("TallyX", "XXXXXXXXX");
	 *	conditions.put("EventID", new String[]{"dddddd","ffffffff","gggggg"});
	 *	conditions.put("EventID2", new int[]{1,3,4});
	 * @throws JsonProcessingException 
	 */
	public static ResultPattern deleteEvent(DataSource dataSource,List<ColumnDefine> columnDefines,long projectId,Map<String,Object> conditions) throws JsonProcessingException{
		Map<String,Object> filter=new HashMap<String,Object>();
		PageDataSetEntity dataset=PageDataSetService.selectOneWithDataSource(dataSource,projectId, null, "updateEvent");
		dataset.setExecSql(PostgreSQLUtils.buildBatchDeleteSql(EventManage.eventTableName,columnDefines,conditions));
		filter.put("condition", conditions);
		filter.put("mainSQL", dataset.getExecSql());
		DataLoadParams params=new DataLoadParams();
		params.setDcName("deleteEvent");
		params.setProjectId(projectId);
		params.setFilter(FastJsonUtils.convertObjectToJSON(filter));
		params.setStart(1);
		params.setLimit(-10);
		params.setEngine(SQLEngine.Freemarker);
		return DataServiceUtils.dataLoad(dataSource, params,dataset);
	}
	/**
	 * 
	 * @param filter {"projectId":10,"engine":"Freemarker","start":1,"limit":10,"conditions":[{"column":"Severity","operator":"=","value":2,"dataType":"INT"},{"column":"Node","operator":"in","value":"173","dataType":"STRING"},{"column":"FirstOccurrence","operator":"between","value":["2021-06-30T16:00:00.000Z","2021-07-08T16:00:00.000Z"],"dataType":"LONGTIMESTAMP"}]}
	 * @return and "Severity"=2 and "Node" in('173') and FirstOccurrence between '2021-06-30T16:00:00.000Z' and '2021-07-08T16:00:00.000Z'
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JsonProcessingException 
	 */
	public static String buildEventWhere(String filter) throws IOException, ParseException{
		StringBuffer sb=new StringBuffer();
		Map<String,JavaDataType> nodes=new HashMap<String,JavaDataType>();
		//Condition.class.getFields();
		nodes.put("column", JavaDataType.STRING);
		nodes.put("operator", JavaDataType.STRING);
		nodes.put("dataType", JavaDataType.STRING);
		nodes.put("value", JavaDataType.OBJECT);
		//sb.append("and \"\"")
		List<Map<String,Object>> conditions=JSONUtils.parser("conditions", filter, nodes, null);
		for(Map<String,Object> condition:conditions){
			sb.append(" and \""+condition.get("column")+"\" ");
			sb.append(condition.get("operator"));sb.append(" ");
			if(condition.get("dataType").toString().equalsIgnoreCase("INT")||condition.get("dataType").toString().equalsIgnoreCase("FLOAT")){
				if(condition.get("operator").toString().equalsIgnoreCase("in")){
					sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"[", "]"},new String[]{"(",")"}));
				}else if(condition.get("operator").toString().equalsIgnoreCase("between")){
					sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"[","\"","]",","}, new String[]{"","",""," and "}));
				}else{
					sb.append(condition.get("value"));
				}
			}else if(condition.get("dataType").toString().equalsIgnoreCase("STRING")){
				if(condition.get("operator").toString().equalsIgnoreCase("in")){
					sb.append("('");sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"\""}, new String[]{""}));sb.append("')");
				}else if(condition.get("operator").toString().equalsIgnoreCase("like")){
					sb.append("'%");sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"\""}, new String[]{""}));sb.append("%'");
				}else{
					sb.append("'");sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"\""}, new String[]{""}));sb.append("'");
				}
			}else if(condition.get("dataType").toString().equalsIgnoreCase("LONGTIMESTAMP")){
				if(condition.get("operator").toString().equalsIgnoreCase("between")){
					sb.append(StringUtils.replaceEach(condition.get("value").toString(), new String[]{"[","]",","}, new String[]{"",""," and "}));
				}else{
					sb.append(condition.get("value"));
				}
			}
		}
		return sb.toString();
	}
	public static void main(String[] args) throws IOException, ParseException {
		//TableDefine tableDefine=new TableDefine();
		//tableDefine.setName("ueh_conf.test");
		//List<ColumnDefine> columns=new ArrayList<ColumnDefine>();
		
		/*String url="jdbc:postgresql://127.0.0.1:5432/ueh?currentSchema=ueh_admin&TimeZone=PRC&useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT";
		String driverClassName="org.postgresql.Driver";
		Properties prop=new Properties();
		prop.put("initial.size", "5");
		prop.put("max.total", "10");
		prop.put("min.idle", "2");
		prop.put("max.wait.seconds", "20");
		DataSource dataSource = DataSourceUtils.setupDataSource(url,driverClassName,"postgresql","root","123456",prop);
		//查询
		PlatformEventColumnConfig columnConfig=new PlatformEventColumnConfig();
		columnConfig.setDataSource(dataSource);
		Map<String,Object> conditions=new HashMap<String,Object>();
		conditions.put("isEnable", "Y");
		//conditions.put("columnType", "C");
		List<ColumnDefine> columns=columnConfig.getPlatformEventColumn(10, conditions);
		DataLoadParams params=new DataLoadParams();
		params.setEngine(SQLEngine.Freemarker);
		params.setProjectId(10l);*/
		//查询测试
		//ResultPattern resultPattern=EventManage.query(dataSource, columns, params);
		//System.out.println(resultPattern);
		//创建表测试
		//EventManage.createTable(dataSource, columns, params);
		//插入测试
		/*List<Map<String,Object>> rawDatas=new ArrayList<Map<String,Object>>();
		for(int i=0;i<100;i++){
			Map<String,Object> data=new HashMap<String,Object>();
			for(ColumnDefine columnDefine:columns){
				//data=new HashMap<String,Object>();
				if(columnDefine.getDataType()==DataType.FLOAT){
					data.put(columnDefine.getColumnInDB(), 0.99999f);
				}else if(columnDefine.getDataType()==DataType.INT){
					data.put(columnDefine.getColumnInDB(), 999999);
				}else if(columnDefine.getDataType()==DataType.LONGTIMESTAMP){
					data.put(columnDefine.getColumnInDB(), System.currentTimeMillis());
				}else if(columnDefine.getDataType()==DataType.STRING){
					data.put(columnDefine.getColumnInDB(), "插入测试"+i);
				}
			}
			rawDatas.add(data);
		}
		
		EventManage.createEvent(dataSource,columns, 10l, rawDatas);*/
		//修改测试
		/*Map<String,Object> values=new HashMap<String,Object>();
		values.put("Tally", -99999);
		//Map<String,Object> conditions=new HashMap<String,Object>();
		conditions.clear();
		conditions.put("FirstOccurrence", 1622102072128l);
		conditions.put("LastOccurrence", 1622102072128l);
		conditions.put("Tally", -99999);
		//EventManage.updateEvent(dataSource,columns, 10l, values,conditions);
		EventManage.deleteEvent(dataSource, columns, 10l, conditions);*/
		//String filter="{\"projectId\":10,\"engine\":\"Freemarker\",\"start\":1,\"limit\":10,\"conditions\":[{\"column\":\"Severity\",\"operator\":\"=\",\"value\":2,\"dataType\":\"INT\"},{\"column\":\"Node\",\"operator\":\"in\",\"value\":\"173\",\"dataType\":\"STRING\"},{\"column\":\"FirstOccurrence\",\"operator\":\"between\",\"value\":[1625068800000,1625760000000],\"dataType\":\"LONGTIMESTAMP\"}]}";
		//String filter="{\"projectId\":10,\"engine\":\"Freemarker\",\"start\":1,\"limit\":10,\"conditions\":[{\"column\":\"Severity\",\"operator\":\"=\",\"value\":[2],\"dataType\":\"INT\"},{\"column\":\"Node\",\"operator\":\"in\",\"value\":\"100\",\"dataType\":\"STRING\"},{\"column\":\"NodeAlias\",\"operator\":\"like\",\"value\":\"222\",\"dataType\":\"STRING\"},{\"column\":\"FirstOccurrence\",\"operator\":\"between\",\"value\":[1625068800000,1625760000000],\"dataType\":\"LONGTIMESTAMP\"},{\"column\":\"Tally\",\"operator\":\">\",\"value\":\"11\",\"dataType\":\"INT\"},{\"column\":\"Summary\",\"operator\":\"!=\",\"value\":\"5555\",\"dataType\":\"STRING\"}]}";
		/*String filter="{\"projectId\":10,\"engine\":\"Freemarker\",\"start\":1,\"limit\":10,\"conditions\":[]}";
		System.out.println(EventManage.buildEventWhere(filter));*/
		/*Field[] fields=Condition.class.getDeclaredFields();
		for(Field field:fields){
			System.out.println(field.getName());
			System.out.println(field.getGenericType().getTypeName());
			System.out.println(field.getDeclaringClass().getName());
		}*/
	}
}
