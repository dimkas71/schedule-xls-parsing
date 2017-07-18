package ua.prettl.ua.prettl.app;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private enum Mode {

        GENERATE("generate"),
        MERGE("merge"),
        BUILDSCHEDULE("buildschedule"),
        FULLTIME("fulltime"),
        TIMETABLE("timetable");

        String name;

        Mode(String name) {
            this.name = name;
        }

    }


    public static void main(String[] args) {

        Options options = new Options();

        //1. add mode option to the cli
        options.addOption(
                Option.builder("m")
                    .argName("mode")
                    .hasArg()
                    .desc("use -m mode([generate|merge|create])")
                    .longOpt("mode")
                    .build()
            );

        //2. add directory option to the cli

        options.addOption(
                Option.builder("d")
                    .hasArg()
                    .argName("dir")
                    .desc("use -d dir")
                    .longOpt("dir")
                    .build()
        );

        //3. add regex option to the cli
        options.addOption(
                Option.builder("r")
                    .hasArg()
                    .argName("regex")
                    .desc("use -r regex")
                    .longOpt("regex")
                    .build()

        );

        //4. add file option to the cli
        options.addOption(
                Option.builder("f")
                    .hasArg()
                    .argName("file")
                    .desc("use -f file")
                    .longOpt("file")
                    .build()

        );

        //5. add new file
        options.addOption(
                Option.builder("n")
                    .hasArg()
                    .argName("newFile")
                    .desc("use -n newFile")
                    .longOpt("newFile")
                    .build()
        );

        //6. add int option to the first day column
        options.addOption(
          Option.builder("c")
            .hasArg()
            .argName("first day column")
            .desc("use -c 4")
            .longOpt("column")
            .build()
        );

        //7. add int option to the first day column
        options.addOption(
                Option.builder("t")
                        .hasArg()
                        .argName("time column")
                        .desc("use -t 20")
                        .longOpt("timecolumn")
                        .build()
        );


        CommandLineParser clp = new DefaultParser();

        try {
            CommandLine cl = clp.parse(options, args);

            if (cl.hasOption("m")) {

                String modeName = cl.getOptionValue("m");

                Mode mode = Mode.valueOf(modeName.toUpperCase());

                switch (mode) {
                    case GENERATE:

                        String dir = "";

                        if (cl.hasOption("d")) {
                            dir = cl.getOptionValue("d");
                        }

                        String regex = "";

                        if (cl.hasOption("r")) {
                            regex = cl.getOptionValue("r");
                        }

                        String file = "configs.json"; //by default

                        if (cl.hasOption("f")) {
                            file = cl.getOptionValue("f");
                        }

                        generate(dir, regex, file);

                        break;
                    case MERGE:

                        String config = "configs.json";
                        if (cl.hasOption("f")) {
                            config = cl.getOptionValue("f");
                        }

                        String newFile = "new.xlsx";
                        if (cl.hasOption("n")) {
                            newFile = cl.getOptionValue("n");
                        }
                        merge(newFile, config);
                        break;
                    case BUILDSCHEDULE:
                        String target = "newfile.xlsx";
                        if (cl.hasOption("f")) {
                            target = cl.getOptionValue("f");
                        }

                        int firstDayColumn = 4;

                        if (cl.hasOption("c")) {
                            firstDayColumn = Integer.parseInt(cl.getOptionValue("c"));
                        }

                        if (!Files.exists(Paths.get(target))) {
                            String userDir = System.getProperty("user.dir");

                            Path newPath = Paths.get(userDir).resolve(target);

                            target = String.valueOf(newPath.toAbsolutePath());

                        }

                        saveUniqueScheduleValuesToXLSX(scanSchedule(target, firstDayColumn), target);

                        break;
                    case FULLTIME:
                        String _target = "new.xlsx";
                        if (cl.hasOption("f")) {
                            _target = cl.getOptionValue("f");
                        }

                        int _firstDayColumn = 4;

                        if (cl.hasOption("c")) {
                            _firstDayColumn = Integer.parseInt(cl.getOptionValue("c"));
                        }

                        int _timeColumn = 20;

                        if (cl.hasOption("t")) {
                            _timeColumn = Integer.parseInt(cl.getOptionValue("t"));
                        }


                        if (!Files.exists(Paths.get(_target))) {
                            String userDir = System.getProperty("user.dir");

                            Path newPath = Paths.get(userDir).resolve(_target);

                            _target = String.valueOf(newPath.toAbsolutePath());

                        }
                        updateFieldTimeForFile(_target, createTimeMapperFromPath(_target), _firstDayColumn, _timeColumn);


                        break;
                    case TIMETABLE:
                        String newPath = "newfff.xlsx";
                        if (cl.hasOption("n")) {
                            newPath = cl.getOptionValue("n");
                            if (!Files.exists(Paths.get(newPath))) {
                                String userDir = System.getProperty("user.dir");
                                Path path1 = Paths.get(userDir).resolve(newPath);
                                newPath = String.valueOf(path1.toAbsolutePath());
                            }

                        }

                        String oldPath = "new.xlsx";
                        if (cl.hasOption("f")) {
                            oldPath = cl.getOptionValue("f");
                            if (!Files.exists(Paths.get(oldPath))) {
                                String userDir = System.getProperty("user.dir");
                                Path path1 = Paths.get(userDir).resolve(oldPath);
                                oldPath = String.valueOf(path1.toAbsolutePath());
                            }

                        }

                        int __firstDayColumn = 4;
                        if (cl.hasOption("c")) {
                            __firstDayColumn = Integer.parseInt(cl.getOptionValue("c"));
                        }

                        int __lastDayColumn = 18;
                        if (cl.hasOption("t")) {
                            __lastDayColumn = Integer.parseInt(cl.getOptionValue("t"));
                        }

                        writeTimeTableForFile(newPath, oldPath, createTimeMapperFromPath(oldPath), __firstDayColumn, __lastDayColumn);
                         break;
                    default:
                        break;

                }


            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private static void merge(String newFile, String config) {

        //1.read config files and restore configs

        File file = new File(config);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                byte[] bytes = Files.readAllBytes(Paths.get(config));

                String fromJson = new String(bytes);

                fis.close();

                Gson gson = new Gson();

                Configuration[] configurations = gson.fromJson(fromJson, Configuration[].class);

                //create xlxs file
                boolean isFirstFile = true;

                Path toFilePath = Paths.get(System.getProperty("user.dir")).resolve(newFile);


                List<XSSFCellStyle> listStyles = new ArrayList<>();


                for (Configuration c : configurations) {

                    if (isFirstFile) {
                        //

                        if (Files.exists(toFilePath)) {
                            Files.delete(toFilePath);
                        }

                        createNewFile(toFilePath);

                        copySheetFrom(c, toFilePath.toString(), true, listStyles);

                        isFirstFile = false;
                    } else {
                        copySheetFrom(c, toFilePath.toString(), false, listStyles);
                    }


                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //TODO: log this config doesn't exist
        }


    }

    private static void createNewFile(Path toFilePath) {
        XSSFWorkbook wbWritable = new XSSFWorkbook();
        wbWritable.createSheet("Sheet1");

        try {
            FileOutputStream fos = new FileOutputStream(toFilePath.toFile());
            wbWritable.write(fos);
            fos.close();

            wbWritable.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generate(String path, String regex, String configFile) {

        //1. get list of files in dir path....
        File dir = new File(path);
        if (!dir.exists()) {
            //TODO: log this situation
            return;
        }

        List<File> files = new ArrayList<>();

        Pattern pattern = Pattern.compile(regex);

        for (File f : new File(path).listFiles()) {
            if (!f.isDirectory()) {
                String name = f.getName();

                Matcher m = pattern.matcher(name);

                if (m.find()) {
                    files.add(f);
                }
            }
        }




        //2. for each file create an array for configuration infos

        List<Configuration> listConfig = new ArrayList<>();

        final int from = 7;
        final int to = 22;
        final int colFio = 1;
        final int colNumber = 2;
        final int colPosition = 3;
        final int colFirstDay = 4;

        for (File f : files) {
            String p = f.getAbsolutePath();
            String t = "11 бригада";

            listConfig.add(new Configuration.Builder()
                    .path(p)
                    .team(t)
                    .from(from)
                    .to(to)
                    .fio(colFio)
                    .number(colNumber)
                    .position(colPosition)
                    .firstDay(colFirstDay)
                    .build());
        }


        Configuration[] configs = listConfig.toArray(new Configuration[]{});

        //3. write json to the file configFile
        if (!Files.exists(Paths.get(configFile))) {
            String userDir = System.getProperty("user.dir");
            Path filePath = Paths.get(userDir).resolve(configFile);

            Gson gson = new Gson();

            String jsonString = gson.toJson(configs);

            try {

                FileOutputStream fos = new FileOutputStream(new File(String.valueOf(filePath.toFile())));
                fos.write(jsonString.getBytes());

                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


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

    private static void copySheetFrom(Configuration source, String target, boolean createSheet, List<XSSFCellStyle> listStyles) {

        final int TEAM_COL = 3;

        int rowToIndex = -Integer.MIN_VALUE;

        try {

            XSSFWorkbook wbReader = new XSSFWorkbook(new FileInputStream(Paths.get(source.getPath()).toFile()));

            XSSFSheet sheetFrom = wbReader.getSheetAt(0);

            XSSFWorkbook wbWriter = new XSSFWorkbook(new FileInputStream(target));

            XSSFSheet sheetTo = createSheet ? wbReader.createSheet(): wbWriter.getSheetAt(0);

            rowToIndex = sheetTo.getLastRowNum() + 1;



            for (int i = source.getFrom() - 1; i < source.getTo(); i++) {
                XSSFRow rowFrom = sheetFrom.getRow(i);
                if (rowFrom == null) continue;

                XSSFRow rowTo = sheetTo.createRow(rowToIndex++);


                for (int j = rowFrom.getFirstCellNum(); j < source.getColFirstDay() + 15; j++) {

                    int toRowIndex = j >= TEAM_COL ? j + 1 : j;


                    if (j == TEAM_COL) {
                        //1. The team value column sould look the same as the fio Column

                        XSSFCell fioCell = rowFrom.getCell(source.getColFio());

                        XSSFCell cellTo = rowTo.createCell(TEAM_COL);
                        //set cell style
                        if (fioCell != null) {
                              //XSSFSheetUtils.copyCell(fioCell, cellTo, listStyles);
//
//                              XSSFCellStyle cellStyle = fioCell.getCellStyle();
//                            if (cellStyle != null) {
//                                XSSFCellStyle newCellStyle = wbWriter.createCellStyle();
//                                try {
//                                    newCellStyle.cloneStyleFrom(cellStyle);
//                                    cellTo.setCellStyle(newCellStyle);
//                                } catch (IllegalArgumentException ex) {
//                                    ex.printStackTrace();
//                                }
//
//                            }
                        }

                        cellTo.setCellType(CellType.STRING);

                        String cellValue = formatPositionFrom(Paths.get(source.getPath()));

                        cellTo.setCellValue(cellValue);

                    }

                    XSSFCell cellFrom = rowFrom.getCell(j);

                    if (cellFrom == null) continue;


                    XSSFCell cellTo = rowTo.createCell(toRowIndex);


                    XSSFCellStyle cellStyleFrom = cellFrom.getCellStyle();


                    if (cellStyleFrom != null) {
                          //XSSFSheetUtils.copyCell(cellFrom, cellTo, listStyles);
//                        XSSFCellStyle cellStyle = wbWriter.createCellStyle();
//                        try {
//                            cellStyle.cloneStyleFrom(cellStyleFrom);
//                            cellTo.setCellStyle(cellStyle);
//                        } catch (IllegalArgumentException ex) {
//                            ex.printStackTrace();
//                        }


                    }

                    //TODO: code below should be removed because of the method XSSFSheetUtils.copyCell does the same!!!!

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

            FileOutputStream fos = new FileOutputStream(target);
            wbWriter.write(fos);
            fos.close();

            wbWriter.close();
            wbReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static List<String> scanSchedule(String target, int firstDayColumn) {
        List<String> differentCellsValues = new ArrayList<>();

        try {
            XSSFWorkbook wbReader = new XSSFWorkbook(new FileInputStream(target));

            XSSFSheet activeSheet = wbReader.getSheetAt(0);

            for (int rowIndex = activeSheet.getFirstRowNum(); rowIndex <= activeSheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = activeSheet.getRow(rowIndex);
                for (int colIndex = firstDayColumn; colIndex <= row.getLastCellNum(); colIndex++) {
                    XSSFCell cell = row.getCell(colIndex);
                    if (cell == null) continue;
                    String rawValue = "";
                    if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                        rawValue = String.valueOf(cell.getNumericCellValue());
                    } else {
                        rawValue = cell.getStringCellValue();
                    }

                    String[] strings = rawValue.split("\\n");

                    for (String current : strings) {
                        if (!current.isEmpty()) {
                            differentCellsValues.add(current);
                        }
                    }
                }
            }
            wbReader.close();
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

            wbWritable.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void updateFieldTimeForFile(String targetPath, Map<String, Double> mapper, int firstDayColumn, int timeColumn) {


        try {

            XSSFWorkbook wbWriteable = new XSSFWorkbook(new FileInputStream(targetPath));

            XSSFSheet sheet = wbWriteable.getSheetAt(0);


            //TODO: change sheet.getFirstRowNum() + 1 on more predictable behaviour
            for (int rowIndex = sheet.getFirstRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                XSSFRow row = sheet.getRow(rowIndex);


                int hours = 0;
                for (int colIndex = firstDayColumn; colIndex <= firstDayColumn + 15; colIndex++ ) {
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

                XSSFCell hoursCell = row.createCell(timeColumn);

                hoursCell.setCellType(CellType.NUMERIC);
                hoursCell.setCellValue(hours);
            }

            FileOutputStream fos = new FileOutputStream(targetPath);

            wbWriteable.write(fos);

            fos.close();

            wbWriteable.close();

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

                wbReadable.close();

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

    private static void writeTimeTableForFile(String newStringPath, String sourcePath, Map<String, Double> mapper, int firstDayColumn, int lastDayColumn) {
        if (!Files.exists(Paths.get(newStringPath))) {
            try {
                Files.copy(Paths.get(sourcePath), Paths.get(newStringPath), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);

                XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(newStringPath));

                XSSFSheet sheet = workbook.getSheetAt(0);

                boolean isModified = false;

                for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {

                    XSSFRow row = sheet.getRow(rowIndex);

                    for (int colIndex = firstDayColumn; colIndex <= lastDayColumn; colIndex++) {
                        XSSFCell cell = row.getCell(colIndex);
                        if (cell == null) continue;

                        String cellValue = "";

                        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                            cellValue = String.valueOf(cell.getNumericCellValue());
                        } else {
                            cellValue = cell.getStringCellValue();
                        }


                        String[] keys = cellValue.split("\\n");

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

                    workbook.close();
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
