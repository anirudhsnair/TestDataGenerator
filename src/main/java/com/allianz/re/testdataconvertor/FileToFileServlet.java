package com.allianz.re.testdataconvertor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class FileToFileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // location to store file uploaded
    private static final String UPLOAD_DIRECTORY = "upload";

    // upload settings
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3; // 3MB
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
    String fileName;
    String filePath;

    private String type;

    private String loc;

    private String filename;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(request)) {
            // if not, we stop here
            PrintWriter writer = response.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }

        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);

        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);

        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);

        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;

        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        try {
            // parses the request's content to extract file data
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(request);

            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {

                        fileName = new File(item.getName()).getName();
                        filePath = uploadPath + File.separator + fileName;
                        File storeFile = new File(filePath);

                        // saves the file on disk
                        item.write(storeFile);
                        request.setAttribute("message", "Upload has been done successfully!");
                    } else {
                        if (item.getFieldName().equals("loc")) {
                            loc = item.getString();
                        } else if (item.getFieldName().equals("type")) {
                            type = item.getString();

                        }

                        if (item.getFieldName().equals("outputfileName")) {
                            filename = item.getString();

                        }

                    }
                }
            }
            // String name = item.getFieldName();
            System.out.println("outputfileName=" + filename);
            System.out.println("type=" + type);

            String outputPath = null;
            String ext = null;
            if (type.equals("JSON to Excel")) {
                outputPath = jsonToExcel(filePath, loc, filename);
                ext = ".xlsx";
            } else if (type.equals("Excel to JSON")) {
                outputPath = excelToJson(filePath, loc, filename);
                ext = ".json";

            } else if (type.equals("XML to JSON")) {
                outputPath = xmlToJson(filePath, loc, filename);
                ext = ".json";

            } else if (type.equals("JSON to XML")) {
                outputPath = jsonToXml(filePath, loc, filename);
                ext = ".xml";

            }

            PrintWriter writer = response.getWriter();
            // build HTML code
            String htmlResponse = "<html>";
            htmlResponse += "<link rel=\"icon\" href=\"logo.jpg\" type=\"image/icon type\">";
            htmlResponse += "<img src=\"allianz_logo.png\" width=\"80\" height=\"20\" style=\"float: left;\" />";
            htmlResponse +=
                         "&ensp; <input type=\"button\" value=\"Home\"\r\n" + "onClick=\"location.href='index.jsp'\">";
            htmlResponse += "<center>";
            if (filename.isEmpty()) {

                htmlResponse +=
                             "<h2 style=\"color:#003d99;\">Hurray! Your test-data is ready @: " + outputPath + "<br/>";
            } else {
                htmlResponse += "<h2 style=\"color:#003d99;\">Hurray! Your test-data [" + filename + ext
                        + "] is ready @: " + outputPath + "<br/>";
            }
            htmlResponse += "</center>";
            htmlResponse += "<footer>\r\n"
                    + "<img src=\"gtf.PNG\" style=\"float: right;\"width=\"95\" height=\"22\" />\r\n" + "</footer>";
            htmlResponse += "</html>";

            writer.println(htmlResponse);
        } catch (Exception ex) {
            request.setAttribute("message", "There was an error: " + ex.getMessage());
        }

    }

    public static String jsonToXml(String input, String loc, String filename) throws IOException, ParseException {
        String name = getFileName("xmlExport");
        FileReader reader = new FileReader(input);
        JSONParser jsonParser = new JSONParser();
        String filepath;
        if (loc.isEmpty()) {
            filepath = System.getProperty("java.io.tmpdir");
        } else {
            filepath = loc;
        }
        if (filename.isEmpty()) {
            filename = filepath + name + ".xml";
        } else {
            filename = filepath + filename + ".xml";
        }
        String jsonStr = jsonParser.parse(reader).toString();
        jsonStr = jsonStr.replace("[", "").replace("]", "");
        String s[] = jsonStr.split("},");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < s.length; i++) {
            JSONObject json = new JSONObject(s[i] + "}");
            jsonArray.add(json);
        }
        FileWriter fileWriter = null;
        try {

            try {
                fileWriter = new FileWriter(filename);
                for (int i = 0; i < jsonArray.size(); i++) {
                    fileWriter.append(XML.toString(jsonArray.get(i)));
                }
            }

            finally {
                fileWriter.close();
            }
        } catch (

        IOException e) {
            e.printStackTrace();
        }

        return filepath;

    }

    public static void main(String args[]) throws IOException, ParseException {
        // jsonToExcel("\\\\WWG00M.ROOTDOM.NET\\DFS\\HOME\\re00691\\ICM\\Desktop\\input\\customers.json");
        // jsonToExcel("\\\\WWG00M.ROOTDOM.NET\\DFS\\HOME\\re00691\\ICM\\Desktop\\fruits.json");

    }

    public static String jsonToExcel(String input, String loc, String filename) throws IOException, ParseException {
        String name = getFileName("excelExport");
        FileReader reader = new FileReader(input);
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(reader);
        Set<String> keySt = null;
        List<HashMap<String, String>> map = null;
        String filepath = null;
        if (obj instanceof JSONArray) {
            reader = new FileReader(input);
            map = (JSONArray) jsonParser.parse(reader);
            keySt = map.get(0).keySet();
            if (loc.isEmpty()) {
                filepath = System.getProperty("java.io.tmpdir");
            } else {
                filepath = loc;
            }
            if (filename.isEmpty()) {
                filename = filepath + name + ".xlsx";
            } else {
                filename = filepath + filename + ".xlsx";
            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("0");
            XSSFRow rowhead = sheet.createRow((short) 0);
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            style.setFont(font);
            int i = 0;
            for (String s : keySt) {
                rowhead.createCell(i).setCellValue(s);
                rowhead.getCell(i).setCellStyle(style);
                i++;
            }
            int k = 1;
            for (int j = 0; j < map.size(); j++) {

                Collection<String> value = map.get(j).values();
                rowhead = sheet.createRow((short) k);
                i = 0;
                for (Object v : value) {
                    rowhead.createCell(i).setCellValue(v.toString());
                    i++;
                }
                k++;
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

        } else if (obj instanceof JSONObject) {
            JSONArray jArray = new JSONArray();
            jArray.add(obj);
            map = jArray;
            keySt = map.get(0).keySet();
            if (loc.isEmpty()) {
                filepath = System.getProperty("java.io.tmpdir");
            } else {
                filepath = loc;
            }
            if (filename.isEmpty()) {
                filename = filepath + name + ".xlsx";
            } else {
                filename = filepath + filename + ".xlsx";
            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("0");
            XSSFRow rowhead = sheet.createRow((short) 0);
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            style.setFont(font);
            int i = 0;
            for (String s : keySt) {
                rowhead.createCell(i).setCellValue(s);
                rowhead.getCell(i).setCellStyle(style);
                i++;
            }
            int k = 1;
            for (int j = 0; j < map.size(); j++) {

                Collection<String> value = map.get(j).values();
                rowhead = sheet.createRow((short) k);
                i = 0;
                for (Object v : value) {
                    rowhead.createCell(i).setCellValue(v.toString());
                    i++;
                }
                k++;
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

        } else {
            JSONArray jArray = new JSONArray();
            jArray.add(obj);
            map = jArray;
            keySt = map.get(0).keySet();
            if (loc.isEmpty()) {
                filepath = System.getProperty("java.io.tmpdir");
            } else {
                filepath = loc;
            }
            if (filename.isEmpty()) {
                filename = filepath + name + ".xlsx";
            } else {
                filename = filepath + filename + ".xlsx";

            }
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("0");
            XSSFRow rowhead = sheet.createRow((short) 0);
            XSSFCellStyle style = workbook.createCellStyle();
            XSSFFont font = workbook.createFont();
            font.setFontName(XSSFFont.DEFAULT_FONT_NAME);
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            style.setFont(font);
            int i = 0;
            for (String s : keySt) {
                rowhead.createCell(i).setCellValue(s);
                rowhead.getCell(i).setCellStyle(style);
                i++;
            }
            int k = 1;
            for (int j = 0; j < map.size(); j++) {

                Collection<String> value = map.get(j).values();
                rowhead = sheet.createRow((short) k);
                i = 0;
                for (Object v : value) {
                    rowhead.createCell(i).setCellValue(v.toString());
                    i++;
                }
                k++;
            }
            FileOutputStream fileOut = new FileOutputStream(filename);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

        }
        // Writing excel headers
        return filepath;
    }

    private static String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s", dateTimeInfo));
    }

    public static String excelToJson(String input, String loc, String filename)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        if (filename.isEmpty()) {
            filename = getFileName("jsonExport");
        }

        JSONArray jarray = new JSONArray();
        Workbook workbook = WorkbookFactory.create(new File(input));
        int sheets = workbook.getNumberOfSheets();
        Iterator<Sheet> sheetIterator = workbook.sheetIterator();
        while (sheetIterator.hasNext()) {
            Sheet sheet = sheetIterator.next();
        }
        Sheet sheet = workbook.getSheetAt(0);
        DataFormatter dataFormatter = new DataFormatter();
        Row topRow = sheet.getRow(0);
        List<String> headers = new ArrayList<String>();
        for (Cell cell : topRow) {
            headers.add(dataFormatter.formatCellValue(cell));
            String cellValue = dataFormatter.formatCellValue(cell);
        }
        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // just skip the rows if row number is 0 or 1
            }
            JSONObject jObj = new JSONObject();
            for (int i = 0; i < row.getPhysicalNumberOfCells() && i < headers.size(); i++) {
                String cellValue = dataFormatter.formatCellValue(row.getCell(i));
                jObj.put(headers.get(i), cellValue);
            }
            jarray.add(jObj);

        }
        FileWriter file = null;
        String filepath;
        if (loc.isEmpty()) {
            filepath = System.getProperty("java.io.tmpdir");
        } else {
            filepath = loc;
        }
        try {

            file = new FileWriter(filepath + filename + ".json");
            file.write(jarray.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();

        } finally {

            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return filepath;
    }

    public static String xmlToJson(String input, String loc, String filename) {
        String filepath = null;
        if (filename.isEmpty()) {
            filename = getFileName("jsonExport");
        }

        JSONObject jsonObj = null;
        try {
            File file = new File(input);
            InputStream inputStream = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            int ptr = 0;
            while ((ptr = inputStream.read()) != -1) {
                builder.append((char) ptr);
            }

            String xml = builder.toString();
            jsonObj = XML.toJSONObject(xml);
            if (loc.isEmpty()) {
                filepath = System.getProperty("java.io.tmpdir");
            } else {
                filepath = loc;
            }
            FileWriter fileWriter = new FileWriter(filepath + filename + ".json");

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (int i = 0; i < jsonObj.toString().split(",").length; i++) {
                bufferedWriter.write(jsonObj.toString().split(",")[i]);
                if (i != jsonObj.toString().split(",").length - 1) {
                    bufferedWriter.write(",");
                }
            }
            bufferedWriter.close();
        } catch (IOException ex) {
            System.out.println("Error writing to file '" + filename + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return filepath;
    }
}
