package no.kristiania.Project;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDao extends AbstractDao<Member> {

    public MemberDao(DataSource dataSource) {

        super(dataSource);
    }
    //TODO: prøve å abstrahere insert og list metodene hvis det går?

    public void insert(Member member) throws SQLException, UnsupportedEncodingException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO project (member_firstname, member_lastname, email) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )){
                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3,URLDecoder.decode(String.valueOf(member.getEmail()), StandardCharsets.UTF_8.toString()));
                statement.executeUpdate();

                //member.getEmail()
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    member.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }
    public void update(Member member) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE members SET taskId = ? WHERE id = ?")) {
                statement.setInt(1, member.getTaskId());
                statement.setInt(2, member.getId());
                statement.executeUpdate();
            }
        }
    }

    public Member retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM project WHERE id = ?");
    }

    public List<Member> list() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM project")) {
                try(ResultSet rs = statement.executeQuery()) {
                    List<Member> members = new ArrayList<>();
                    while(rs.next()) {
                        members.add(mapRow(rs));
                    }
                    return members;
                }
            }
        }
    }


    @Override
    protected Member mapRow(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setTaskId(rs.getInt("task_id"));
        member.setFirstName(rs.getString("member_firstname"));
        member.setLastName(rs.getString("member_lastname"));
        member.setEmail(rs.getString("email"));
        return member;
    }

}