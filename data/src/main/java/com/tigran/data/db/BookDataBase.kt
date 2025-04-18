package com.tigran.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tigran.data.model.AuthorEntity
import com.tigran.data.model.BookAuthorCrossRef
import com.tigran.data.model.BookEntity

@Database(
    entities = [BookEntity::class, AuthorEntity::class, BookAuthorCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class BookDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
}
