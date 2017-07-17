package ua.prettl.ua.prettl.app;

import com.google.gson.Gson;
import org.apache.commons.cli.*;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private enum Mode {

        GENERATE("generate"),
        MERGE("merge");

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

                int rowToAppend = 0;

                Path toFilePath = Paths.get(System.getProperty("user.dir")).resolve(newFile);

                for (Configuration c : configurations) {

                    if (isFirstFile) {
                        //

                        if (Files.exists(toFilePath)) {
                            Files.delete(toFilePath);
                        }

                        createNewFile(toFilePath);

                        rowToAppend += copySheetFrom(c, toFilePath.toString(), true, rowToAppend);

                        isFirstFile = false;
                    } else {
                        rowToAppend += copySheetFrom(c, toFilePath.toString(), false, rowToAppend);
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

    private static int copySheetFrom(Configuration source, String target, boolean createSheet, int rowToNumber) {

        final int TEAM_COL = 3;

        int rowToIndex = rowToNumber++;

        try {

            XSSFWorkbook wbReader = new XSSFWorkbook(new FileInputStream(source.getPath()));

            XSSFSheet sheetFrom = wbReader.getSheetAt(0);

            XSSFWorkbook wbWriter = new XSSFWorkbook(new FileInputStream(target));

            XSSFSheet sheetTo = createSheet ? wbReader.createSheet(): wbWriter.getSheetAt(0);

            for (int i = source.getFrom() - 1; i < source.getTo(); i++) {
                XSSFRow rowFrom = sheetFrom.getRow(i);
                if (rowFrom == null) continue;

                XSSFRow rowTo = sheetTo.createRow(rowToIndex);


                for (int j = rowFrom.getFirstCellNum(); j <= source.getColFirstDay() + 15; j++) {

                    int toRowIndex = j >= TEAM_COL ? j + 1 : j;


                    if (j == TEAM_COL) {
                        //1. The team value column sould look the same as the fio Column

                        XSSFCell fioCell = rowFrom.getCell(source.getColFio());

                        XSSFCell cellTo = rowTo.createCell(TEAM_COL);
                        //set cell style
                        if (fioCell != null) {

//                            XSSFCellStyle cellStyle = fioCell.getCellStyle();
//                            if (cellStyle != null) {
//                                XSSFCellStyle newCellStyle = wbWriter.createCellStyle();
//                                newCellStyle.cloneStyleFrom(cellStyle);
//                                cellTo.setCellStyle(newCellStyle);
//                            }
                        }

                        cellTo.setCellType(CellType.STRING);

                        String cellValue = formatPositionFrom(Paths.get(source.getPath()));

                        cellTo.setCellValue(cellValue);

                    }

                    XSSFCell cellFrom = rowFrom.getCell(j);

                    if (cellFrom == null) continue;


                    XSSFCell cellTo = rowTo.createCell(toRowIndex);


//                    XSSFCellStyle cellStyleFrom = cellFrom.getCellStyle();
//
//                    if (cellStyleFrom != null) {
//                        XSSFCellStyle cellStyle = wbWriter.createCellStyle();
//                        cellStyle.cloneStyleFrom(cellStyleFrom);
//                        cellTo.setCellStyle(cellStyle);
//                    }

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

        return rowToIndex;
    }



}
