package com.example.apicallingdemo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "Repository")
data class Repository(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "contributors_url")
    val contributorsUrl: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "stargazers_count")
    val stargazersCount: Int,

    @ColumnInfo(name = "watchers_count")
    val watchersCount: Int,

    var isExpanded: Boolean = false,

    @ColumnInfo(name = "owner")
    val owner: Owner,

    val lastFetched: Long
)

data class Owner(
    @ColumnInfo(name = "login")
    val login: String,
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String
)

data class RepositoryResponse(
    val items: List<Repository>
)

@Entity(tableName = "contributors")
data class Contributors(
    @PrimaryKey val id: Int,
    val repoId: Int,
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String
)
