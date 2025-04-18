package com.tigran.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val smallThumbnailPath: String,
    val thumbnailPath: String
)

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey val name: String
)
@Entity(
    primaryKeys = ["bookId", "authorName"],
    indices = [Index(value = ["authorName"])],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AuthorEntity::class,
            parentColumns = ["name"],
            childColumns = ["authorName"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookAuthorCrossRef(
    val bookId: String,
    val authorName: String
)

data class BookWithAuthors(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "name",
        associateBy = Junction(
            value = BookAuthorCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "authorName"
        )
    )
    val authors: List<AuthorEntity>
)

