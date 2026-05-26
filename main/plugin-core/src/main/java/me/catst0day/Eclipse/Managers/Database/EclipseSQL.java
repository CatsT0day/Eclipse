package me.catst0day.Eclipse.Managers.Database;

import org.bukkit.Location;
import java.util.List;

public interface EclipseSQL {

    boolean saveLocation(String name, Location loc) throws EclipseSQLException;

    Location loadLocation(String name) throws EclipseSQLException;

    boolean delete(String name) throws EclipseSQLException;

    List<String> getAllNames() throws EclipseSQLException;
}