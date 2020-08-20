package com.allianz.re.testdataconvertor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

public class TestDataGenerator {
    public static void main(String args[])
            throws EncryptedDocumentException, InvalidFormatException, IOException, ParseException {

        // jsonToExcel("customers.json");
        // excelToJson("customers.xlsx");
        // xmlToJson("books.xml");
        jsonToXml("\\\\WWG00M.ROOTDOM.NET\\DFS\\HOME\\re00691\\ICM\\Desktop\\input\\customers.json");

    }

    private static String jsonToXml(String input) throws IOException, ParseException {
        String name = getFileName("xmlExport");
        FileReader reader = new FileReader(input);
        JSONParser jsonParser = new JSONParser();
        String filepath = System.getProperty("java.io.tmpdir");
        String filename = filepath + name + ".xml";
        String jsonStr = jsonParser.parse(reader).toString();
        jsonStr = jsonStr.replace("[", "").replace("]", "");
        String s[] = jsonStr.split("},");
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < s.length; i++) {
            JSONObject json = new JSONObject(s[i] + "}");
            jsonArray.add(json);
        }
        System.out.println(jsonArray);
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

    public static void jsonToExcel(String input) throws IOException, ParseException {
        String name = getFileName(input.replace(".json", ""));
        FileReader reader = new FileReader("src/main/resources/input/" + input);
        JSONParser jsonParser = new JSONParser();
        List<HashMap<String, String>> map = (JSONArray) jsonParser.parse(reader);
        Set<String> keySt = map.get(0).keySet();
        // Writing excel headers
        String filepath = System.getProperty("java.io.tmpdir");
        String filename = filepath + name + ".xlsx";
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

    private static String getFileName(String baseName) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimeInfo = dateFormat.format(new Date());
        return baseName.concat(String.format("_%s", dateTimeInfo));
    }

    public static void excelToJson(String input)
            throws EncryptedDocumentException, InvalidFormatException, IOException {
        String filename = getFileName(input.replace(".xlsx", ""));
        JSONArray jarray = new JSONArray();
        Workbook workbook = WorkbookFactory.create(new File("src/main/resources/input/" + input));
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
        String filepath = System.getProperty("java.io.tmpdir");
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
    }

    public static void xmlToJson(String input) {
        String filename = getFileName(input.replace(".xml", ""));
        JSONObject jsonObj = null;
        try {
            File file = new File("src/main/resources/input/" + input);
            InputStream inputStream = new FileInputStream(file);
            StringBuilder builder = new StringBuilder();
            int ptr = 0;
            while ((ptr = inputStream.read()) != -1) {
                builder.append((char) ptr);
            }

            String xml = builder.toString();
            jsonObj = XML.toJSONObject(xml);
            String filepath = System.getProperty("java.io.tmpdir");
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
    }
}
