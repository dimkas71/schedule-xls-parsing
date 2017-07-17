package ua.prettl.ua.prettl.app;

public class Configuration {

    private String path;
    private String team;

    private int from;
    private int to;

    private int colFio;
    private int colNumber;
    private int colPosition;
    private int colFirstDay;

    private Configuration() {
    }

    private Configuration(String path, String team, int from, int to, int colFio, int colNumber, int colPositon, int colFirstDay) {

        this.path = path;
        this.team = team;
        this.from = from;
        this.to = to;
        this.colFio = colFio;
        this.colNumber = colNumber;
        this.colPosition = colPositon;
        this.colFirstDay = colFirstDay;

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getColFio() {
        return colFio;
    }

    public void setColFio(int colFio) {
        this.colFio = colFio;
    }

    public int getColNumber() {
        return colNumber;
    }

    public void setColNumber(int colNumber) {
        this.colNumber = colNumber;
    }

    public int getColPosition() {
        return colPosition;
    }

    public void setColPosition(int colPositon) {
        this.colPosition = colPositon;
    }

    public int getColFirstDay() {
        return colFirstDay;
    }

    public void setColFirstDay(int colFirstDay) {
        this.colFirstDay = colFirstDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;

        if (from != that.from) return false;
        if (to != that.to) return false;
        if (colFio != that.colFio) return false;
        if (colNumber != that.colNumber) return false;
        if (colPosition != that.colPosition) return false;
        if (colFirstDay != that.colFirstDay) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        return team != null ? team.equals(that.team) : that.team == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (team != null ? team.hashCode() : 0);
        result = 31 * result + from;
        result = 31 * result + to;
        result = 31 * result + colFio;
        result = 31 * result + colNumber;
        result = 31 * result + colPosition;
        result = 31 * result + colFirstDay;
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Configuration{");
        sb.append("path='").append(path).append('\'');
        sb.append(", team='").append(team).append('\'');
        sb.append(", from=").append(from);
        sb.append(", to=").append(to);
        sb.append(", colFio=").append(colFio);
        sb.append(", colNumber=").append(colNumber);
        sb.append(", colPositon=").append(colPosition);
        sb.append(", colFirstDay=").append(colFirstDay);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {
        private String path;
        private String team;

        private int from;
        private int to;

        private int colFio;
        private int colNumber;
        private int colPosition;
        private int colFirstDay;

        public Builder() {

        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder team(String team) {
            this.team = team;
            return this;
        }


        public Builder from(int from) {
            this.from = from;
            return this;
        }

        public Builder to(int to) {
            this.to = to;
            return this;
        }

        public Builder fio(int fio) {
            this.colFio = fio;
            return this;
        }

        public Builder number(int number) {
            this.colNumber = number;
            return this;
        }

        public Builder position(int position) {
            this.colPosition = position;
            return this;
        }

        public Builder firstDay(int firstDay) {
            this.colFirstDay = firstDay;
            return this;
        }

        public Configuration build() {
            return new Configuration(path, team, from, to, colFio, colNumber, colPosition, colFirstDay);
        }

    }


}
