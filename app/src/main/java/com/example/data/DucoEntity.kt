package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ducos")
data class DucoEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0L,
  val title: String,
  val text: String,
  val count: Int = 0,
  val targetCount: Int? = null,
  val createdAtMillis: Long = System.currentTimeMillis(),
)
