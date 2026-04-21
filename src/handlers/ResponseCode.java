package handlers;

public enum ResponseCode {
    NOT_FOUND(404),
    HAS_OVERLAP(406),
    OK(200),
    CREATED(201),
    INTERNAL_ERROR(500);

    private final int value;

    ResponseCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
