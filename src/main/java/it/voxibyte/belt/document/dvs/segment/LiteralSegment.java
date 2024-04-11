package it.voxibyte.belt.document.dvs.segment;

import java.util.Arrays;


public class LiteralSegment implements Segment {


    private final String[] elements;


    public LiteralSegment(String... elements) {
        this.elements = elements;
    }

    @Override

    public int parse(String versionId, int index) {

        for (int i = 0; i < elements.length; i++) {

            if (versionId.startsWith(elements[i], index))

                return i;
        }


        return -1;
    }

    @Override

    public String getElement(int index) {
        return elements[index];
    }

    @Override

    public int getElementLength(int index) {
        return elements[index].length();
    }

    @Override

    public int length() {
        return elements.length;
    }

    @Override

    public String toString() {
        return "LiteralSegment{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }
}
