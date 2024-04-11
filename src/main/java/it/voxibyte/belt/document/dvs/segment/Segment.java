package it.voxibyte.belt.document.dvs.segment;


public interface Segment {


    static Segment range(int start, int end, int step, int fill) {
        return new RangeSegment(start, end, step, fill);
    }


    static Segment range(int start, int end, int step) {
        return new RangeSegment(start, end, step, 0);
    }


    static Segment range(int start, int end) {
        return new RangeSegment(start, end, start < end ? 1 : -1, 0);
    }


    static Segment literal(String... elements) {
        return new LiteralSegment(elements);
    }


    int parse(String versionId, int index);


    String getElement(int index);


    int getElementLength(int index);


    int length();
}
