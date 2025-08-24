package org.twightlight.skywars.utils;

import org.bukkit.util.Vector;

public class VectorUtils {
    public static void rotateAroundAxisX(Vector v, double angle) {
        double y = v.getY();
        double z = v.getZ();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        v.setY(y * cos - z * sin);
        v.setZ(y * sin + z * cos);
    }

    public static void rotateAroundAxisY(Vector v, double angle) {
        double x = v.getX();
        double z = v.getZ();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        v.setX(x * cos + z * sin);
        v.setZ(-x * sin + z * cos);
    }

    public static void rotateAroundAxisZ(Vector v, double angle) {
        double x = v.getX();
        double y = v.getY();
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        v.setX(x * cos - y * sin);
        v.setY(x * sin + y * cos);
    }

    public static void rotate(Vector v, double pitch, double yaw, double roll) {
        rotateAroundAxisX(v, pitch);
        rotateAroundAxisY(v, yaw);
        rotateAroundAxisZ(v, roll);
    }

    public static Vector randomVector() {
        Vector vec = Vector.getRandom().multiply(2);
        vec.setX(vec.getX() - 1.0D);
        vec.setY(vec.getY() - 1.0D);
        vec.setZ(vec.getZ() - 1.0D);
        return vec;
    }
}
