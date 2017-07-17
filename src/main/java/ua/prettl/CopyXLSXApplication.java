package ua.prettl;


import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CopyXLSXApplication {

    private static final String SOURCE_PATH = "c:/temp/таб.Адмін.xlsx";
    private static final String TARGET_PATH = "c:/temp/target3.xlsx";



    //Configuration info
    private static final int FIO_COL = 0;
    private static final int PERSON_NUMBER_COL = 1;
    private static final int POSITION_COL = 2;
    private static final int TEAM_COL = 3;
    private static final int FIRST_DAY_COL = 4;

    private static final int TIME_COL = 21;

    public static void main(String[] args) {

        //TODO: add logging for the project....


       //copySheet();

        //saveUniqueScheduleValuesToXLSX(scanSchedule(), TARGET_PATH);
        //updateFieldTimeForFile(TARGET_PATH, createTimeMapperFromPath(TARGET_PATH));
        writeTimeTableForFile("c:/temp/new.xlsx", TARGET_PATH, createTimeMapperFromPath(TARGET_PATH));



    }

    private static void writeTimeTableForFile(String newStringPath, String sourcePath, Map<String, Double> mapper) {
        if (!Files.exists(Paths.get(newStringPath))) {
            try {
                Files.copy(Paths.get(sourcePath), Paths.get(newStringPath), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(newStringPath));

                XSSFSheet sheet = workbook.getSheetAt(0);

                boolean isModified = false;

                for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                    XSSFRow row = sheet.getRow(rowIndex);

                    for (int colIndex = 4; colIndex <= row.getLastCellNum(); colIndex++) {
                        XSSFCell cell = row.getCell(colIndex);
                        if (cell == null) continue;

                        String[] keys = cell.getStringCellValue().split("\\n");

                        double hours = 0d;
                        for (String key : keys) {
                            if (!key.isEmpty()) {
                                Double value = mapper.get(key);
                                hours += value;
                            }
                        }

                        cell.setCellType(CellType.NUMERIC);
                        cell.setCellValue(hours);
                        isModified = true;
                    }

                }

                if (isModified) {
                    FileOutputStream fos = new FileOutputStream(newStringPath);

                    workbook.write(fos);

                    fos.close();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void updateFieldTimeForFile(String targetPath, Map<String, Double> mapper) {


        try {

            XSSFWorkbook wbWriteable = new XSSFWorkbook(new FileInputStream(TARGET_PATH));

            XSSFSheet sheet = wbWriteable.getSheetAt(0);

            for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);


                int hours = 0;
                for (int colIndex = FIRST_DAY_COL; colIndex <= FIRST_DAY_COL + 15; colIndex++ ) {
                    XSSFCell cell = row.getCell(colIndex);

                    if (cell == null) continue;

                    String[] keys = cell.getStringCellValue().split("\\n");

                    for (String key : keys) {
                        if (!key.isEmpty()) {
                            Double value = mapper.get(key);
                            hours += value.intValue();
                        }
                    }
                }

                XSSFCell hoursCell = row.createCell(TIME_COL);

                hoursCell.setCellType(CellType.NUMERIC);
                hoursCell.setCellValue(hours);
            }

            FileOutputStream fos = new FileOutputStream(targetPath);

            wbWriteable.write(fos);

            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Map<String, Double> createTimeMapperFromPath(String stringPath) {
        Map<String, Double> map = new HashMap<>();

        Path path = Paths.get(stringPath);

        if (Files.exists(path)) {

            try {
                XSSFWorkbook wbReadable = new XSSFWorkbook(new FileInputStream(path.toFile()));

                XSSFSheet sheet = wbReadable.getSheet("map");

                for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum();rowIndex++) {
                    XSSFRow row = sheet.getRow(rowIndex);
                    String key = row.getCell(0).getStringCellValue();
                    double value = 0d;

                    XSSFCell cell = row.getCell(1);
                    if (cell != null) {
                        value = cell.getNumericCellValue();
                    }
                    map.put(key, value);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //TODO: log this situation
        }

        return Collections.unmodifiableMap(map);

    }

    private static List<String> scanSchedule() {
        List<String> differentCellsValues = new ArrayList<>();

        try {
            XSSFWorkbook wbReader = new XSSFWorkbook(new FileInputStream(TARGET_PATH));

            XSSFSheet activeSheet = wbReader.getSheetAt(0);

            for (int rowIndex = activeSheet.getFirstRowNum(); rowIndex <= activeSheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = activeSheet.getRow(rowIndex);
                for (int colIndex = FIRST_DAY_COL; colIndex <= row.getLastCellNum(); colIndex++) {
                    XSSFCell cell = row.getCell(colIndex);
                    if (cell == null) continue;
                    String rawValue = cell.getStringCellValue();

                    String[] strings = rawValue.split("\\n");

                    for (String current : strings) {
                        if (!current.isEmpty()) {
                            differentCellsValues.add(current);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return differentCellsValues;
    }

    private static void saveUniqueScheduleValuesToXLSX(List<String> differentCellsValues, String stringPath) {

        Path path = Paths.get(stringPath);

        XSSFWorkbook wbWritable = null;
        try {
            wbWritable = new XSSFWorkbook(new FileInputStream(stringPath));


        XSSFSheet sheet = wbWritable.getSheet("map");

        if (sheet == null) {
            sheet = wbWritable.createSheet("map");
        }

        Set<String> uniqueSet = new HashSet<>(differentCellsValues);


        int rowIndex = 0;
        for (String s : uniqueSet) {
            XSSFRow row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(s);
        }

            FileOutputStream fos = new FileOutputStream(path.toFile());

            wbWritable.write(fos);

            fos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void copySheet() {
        try {
            if (Files.exists(Paths.get(TARGET_PATH))) {
                Files.delete(Paths.get(TARGET_PATH));
            }

            XSSFWorkbook wbReader = new XSSFWorkbook(new FileInputStream(SOURCE_PATH));

            XSSFSheet sheetFrom = wbReader.getSheetAt(0);

            XSSFWorkbook wbWriter = new XSSFWorkbook();

            XSSFSheet sheetTo = wbWriter.createSheet(sheetFrom.getSheetName());


            int rowToNumber = 0;

            for (int i = 6; i < 32; i++) {
                XSSFRow rowFrom = sheetFrom.getRow(i);

                XSSFRow rowTo = sheetTo.createRow(rowToNumber++);


                for (int j = rowFrom.getFirstCellNum(); j <= rowFrom.getLastCellNum(); j++) {

                    int toRowIndex = j >= TEAM_COL ? j + 1 : j;


                    if (j == TEAM_COL) {
                        //1. The team value column sould look the same as the fio Column

                        XSSFCell fioCell = rowFrom.getCell(FIO_COL);

                        XSSFCell cellTo = rowTo.createCell(TEAM_COL);
                        //set cell style
                        if (fioCell != null) {

                            XSSFCellStyle cellStyle = fioCell.getCellStyle();
                            if (cellStyle != null) {
                                XSSFCellStyle newCellStyle = wbWriter.createCellStyle();
                                newCellStyle.cloneStyleFrom(cellStyle);
                                cellTo.setCellStyle(newCellStyle);
                            }
                        }

                        cellTo.setCellType(CellType.STRING);

                        String cellValue = formatPositionFrom(Paths.get(SOURCE_PATH));

                        cellTo.setCellValue(cellValue);

                    }

                    XSSFCell cellFrom = rowFrom.getCell(j);

                    if (cellFrom == null) continue;


                    XSSFCell cellTo = rowTo.createCell(toRowIndex);


                    XSSFCellStyle cellStyleFrom = cellFrom.getCellStyle();

                    if (cellStyleFrom != null) {
                        XSSFCellStyle cellStyle = wbWriter.createCellStyle();
                        cellStyle.cloneStyleFrom(cellStyleFrom);
                        cellTo.setCellStyle(cellStyle);
                    }

                    CellType cellType = cellFrom.getCellTypeEnum();

                    cellTo.setCellType(cellType);

                    switch (cellType) {
                        case _NONE:
                            break;
                        case BLANK:
                            break;
                        case ERROR:
                            cellTo.setCellValue(cellFrom.getErrorCellValue());
                            break;
                        case STRING:
                            cellTo.setCellValue(cellFrom.getStringCellValue());
                            break;
                        case BOOLEAN:
                            cellTo.setCellValue(cellFrom.getBooleanCellValue());
                            break;
                        case FORMULA:
                            cellTo.setCellValue(cellFrom.getCellFormula());
                            break;
                        case NUMERIC:
                            cellTo.setCellValue(cellFrom.getNumericCellValue());
                            break;
                        default:
                            cellTo.setCellValue("");
                    }


                }
            }

            FileOutputStream fos = new FileOutputStream(TARGET_PATH);
            wbWriter.write(fos);
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String formatPositionFrom(Path p) {

        final String TEAM_FORMAT = "бригада";

        File file = new File(p.toUri());

        final String fileName = file.getName();
        final String regex = "\\d{2}";


        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(fileName);

        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            int teamNumber = Integer.parseInt(matcher.group(0));

            sb.append(teamNumber)
                    .append(" ");
            break;
        }

        sb.append(TEAM_FORMAT);

        return sb.toString().trim();

    }

}
