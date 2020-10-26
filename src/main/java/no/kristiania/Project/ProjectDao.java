package no.kristiania.Project;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectDao extends AbstractDao {

    public ProjectDao(DataSource dataSource) {
        super(dataSource);
    }

    public void insert(Project project) throws SQLException {

    }

    @Override
    protected Project mapRow(ResultSet rs) throws SQLException {
        return null;
    }
}
