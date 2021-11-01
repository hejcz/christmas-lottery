package io.github.hejcz.domain.lottery;

import java.util.List;

public record StartLotteryRequest(int groupId, List<Integer> participantsIds) {
}
