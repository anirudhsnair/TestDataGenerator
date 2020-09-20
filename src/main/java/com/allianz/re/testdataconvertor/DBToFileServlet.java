package com.allianz.re.testdataconvertor;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DBToFileServlet extends HttpServlet {

    static Connection connection;
    static Statement statement;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        response.setContentType("text/html");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String query = request.getParameter("query");
        String qry[] = query.split(";");
        String dbURL = request.getParameter("url");
        String type = request.getParameter("type");
        String loc = request.getParameter("loc");
        ArrayList<String> fileName = new ArrayList<String>();
        for (int i = 0; i < qry.length; i++) {
            fileName.add(request.getParameter("fileName" + i));
        }
        System.out.println(fileName.toString());
        String ext = null;
        String outputPath = null;
        if (!session.equals(null)) {
            session.setAttribute("Query", query);
        }
        if (type.equals("DB to CSV")) {
            type = "csv";
            ext = ".csv";
        } else if (type.equals("DB to JSON")) {
            type = "json";
            ext = ".json";

        } else if (type.equals("DB to Excel")) {
            type = "excel";
            ext = ".xlsx";
        }
        PrintWriter writer = response.getWriter();
        // System.out.println("Stored in session" + session.getAttribute("Query"));
        // build HTML code
        String htmlResponse = "<html>";
        htmlResponse += "<script>function GoBackWithRefresh(event) {\r\n" + "    if ('referrer' in document) {\r\n"
                + "        window.location = document.referrer;\r\n" + "        /* OR */\r\n"
                + "        //location.replace(document.referrer);\r\n" + "    } else {\r\n"
                + "        window.history.back();\r\n" + "    }\r\n" + "} function logOut()\r\n"
                + "                {window.location.replace(\"index.jsp\");}</script>";
        htmlResponse += "<link rel=\"icon\" href=\"logo.jpg\" type=\"image/icon type\">";
        htmlResponse += "<img src=\"allianz_logo.png\" width=\"80\" height=\"20\" style=\"float: left;\" />";
        htmlResponse +=
                     "<img src=\"logOut.png\" width=\"80\" height=\"20\" style=\"float: right; \" onClick=\"logOut()\" />\r\n"
                             + "";
        htmlResponse += "&ensp; <input type=\"button\" value=\"Go Back\"\r\n"
                + "onClick=\"GoBackWithRefresh();return false;\">";
        htmlResponse += "<center>";
        htmlResponse += "<br/>";
        if (qry.length > 1) {
            for (int i = 0; i < qry.length; i++) {
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    outputPath = DBConversion(qry[i], type, dbURL, username, password, loc, fileName.get(i));
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (fileName.get(i).isEmpty()) {
                    htmlResponse += "<h2 style=\"color:#003d99;\">Hurray! Your test-data is ready @: " + outputPath
                            + "<br/>";
                } else {
                    htmlResponse += "<h2 style=\"color:#003d99;\">Hurray! Your test-data [" + fileName.get(i) + ext
                            + "] is ready @: " + outputPath + "<br/>";
                }
            }
        } else {
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                outputPath = DBConversion(query, type, dbURL, username, password, loc, fileName.get(0));
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (fileName.isEmpty()) {

                htmlResponse +=
                             "<h2 style=\"color:#003d99;\">Hurray! Your test-data is ready @: " + outputPath + "<br/>";
            } else {
                htmlResponse += "<h2 style=\"color:#003d99;\">Hurray! Your test-data [" + fileName.get(0) + ext
                        + "] is ready @: " + outputPath + "<br/>";
            }

        }
        htmlResponse += "</center>";
        htmlResponse += "<footer>\r\n" + "<img src=\"gtf.PNG\" style=\"float: right;\"width=\"95\" height=\"22\" />\r\n"
                + "</footer>";
        htmlResponse += "</html>";

        writer.println(htmlResponse);

    }

    public static void main(String[] args) throws SQLException, IOException {
        // String outputPath = DBConversion("SELECT * FROM SII_TABLES", "json",
        // "jdbc:oracle:thin:@sla06184.srv.allianz:1521/REGRIPT", "PRISMRI_D", "solv_core");
        // System.out.print(outputPath);
    }

    public static String DBConversion(String query, String outputformat, String jdbcUrl, String username,
            String password, String loc, String fileName) throws SQLException, IOException {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
            statement = connection.createStatement();
        } catch (SQLException e) {

            e.printStackTrace();
        }
        String filepath = null;
        if (loc.isEmpty()) {
            filepath = System.getProperty("java.io.tmpdir");
        } else {
            filepath = loc;
        }
        switch (outputformat) {
            case "csv":
                exportToCSV(query, filepath, fileName);
                break;
            case "json":
                exportToJson(query, filepath, fileName);
                break;
            case "excel":
                exportToExcel(query, filepath, fileName);
                break;
        }
        return filepath;
    }

    // ====================================================================================================================
    private static String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s", dateTimeInfo));
    }

    public static void exportToExcel(String query, String filepath, String filename) {
        if (filename.isEmpty()) {
            filename = getFileName("ExcelExport");
        }
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

    private static void writeHeaderLine(ResultSet result, XSSFSheet sheet) throws SQLException {
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

    private static void writeDataLines(ResultSet result, XSSFWorkbook workbook, XSSFSheet sheet) throws SQLException {
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

    private static void formatDateCell(XSSFWorkbook workbook, Cell cell) {
        CellStyle cellStyle = workbook.createCellStyle();
        CreationHelper creationHelper = workbook.getCreationHelper();
        cellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        cell.setCellStyle(cellStyle);
    }

    // =====================================================csv===========================================
    public static void exportToCSV(String query, String filepath, String filename) throws SQLException {
        try {
            if (filename.isEmpty()) {
                filename = getFileName("CSVExport");
            }
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
    public static void exportToJson(String query, String filepath, String filename) throws SQLException, IOException {
        if (filename.isEmpty()) {

            filename = getFileName("JsonExport");
        }
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
