package no.kristiania.Project;

import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class taskDaoTest {
    private TaskDao taskDao;

    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldListInsertedProjects() throws SQLException {
        Task task1 = exampleTask();
        Task task2 = exampleTask();
        taskDao.insert(task1);
        taskDao.insert(task2);
        assertThat(taskDao.list())
                .extracting(Task::getName)
                .contains(task1.getName(), task2.getName());
    }


    @Test
    void shouldRetrieveAllTaskProperties() throws SQLException, UnsupportedEncodingException {
        taskDao.insert(exampleTask());
        taskDao.insert(exampleTask());
        Task projectTask = exampleTask();
        taskDao.insert(projectTask);
        assertThat(taskDao.retrieve(projectTask.getId()))
                .usingRecursiveComparison()
                .isEqualTo(projectTask);
    }
    private Task exampleTask() {
        return null;
    }

    private Member exampleMem() throws UnsupportedEncodingException {
        Member member = new Member();
        member.setFirstName(exampleMemberName());
        member.setLastName("Richard");
        member.setEmail("Chris@gmail.com");
        return member;
    }
    private String exampleMemberName(){
        String[] options = {"Ole", "Hadron", "Chris", "Gabriel", "Jesus"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }


}

