package no.kristiania.Project;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public Object retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM task WHERE id = ?");
    }

    public List<Task> list() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM task")) {
                try(ResultSet rs = statement.executeQuery()) {
                    List<Task> tasks = new ArrayList<>();
                    while(rs.next()) {
                        tasks.add(mapRow(rs));
                    }
                    return tasks;
                }
            }
        }
    }

    @Override
    protected Task mapRow(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setId(rs.getInt("id"));
        task.setName(rs.getString("name"));
        task.setStatus(rs.getString("status"));
        return task;
    }
}
