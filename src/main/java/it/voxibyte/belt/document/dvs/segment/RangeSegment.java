package it.voxibyte.belt.document.dvs.segment;

import java.util.Arrays;


public class RangeSegment implements Segment {


    private final int start, end, step, minStringLength, maxStringLength, fill, length;


    public RangeSegment(int start, int end, int step, int fill) {
        this.start = start;
        this.end = end;
        this.step = step;
        this.fill = fill;


        if (step == 0)
            throw new IllegalArgumentException("Step cannot be zero!");

        if ((start < end && step < 0) || (start > end && step > 0))
            throw new IllegalArgumentException(String.format("Invalid step for the given range! start=%d end=%d step=%d", start, end, step));

        if (start == end)
            throw new IllegalArgumentException(String.format("Parameters define an empty range, start=end! start=%d end=%d", start, end));


        this.length = (int) Math.ceil((double) Math.abs(start - end) / Math.abs(step));

        int last = start + step * (length - 1);

        if (start < 0 || (end < 0 && last < 0))
            throw new IllegalArgumentException(String.format("Range contains negative integers! start=%d end=%d step=%d", start, end, step));

        if (fill > 0 && !validateFill(fill, Math.max(start, last)))
            throw new IllegalArgumentException(String.format("Some integer from the range exceeds maximum length defined by the filling parameter! start=%d end=%d last=%d fill=%d", start, end, last, fill));


        this.maxStringLength = fill > 0 ? fill : countDigits(step > 0 ? end : start);
        this.minStringLength = fill > 0 ? fill : countDigits(step > 0 ? start : end);
    }


    
    public RangeSegment(int start, int end) {
        this(start, end, start < end ? 1 : -1, 0);
    }


    
    public RangeSegment(int start, int end, int step) {
        this(start, end, step, 0);
    }


    private boolean validateFill(int fill, int maxValue) {

        int maxFillValue = 9;

        for (int i = 0; i < fill; i++) {

            if (maxFillValue >= maxValue)
                return true;

            maxFillValue *= 10;
            maxFillValue += 9;
        }
        return false;
    }


    @Override

    public int parse(String versionId, int index) {

        if (fill > 0) {

            if (fill > versionId.length() - index)
                return -1;

            try {
                return getRangeIndex(Integer.parseInt(versionId.substring(index, fill)));
            } catch (NumberFormatException ignored) {
                return -1;
            }
        }


        if (versionId.length() <= index)
            return -1;


        int value = 0;
        int digits = 0;

        for (int i = 0; i < maxStringLength; i++) {

            if (i >= versionId.length() - index)
                break;

            if (i == 1 && value == 0 && digits == 1)
                break;


            int digit = Character.digit(versionId.charAt(index + i), 10);

            if (digit == -1)
                break;


            value *= 10;

            value += digit;
            digits += 1;
        }


        if (digits == 0)
            return -1;

        if (value == 0)
            return getRangeIndex(0);


        while (value > 0) {

            if (digits < minStringLength)
                break;

            int rangeIndex = getRangeIndex(value);

            if (rangeIndex != -1)
                return rangeIndex;

            value /= 10;

            digits -= 1;
        }


        return -1;
    }


    private int countDigits(int value) {

        if (value == 0)
            return 1;

        int digits = 0;

        for (; value > 0; digits++)
            value /= 10;

        return digits;
    }


    private int getRangeIndex(int value) {

        if (step > 0) {
            if (start > value || end <= value)
                return -1;
        } else {
            if (start < value || end >= value)
                return -1;
        }


        int diff = Math.abs(value - start);

        if (value >= 0 && diff % step == 0)

            return diff / Math.abs(step);
        else

            return -1;
    }

    @Override

    public String getElement(int index) {

        if (index >= length)
            throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", index, length));

        String value = Integer.toString(start + step * index, 10);

        if (fill <= 0 || value.length() == fill)
            return value;


        char[] fill = new char[this.fill - value.length()];
        Arrays.fill(fill, '0');

        return new StringBuilder(value).insert(0, fill).toString();
    }

    @Override

    public int getElementLength(int index) {

        if (index >= length)
            throw new IndexOutOfBoundsException(String.format("Index out of bounds! i=%d length=%d", index, length));

        return fill > 0 ? fill : countDigits(start + step * index);
    }

    @Override

    public int length() {
        return length;
    }

    @Override

    public String toString() {
        return "RangeSegment{" +
                "start=" + start +
                ", end=" + end +
                ", step=" + step +
                ", fill=" + fill +
                ", length=" + length +
                '}';
    }
}
