package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import static ru.javawebinar.topjava.util.ValidationUtil.beanEntityValidate;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    private final ResultSetExtractor<List<User>> resultSetExtractor = rs -> {
        Map<Integer, User> map = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            User user = map.get(id);
            if (user == null) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRegistered(rs.getDate("registered"));
                user.setEnabled(rs.getBoolean("enabled"));
                user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                if (rs.getString("role") != null) {
                    user.setRoles(Set.of(Role.valueOf(rs.getString("role"))));
                } else {
                    user.setRoles(Set.of());
                }
                map.put(id, user);
            } else {
                user.getRoles().add(Role.valueOf(rs.getString("role")));
            }
        }
        return new ArrayList<>(map.values());
    };

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        beanEntityValidate(user);
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password, 
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            }
            jdbcTemplate.update("DELETE FROM user_role WHERE user_id=?", user.getId());
        }
        saveRoles(user);
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ur on users.id = ur.user_id WHERE id=?", resultSetExtractor, id);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ur on users.id = ur.user_id WHERE email=?", resultSetExtractor, email);
        return DataAccessUtils.singleResult(users);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM users LEFT OUTER JOIN user_role ur on users.id = ur.user_id ORDER BY name, email", resultSetExtractor);
    }

    private void saveRoles(User user) {
        List<Role> roles = user.getRoles().stream().toList();
        jdbcTemplate.batchUpdate(
                "insert into user_role (role, user_id) values(?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, roles.get(i).toString());
                        ps.setInt(2, user.getId());
                    }

                    public int getBatchSize() {
                        return user.getRoles().size();
                    }
                });
    }
}
