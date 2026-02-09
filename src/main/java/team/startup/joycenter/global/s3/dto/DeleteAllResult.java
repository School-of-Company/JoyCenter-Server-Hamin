package team.startup.joycenter.global.s3.dto;

import java.util.List;

public record DeleteAllResult(
        List<String> successKeys,
        List<String> failedKeys
) {
    public int successCount() { return successKeys == null ? 0 : successKeys.size(); }
}