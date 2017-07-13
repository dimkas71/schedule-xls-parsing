package ua.prettl;

public class FileConfiguration {

    private String teamValue;
    private int firstRow;
    private int lastRow;

    private int colFio;
    private int colPersNumber;
    private int colPosition;

    private FileConfiguration() {

    }

    private FileConfiguration(String teamValue, int firstRow, int lastRow, int colFio, int colPersNumber, int colPosition) {

        this.teamValue = teamValue;
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.colFio = colFio;
        this.colPersNumber = colPersNumber;
        this.colPosition = colPosition;
    }


    public String getTeamValue() {
        return teamValue;
    }

    public int getFirstRow() {
        return firstRow;
    }

    public int getLastRow() {
        return lastRow;
    }

    public int getColFio() {
        return colFio;
    }

    public int getColPersNumber() {
        return colPersNumber;
    }

    public int getColPosition() {
        return colPosition;
    }


    @Override
    public String toString() {
        return "FileConfiguration{" +
                "teamValue='" + teamValue + '\'' +
                ", firstRow=" + firstRow +
                ", lastRow=" + lastRow +
                ", colFio=" + colFio +
                ", colPersNumber=" + colPersNumber +
                ", colPosition=" + colPosition +
                '}';
    }

    public static class FileConfigurationBuilder {

        private String teamValue;
        private int firstRow;
        private int lastRow;

        private int colFio;
        private int colPersNumber;
        private int colPosition;

        public FileConfigurationBuilder teamValue(String teamValue) {
            this.teamValue = teamValue;
            return this;
        }

        public FileConfigurationBuilder firstRow(int firstRow) {
            this.firstRow = firstRow;
            return this;
        }

        public FileConfigurationBuilder lastRow(int lastRow) {
            this.lastRow = lastRow;
            return this;
        }

        public FileConfigurationBuilder fio(int fio) {
            this.colFio = fio;
            return this;
        }

        public FileConfigurationBuilder personNumber(int persNum) {
            this.colPersNumber = persNum;
            return this;
        }

        public FileConfigurationBuilder position(int position) {
            this.colPosition = position;
            return this;
        }

        public FileConfiguration build() {
            return new FileConfiguration(teamValue, firstRow, lastRow, colFio, colPersNumber, colPosition);
        }

    }



}
