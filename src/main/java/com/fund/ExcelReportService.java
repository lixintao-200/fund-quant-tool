package com.fund;

/**
 * @ClassName ExcelReportService
 * @Description TODO
 * @Author xintao.li
 * @Date 2026-03-18 21:23
 * @Version 1.0
 */
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.util.List;

@Service
public class ExcelReportService {

    public void create(List<FundResult> results, String path) throws Exception {
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("基金决策报告");

        String[] headers = {
                "基金代码","基金名称","最新净值","MA20","5日涨幅","20日涨幅",
                "趋势","动量","信号","得分","评级","仓位建议"
        };

        // 表头
        Row header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // ✅ 修复颜色：换成所有Excel版本都支持的写法
        CellStyle greenStyle = wb.createCellStyle();
        greenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        greenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());

        CellStyle redStyle = wb.createCellStyle();
        redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        redStyle.setFillForegroundColor(IndexedColors.RED.getIndex());

        int row = 1;
        for (FundResult r : results) {
            Row dataRow = sheet.createRow(row++);
            dataRow.createCell(0).setCellValue(r.getCode());
            dataRow.createCell(1).setCellValue(r.getName());
            dataRow.createCell(2).setCellValue(r.getLatestNet());
            dataRow.createCell(3).setCellValue(r.getMa20());
            dataRow.createCell(4).setCellValue(r.getRise5d());
            dataRow.createCell(5).setCellValue(r.getRise20d());
            dataRow.createCell(6).setCellValue(r.getTrend());
            dataRow.createCell(7).setCellValue(r.getMomentum());

            Cell signalCell = dataRow.createCell(8);
            signalCell.setCellValue(r.getSignal());


            dataRow.createCell(9).setCellValue(r.getScore());
            dataRow.createCell(10).setCellValue(r.getRankLevel());

            // ✅ 标记颜色
            if ("买入".equals(r.getSignal())) {
                signalCell.setCellStyle(greenStyle);
            }
            if ("卖出".equals(r.getSignal())) {
                signalCell.setCellStyle(redStyle);
            }

            dataRow.createCell(11).setCellValue(r.getSuggest());
        }

        try (FileOutputStream out = new FileOutputStream(path)) {
            wb.write(out);
        }
        wb.close();
    }
}
