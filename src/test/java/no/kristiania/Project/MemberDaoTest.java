package no.kristiania.Project;

import no.kristiania.http.HttpMessage;
import no.kristiania.controllers.MemberOptionsController;
import no.kristiania.controllers.UpdateProjectController;
import org.flywaydb.core.Flyway;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class MemberDaoTest {

    private MemberDao memberDao;
    private static Random random = new Random();
    private TaskDao taskDao;
    private Task defaultTask;

    @BeforeEach
    void setUp() throws SQLException {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        Flyway.configure().dataSource(dataSource).load().migrate();
        memberDao = new MemberDao(dataSource);
        taskDao = new TaskDao(dataSource);

        defaultTask = TaskDaoTest.exampleTask();
        taskDao.insert(defaultTask);
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
    @Test
    void shouldListInsertedMembers() throws SQLException, UnsupportedEncodingException {
        Member member1 = exampleMember();
        Member member2 = exampleMember();
        memberDao.insert(member1);
        memberDao.insert(member2);
        assertThat(memberDao.list())
                .extracting(Member::getFirstName)
                .contains(member1.getFirstName(), member2.getFirstName());
    }

    @Test
    void shouldqueryProjectsByTask() throws SQLException, UnsupportedEncodingException {
        Task task = TaskDaoTest.exampleTask();
        taskDao.insert(task);
        Task otherTask = TaskDaoTest.exampleTask();
        taskDao.insert(otherTask);

        Member matchingMember = exampleMember();
        matchingMember.setTaskId(task.getId());
        memberDao.insert(matchingMember);
        Member nonMatchingMember = exampleMember();
        nonMatchingMember.setTaskId(otherTask.getId());
        memberDao.insert(nonMatchingMember);

        assertThat(memberDao.queryProjectsByTaskId(task.getId()))
                .extracting(Member::getId)
                .contains(matchingMember.getId())
                .doesNotContain(nonMatchingMember.getId());
    }

    @Test
    void shouldReturnMembersAsOptions() throws UnsupportedEncodingException, SQLException {
        MemberOptionsController controller = new MemberOptionsController(memberDao);
        Member member = exampleMember();
        memberDao.insert(member);
        assertThat(controller.getBody())
                .contains("<option value=" + member.getId() + ">" + member.getFirstName() + "</option>");
    }

    @Test
    void shouldUpdateExistingProjectwithMember() throws IOException, SQLException{
        UpdateProjectController controller = new  UpdateProjectController(memberDao);

        Member member = exampleMember();
        memberDao.insert(member);

        Task task = TaskDaoTest.exampleTask();
        taskDao.insert(task);

        String body = "memberId=" + member.getId() + "&taskId=" + task.getId();

        HttpMessage response = controller.handle(new HttpMessage(body));
        assertThat(memberDao.retrieve(member.getId()).getTaskId())
                .isEqualTo(task.getId());
        assertThat(response.getStartLine())
                .isEqualTo("HTTP/1.1 302 Redirect");
        assertThat(response.getHeaders().get("Location"))
                .isEqualTo("http://localhost:8080/index.html");
    }


    public Member exampleMember() {
        Member member = new Member();
        member.setFirstName(exampleMemberName());
        member.setLastName("Richard");
        member.setEmail("Chris@gmail.com");
        member.setTaskId(defaultTask.getId());
        return member;
    }

    private static String exampleMemberName(){
        String[] options = {"Ole", "Hadron", "Chris", "Gabriel", "Jesus"};
        Random random = new Random();
        return options[random.nextInt(options.length)];
    }

}