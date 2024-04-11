package it.voxibyte.belt.document.dvs;

import it.voxibyte.belt.document.dvs.segment.Segment;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;


public class Pattern {


    private final Segment[] segments;


    public Pattern(Segment... segments) {
        this.segments = segments;
    }


    public Segment getPart(int index) {
        return segments[index];
    }

    public Segment getSegment(int index) {
        return segments[index];
    }


    @Nullable
    public Version getVersion(String versionId) {

        int[] cursors = new int[segments.length];

        int start = 0;

        for (int index = 0; index < segments.length; index++) {

            int cursor = segments[index].parse(versionId, start);

            if (cursor == -1)
                return null;

            cursors[index] = cursor;

            start += segments[index].getElementLength(cursor);
        }


        return new Version(versionId, this, cursors);
    }


    public Version getFirstVersion() {
        return new Version(null, this, new int[segments.length]);
    }

    @Override

    public String toString() {
        return "Pattern{" +
                "segments=" + Arrays.toString(segments) +
                '}';
    }
}
