package dtos;

public class BoredomDTO {
    private String activity;
    private String social;
    private String participants;
    private String type;

    public BoredomDTO(String activity, String social, String participants, String type) {
        this.activity = activity;
        this.social = social;
        this.participants = participants;
        this.type = type;
    }
}
