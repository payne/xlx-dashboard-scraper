package org.mattpayne.scraper.xlx_dashboard.schedule;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mattpayne.scraper.xlx_dashboard.config.BaseIT;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;


public class ScheduleResourceTest extends BaseIT {

    @Test
    @Sql("/data/scheduleData.sql")
    void getAllSchedules_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/schedules")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(2))
                    .body("content.get(0).id", Matchers.equalTo(1000));
    }

    @Test
    @Sql("/data/scheduleData.sql")
    void getAllSchedules_filtered() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/schedules?filter=1001")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("page.totalElements", Matchers.equalTo(1))
                    .body("content.get(0).id", Matchers.equalTo(1001));
    }

    @Test
    @Sql("/data/scheduleData.sql")
    void getSchedule_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/schedules/1000")
                .then()
                    .statusCode(HttpStatus.OK.value())
                    .body("url", Matchers.equalTo("Nec ullamcorper."));
    }

    @Test
    void getSchedule_notFound() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .get("/api/schedules/1666")
                .then()
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .body("code", Matchers.equalTo("NOT_FOUND"));
    }

    @Test
    void createSchedule_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/scheduleDTORequest.json"))
                .when()
                    .post("/api/schedules")
                .then()
                    .statusCode(HttpStatus.CREATED.value());
        assertEquals(1, scheduleRepository.count());
    }

    @Test
    void createSchedule_missingField() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/scheduleDTORequest_missingField.json"))
                .when()
                    .post("/api/schedules")
                .then()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .body("code", Matchers.equalTo("VALIDATION_FAILED"))
                    .body("fieldErrors.get(0).property", Matchers.equalTo("url"))
                    .body("fieldErrors.get(0).code", Matchers.equalTo("REQUIRED_NOT_NULL"));
    }

    @Test
    @Sql("/data/scheduleData.sql")
    void updateSchedule_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(readResource("/requests/scheduleDTORequest.json"))
                .when()
                    .put("/api/schedules/1000")
                .then()
                    .statusCode(HttpStatus.OK.value());
        assertEquals("Commodo consequat.", scheduleRepository.findById(((long)1000)).orElseThrow().getUrl());
        assertEquals(2, scheduleRepository.count());
    }

    @Test
    @Sql("/data/scheduleData.sql")
    void deleteSchedule_success() {
        RestAssured
                .given()
                    .accept(ContentType.JSON)
                .when()
                    .delete("/api/schedules/1000")
                .then()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        assertEquals(1, scheduleRepository.count());
    }

}
