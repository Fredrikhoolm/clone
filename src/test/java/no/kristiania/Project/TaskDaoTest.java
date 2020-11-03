package no.kristiania.Project;

import no.kristiania.controllers.ProjectTaskOptionsController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class TaskDaoTest {

    private TaskDao taskDao;
    private static Random random = new Random();

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldListAllCategories() throws SQLException {
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
        assertThat(task).hasNoNullFieldsOrProperties();
        assertThat(taskDao.retrieve(task.getId()))
                .usingRecursiveComparison()
                .isEqualTo(task);
    }
    @Test
    void shouldReturnTasksAsOptions() throws SQLException {
        ProjectTaskOptionsController controller = new ProjectTaskOptionsController(taskDao);
        Task task = exampleTask();
        taskDao.insert(task);
        assertThat(controller.getBody())
                .contains("<option value=" + task.getId() + ">" + task.getName() + "</option");
    }

    //public static task --?

    public static Task exampleTask() {
        Task task = new Task();
        task.setName(exampleProjectName());
        task.setStatus(exampleStatus());
        return task;
    }

    private static String exampleProjectName() {
        String[] options = {"HTML", "CSS", "Javascript", "JAVA"};
        return options[random.nextInt(options.length)];
    }

    private static String exampleStatus() {
        String[] option = {"DONE", "ALMOST DONE", "NOT STARTED"};
        return option[random.nextInt(option.length)];
    }
}

