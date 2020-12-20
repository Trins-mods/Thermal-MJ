package trinsdar.thermal_mj;

import buildcraft.api.mj.MjAPI;

public class MJHelper {
    public static int fromMicroJoules(long microJoules) {
        return (int) (microJoules / MjAPI.MJ);
    }

    public static long toMicroJoules(long mj) {
        return mj * MjAPI.MJ;
    }

    public static int rfToMj(int rf) {
        return rf / 10;
    }

    public static int mjToRf(int mj) {
        return mj * 10;
    }

    public static long rfToMicro(int rf) {
        return toMicroJoules(rfToMj(rf));
    }

    public static int microToRf(long microJoules) {
        return mjToRf(fromMicroJoules(microJoules));
    }
}
