package no.kristiania.Project;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao extends AbstractDao<Task> {

    public TaskDao(DataSource dataSource) {

        super(dataSource);
    }
    //TODO: prøve å abstrahere insert og list metodene hvis det går?
    public void insert(Task task) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO tasks (name, status) values (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                statement.setString(1, task.getName());
                statement.setString(2, task.getStatus());
                statement.executeUpdate();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    generatedKeys.next();
                    task.setId(generatedKeys.getInt("id"));
                }
            }
        }
    }

    public void update(Task task) throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement(
                    "UPDATE tasks SET status = ? WHERE id = ?")) {
                statement.setString(1, task.getStatus());
                statement.setInt(2, task.getId());
                statement.executeUpdate();
            }
        }
    }

    public Task retrieve(Integer id) throws SQLException {
        return retrieve(id, "SELECT * FROM tasks WHERE id = ?");
    }

    public List<Task> list() throws SQLException {
        try(Connection connection = dataSource.getConnection()) {
            try(PreparedStatement statement = connection.prepareStatement("SELECT * FROM tasks")) {
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
