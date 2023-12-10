package io.github.hejcz.domain.lottery;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphHelper {

    private GraphHelper() {
    }

    // not optimal, but simple and good enough for small groups
    public static int countStrongComponents(Collection<Match> matches) {
        final Map<UserId, Integer> userToIndex = new HashMap<>();
        int index = 0;
        for (Match m : matches) {
            userToIndex.put(m.giver(), index++);
        }

        int[] groups = new int[matches.size()];
        int freeGroupId = 1;

        for (Match match : matches) {
            int giverGroup = groups[userToIndex.get(match.giver())];
            int recipientGroup = groups[userToIndex.get(match.recipient())];
            if (giverGroup != 0 && recipientGroup != 0) {
                int newGroup = freeGroupId++;
                for (int i = 0; i < groups.length; i++) {
                    if (groups[i] == giverGroup || groups[i] == recipientGroup) {
                        groups[i] = newGroup;
                    }
                }
            } else if (giverGroup == 0 && recipientGroup == 0) {
                int newGroup = freeGroupId++;
                groups[userToIndex.get(match.giver())] = newGroup;
                groups[userToIndex.get(match.recipient())] = newGroup;
            } else if (giverGroup != 0) {
                groups[userToIndex.get(match.recipient())] = giverGroup;
            } else {
                groups[userToIndex.get(match.giver())] = recipientGroup;
            }
        }

        return Math.toIntExact(Arrays.stream(groups).distinct().count());
    }

}
