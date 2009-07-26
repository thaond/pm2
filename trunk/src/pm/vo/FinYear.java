package pm.vo;

import pm.util.PMDate;

public class FinYear {
    private int stYear;
    private int endYear;

    public FinYear(int stYear) {
        this.stYear = stYear;
        endYear = stYear + 1;
    }

    @Override
    public String toString() {
        return stYear + " - " + endYear;
    }

    public PMDate startDate() {
        return new PMDate(1, 4, stYear);
    }

    public PMDate endDate() {
        return new PMDate(31, 3, endYear);
    }

}
