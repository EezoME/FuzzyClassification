package edu.eezo.fzcl.entities.internal;

import java.util.Arrays;

/**
 * This class represents a range (or interval) for letter type.
 */
public class Range {
    private int startPoint;
    private int endPoint;
    private int[] lowSubrange;
    private int[] mediumSubrange;
    private int[] highSubrange;

    public Range(int startPoint, int endPoint) {
        if (startPoint >= endPoint) return;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    private Range(int[] subrange) {
        this.startPoint = subrange[0];
        this.endPoint = subrange[1];
    }

    public Range getLowSubrange() {
        if (this.lowSubrange == null) {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int high = (int) (localInterval * 0.5);
            this.lowSubrange = new int[]{this.startPoint, this.startPoint + high};
        }
        return new Range(this.lowSubrange);
    }

    public Range getMiddleSubrange() {
        if (this.mediumSubrange == null) {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int low = (int) (localInterval * 0.25);
            int high = (int) (localInterval * 0.75);
            this.mediumSubrange = new int[]{this.startPoint + low, this.startPoint + high};
        }
        return new Range(this.mediumSubrange);
    }

    public Range getHighSubrange() {
        if (this.highSubrange == null) {
            if (!isCorrect()) return null;
            int localInterval = this.endPoint - this.startPoint + 1;
            int low = (int) (localInterval * 0.5);
            this.highSubrange = new int[]{this.startPoint + low, this.endPoint};
        }
        return new Range(this.highSubrange);
    }

    private boolean isCorrect() {
        return this.startPoint < this.endPoint;
    }

    public String toFullString() {
        return "Range{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                ", lowSubrange=" + Arrays.toString(lowSubrange) +
                ", mediumSubrange=" + Arrays.toString(mediumSubrange) +
                ", highSubrange=" + Arrays.toString(highSubrange) +
                '}';
    }

    @Override
    public String toString() {
        return "Range{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }
}
