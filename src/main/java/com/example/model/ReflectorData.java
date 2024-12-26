
package com.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReflectorData {
    private String version;
    private String dashboardVersion;
    private String uptime;
    private List<User> users;
    private List<Module> modules;
}

