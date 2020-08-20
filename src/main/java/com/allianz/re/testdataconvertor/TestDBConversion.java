package com.allianz.re.testdataconvertor;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class TestDBConversion {

    String jdbcURL;
    String username;
    String password;
    Connection connection;
    Statement statement;

    public TestDBConversion(String jdbcurl, String username, String password) {
        this.jdbcURL = jdbcurl;
        this.username = username;
        this.password = password;
        try {
            connection = DriverManager.getConnection(jdbcURL, username, password);
            statement = connection.createStatement();
        } catch (SQLException e) {

            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        TestDBConversion exporter = new TestDBConversion("jdbc:oracle:thin:@sla06184.srv.allianz:1521/REGRIPT",
                "PRISMRI_D", "solv_core");
        exporter.DBConversion("SELECT * FROM SII_TABLES", "resources/output/", "json");

    }

    public void DBConversion(String query, String filepath, String outputformat) throws SQLException, IOException {
        switch (outputformat) {
            case "csv":
                this.exportToCSV(query, filepath);
                break;
            case "json":
                this.exportToJson(query, filepath);
                break;
            case "excel":
                this.exportToExcel(query, filepath);
                break;
        }
    }

    // ====================================================================================================================
    private String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s", dateTimeInfo));
    }

    public void exportToExcel(String query, String filepath) {
        String filename = getFileName("ExcelExport");
        try {
            String sql = query;
            ResultSet result = statement.executeQuery(sql);

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Testdata");

            writeHeaderLine(result, sheet);

            writeDataLines(result, workbook, sheet);

            FileOutputStream outputStream = new FileOutputStream(filepath + filename + ".xlsx");
            workbook.write(outputStream);
            workbook.close();

            statement.close();
            connection.close();

        } catch (SQLException e) {
            System.out.println("Datababse error:");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("File IO error:");
            e.printStackTrace();
        }
    }

    private void writeHeaderLine(ResultSet result, XSSFSheet sheet) throws SQLException {
        // write header line containing column names
        ResultSetMetaData metaData = result.getMetaData();
        int numberOfColumns = metaData.getColumnCount();

        Row headerRow = sheet.createRow(0);

        // exclude the first column which is the ID field
        for (int i = 1; i <= numberOfColumns; i++) {
            String columnName = metaData.getColumnName(i);
            Cell headerCell = headerRow.createCell(i - 1);
            headerCell.setCellValue(columnName);
        }
    }

    private void writeDataLines(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
        ResultSetMetaData metaData = result.getMetaData();
        int numberOfColumns = metaData.getColumnCount();

        int rowCount = 1;

        while (result.next()) {
            Row row = sheet.createRow(rowCount++);

            for (int i = 1; i <= numberOfColumns; i++) {
                Object valueObject = result.getObject(i);

                Cell cell = row.createCell(i - 1);

                if (valueObject instanceof Boolean)
                    cell.setCellValue((Boolean) valueObject);
                else if (valueObject instanceof Double)
                    cell.setCellValue((double) valueObject);
                else if (valueObject instanceof Float)
                    cell.setCellValue((float) valueObject);
                else if (valueObject instanceof BigDecimal) {
                    String value = valueObject.toString();
                    cell.setCellValue(value);
                } else if (valueObject instanceof Date) {
                    cell.setCellValue((Date) valueObject);
                    formatDateCell(workbook, cell);
                } else
                    cell.setCellValue((String) valueObject);

            }

        }
    }

    private void formatDateCell(XSSFWorkbook workbook, Cell cell) {
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        cell.setCellStyle(cellStyle);
    }

    // =====================================================csv===========================================
    public void exportToCSV(String query, String filepath) throws SQLException {
        try {
            String filename = this.getFileName("CSVExport");
            FileWriter fw = new FileWriter(filepath + filename + ".csv");
            if (connection.isClosed())
                statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            int cols = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= cols; i++) {
                fw.append(rs.getMetaData().getColumnLabel(i));
                if (i < cols)
                    fw.append(',');
                else
                    fw.append('\n');
            }

            while (rs.next()) {

                for (int i = 1; i <= cols; i++) {
                    fw.append(rs.getString(i));
                    if (i < cols)
                        fw.append(',');
                }
                fw.append('\n');
            }
            fw.flush();
            fw.close();
            connection.close();
        } catch (Exception e) {

        }
    }

    // ====================================================db to
    // json==========================================================
    public void exportToJson(String query, String filepath) throws SQLException, IOException {

        String filename = this.getFileName("JsonExport");
        ResultSet rs = statement.executeQuery(query);

        JSONArray jsonArray = new JSONArray();
        while (rs.next()) {
            int total_rows = rs.getMetaData().getColumnCount();
            JSONObject obj = new JSONObject();

            for (int i = 0; i < total_rows; i++) {
                String columnName = rs.getMetaData().getColumnLabel(i + 1).toLowerCase();
                Object columnValue = rs.getObject(i + 1);

                // if value in DB is null, then we set it to default value
                if (columnValue == null) {
                    columnValue = "null";
                }

                if (obj.containsValue(columnName)) {
                    columnName += "1";
                }
                obj.put(columnName, columnValue);

            }
            jsonArray.add(obj);
        }
        FileWriter fw = new FileWriter(filepath + filename + ".json");
        fw.write(jsonArray.toString());
        fw.flush();
        fw.close();
        connection.close();

    }
}
