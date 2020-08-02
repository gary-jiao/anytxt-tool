package com.example.anytxttool.manager;

import com.example.anytxttool.entity.IndexStat;
import com.example.anytxttool.objects.enums.EntityStat;
import com.example.devutils.utils.collection.MapUtils;
import com.example.devutils.utils.jdbc.JdbcUtils;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

/**
 * Created by AMe on 2020-08-02 01:06.
 */
@Component
public class IndexStatManager {

    @Autowired
    private JdbcUtils jdbcUtils;

    public IndexStat add(IndexStat indexStat) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int affectedRows = jdbcUtils.insert(
            IndexStat.TABLE,
            MapUtils.of(LinkedHashMap::new, "ext", indexStat.getExt(), "stat", indexStat.getStat(), "total", indexStat.getTotal(), "rule", indexStat.getRule()),
            keyHolder
        );
        Optional.ofNullable(keyHolder.getKey()).ifPresent(key -> {
            long autoId = key.longValue();
            indexStat.setRowId(autoId);
            indexStat.setId(autoId);
        });
        return indexStat;
    }

    public <C extends Collection<IndexStat>> C addAll(C indexStats) {
        indexStats.forEach(this::add);
        return indexStats;
    }

    public int deleteByRowId(long rowId, boolean trueDel) {
        if (trueDel) {
            String sql = String.format("delete from %s where rowid = %s", IndexStat.TABLE, rowId);
            return jdbcUtils.delete(sql);
        } else {
            String sql = String.format("update %s set stat = %s where rowid = %s", IndexStat.TABLE, EntityStat.DELETED.getCoordinate(), rowId);
            return jdbcUtils.update(sql);
        }
    }

    public int deleteById(long id, boolean trueDel) {
        if (trueDel) {
            String sql = String.format("delete from %s where id = %s", IndexStat.TABLE, id);
            return jdbcUtils.delete(sql);
        } else {
            String sql = String.format("update %s set stat = %s where id = %s", IndexStat.TABLE, EntityStat.DELETED.getCoordinate(), id);
            return jdbcUtils.update(sql);
        }
    }

    public int deleteByExt(String extName, boolean trueDel) {
        if (trueDel) {
            String sql = String.format("delete from %s where ext = %s", IndexStat.TABLE, Objects.requireNonNull(extName));
            return jdbcUtils.delete(sql);
        } else {
            String sql = String.format("update %s set stat = %s where ext = %s", IndexStat.TABLE, EntityStat.DELETED.getCoordinate(), Objects.requireNonNull(extName));
            return jdbcUtils.update(sql);
        }
    }

    public int deleteAllByStatIn(int[] stat, boolean trueDel) {
        String statInClause = Arrays.stream(stat).boxed().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
        if (trueDel) {
            String sql = String.format("delete from %s where stat in %s", IndexStat.TABLE, statInClause);
            return jdbcUtils.delete(sql);
        } else {
            String sql = String.format("update %s set stat = %s where stat in %s", IndexStat.TABLE, EntityStat.DELETED.getCoordinate(), statInClause);
            return jdbcUtils.update(sql);
        }
    }

    public int deleteAll(boolean trueDel) {
        if (trueDel) {
            String sql = String.format("delete from %s", IndexStat.TABLE);
            return jdbcUtils.delete(sql);
        } else {
            String sql = String.format("update %s set stat = %s", IndexStat.TABLE, EntityStat.DELETED.getCoordinate());
            return jdbcUtils.update(sql);
        }
    }

    public void truncate() {
        String sql = String.format("truncate %s", IndexStat.TABLE);
        jdbcUtils.execute(sql);
    }

    public int updateByRowId(IndexStat indexStat) {
        String preparedSql = String.format("update %s set id = ?, ext = ?, stat = ?, total = ?, rule = ? where rowid = %s", IndexStat.TABLE, Objects.requireNonNull(indexStat.getRowId()));
        Object[] args = {indexStat.getId(), indexStat.getExt(), indexStat.getStat(), indexStat.getTotal(), indexStat.getRule()};
        int[] argTypes = {Types.BIGINT, Types.VARCHAR, Types.INTEGER, Types.INTEGER, Types.VARCHAR};
        return jdbcUtils.update(preparedSql, args, argTypes);
    }

    public int updateSelectiveByRowId(IndexStat indexStat) {
        LinkedHashMap<String, Object> setFields = MapUtils.of(LinkedHashMap::new, "id", indexStat.getId(), "ext", indexStat.getExt(), "stat", indexStat.getStat(), "total", indexStat.getTotal(), "rule", indexStat.getRule());
        List<Object> argList = new ArrayList<>();
        String setClause = setFields.entrySet().stream().filter(entry -> entry.getValue() != null).map(entry -> {
                argList.add(entry.getValue());
                return entry.getKey() + " = ?";
            }).collect(Collectors.joining(","));
        String preparedSql = String.format("update %s set %s where rowid = %s", IndexStat.TABLE, setClause, Objects.requireNonNull(indexStat.getRowId()));
        return jdbcUtils.jdbcTemplate().update(preparedSql, argList.toArray());
    }

    public Optional<IndexStat> getByRowId(long rowId) {
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s where rowid = %s", IndexStat.TABLE, rowId);
        return jdbcUtils.selectRow(sql, IndexStat.class);
    }

    public Optional<IndexStat> getById(long id) {
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s where id = %s",IndexStat.TABLE, id);
        return jdbcUtils.selectRow(sql, IndexStat.class);
    }

    public Optional<IndexStat> getByExt(String extName) {
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s where ext = %s", IndexStat.TABLE, Objects.requireNonNull(extName));
        return jdbcUtils.selectRow(sql, IndexStat.class);
    }

    public List<IndexStat> getAllByStatIn(int... stat) {
        String statInClause = Arrays.stream(stat).boxed().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s where stat in %s", IndexStat.TABLE, statInClause);
        return jdbcUtils.selectRows(sql, IndexStat.class);
    }

    public List<IndexStat> getAllByStatNotIn(int... stat) {
        String statInClause = Arrays.stream(stat).boxed().map(String::valueOf).collect(Collectors.joining(",", "(", ")"));
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s where stat not in %s", IndexStat.TABLE, statInClause);
        return jdbcUtils.selectRows(sql, IndexStat.class);
    }

    public List<IndexStat> getAll() {
        String sql = String.format("select rowid, id, ext, stat, total, rule from %s", IndexStat.TABLE);
        return jdbcUtils.selectRows(sql, IndexStat.class);
    }

}
