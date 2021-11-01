package io.github.hejcz.web.resources;

import java.util.List;

public record StartLotteryDto(int groupId, List<Integer> participantsIds) {
}
