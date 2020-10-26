package no.kristiania.Project;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MemberDao {

    private DataSource dataSource;

    public MemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insert(Member member) throws SQLException, UnsupportedEncodingException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO members (member_name, lastname, email) values (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )){
                statement.setString(1, member.getFirstName());
                statement.setString(2, member.getLastName());
                statement.setString(3,URLDecoder.decode(String.valueOf(member.getEmail()), StandardCharsets.UTF_8.toString()));
                statement.executeUpdate();

                //member.getEmail()
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    member.setId(generatedKeys.getLong("id"));
                }catch (UnsupportedOperationException e){
                    e.printStackTrace();
                }

            }
        }
    }
    public Member retrieve(long id) throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM members WHERE id = ?")) {
                statement.setDouble(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return mapToMembers(rs);
                    }else {
                        throw new EntityNotFoundException("Could not find Member with id " + id);
                    }

                }
            }
        }
    }

    private Member mapToMembers(ResultSet rs) throws SQLException, UnsupportedEncodingException {
        Member member = new Member();
        member.setId(rs.getLong("id"));
        member.setFirstName(rs.getString("member_name"));
        member.setLastName(rs.getString("lastname"));
        member.setEmail(rs.getString("email"));
        return member;
    }

    public List<Member> list() throws SQLException, UnsupportedEncodingException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM members")) {
                try(ResultSet rs = statement.executeQuery()) {
                    List<Member> members = new ArrayList<>();
                    while(rs.next()) {
                        members.add(mapToMembers(rs));
                    }
                    return members;
                }
            }
        }
    }


    public static void main(String[] args) throws SQLException, UnsupportedEncodingException {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/members");
        dataSource.setUser("projectadmin");

        //passordet er lett men dette er kun for testing
        dataSource.setPassword(",qe3(HEme4Y-uy)#");

        MemberDao memberDao = new MemberDao(dataSource);
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter members firs name");

        Member newMember = new Member();
        newMember.setFirstName(scanner.nextLine());
        newMember.setLastName(scanner.nextLine());
        newMember.setEmail(scanner.nextLine());
    }

}