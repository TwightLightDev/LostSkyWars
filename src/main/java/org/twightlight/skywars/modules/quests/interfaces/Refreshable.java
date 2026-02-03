package org.twightlight.skywars.modules.quests.interfaces;

import java.time.temporal.ChronoUnit;

public interface Refreshable {
    ChronoUnit getRefreshTimeUnit();
    int getRefreshTimeMultiplication();
    void generateNextRefresh();
    long getNextRefresh();

}
