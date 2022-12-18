package com.example.CourseWork.export;

import com.example.CourseWork.entity.User;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class UserExcelExporter extends AbstractExporter{

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    public UserExcelExporter() {
        workbook = new XSSFWorkbook();
    }
    private void writeHeaderLine() {
        sheet = workbook.createSheet("Users");
        XSSFRow row = sheet.createRow(0);

        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        cellStyle.setFont(font);

        createCell(row, 0, "Id", cellStyle);
        createCell(row, 1, "E-mail", cellStyle);
        createCell(row, 2, "Фамилия", cellStyle);
        createCell(row, 3, "Имя", cellStyle);
        createCell(row, 4, "Отчество", cellStyle);
        createCell(row, 5, "Роль", cellStyle);
        createCell(row, 6, "Доступ к сайту", cellStyle);
    }
    private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
        XSSFCell cell = row.createCell(columnIndex);
        sheet.autoSizeColumn(columnIndex);
        if(value instanceof Long) {
            cell.setCellValue((Long)value);
        }else if(value instanceof Boolean){
            cell.setCellValue((Boolean) value);
        }else{
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        super.setResponseHeader(response,"application/octet-stream",".xlsx");

        writeHeaderLine();
        writeDataLine(listUsers);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
    }

    private void writeDataLine(List<User> listUsers) {
        int rowIndex = 1;
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        cellStyle.setFont(font);

        for (User user : listUsers) {
            XSSFRow row = sheet.createRow(rowIndex++);
            int columnIndex = 0;
            createCell(row, columnIndex++, user.getId(), cellStyle);
            createCell(row, columnIndex++, user.getEmail(), cellStyle);
            createCell(row, columnIndex++, user.getLastName(), cellStyle);
            createCell(row, columnIndex++, user.getFirstName(), cellStyle);
            createCell(row, columnIndex++, user.getPatronymic(), cellStyle);
            createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
            createCell(row, columnIndex++, user.getEnabled(), cellStyle);
        }
    }
}
