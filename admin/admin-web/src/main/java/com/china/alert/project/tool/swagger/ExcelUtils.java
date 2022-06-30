package com.china.alert.project.tool.swagger;

import com.china.alert.framework.config.SystemConfig;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author yanzeyang
 * date 2021/12/27 17:02
 * @version V1.0
 * @Package com.china.alert.project.tool.swagger
 */
public class ExcelUtils {
    /**
     * 创建下拉列表选项(单元格下拉框数据小于255字节时使用)
     *
     * @param sheet    所在Sheet页面
     * @param values   下拉框的选项值
     * @param firstRow 起始行（从0开始）
     * @param lastRow  终止行（从0开始）
     * @param firstCol 起始列（从0开始）
     * @param lastCol  终止列（从0开始）
     */
    public void createDropDownList(Sheet sheet, String[] values, int firstRow, int lastRow, int firstCol, int lastCol) {
        DataValidationHelper helper = sheet.getDataValidationHelper();
        CellRangeAddressList addressList = new CellRangeAddressList(firstRow, lastRow, firstCol, lastCol);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(values);
        DataValidation dataValidation = helper.createValidation(constraint, addressList);
        if (dataValidation instanceof HSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(false);
        } else {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        }
        sheet.addValidationData(dataValidation);
    }

    public String exportTemplate(String[] values, int firstRow, int lastRow, int firstCol, int lastCol, String path) throws IOException {
        Resource resource = new ClassPathResource(path);
        InputStream is = resource.getInputStream();
        //FileInputStream fileInputStream = new FileInputStream(path);
        XSSFWorkbook xssfwb = new XSSFWorkbook(is);
        XSSFSheet sheetAt = xssfwb.getSheetAt(0);
        is.close();
        createDropDownList(sheetAt,values, firstRow,lastRow, firstCol, lastCol);
        /*SXSSFWorkbook sxssfworkbook = new SXSSFWorkbook(xssfwb, 100);
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
                cell.setCellValue(String.valueOf(data.get(list.get(j))));
            }

        }*/
        String filename = encodingFilename("111");
        FileOutputStream fileOut = new FileOutputStream(getAbsoluteFile(filename));
        xssfwb.write(fileOut);
        fileOut.close();
        return filename;
    }

    /**
     * 编码文件名
     */
    public String encodingFilename(String filename) {
        filename = UUID.randomUUID().toString() + "_" + filename + ".xlsx";
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
}
