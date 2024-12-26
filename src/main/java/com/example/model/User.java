
package com.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String number;
    private String country;
    private String callSign;
    private String suffix;
    private boolean dprsEnabled;
    private String viaPeer;
    private String lastHeard;
    private String listeningOn;
    private boolean isTransmitting;
}
