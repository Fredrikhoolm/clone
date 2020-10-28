package no.kristiania.Project;

import javax.sql.DataSource;
import java.sql.*;

public class TaskDao extends AbstractDao {

    public TaskDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Task project) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO project (name) values (?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, project.getName());
                statement.setString(2, project.getStatus());
                statement.executeUpdate();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    project.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        return null;
    }
}
