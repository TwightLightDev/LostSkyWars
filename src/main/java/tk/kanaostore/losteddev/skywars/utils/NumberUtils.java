package tk.kanaostore.losteddev.skywars.utils;

import java.util.List;

public class NumberUtils {
    public static int closestGreater(List<Integer> list, int pivot) {
        int low = 0;
        int high = list.size();
        while (low < high) {
            int mid = low + (high - low) / 2;
            if (list.get(mid) <= pivot) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }
        return low;
    }
}
