package com.china.alert.project.tool.swagger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.china.alert.framework.config.SystemConfig;
import com.china.alert.framework.web.domain.AjaxResult;
import com.china.alert.project.system.service.ISysDictDataService;

import cn.mcinfotech.data.service.domain.DataLoadParams;
import cn.mcinfotech.data.service.domain.ResultPattern;
import cn.mcinfotech.data.service.util.DataServiceUtils;

/**
 * date 2021/10/29 16:39
 * @version V1.0
 * @Package com.china.alert
 */
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    DataSource dataSource;
    @Autowired
    ISysDictDataService iSysDictDataService;

    /**
     * 导出历史告警数据
     *
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("exportHistory")
    public AjaxResult exportHistory(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("getEventsForHistory", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportHistoryData.xlsx";
            String filename = exportHistoryData(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }

    /**
     * 导出实时告警数据
     *
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("exportStatus")
    public AjaxResult exportStatus(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("getEventsForRealtime", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportStatusData.xlsx";
            String filename = exportStatusData(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }

    /**
     * 导出未恢复告警数据
     *
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("exportWaitRecover")
    public AjaxResult exportWaitRecover(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("getEventsForWaitRecover", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportStatusData.xlsx";
            String filename = exportStatusData(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }

    /**
     * 导出设备信息
     *
     * @param map
     * @return
     * @throws IOException
     */
    @PostMapping("exportDevice")
    public AjaxResult exportDevice(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("getResourceList", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportDeviceData.xlsx";
            String filename = exportDeviceData(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }
    @PostMapping("exportCustomList")
    public AjaxResult exportCustomList(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("exportCustomList", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportCustomMonitor.xlsx";
            String filename = exportCustomListData(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }

    @PostMapping("exportPerformanceData")
    public AjaxResult exportPerformanceData(@RequestBody Map<String, Object> map) throws IOException {
        ResultPattern resultPattern = getDatas("exportPerformanceData", map);
        List<Map<String, Object>> datas = resultPattern.getDatas();
        AjaxResult result = new AjaxResult();
        if (datas != null && datas.size() > 0) {
            String path = "template/exportPerformanceData.xlsx";
            String filename = exportPerformanceDataExcel(datas, path);
            result = AjaxResult.success(filename);
        } else {
            result = AjaxResult.error("没有数据可以导出");
        }
        return result;
    }
    /**
     * 下载设备导入模板
     *
     * @return
     * @throws IOException
     */
    /*@PostMapping("importDeviceTemplate")
    public AjaxResult importDeviceTemplate() throws IOException {
        List<Map<String, Object>> datas = new ArrayList<>();
        String path = "template/设备导出模板.xlsx";
        String filename = export(datas, path);
        return AjaxResult.success(filename);
    }

    @PostMapping("importDevice")
    public AjaxResult importExcel(@RequestParam("file") MultipartFile multipartFile) {
        Map<Object, Object> pycMap = getPysicalMap();
        Map<Object, Object> groupMap = getHostGroup();
        Map<Object, Object> hostMap = getHost("5");
        Map<Object, Object> tempateMap = getHost("3");
        //MultipartFile转换成File
        File file = MultipartFile2FileUtil.MultipartFile2File(multipartFile);
        //需要解析的Excel文件
        try {
            //获取工作簿
            FileInputStream fs = FileUtils.openInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(fs);
            //获取最Excel中的sheet数量
            int numberOfSheets = workbook.getNumberOfSheets();
            for (int j = 0; j < 1; j++) {
                //获取第一个工作表
                XSSFSheet hs = workbook.getSheetAt(j);
                //获取Sheet的第一个行号和最后一个行号
                int last = hs.getLastRowNum();
                int first = hs.getFirstRowNum() + 1;
                List<Map> list = getSheetDataMaps(pycMap, hs, last, first);
                //校验字段有效性
                String s1 = validateSheet(list, pycMap, groupMap, hostMap, tempateMap);
                if (StringUtils.isNotBlank(s1)) {
                    return AjaxResult.error(500, s1);
                }
                if (list.size() > 0) {
                    for (Map map : list) {
                        //入库
                        String count = getStrData("getResourceByKey", map);
                        String dcName = "insertResource";
                        if (!"0".equals(count)) {
                            dcName = "updateResourceByKey";
                        }
                        ResultPattern datas = getDatas(dcName, map);
                        if (datas.isSuccess()) {
                            SysDictData sysDictDataUrl = iSysDictDataService.selectDictDataByTypeAndLabel(10, "monitor_tool", "zabbix_url");
                            SysDictData sysDictDataName = iSysDictDataService.selectDictDataByTypeAndLabel(10, "monitor_tool", "zabbix_userName");
                            SysDictData sysDictDataPass = iSysDictDataService.selectDictDataByTypeAndLabel(10, "monitor_tool", "zabbix_password");
                            String zabbixUrl = sysDictDataUrl.getDictValue();
                            String zabbixAdmin = sysDictDataName.getDictValue();
                            String zabbixPassword = sysDictDataPass.getDictValue();
                            Integer classId = Integer.valueOf(String.valueOf(map.get("class_id")));
                            String host = String.valueOf(map.get("key_"));
                            String hostIp = String.valueOf(map.get("ip_address"));
                            String visibleName = map.get("label_text") == null ? String.valueOf(map.get("name")) : String.valueOf(map.get("label_text"));
                            String proxyip = map.get("proxyip") == null ? null : hostMap.get(map.get("proxyip")) == null ? null : String.valueOf(hostMap.get(map.get("proxyip")));
                            List<String> groups = new ArrayList<>();
                            for (String s : String.valueOf(map.get("groups")).split("#")) {
                                if (groupMap.get(s) != null) {
                                    groups.add(String.valueOf(groupMap.get(s)));
                                }
                            }
                            List<String> templates = new ArrayList<>();
                            if (ObjectUtils.allNotNull(map.get("templates"))) {
                                for (String s : String.valueOf(map.get("templates")).split("#")) {
                                    if (tempateMap.get(s) != null) {
                                        templates.add(String.valueOf(tempateMap.get(s)));
                                    }
                                }
                            }
                            //添加到zabbixS
                            ZabbixHostHelper.monitor(dataSource, host, hostIp, proxyip, visibleName, groups, templates, "0",zabbixUrl, zabbixAdmin, zabbixPassword);
                        } else {
                            return AjaxResult.error(500, datas.getErrorMsg());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error(500, e.getMessage());
        } finally {
            //清空文件缓存，并且调用delete0关闭文件
            MultipartFile2FileUtil.deleteFile(file);
        }
        return AjaxResult.success();
    }*/

    private String getStrData(String dcName, Map map) {
        ResultPattern datas = getDatas(dcName, map);
        return datas.getStrData();
    }

    private String validateSheet(List<Map> list, Map<Object, Object> pycMap, Map<Object, Object> groupMap, Map<Object, Object> hostMap, Map<Object, Object> tempateMap) {
        String result = validateIfnull(list);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        result = validatePhysicalCategory(list, pycMap);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        result = validateTemplate(list, tempateMap);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        result = validateHostGroup(list, groupMap);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        result = validateAgentProxy(list, hostMap);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return result;
    }

    private String validateAgentProxy(List<Map> list, Map<Object, Object> hostMap) {
        for (Map map : list) {
            Object proxyip = map.get("proxyip");
            Object rowNum = map.get("rowNum");
            if (proxyip != null && StringUtils.isNotBlank(String.valueOf(proxyip)) && hostMap.get(proxyip) == null)
                return "第" + String.valueOf(rowNum) + "行agent代理程序填写错误";
        }
        return null;
    }

    private String validateHostGroup(List<Map> list, Map<Object, Object> groupMap) {
        for (Map map : list) {
            Object rowNum = map.get("rowNum");
            Object groups = map.get("groups");
            String[] split = String.valueOf(groups).split("#");
            for (String s : split) {
                Object o = groupMap.get(s);
                if (o == null) {
                    return "第" + rowNum + "行群组" + s + "填写错误";
                }
            }
        }
        return null;
    }

    private String validateTemplate(List<Map> list, Map<Object, Object> tempateMap) {
        for (Map map : list) {
            Object rowNum = map.get("rowNum");
            Object templates = map.get("templates");
            if (ObjectUtils.allNotNull(templates) && !templates.equals("")) {
                String[] split = String.valueOf(templates).split("#");
                for (String s : split) {
                    if (StringUtils.isNotBlank(s)) {
                        Object o = tempateMap.get(s);
                        if (o == null) {
                            return "第" + rowNum + "行模板" + s + "填写错误";
                        }
                    }
                }
            }
        }
        return null;
    }

    private String validatePhysicalCategory(List<Map> list, Map<Object, Object> pycMap) {
        for (Map map : list) {
            Object class_id = map.get("class_id");
            Object rowNum = map.get("rowNum");
            if (class_id == null || class_id.equals("")) return "第" + rowNum + "行设备类别填写错误";
        }
        return null;
    }

    private String validateIfnull(List<Map> list) {
        for (Map map : list) {
            Object class_id = map.get("class_id");
            Object name = map.get("name");
            Object key_ = map.get("key_");
            Object ip_address = map.get("ip_address");
            Object vendor = map.get("vendor");
            Object groups = map.get("groups");
            Object rowNum = map.get("rowNum");
            boolean b = ObjectUtils.allNotNull(class_id, name, key_, ip_address, vendor, groups);
            if (!b) {
                if (class_id == null) return "第" + String.valueOf(rowNum) + "行设备类别为空";
                if (name == null) return "第" + String.valueOf(rowNum) + "行设备名称为空";
                if (key_ == null) return "第" + String.valueOf(rowNum) + "行设备标识为空";
                if (ip_address == null) return "第" + String.valueOf(rowNum) + "行IP地址为空";
                if (vendor == null) return "第" + String.valueOf(rowNum) + "行厂商为空";
                if (groups == null) return "第" + String.valueOf(rowNum) + "行群组为空";
            }
            if (b) {
                if (class_id.equals("")) return "第" + String.valueOf(rowNum) + "行设备类别为空";
                if (name.equals("")) return "第" + String.valueOf(rowNum) + "行设备名称为空";
                if (key_.equals("")) return "第" + String.valueOf(rowNum) + "行设备标识为空";
                if (ip_address.equals("")) return "第" + String.valueOf(rowNum) + "行IP地址为空";
                if (vendor.equals("")) return "第" + String.valueOf(rowNum) + "行厂商为空";
                if (groups.equals("")) return "第" + String.valueOf(rowNum) + "行群组为空";
            }
        }
        return null;
    }

    /**
     * sheet数据封装到list
     *
     * @param pycMap 物理分类map
     * @param hs     excel sheet
     * @param last   最后一个行号
     * @param first  第一个行号
     * @return
     */
    private List<Map> getSheetDataMaps(Map<Object, Object> pycMap, XSSFSheet hs, int last, int first) {
        //遍历获取单元格里的信息
        //List<AuditContentImportBo> list = new ArrayList<>();
        //注：row为行   cell为列
        List<Map> list = new ArrayList<>();
        for (int i = first + 1; i <= last; i++) {
            Map<String, Object> map = new HashMap<>();
            XSSFRow row = hs.getRow(i);
            map.put("rowNum", i + 1);

            XSSFCell cell0 = row.getCell(0);
            if (cell0 != null) {
                cell0.setCellType(CellType.STRING);
                map.put("class_id", pycMap.get(cell0.getStringCellValue().toUpperCase()));
            }

            XSSFCell cell1 = row.getCell(1);
            if (cell1 != null) {
                cell1.setCellType(CellType.STRING);
                map.put("name", cell1.getStringCellValue().toUpperCase());
            }

            XSSFCell cell3 = row.getCell(3);
            if (cell3 != null) {
                cell3.setCellType(CellType.STRING);
                map.put("key_", cell3.getStringCellValue().trim());
            }

            XSSFCell cell2 = row.getCell(2);
            if (cell2 != null) {
                cell2.setCellType(CellType.STRING);
                map.put("label_text", cell2.getStringCellValue().trim());
            }else {
                map.put("label_text", map.get("name"));
            }

            XSSFCell cell4 = row.getCell(4);
            if (cell4 != null) {
                cell4.setCellType(CellType.STRING);
                map.put("ip_address", cell4.getStringCellValue().trim());

            }

            XSSFCell cell5 = row.getCell(5);
            if (cell5 != null) {
                cell5.setCellType(CellType.STRING);
                map.put("control_ip", cell5.getStringCellValue().trim());
            }

            XSSFCell cell6 = row.getCell(6);
            if (cell6 != null) {
                cell6.setCellType(CellType.STRING);
                map.put("owner", cell6.getStringCellValue().trim());
            }

            XSSFCell cell7 = row.getCell(7);
            if (cell7 != null) {
                cell7.setCellType(CellType.STRING);
                map.put("area", cell7.getStringCellValue().trim());
            }

            XSSFCell cell8 = row.getCell(8);
            if (cell8 != null) {
                cell8.setCellType(CellType.STRING);
                map.put("vendor", cell8.getStringCellValue().trim());
            }

            XSSFCell cell9 = row.getCell(9);
            if (cell9 != null) {
                cell9.setCellType(CellType.STRING);
                map.put("templates", cell9.getStringCellValue().trim());
            }

            XSSFCell cell10 = row.getCell(10);
            if (cell10 != null) {
                cell10.setCellType(CellType.STRING);
                map.put("groups", cell10.getStringCellValue().trim());
            }

            XSSFCell cell11 = row.getCell(11);
            if (cell11 != null) {
                cell11.setCellType(CellType.STRING);
                map.put("proxyip", cell11.getStringCellValue().trim());
            }

            XSSFCell cell12 = row.getCell(12);
            if (cell12 != null) {
                cell12.setCellType(CellType.STRING);
                map.put("remark", cell12.getStringCellValue().trim());
            }

            list.add(map);
        }
        return list;
    }

    private Map<Object, Object> getHost(String status) {
        Map filter = new HashMap();
        filter.put("status", status);
        ResultPattern resultPattern = getDatas("getZabbixHost", filter);
        List<Map<String, Object>> hostList = resultPattern.getDatas();
        HashMap<Object, Object> hostMap = new HashMap<>();
        for (Map<String, Object> map : hostList) {
            hostMap.put(map.get("host"), map.get("hostid"));
        }
        return hostMap;
    }

    private Map<Object, Object> getHostGroup() {
        ResultPattern resultPattern = getDatas("getZabbixHostGroups", new HashMap());
        List<Map<String, Object>> groupList = resultPattern.getDatas();
        HashMap<Object, Object> groupMap = new HashMap<>();
        for (Map<String, Object> map : groupList) {
            groupMap.put(map.get("name"), map.get("groupid"));
        }
        return groupMap;
    }

    private Map<Object, Object> getPysicalMap() {
        ResultPattern resultPattern = getDatas("getPysicalCategoryImport", new HashMap());
        List<Map<String, Object>> pycList = resultPattern.getDatas();
        Map<Object, Object> pycMap = new HashMap<>();
        for (Map<String, Object> map : pycList) {
            pycMap.put(map.get("short_name"), map.get("id"));
        }
        return pycMap;
    }

    private String exportPerformanceDataExcel(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
        for (int i = 0; i < datas.size(); i++) {
            Row row1 = sheet.createRow(i + 1);
            Map<String, Object> data = datas.get(i);
            Cell cell = row1.createCell(0);
            Object cellData = data.get("host");
            if (cellData != null) {
                cell.setCellValue(String.valueOf(cellData));
            } else {
                cell.setCellValue("");
            }
            Cell cell1 = row1.createCell(1);
            Object cellData1 = data.get("lable_text");
            if (cellData1 != null) {
                cell1.setCellValue(String.valueOf(cellData1));
            } else {
                cell1.setCellValue("");
            }
            Cell cell2 = row1.createCell(2);
            Object cellData2 = data.get("clockTime");
            if (cellData2 != null) {
                cell2.setCellValue(String.valueOf(cellData2));
            } else {
                cell2.setCellValue("");
            }
            Cell cell3 = row1.createCell(3);
            Object cellData3 = data.get("value");
            if (cellData3 != null) {
                cell3.setCellValue(String.valueOf(cellData3));
            } else {
                cell3.setCellValue("");
            }
            Cell cell4 = row1.createCell(4);
            Object cellData4 = data.get("tag");
            if (cellData4 != null) {
                cell4.setCellValue(String.valueOf(cellData4));
            } else {
                cell4.setCellValue("");
            }
        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }
    private String exportStatusData(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
        for (int i = 0; i < datas.size(); i++) {
            Row row1 = sheet.createRow(i + 1);
            Map<String, Object> data = datas.get(i);
            Cell cell = row1.createCell(0);
            Object cellData = data.get("st");
            if (cellData != null) {
                cell.setCellValue(String.valueOf(cellData));
            } else {
                cell.setCellValue("");
            }
            Cell cell1 = row1.createCell(1);
            Object cellData1 = data.get("EventCategoryLabelText");
            if (cellData1 != null) {
                cell1.setCellValue(String.valueOf(cellData1));
            } else {
                cell1.setCellValue("");
            }
            Cell cell2 = row1.createCell(2);
            Object cellData2 = data.get("Node");
            if (cellData2 != null) {
                cell2.setCellValue(String.valueOf(cellData2));
            } else {
                cell2.setCellValue("");
            }
            Cell cell3 = row1.createCell(3);
            Object cellData3 = data.get("NodeAlias");
            if (cellData3 != null) {
                cell3.setCellValue(String.valueOf(cellData3));
            } else {
                cell3.setCellValue("");
            }
            Cell cell4 = row1.createCell(4);
            Object cellData4 = data.get("fs");
            if (cellData4 != null) {
                cell4.setCellValue(String.valueOf(cellData4));
            } else {
                cell4.setCellValue("");
            }
            Cell cell5 = row1.createCell(5);
            Object cellData5 = data.get("lo");
            if (cellData5 != null) {
                cell5.setCellValue(String.valueOf(cellData5));
            } else {
                cell5.setCellValue("");
            }
            Cell cell6 = row1.createCell(6);
            Object cellData6 = data.get("Tally");
            if (cellData6 != null) {
                cell6.setCellValue(String.valueOf(cellData6));
            } else {
                cell6.setCellValue("");
            }
            Cell cell7 = row1.createCell(7);
            Object cellData7 = data.get("Manager");
            if (cellData7 != null) {
                cell7.setCellValue(String.valueOf(cellData7));
            } else {
                cell7.setCellValue("");
            }
            Cell cell8 = row1.createCell(8);
            Object cellData8 = data.get("Summary");
            if (cellData8 != null) {
                cell8.setCellValue(String.valueOf(cellData8));
            } else {
                cell8.setCellValue("");
            }
            Cell cell9 = row1.createCell(9);
            Object cellData9 = data.get("ic");
            if (cellData9 != null) {
                cell9.setCellValue(String.valueOf(cellData9));
            } else {
                cell9.setCellValue("");
            }
            Cell cell10 = row1.createCell(10);
            Object cellData10 = data.get("ag");
            if (cellData10 != null) {
                cell10.setCellValue(String.valueOf(cellData10));
            } else {
                cell10.setCellValue("");
            }
            Cell cell11 = row1.createCell(11);
            Object cellData11 = data.get("RecoveredStatusTag");
            if (cellData11 != null) {
                cell11.setCellValue(String.valueOf(cellData11));
            } else {
                cell11.setCellValue("");
            }
            Cell cell12 = row1.createCell(12);
            Object cellData12 = data.get("ost");
            if (cellData12 != null) {
                cell12.setCellValue(String.valueOf(cellData12));
            } else {
                cell12.setCellValue("");
            }
            Cell cell13 = row1.createCell(13);
            Object cellData13 = data.get("AlertGroup");
            if (cellData13 != null) {
                cell13.setCellValue(String.valueOf(cellData13));
            } else {
                cell13.setCellValue("");
            }
            Cell cell14 = row1.createCell(14);
            Object cellData14 = data.get("AlertKey");
            if (cellData14 != null) {
                cell14.setCellValue(String.valueOf(cellData14));
            } else {
                cell14.setCellValue("");
            }
        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }
    private String exportDeviceData(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
        for (int i = 0; i < datas.size(); i++) {
            Row row1 = sheet.createRow(i + 1);
            Map<String, Object> data = datas.get(i);
            Cell cell = row1.createCell(0);
            Object cellData = data.get("className");
            if (cellData != null) {
                cell.setCellValue(String.valueOf(cellData));
            } else {
                cell.setCellValue("");
            }
            Cell cell1 = row1.createCell(1);
            Object cellData1 = data.get("name");
            if (cellData1 != null) {
                cell1.setCellValue(String.valueOf(cellData1));
            } else {
                cell1.setCellValue("");
            }
            Cell cell2 = row1.createCell(2);
            Object cellData2 = data.get("labelText");
            if (cellData2 != null) {
                cell2.setCellValue(String.valueOf(cellData2));
            } else {
                cell2.setCellValue("");
            }
            Cell cell3 = row1.createCell(3);
            Object cellData3 = data.get("key_");
            if (cellData3 != null) {
                cell3.setCellValue(String.valueOf(cellData3));
            } else {
                cell3.setCellValue("");
            }
            Cell cell4 = row1.createCell(4);
            Object cellData4 = data.get("ipAddress");
            if (cellData4 != null) {
                cell4.setCellValue(String.valueOf(cellData4));
            } else {
                cell4.setCellValue("");
            }
            Cell cell5 = row1.createCell(5);
            Object cellData5 = data.get("owner");
            if (cellData5 != null) {
                cell5.setCellValue(String.valueOf(cellData5));
            } else {
                cell5.setCellValue("");
            }
            Cell cell6 = row1.createCell(6);
            Object cellData6 = data.get("vendor");
            if (cellData6 != null) {
                cell6.setCellValue(String.valueOf(cellData6));
            } else {
                cell6.setCellValue("");
            }
            Cell cell7 = row1.createCell(7);
            Object cellData7 = data.get("create_timestamp");
            if (cellData7 != null) {
                cell7.setCellValue(String.valueOf(cellData7));
            } else {
                cell7.setCellValue("");
            }
            Cell cell8 = row1.createCell(8);
            Object cellData8 = data.get("ie");
            if (cellData8 != null) {
                cell8.setCellValue(String.valueOf(cellData8));
            } else {
                cell8.setCellValue("");
            }
            Cell cell9 = row1.createCell(9);
            Object cellData9 = data.get("remark");
            if (cellData9 != null) {
                cell9.setCellValue(String.valueOf(cellData9));
            } else {
                cell9.setCellValue("");
            }
        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }
    private String exportCustomListData(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        if (CollectionUtils.isNotEmpty(datas)) {
            List<Map<String, Object>> webAndPingMonitorList=new ArrayList<>();
            List<Map<String, Object>> procMonitorList=new ArrayList<>();
            List<Map<String, Object>> logMonitorList=new ArrayList<>();
            List<Map<String, Object>> portMonitorList=new ArrayList<>();
            for (Map<String, Object> data : datas) {
                String monitorType = (String)data.get("monitor_type");
                if ("PROC".equalsIgnoreCase(monitorType)) {
                    procMonitorList.add(data);
                } else if ("LOG".equalsIgnoreCase(monitorType)) {
                    logMonitorList.add(data);
                } else if ("WBP".equalsIgnoreCase(monitorType)) {
                    webAndPingMonitorList.add(data);
                } else if ("PING".equalsIgnoreCase(monitorType)) {
                    webAndPingMonitorList.add(data);
                } else if ("PORT".equalsIgnoreCase(monitorType)) {
                    portMonitorList.add(data);
                }
            }
            if (CollectionUtils.isNotEmpty(webAndPingMonitorList)) {
                SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
                for (int i = 0; i < webAndPingMonitorList.size(); i++) {
                    Row row1 = sheet.createRow(i + 1);
                    Map<String, Object> data = webAndPingMonitorList.get(i);
                    Cell cell = row1.createCell(0);
                    Object cellData = data.get("monitor_name");
                    if (cellData != null) {
                        cell.setCellValue(String.valueOf(cellData));
                    } else {
                        cell.setCellValue("");
                    }
                    Cell cell1 = row1.createCell(1);
                    Object cellData1 = data.get("monitor_type");
                    if (cellData1 != null) {
                        cell1.setCellValue(String.valueOf(cellData1));
                    } else {
                        cell1.setCellValue("");
                    }
                    Cell cell2 = row1.createCell(2);
                    Object cellData2 = data.get("monitored_proxy");
                    if (cellData2 != null) {
                        cell2.setCellValue(String.valueOf(cellData2));
                    } else {
                        cell2.setCellValue("");
                    }
                    Cell cell3 = row1.createCell(3);
                    Object cellData3 = data.get("monitored_host");
                    if (cellData3 != null) {
                        cell3.setCellValue(String.valueOf(cellData3));
                    } else {
                        cell3.setCellValue("");
                    }
                    Cell cell4 = row1.createCell(4);
                    Object cellData4 = data.get("day_of_week_at");
                    if (cellData4 != null) {
                        cell4.setCellValue(String.valueOf(cellData4));
                    } else {
                        cell4.setCellValue("");
                    }
                    Cell cell5 = row1.createCell(5);
                    Object cellData5 = data.get("execute_at");
                    if (cellData5 != null) {
                        cell5.setCellValue(String.valueOf(cellData5));
                    } else {
                        cell5.setCellValue("");
                    }
                    Cell cell6 = row1.createCell(6);
                    Object cellData6 = data.get("execute_util");
                    if (cellData6 != null) {
                        cell6.setCellValue(String.valueOf(cellData6));
                    } else {
                        cell6.setCellValue("");
                    }
                    Cell cell7 = row1.createCell(7);
                    Object cellData7 = data.get("effect_timestamp");
                    if (cellData7 != null) {
                        cell7.setCellValue(String.valueOf(cellData7));
                    } else {
                        cell7.setCellValue("");
                    }
                    Cell cell8 = row1.createCell(8);
                    Object cellData8 = data.get("is_effect");
                    if (cellData8 != null) {
                        cell8.setCellValue(String.valueOf(cellData8));
                    } else {
                        cell8.setCellValue("");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(procMonitorList)) {
                SXSSFSheet sheet = sxssfworkbook.getSheetAt(1);
                for (int i = 0; i < procMonitorList.size(); i++) {
                    Row row1 = sheet.createRow(i + 1);
                    Map<String, Object> data = procMonitorList.get(i);
                    String monitorParam = (String)data.get("monitor_param");
                    Map map = JSON.parseObject(monitorParam, Map.class);
                    Cell cell = row1.createCell(0);
                    Object cellData = data.get("monitor_name");
                    if (cellData != null) {
                        cell.setCellValue(String.valueOf(cellData));
                    } else {
                        cell.setCellValue("");
                    }
                    Cell cell1 = row1.createCell(1);
                    Object cellData1 = data.get("monitor_type");
                    if (cellData1 != null) {
                        cell1.setCellValue(String.valueOf(cellData1));
                    } else {
                        cell1.setCellValue("");
                    }
                    Cell cell2 = row1.createCell(2);
                    Object cellData2 = data.get("monitored_host");
                    if (cellData2 != null) {
                        cell2.setCellValue(String.valueOf(cellData2));
                    } else {
                        cell2.setCellValue("");
                    }
                    Cell cell3 = row1.createCell(3);
                    Object cellData3 = map.get("proc_flag");
                    if (cellData3 != null) {
                        cell3.setCellValue(String.valueOf(cellData3));
                    } else {
                        cell3.setCellValue("");
                    }
                    Cell cell4 = row1.createCell(4);
                    Object cellData4 = map.get("proc_user");
                    if (cellData4 != null) {
                        cell4.setCellValue(String.valueOf(cellData4));
                    } else {
                        cell4.setCellValue("");
                    }
                    Cell cell5 = row1.createCell(5);
                    Object cellData5 = map.get("parent_proc_id");
                    if (cellData5 != null) {
                        cell5.setCellValue(String.valueOf(cellData5));
                    } else {
                        cell5.setCellValue("");
                    }
                    Cell cell6 = row1.createCell(6);
                    Object cellData6 = data.get("day_of_week_at");
                    if (cellData6 != null) {
                        cell6.setCellValue(String.valueOf(cellData6));
                    } else {
                        cell6.setCellValue("");
                    }
                    Cell cell7 = row1.createCell(7);
                    Object cellData7 = data.get("execute_at");
                    if (cellData7 != null) {
                        cell7.setCellValue(String.valueOf(cellData7));
                    } else {
                        cell7.setCellValue("");
                    }
                    Cell cell8 = row1.createCell(8);
                    Object cellData8 = data.get("execute_util");
                    if (cellData8 != null) {
                        cell8.setCellValue(String.valueOf(cellData8));
                    } else {
                        cell8.setCellValue("");
                    }
                    Cell cell9 = row1.createCell(9);
                    Object cellData9 = map.get("alarm_operator");
                    if (cellData9 != null) {
                        cell9.setCellValue(String.valueOf(cellData9));
                    } else {
                        cell9.setCellValue("");
                    }
                    Cell cell10 = row1.createCell(10);
                    Object cellData10 = map.get("threshold");
                    if (cellData10 != null) {
                        cell10.setCellValue(String.valueOf(cellData10));
                    } else {
                        cell10.setCellValue("");
                    }
                    Cell cell11 = row1.createCell(11);
                    Object cellData11 = map.get("alarm_severity");
                    if (cellData11 != null) {
                        cell11.setCellValue(String.valueOf(cellData11));
                    } else {
                        cell11.setCellValue("");
                    }
                    Cell cell12 = row1.createCell(12);
                    Object cellData12 = data.get("effect_timestamp");
                    if (cellData12 != null) {
                        cell12.setCellValue(String.valueOf(cellData12));
                    } else {
                        cell12.setCellValue("");
                    }
                    Cell cell13 = row1.createCell(13);
                    Object cellData13 = data.get("is_effect");
                    if (cellData13 != null) {
                        cell13.setCellValue(String.valueOf(cellData13));
                    } else {
                        cell13.setCellValue("");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(logMonitorList)) {
                SXSSFSheet sheet = sxssfworkbook.getSheetAt(2);
                for (int i = 0; i < logMonitorList.size(); i++) {
                    Row row1 = sheet.createRow(i + 1);
                    Map<String, Object> data = logMonitorList.get(i);
                    String monitorParam = (String)data.get("monitor_param");
                    Map map = JSON.parseObject(monitorParam, Map.class);
                    Cell cell = row1.createCell(0);
                    Object cellData = data.get("monitor_name");
                    if (cellData != null) {
                        cell.setCellValue(String.valueOf(cellData));
                    } else {
                        cell.setCellValue("");
                    }
                    Cell cell1 = row1.createCell(1);
                    Object cellData1 = data.get("monitor_type");
                    if (cellData1 != null) {
                        cell1.setCellValue(String.valueOf(cellData1));
                    } else {
                        cell1.setCellValue("");
                    }
                    Cell cell2 = row1.createCell(2);
                    Object cellData2 = data.get("monitored_host");
                    if (cellData2 != null) {
                        cell2.setCellValue(String.valueOf(cellData2));
                    } else {
                        cell2.setCellValue("");
                    }
                    Cell cell3 = row1.createCell(3);
                    Object cellData3 = map.get("logpath");
                    if (cellData3 != null) {
                        cell3.setCellValue(String.valueOf(cellData3));
                    } else {
                        cell3.setCellValue("");
                    }
                    Cell cell4 = row1.createCell(4);
                    Object cellData4 = map.get("logkey");
                    if (cellData4 != null) {
                        cell4.setCellValue(String.valueOf(cellData4));
                    } else {
                        cell4.setCellValue("");
                    }
                    Cell cell5 = row1.createCell(5);
                    Object cellData5 = map.get("logtype");
                    if (cellData5 != null) {
                        cell5.setCellValue(String.valueOf(cellData5));
                    } else {
                        cell5.setCellValue("");
                    }
                    Cell cell6 = row1.createCell(6);
                    Object cellData6 = data.get("effect_timestamp");
                    if (cellData6 != null) {
                        cell6.setCellValue(String.valueOf(cellData6));
                    } else {
                        cell6.setCellValue("");
                    }
                    Cell cell7 = row1.createCell(7);
                    Object cellData7 = data.get("is_effect");
                    if (cellData7 != null) {
                        cell7.setCellValue(String.valueOf(cellData7));
                    } else {
                        cell7.setCellValue("");
                    }
                }
            }
            if (CollectionUtils.isNotEmpty(portMonitorList)) {
                SXSSFSheet sheet = sxssfworkbook.getSheetAt(3);
                for (int i = 0; i < portMonitorList.size(); i++) {
                    Row row1 = sheet.createRow(i + 1);
                    Map<String, Object> data = portMonitorList.get(i);
                    String monitorParam = (String)data.get("monitor_param");
                    Map map = JSON.parseObject(monitorParam, Map.class);
                    Cell cell = row1.createCell(0);
                    Object cellData = data.get("monitor_name");
                    if (cellData != null) {
                        cell.setCellValue(String.valueOf(cellData));
                    } else {
                        cell.setCellValue("");
                    }
                    Cell cell1 = row1.createCell(1);
                    Object cellData1 = data.get("monitor_type");
                    if (cellData1 != null) {
                        cell1.setCellValue(String.valueOf(cellData1));
                    } else {
                        cell1.setCellValue("");
                    }
                    Cell cell2 = row1.createCell(2);
                    Object cellData2 = data.get("monitored_proxy");
                    if (cellData2 != null) {
                        cell2.setCellValue(String.valueOf(cellData2));
                    } else {
                        cell2.setCellValue("");
                    }
                    Cell cell3 = row1.createCell(3);
                    Object cellData3 = data.get("monitored_host");
                    if (cellData3 != null) {
                        cell3.setCellValue(String.valueOf(cellData3));
                    } else {
                        cell3.setCellValue("");
                    }
                    Cell cell4 = row1.createCell(4);
                    Object cellData4 = map.get("port");
                    if (cellData4 != null) {
                        cell4.setCellValue(String.valueOf(cellData4));
                    } else {
                        cell4.setCellValue("");
                    }
                    Cell cell5 = row1.createCell(5);
                    Object cellData5 = data.get("day_of_week_at");
                    if (cellData5 != null) {
                        cell5.setCellValue(String.valueOf(cellData5));
                    } else {
                        cell5.setCellValue("");
                    }
                    Cell cell6 = row1.createCell(6);
                    Object cellData6 = data.get("execute_at");
                    if (cellData6 != null) {
                        cell6.setCellValue(String.valueOf(cellData6));
                    } else {
                        cell6.setCellValue("");
                    }
                    Cell cell7 = row1.createCell(7);
                    Object cellData7 = data.get("execute_util");
                    if (cellData7 != null) {
                        cell7.setCellValue(String.valueOf(cellData7));
                    } else {
                        cell7.setCellValue("");
                    }
                    Cell cell8 = row1.createCell(8);
                    Object cellData8 = data.get("effect_timestamp");
                    if (cellData8 != null) {
                        cell8.setCellValue(String.valueOf(cellData8));
                    } else {
                        cell8.setCellValue("");
                    }
                    Cell cell9 = row1.createCell(9);
                    Object cellData9 = data.get("is_effect");
                    if (cellData9 != null) {
                        cell9.setCellValue(String.valueOf(cellData9));
                    } else {
                        cell9.setCellValue("");
                    }
                }
            }
        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }
    private String exportHistoryData(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
        for (int i = 0; i < datas.size(); i++) {
            Row row1 = sheet.createRow(i + 1);
            Map<String, Object> data = datas.get(i);
            Cell cell = row1.createCell(0);
            Object cellData = data.get("st");
            if (cellData != null) {
                cell.setCellValue(String.valueOf(cellData));
            } else {
                cell.setCellValue("");
            }
            Cell cell1 = row1.createCell(1);
            Object cellData1 = data.get("EventCategoryLabelText");
            if (cellData1 != null) {
                cell1.setCellValue(String.valueOf(cellData1));
            } else {
                cell1.setCellValue("");
            }
            Cell cell2 = row1.createCell(2);
            Object cellData2 = data.get("Node");
            if (cellData2 != null) {
                cell2.setCellValue(String.valueOf(cellData2));
            } else {
                cell2.setCellValue("");
            }
            Cell cell3 = row1.createCell(3);
            Object cellData3 = data.get("NodeAlias");
            if (cellData3 != null) {
                cell3.setCellValue(String.valueOf(cellData3));
            } else {
                cell3.setCellValue("");
            }
            Cell cell4 = row1.createCell(4);
            Object cellData4 = data.get("fs");
            if (cellData4 != null) {
                cell4.setCellValue(String.valueOf(cellData4));
            } else {
                cell4.setCellValue("");
            }
            Cell cell5 = row1.createCell(5);
            Object cellData5 = data.get("lo");
            if (cellData5 != null) {
                cell5.setCellValue(String.valueOf(cellData5));
            } else {
                cell5.setCellValue("");
            }
            Cell cell6 = row1.createCell(6);
            Object cellData6 = data.get("Tally");
            if (cellData6 != null) {
                cell6.setCellValue(String.valueOf(cellData6));
            } else {
                cell6.setCellValue("");
            }
            Cell cell7 = row1.createCell(7);
            Object cellData7 = data.get("Manager");
            if (cellData7 != null) {
                cell7.setCellValue(String.valueOf(cellData7));
            } else {
                cell7.setCellValue("");
            }
            Cell cell8 = row1.createCell(8);
            Object cellData8 = data.get("Summary");
            if (cellData8 != null) {
                cell8.setCellValue(String.valueOf(cellData8));
            } else {
                cell8.setCellValue("");
            }
            Cell cell9 = row1.createCell(9);
            Object cellData9 = data.get("ic");
            if (cellData9 != null) {
                cell9.setCellValue(String.valueOf(cellData9));
            } else {
                cell9.setCellValue("");
            }
            Cell cell10 = row1.createCell(10);
            Object cellData10 = data.get("ag");
            if (cellData10 != null) {
                cell10.setCellValue(String.valueOf(cellData10));
            } else {
                cell10.setCellValue("");
            }
            Cell cell11 = row1.createCell(11);
            Object cellData11 = data.get("RecoveredStatusTag");
            if (cellData11 != null) {
                cell11.setCellValue(String.valueOf(cellData11));
            } else {
                cell11.setCellValue("");
            }
            Cell cell12 = row1.createCell(12);
            Object cellData12 = data.get("ost");
            if (cellData12 != null) {
                cell12.setCellValue(String.valueOf(cellData12));
            } else {
                cell12.setCellValue("");
            }
            Cell cell13 = row1.createCell(13);
            Object cellData13 = data.get("AlertGroup");
            if (cellData13 != null) {
                cell13.setCellValue(String.valueOf(cellData13));
            } else {
                cell13.setCellValue("");
            }
            Cell cell14 = row1.createCell(14);
            Object cellData14 = data.get("AlertKey");
            if (cellData14 != null) {
                cell14.setCellValue(String.valueOf(cellData14));
            } else {
                cell14.setCellValue("");
            }
        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }
    private String export(List<Map<String, Object>> datas, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        //FileInputStream fileInputStream = new FileInputStream(path);
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        is.close();
        SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
        XSSFWorkbook wb = sxssfworkbook.getXSSFWorkbook();
        XSSFSheet sheetAt = wb.getSheetAt(0);
        XSSFRow row = sheetAt.getRow(1);
        Iterator<Cell> cellIterator = row.cellIterator();
        ArrayList<String> list = new ArrayList<>();
        while (cellIterator.hasNext()) {
            Cell next = cellIterator.next();
            String stringCellValue = next.getStringCellValue();
            list.add(stringCellValue);
        }

        SXSSFSheet sheet = sxssfworkbook.getSheetAt(0);
        for (int i = 0; i < datas.size(); i++) {
            Row row1 = sheet.createRow(i + 2);
            Map<String, Object> data = datas.get(i);
            for (int j = 0; j < list.size(); j++) {
                Cell cell = row1.createCell(j);
                Object cellData = data.get(list.get(j));
                if (cellData!=null) {
                    cell.setCellValue(String.valueOf(cellData));
                }else {
                    cell.setCellValue("");
                }

            }

        }
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        sxssfworkbook.write(fileOut);
        fileOut.close();
        sxssfworkbook.dispose();
        return filename;
    }

    private ResultPattern getDatas(String dcName, Map filter) {
        DataLoadParams dataLoadParams = new DataLoadParams();
        dataLoadParams.setFilter(JSON.toJSONString(filter));
        dataLoadParams.setDcName(dcName);
        dataLoadParams.setProjectId(10L);
        dataLoadParams.setStart(1);
        dataLoadParams.setLimit(-10);
        ResultPattern resultPattern = DataServiceUtils.dataLoad(dataSource, dataLoadParams);
        return resultPattern;
    }

    /**
     * 编码文件名
     */
    public static String encodingFilename(String filename) {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String dateStr = dateFormatter.format(date);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
        String timeStr = timeFormatter.format(time);
//        filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
        filename = dateStr + "_" + timeStr + ".xlsx";
        return filename;
    }

    public String getAbsoluteFile(String filename) {
        String downloadPath = SystemConfig.getDownloadPath() + filename;
        File desc = new File(downloadPath);
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        return downloadPath;
    }

    @PostMapping("alarmResource")
    public AjaxResult getAlarmResource(){
        AjaxResult result =AjaxResult.success();
        ResultPattern alarmsResult = getAllAlarmResources();
        List<Map<String, Object>> alarmList = alarmsResult.getDatas();
        if (alarmsResult.isSuccess()&&alarmList.size()>0) {
            ResultPattern productResult = getAllProduct();
            ResultPattern relationResult = getAllRelation();
            Map<String, List> productRelation = new HashMap<>();
            if (productResult.isSuccess()&&relationResult.isSuccess()) {
                List<Map<String, Object>> productList = productResult.getDatas();
                List<Map<String, Object>> relationList = relationResult.getDatas();
                if (CollectionUtils.isNotEmpty(productList)&&CollectionUtils.isNotEmpty(relationList)) {
                    for (Map<String, Object> product : productList) {
                        String productId = String.valueOf(product.get("id"));
                        List<String> list = new ArrayList<>();
                        productRelation.put(productId,list);
                        getRelationId(relationList,productId,list);
                    }
                }
            }

            List<Object> armProduct = new ArrayList<>();
            result=AjaxResult.success(armProduct);
            if (MapUtils.isNotEmpty(productRelation)) {
                for (Map<String, Object> map : alarmList) {
                    String id = String.valueOf(map.get("id"));
                    for (Map.Entry<String, List> entry : productRelation.entrySet()) {
                        if (entry.getValue().contains(id)&&!armProduct.contains(entry.getKey())) {
                            armProduct.add(entry.getKey());
                        }
                    }
                }
            }
        }
        return result;
    }

    private ResultPattern getAllAlarmResources() {
        ResultPattern result =new ResultPattern();
        ResultPattern alarm24Hours = getDatas("getCMDBAlarm24Hours", new HashMap());
        List<Object> listData = alarm24Hours.getListData();
        if (alarm24Hours.isSuccess()&& CollectionUtils.isNotEmpty(listData)) {
            Map map = new HashMap();
            map.put("keyList",listData);
            result = getDatas("getAllAlarmResources", map);
        }
        return result;
    }

    private ResultPattern getAllRelation() {
        return getDatas("getAllRelation", new HashMap());
    }

    private ResultPattern getAllProduct() {
        return getDatas("getAllProduct", new HashMap());
    }

    private void getRelationId(List<Map<String, Object>> relationList, String productId, List<String> list) {
        for (Map<String, Object> relation : relationList) {
            String id = String.valueOf(relation.get("id"));
            if(productId.equals(id)){
                String lTargetCardId = String.valueOf(relation.get("lTargetCardId"));
                list.add(lTargetCardId);
                getRelationId(relationList,lTargetCardId,list);
            }
        }
    }


}
