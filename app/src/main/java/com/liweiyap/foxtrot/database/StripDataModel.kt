package com.liweiyap.foxtrot.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.liweiyap.foxtrot.util.StripDate

/**
 * If @PrimaryKey annotation is used on an @Embedded field,
 * all columns inherited from that @Embedded field become the composite primary key
 * (including its grand children fields).
 * (https://medium.com/@kinnerapriyap/entity-embedded-and-composite-primary-keys-with-room-db-8cb6ca6256e8)
 */
@Entity(tableName = "strips")
data class StripDataModel(
    @PrimaryKey val url: String,
    val title: String,
    @Embedded val date: StripDate,
    @ColumnInfo(name = "image_src") val imageSrc: String,
    @ColumnInfo(name = "image_alt_text") val imageAltText: String,
    val tags: ArrayList<String>,
    @ColumnInfo(name = "prev_strip_url") val prevStripUrl: String?,
    @ColumnInfo(name = "next_strip_url") val nextStripUrl: String?,
    @ColumnInfo(name = "is_favourite") val isFavourite: Boolean)