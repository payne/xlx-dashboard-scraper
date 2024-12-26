package org.mattpayne.scraper.xlx_dashboard.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ScheduleDTO {

    private Long id;

    @NotNull
    @Size(max = 255)
    @ScheduleUrlUnique
    private String url;

    @Size(max = 255)
    private String cron;

}
