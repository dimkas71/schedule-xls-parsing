package ua.prettl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Application2 {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("/home/dimkas71/temp1/test.xlsx");

        if (Files.exists(path)) {
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(path.toFile()));
            if (wb.getNumberOfSheets() > 1) {
                //TODO: wb.close() should be done coreectly!!!
                throw new RuntimeException("Should be only one sheet on the workbook");

            }
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                System.out.println(wb.getSheetName(i));
                for (Row row : sheet) {
                    System.out.println("rownum: " + row.getRowNum());
                    for (Cell cell : row) {
                        System.out.println(cell);
                    }
                }
            }
            wb.close();
        }

        FileConfiguration.FileConfigurationBuilder builder = new FileConfiguration.FileConfigurationBuilder();

        FileConfiguration configuration = builder
                .teamValue("11 brigada")
                .fio(1)
                .firstRow(7)
                .lastRow(22)
                .personNumber(2)
                .position(3)
                .build();

        Map<Path, FileConfiguration> map = new HashMap<>();

        map.put(path, configuration);

        System.out.println(map);



    }
}
