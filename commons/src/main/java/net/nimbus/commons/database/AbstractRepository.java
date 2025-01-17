package net.nimbus.commons.database;

import com.google.common.util.concurrent.ListenableFuture;
import net.nimbus.commons.Commons;
import net.nimbus.commons.cache.Cache;
import net.nimbus.commons.database.query.Result;
import net.nimbus.commons.database.query.Row;
import net.nimbus.commons.entities.Entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<K, T extends Entity<K>> extends AbstractCacheManager<K, T> {

    public AbstractRepository() {
        super(new Cache<>());
    }

    /**
     * @return should return the identifier (often used for the generated primary key)
     */
    protected abstract Long insertInternal(T object);

    public abstract void update(T object);

    public abstract T buildEntity(Row row);

    public ListenableFuture<Optional<T>> queryAsynchronously(String query, Object... parameters) {
        return Commons.getInstance().getExecutorService().submit(() -> query(query, parameters));
    }

    public ListenableFuture<List<T>> queryListAsynchronously(String query, Object... parameters) {
        return Commons.getInstance().getExecutorService().submit(() -> queryList(query, parameters));
    }

    public Optional<T> query(String query, Object... parameters) {
        Result result = sqlQuery(query, parameters);

        Optional<T> optional = Optional.ofNullable(buildEntity(result.getFirstRow()));
        optional.ifPresent(update -> updateCache(optional.get().getId(), optional.get()));

        return optional;
    }

    public List<T> queryList(String query, Object... parameters) {
        List<T> entities = new ArrayList<>();
        Result result = sqlQuery(query, parameters);

        for (Row row : result.getRows()) {
            T entity = buildEntity(row);
            entities.add(entity);
            updateCache(entity.getId(), entity);
        }

        return entities;
    }

    /**
     * @return Result: rows of data
     */
    public Result sqlQuery(String query, Object... parameters) {
        try (PreparedStatement preparedStatement = Commons.getInstance().getConnection().prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

            return new Result(preparedStatement.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private PreparedStatement buildPreparedStatement(String sql, Object... parameters) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Commons.getInstance().getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (int i = 0; i < parameters.length; i++) {
                preparedStatement.setObject(i + 1, parameters[i]);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return preparedStatement;
    }

    /**
     * @return generated ID (primary key) of the entity
     */
    public Long sqlWrite(String sql, Object... parameters) {
        Long generatedKey = -1L;
        try {
            PreparedStatement statement = buildPreparedStatement(sql, parameters);
            statement.execute();

            Result result = new Result(statement.getGeneratedKeys());
            if (!result.isEmpty()) {
                generatedKey = result.getFirstRow().getLong("GENERATED_KEY");
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedKey;
    }

    public void createNew(K identifier, T object) {
        if (object.getId() instanceof Long) return;

        insertInternal(object);
        updateCache(object.getId(), object);
        System.out.println("successfully created and cached: id: " + identifier + " object: " + object.toString());
    }

    /**
     * @param object Should only be used with entities of identification type Long (LongIdentifierEntity)
     */
    public void createNew(T object) {
        if(!(object.getId() instanceof Long)) return;
        
        Commons.getInstance().getExecutorService().execute(() -> {
            Long key = insertInternal(object);
            object.setId((K) key);
            updateCache((K) key, object);
            System.out.println("successfully created and cached: id: " + key + " object: " + object);
        });
    }

    public void persistAll() {
        for (T entity : getCache().getAll()) {
            update(entity);
        }
    }
    
}
