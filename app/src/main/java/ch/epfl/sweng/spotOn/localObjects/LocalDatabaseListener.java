package ch.epfl.sweng.spotOn.localObjects;

/** Provides methods allowing the LocalDatabase to notify the object of changes
 */

public interface LocalDatabaseListener {

    void databaseUpdated();
}
