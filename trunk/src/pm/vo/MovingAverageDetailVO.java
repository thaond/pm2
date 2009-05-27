package pm.vo;

public class MovingAverageDetailVO {

    private int numberOfDays;

    private float movingAverage;

    public MovingAverageDetailVO(int numberOfDays, float movingAverage) {
        this.numberOfDays = numberOfDays;
        this.movingAverage = movingAverage;
    }

    public float getMovingAverage() {
        return movingAverage;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

}
