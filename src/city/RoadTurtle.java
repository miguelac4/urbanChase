package city;

import fractals.Turtle;
import processing.core.PVector;

import java.util.ArrayList;

public class RoadTurtle extends Turtle {

    private ArrayList<RoadSegment> roads;

    public RoadTurtle(float len, float angle, ArrayList<RoadSegment> roads) {
        super(len, angle);
        this.roads = roads;
    }

    @Override
    protected void onSegment(PVector startWorld, PVector endWorld) {
        roads.add(new RoadSegment(startWorld.copy(), endWorld.copy()));
    }
}
