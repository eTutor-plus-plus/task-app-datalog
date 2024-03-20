package at.jku.dke.task_app.datalog.controllers;

import at.jku.dke.etutor.task_app.auth.AuthConstants;
import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.datalog.ClientSetupExtension;
import at.jku.dke.task_app.datalog.DatabaseSetupExtension;
import at.jku.dke.task_app.datalog.data.entities.DatalogTaskGroup;
import at.jku.dke.task_app.datalog.data.repositories.DatalogTaskGroupRepository;
import at.jku.dke.task_app.datalog.dto.ModifyDatalogTaskGroupDto;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({DatabaseSetupExtension.class, ClientSetupExtension.class})
class TaskGroupControllerTest {

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

    //#region --- GET ---
    @Test
    void getShouldReturnOk() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("diagnoseFacts", equalTo("person(mike)."))
            .body("submissionFacts", equalTo("person(steve)."));
    }

    @Test
    void getShouldReturnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/taskGroup/{id}", this.taskGroupId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

    @Test
    void getShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .accept(ContentType.JSON)
            // WHEN
            .when()
            .get("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- CREATE ---
    @Test
    void createShouldReturnCreated() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve).")))
            // WHEN
            .when()
            .post("/api/taskGroup/{id}", this.taskGroupId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .header("Location", containsString("/api/taskGroup/" + (this.taskGroupId + 2)))
            .body("descriptionDe", containsString("person(X)"))
            .body("descriptionEn", containsString("person(X)"));
    }

    @Test
    void createShouldReturnBadRequestOnInvalidBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve).")))
            // WHEN
            .when()
            .post("/api/taskGroup/{id}", this.taskGroupId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void createShouldReturnBadRequestOnEmptyBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            // WHEN
            .when()
            .post("/api/taskGroup/{id}", this.taskGroupId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void createShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(mike).", "person(steve).")))
            // WHEN
            .when()
            .post("/api/taskGroup/{id}", this.taskGroupId + 2)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- UPDATE ---
    @Test
    void updateShouldReturnOk() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(steve).", "person(mike).")))
            // WHEN
            .when()
            .put("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("descriptionDe", containsString("person(X)"))
            .body("descriptionEn", containsString("person(X)"));
    }

    @Test
    void updateShouldReturnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(steve).", "person(mike).")))
            // WHEN
            .when()
            .put("/api/taskGroup/{id}", this.taskGroupId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(404);
    }

    @Test
    void updateShouldReturnBadRequestOnInvalidBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("sql", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(steve).", "person(mike).")))
            // WHEN
            .when()
            .put("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void updateShouldReturnBadRequestOnEmptyBody() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            .contentType(ContentType.JSON)
            // WHEN
            .when()
            .put("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(400);
    }

    @Test
    void updateShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            .contentType(ContentType.JSON)
            .body(new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyDatalogTaskGroupDto("person(steve).", "person(mike).")))
            // WHEN
            .when()
            .put("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    //#region --- DELETE ---
    @Test
    void deleteShouldReturnNoContent() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            // WHEN
            .when()
            .delete("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(204);
    }

    @Test
    void deleteShouldReturnNoContentOnNotFound() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.CRUD_API_KEY)
            // WHEN
            .when()
            .delete("/api/taskGroup/{id}", this.taskGroupId + 1)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(204);
    }

    @Test
    void deleteShouldReturnForbidden() {
        given()
            .port(port)
            .header(AuthConstants.AUTH_TOKEN_HEADER_NAME, ClientSetupExtension.SUBMIT_API_KEY)
            // WHEN
            .when()
            .delete("/api/taskGroup/{id}", this.taskGroupId)
            // THEN
            .then()
            .log().ifValidationFails()
            .statusCode(403);
    }
    //#endregion

    @Test
    void mapToDto() {
        // Arrange
        var taskGroup = new DatalogTaskGroup("person(mike).", "person(steve).");

        // Act
        var result = new TaskGroupController(null).mapToDto(taskGroup);

        // Assert
        assertEquals(taskGroup.getDiagnoseFacts(), result.diagnoseFacts());
        assertEquals(taskGroup.getSubmissionFacts(), result.submissionFacts());
    }
}
