package org.tally.batchtutorial.job.file.dto;

import lombok.Data;

@Data
public class Player{

    private String id;
    private String lastName;
    private String firstName;
    private String position;
    private int birthYear;
    private int debutYear;

}
