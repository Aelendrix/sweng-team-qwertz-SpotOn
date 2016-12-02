package ch.epfl.sweng.spotOn.localObjects;

/** Provides methods allowing the LocalDatabase to notify the object of changes
 */

public abstract interface LocalDatabaseListener {

    void databaseUpdated();
}
