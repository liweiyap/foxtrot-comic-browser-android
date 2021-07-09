package com.liweiyap.foxtrot.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Note that querying a table with a return type of Flow<T> always returns the first row in the result set,
 * rather than emitting all of the rows in sequence. To observe changes over multiple rows in a table,
 * use a return type of Flow<List<T>> instead.
 *
 * Keep nullability in mind when choosing a return type, as it affects how the query method handles empty tables:
 * - When the return type is Flow<T>, querying an empty table throws a null pointer exception.
 * - When the return type is Flow<T?>, querying an empty table emits a null value.
 * - When the return type is Flow<List<T>>, querying an empty table emits an empty list.
 *
 * (https://developer.android.com/reference/androidx/room/Query)
 */
@Dao
interface StripDao {

    // replace prevUrlString if need be
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(strip: StripDataModel)

    @Query("SELECT * FROM strips WHERE url = :urlString")
    suspend fun get(urlString: String): StripDataModel?

    @Query("SELECT COUNT(*) FROM (SELECT * FROM strips WHERE prev_strip_url IS NOT NULL AND next_strip_url IS NULL)")
    suspend fun countLatest(): Int

    @Query("SELECT COUNT(*) FROM (SELECT * FROM strips WHERE prev_strip_url IS NULL AND next_strip_url IS NOT NULL)")
    suspend fun countEarliest(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM strips WHERE url = :urlString LIMIT 1)")
    suspend fun hasStrip(urlString: String): Boolean
}