package no.kristiania.Project;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatusDao  extends AbstractDao<Status>{
    public StatusDao(DataSource dataSource) {
        super(dataSource);
    }
    //TODO: prøve å abstrahere insert og list metodene hvis det går?
    public void insert(Status status) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO status (name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, status.getName());
                statement.executeUpdate();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    status.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public Status retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM status WHERE id = ?");
    }

    public List<Status> list() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM status")) {
                try(ResultSet rs = statement.executeQuery()) {
                    List<Status> statu = new ArrayList<>();
                    while(rs.next()) {
                        statu.add(mapRow(rs));
                    }
                    return statu;
                }
            }
        }
    }
    @Override
    protected Status mapRow(ResultSet rs) throws SQLException {
        Status status = new Status();
        status.setId(rs.getInt("id"));
        status.setName(rs.getString("name"));
        return status;
    }
}
