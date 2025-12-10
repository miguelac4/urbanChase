package physics;

import processing.core.PVector;

public class PSControl {

    private float averageAngle;
    private float dispersionAngle;
    private float minVelocity;
    private float maxVelocity;

    public PSControl(float[] velControl){
        averageAngle = velControl[0];
        dispersionAngle = velControl[1];
        minVelocity = velControl[2];
        maxVelocity = velControl[3];
    }

    public PVector getRndVel(){
        float angle = getRnd(averageAngle - dispersionAngle/2, averageAngle + dispersionAngle/2);
        PVector v = PVector.fromAngle(angle);
        v.mult(getRnd(minVelocity, maxVelocity));
        return v;
    }

    public static float getRnd(float min, float max){
        return min + (float) (Math.random() * (max - min));
    }
}
