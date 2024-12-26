
package com.example.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Module {
    private String name;
    private String label;
    private List<String> connections;
    private String dgId;
    private List<String> nodes;
}

