package no.kristiania.Project;

import no.kristiania.controllers.ProjectTaskOptionsController;
import no.kristiania.controllers.UpdateProjectController;
import no.kristiania.controllers.UpdateTaskStatusController;
import no.kristiania.http.HttpMessage;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {

    private TaskDao taskDao;
    private static Random random = new Random();
    private StatusDao statusDao;
    private Status defaultStatus;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
        statusDao = new StatusDao(dataSource);

        defaultStatus = StatusDaoTest.exampleStatus();
        statusDao.insert(defaultStatus);
    }

    @Test
    void shouldListAllStatus() throws SQLException {
        Task project1 = exampleTask();
        Task project2 = exampleTask();
        taskDao.insert(project1);
        taskDao.insert(project2);
        assertThat(taskDao.list())
                .extracting(Task::getName)
                .contains(project1.getName(), project2.getName());
    }
    @Test
    void shouldRetrieveAllProjectProperties() throws SQLException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        Task task = exampleTask();
        taskDao.insert(task);
        //assertThat(task).hasNoNullFieldsOrProperties();
        assertThat(taskDao.retrieve(task.getId()))
                .usingRecursiveComparison()
                .isEqualTo(task);
    }
    @Test
    void shouldqueryTaskByStatus() throws SQLException{
        Status status = StatusDaoTest.exampleStatus();
        statusDao.insert(status);
        Status otherStatus = StatusDaoTest.exampleStatus();
        statusDao.insert(otherStatus);

        Task matchingTask = exampleTask();
        matchingTask.setStatusId(status.getId());
        taskDao.insert(matchingTask);
        Task nonMatchingTask = exampleTask();
        nonMatchingTask.setStatusId(otherStatus.getId());
        taskDao.insert(nonMatchingTask);

        assertThat(taskDao.queryTaskByStatusId(status.getId()))
                .extracting(Task::getId)
                .contains(matchingTask.getId())
                .doesNotContain(nonMatchingTask.getId());
    }


    @Test
    void shouldReturnTasksAsOptions() throws SQLException {
        ProjectTaskOptionsController controller = new ProjectTaskOptionsController(taskDao);
        Task task = exampleTask();
        taskDao.insert(task);
        assertThat(controller.getBody())
                .contains("<option value=" + task.getId() + ">" + task.getName() +  "</option>");
    }
    @Test
    void shouldUpdateExistingtTaskwithStatus() throws IOException, SQLException{
        UpdateTaskStatusController controller = new UpdateTaskStatusController(taskDao);

        Task task = exampleTask();
        taskDao.insert(task);

        Status status = StatusDaoTest.exampleStatus();
        statusDao.insert(status);

        String body = "taskId=" + task.getId() + "&statusId=" + status.getId();

        HttpMessage response = controller.handle(new HttpMessage(body));
        assertThat(taskDao.retrieve(task.getId()).getStatusId())
                .isEqualTo(status.getId());
        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeaders().get("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }

    public static Task exampleTask() {
        Task task = new Task();
        task.setName(exampleProjectName());
        return task;
    }

    private static String exampleProjectName() {
        String[] options = {"HTML", "CSS", "Javascript", "JAVA"};
        return options[random.nextInt(options.length)];
    }

}

