package vesnell.pl.quiz.database.controller.base;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

import java.sql.SQLException;
import java.util.List;

import vesnell.pl.quiz.database.DBHelper;

/**
 * Created by alek6 on 20.04.2016.
 */
public abstract class BaseController<M> {
    protected final RuntimeExceptionDao<M, String> dao;
    protected final Context context;

    protected BaseController(Context context, Class clazz) {
        this.dao = OpenHelperManager.getHelper(context, DBHelper.class).getRuntimeExceptionDao(clazz);
        this.context = context;
    }

    protected List<M> listAll() {
        return dao.queryForAll();
    }

    protected boolean create(M item) {
        if (item == null) {
            return false;
        }
        return dao.create(item) == 1;
    }

    protected boolean update(M item) {
        if (item == null) {
            return false;
        }
        return dao.update(item) == 1;
    }

    protected M get(String id) {
        return dao.queryForId(id);
    }

    protected boolean remove(M item) {
        return dao.delete(item) == 1;
    }

    protected M getFirst(String field, Object parentId) throws SQLException {
        QueryBuilder<M, String> queryBuilder = dao.queryBuilder();
        SelectArg arg = new SelectArg();
        queryBuilder.where().eq(field, arg);
        PreparedQuery<M> preparedQuery = queryBuilder.prepare();
        arg.setValue(parentId);
        return dao.queryForFirst(preparedQuery);
    }
}
