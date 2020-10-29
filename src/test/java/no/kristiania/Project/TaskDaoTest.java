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
    @BeforeEach
    void setUp() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        taskDao = new TaskDao(dataSource);
    }

    @Test
    void shouldListInsertedMembers() throws SQLException, UnsupportedEncodingException {
        TaskDao task1 = exampleTask();
        TaskDao task2 = exampleTask();
        taskDao.insert(task1);
        taskDao.insert(task2);
        assertThat(taskdDao())
                .extracting(Member::getFirstName)
                .contains(member1.getFirstName(), member2.getFirstName());
    }

    @Test
    void shouldRetrieveAllMemberProperties() throws SQLException, UnsupportedEncodingException {
        memberDao.insert(exampleMember());
        memberDao.insert(exampleMember());
        Member member = exampleMember();
        memberDao.insert(member);
        assertThat(member).hasNoNullFieldsOrProperties();
        assertThat(memberDao.retrieve(member.getId()))
                .usingRecursiveComparison()
                .isEqualTo(member);
    }
    private Member exampleMember() throws UnsupportedEncodingException {
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

