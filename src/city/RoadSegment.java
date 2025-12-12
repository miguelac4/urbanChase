package city;

import processing.core.PVector;

public class RoadSegment {
    public PVector start;
    public PVector end;

    public RoadSegment(PVector start, PVector end) {
        this.start = start;
        this.end = end;
    }
}
