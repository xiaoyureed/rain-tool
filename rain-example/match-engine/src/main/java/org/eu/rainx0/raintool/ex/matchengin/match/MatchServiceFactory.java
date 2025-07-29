package org.eu.rainx0.raintool.ex.matchengin.match;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiaoyu
 * @time 2025/7/20 19:49
 */
public class MatchServiceFactory {
    private static final Map<MatchStrategy, MatchService> store = new HashMap<>();

    public static MatchService get(MatchStrategy strategy) {
        return store.get(strategy);
    }

    public static void register(MatchStrategy strategy, MatchService matchService) {
        if (store.containsKey(strategy)) {
            throw new RuntimeException("Match service " + strategy.name() + " already registered");
        }
        store.put(strategy, matchService);
    }
}
