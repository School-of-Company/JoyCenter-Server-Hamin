package team.startup.joycenter.domain.post.entity.constant;

public enum Sort {
    CREATED_AT_DESC,
    CREATED_AT_ASC;

    public org.springframework.data.domain.Sort toSpringSort() {
        return switch (this) {
            case CREATED_AT_DESC ->
                    org.springframework.data.domain.Sort.by("createdAt").descending();
            case CREATED_AT_ASC ->
                    org.springframework.data.domain.Sort.by("createdAt").ascending();
        };
    }
}
