package com.tigran.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tigran.data.model.AuthorEntity
import com.tigran.data.model.BookAuthorCrossRef
import com.tigran.data.model.BookEntity
import com.tigran.data.model.BookWithAuthors
import com.tigran.domain.model.Book

@Dao
interface BookDao {

    @Transaction
    @Query("SELECT * FROM books")
    suspend fun getAllBooksWithAuthors(): List<BookWithAuthors>

    @Transaction
    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getBookWithAuthors(bookId: String): BookWithAuthors?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(bookEntity: BookEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<BookAuthorCrossRef>)
}
