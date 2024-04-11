package it.voxibyte.belt.document.dvs;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;


public class Version implements Comparable<Version> {


    @Getter
    private final Pattern pattern;

    private final int[] cursors;

    private String id;


    Version(String id, Pattern pattern, int[] cursors) {
        this.id = id;
        this.pattern = pattern;
        this.cursors = cursors;

        if (id == null)
            buildID();
    }

    @Override

    public int compareTo(Version o) {

        if (!pattern.equals(o.pattern))
            throw new ClassCastException("Compared versions are not defined by the same pattern!");


        for (int index = 0; index < cursors.length; index++) {

            int compared = Integer.compare(cursors[index], o.cursors[index]);

            if (compared == 0)
                continue;

            return compared;
        }


        return 0;
    }


    public int getCursor(int index) {
        return cursors[index];
    }


    public void next() {

        for (int index = cursors.length - 1; index >= 0; index--) {

            int cursor = cursors[index];

            if (cursor + 1 >= pattern.getSegment(index).length()) {

                cursors[index] = 0;

                continue;
            }


            cursors[index] = cursor + 1;
            break;
        }


        buildID();
    }


    private void buildID() {

        StringBuilder builder = new StringBuilder();

        for (int index = 0; index < cursors.length; index++)

            builder.append(pattern.getSegment(index).getElement(cursors[index]));

        id = builder.toString();
    }


    public String asID() {
        return id;
    }


    public Version copy() {
        return new Version(id, pattern, Arrays.copyOf(cursors, cursors.length));
    }


    @Override

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Version version)) return false;
        return pattern.equals(version.pattern) && Arrays.equals(cursors, version.cursors);
    }

    @Override

    public int hashCode() {
        int result = Objects.hash(pattern);
        result = 31 * result + Arrays.hashCode(cursors);
        return result;
    }

    @Override

    public String toString() {
        return "Version{" +
                "pattern=" + pattern +
                ", cursors=" + Arrays.toString(cursors) +
                ", id='" + id + '\'' +
                '}';
    }
}
