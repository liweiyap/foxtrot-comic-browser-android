package com.liweiyap.foxtrot.util

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * If @PrimaryKey annotation is used on an @Embedded field,
 * all columns inherited from that @Embedded field become the composite primary key
 * (including its grand children fields).
 * (https://medium.com/@kinnerapriyap/entity-embedded-and-composite-primary-keys-with-room-db-8cb6ca6256e8)
 */
@Entity(tableName = "Strips")
data class StripDataModel(
    val url: String,
    val title: String,
    @PrimaryKey @Embedded val date: StripDate,
    val imageSrc: String,
    val imageAltText: String,
    val tags: ArrayList<String>,
    val prevStripUrl: String?)