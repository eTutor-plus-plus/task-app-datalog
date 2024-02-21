package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.ClientSetupExtension;
import at.jku.dke.task_app.datalog.DatabaseSetupExtension;
import at.jku.dke.task_app.datalog.config.SecurityConfig;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.services.HashIds;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({DatabaseSetupExtension.class, ClientSetupExtension.class})
@Import(SecurityConfig.class)
class DatalogControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private DatalogTaskGroupRepository repository;

    private long taskGroupId;

    @BeforeEach
    void initDb() {
        this.repository.deleteAll();
        this.taskGroupId = this.repository.save(new DatalogTaskGroup(1L, TaskStatus.APPROVED, "person(mike).", "person(steve).")).getId();
    }

    @Test
    void getFacts_exists() {
        given()
            .port(port)
            // WHEN
            .when()
            .get("/dlg/{id}", HashIds.encode(this.taskGroupId))
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.TEXT)
            .body(equalTo("person(mike)."));
    }

    @Test
    void getFacts_notExists() {
        given()
            .port(port)
            // WHEN
            .when()
            .get("/dlg/{id}", HashIds.encode(this.taskGroupId + 1))
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

}
