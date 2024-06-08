# eTutor Task-App: Datalog / ASP

This application provides a REST-interface for following task types: datalog, asp.

## Development

In development environment, the API documentation is available at http://localhost:8081/docs.

See [CONTRIBUTING.md](CONTRIBUTING.md) and the [Wiki](https://github.com/eTutor-plus-plus/task-app-datalog/wiki) for details.

## Docker

Start a new instance of the application using Docker:

```bash
docker run -p 8090:8081 \ 
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://postgres:5432/etutor_datalog" \
  -e SPRING_DATASOURCE_USERNAME=etutor_datalog \
  -e SPRING_DATASOURCE_PASSWORD=myPwd \
  -e SPRING_FLYWAY_USER=etutor_datalog_admin \
  -e SPRING_FLYWAY_PASSWORD=adPwd \
  -e CLIENTS_API_KEYS_0_NAME=task-administration \
  -e CLIENTS_API_KEYS_0_KEY=4fsda6f465sad4f6sfd \
  -e CLIENTS_API_KEYS_0_ROLES_0=CRUD \
  -e CLIENTS_API_KEYS_0_ROLES_1=SUBMIT \
  -e CLIENTS_API_KEYS_1_NAME=moodle \
  -e CLIENTS_API_KEYS_1_KEY=as89df47s98ad7f98s7d \
  -e CLIENTS_API_KEYS_1_ROLES_0=SUBMIT \
  -e CLIENTS_API_KEYS_2_NAME=plagiarism-checker \
  -e CLIENTS_API_KEYS_2_KEY=adf455jfil45646 \
  -e CLIENTS_API_KEYS_2_ROLES_0=READ_SUBMISSION \
  -e DATALOG_MAX_EXECUTION_TIME=10 \
  -e DATALOG_FACT_ENCODING_SUFFIX=0 \
  -e DATALOG_DOC_URL=https://etutor.dke.uni-linz.ac.at/api/forwardPublic/datalog/dlg/
  etutorplusplus/task-app-datalog
```

or with Docker Compose:

```yaml
version: '3.8'

services:
    task-app-datalog:
        image: etutorplusplus/task-app-datalog
        restart: unless-stopped
        ports:
            -   target: 8081
                published: 8090
        environment:
            SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/etutor_datalog
            SPRING_DATASOURCE_USERNAME: etutor_datalog
            SPRING_DATASOURCE_PASSWORD: myPwd
            SPRING_FLYWAY_USER: etutor_datalog_admin
            SPRING_FLYWAY_PASSWORD: adPwd
            CLIENTS_API_KEYS_0_NAME: task-administration
            CLIENTS_API_KEYS_0_KEY: 4fsda6f465sad4f6sfd
            CLIENTS_API_KEYS_0_ROLES_0: CRUD
            CLIENTS_API_KEYS_0_ROLES_1: SUBMIT
            CLIENTS_API_KEYS_1_NAME: moodle
            CLIENTS_API_KEYS_1_KEY: as89df47s98ad7f98s7d
            CLIENTS_API_KEYS_1_ROLES_0: SUBMIT
            CLIENTS_API_KEYS_2_NAME: plagiarism-checker
            CLIENTS_API_KEYS_2_KEY: adf455jfil45646
            CLIENTS_API_KEYS_2_ROLES_0: READ_SUBMISSION
            DATALOG_MAX_EXECUTION_TIME: 10
            DATALOG_FACT_ENCODING_SUFFIX: 0
            DATALOG_DOC_URL: https://etutor.dke.uni-linz.ac.at/api/forwardPublic/datalog/dlg/
```

### Environment Variables

In production environment, the application requires two database users:

* A database administrator user which has the permission to create the tables.
* A JPA user which has read/write access (`SELECT, INSERT, UPDATE, DELETE, TRUNCATE`) to the database tables.

> In development environment, one user will be used for both.

The users must be configured via environment variables. The clients have to be configured via environment variables as well (`X`/`Y` stands for a 0-based index).

| Variable                       | Description                                                                                                                      |
|--------------------------------|----------------------------------------------------------------------------------------------------------------------------------|
| `SERVER_PORT`                  | The server port.                                                                                                                 |
| `SPRING_DATASOURCE_URL`        | JDBC-URL to the database                                                                                                         |
| `SPRING_DATASOURCE_USERNAME`   | The username of the JPA user.                                                                                                    |
| `SPRING_DATASOURCE_PASSWORD`   | The password of the JPA user.                                                                                                    |
| `SPRING_FLYWAY_USER`           | The username of the database administrator user.                                                                                 |
| `SPRING_FLYWAY_PASSWORD`       | The password of the database administrator user.                                                                                 |
| `CLIENTS_API_KEYS_X_NAME`      | The name of the client.                                                                                                          |
| `CLIENTS_API_KEYS_X_KEY`       | The API key of the client.                                                                                                       |
| `CLIENTS_API_KEYS_X_ROLES_Y`   | The role of the client.                                                                                                          |
| `DATALOG_EXE`                  | The path to the DLV exe (if different than default).                                                                             |
| `DATALOG_MAX_EXECUTION_TIME`   | The maximum allowed execution time of the DLV process.                                                                           |
| `DATALOG_FACT_ENCODING_SUFFIX` | The suffix to append to facts (may only contain digits).                                                                         |
| `DATALOG_DOC_URL`              | The public URL where the datalog facts can be viewed. The URL must end with a slash. The ID will be appended by the application. |
