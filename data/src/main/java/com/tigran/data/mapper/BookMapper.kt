package com.tigran.data.mapper

import com.tigran.data.model.AuthorEntity
import com.tigran.data.model.BookAuthorCrossRef
import com.tigran.data.model.BookEntity
import com.tigran.data.model.BookItem
import com.tigran.data.model.BookWithAuthors
import com.tigran.domain.model.Book

fun BookItem.toDomain(): Book? {
    val authors = volumeInfo.authors
    if (authors.isNullOrEmpty()) return null

    return Book(
        id = id,
        title = volumeInfo.title,
        authors = authors,
        smallThumbnailUrl = volumeInfo.imageLinks?.smallThumbnail,
        thumbnailUrl = volumeInfo.imageLinks?.thumbnail
    )
}

fun BookWithAuthors.toDomain(): Book {
    return Book(
        id = book.id,
        title = book.title,
        authors = authors.map { it.name },
        smallThumbnailUrl = null,
        thumbnailUrl = null,
        thumbnailPath = book.thumbnailPath,
        smallThumbnailPath = book.smallThumbnailPath
    )
}

fun Book.toEntities(): Triple<BookEntity, List<AuthorEntity>, List<BookAuthorCrossRef>> {
    val bookEntity = BookEntity(
        id = id,
        title = title,
        smallThumbnailPath = smallThumbnailPath ?: "",
        thumbnailPath = thumbnailPath ?: ""
    )

    val authorsList = authors.map { author ->
        AuthorEntity(name = author)
    }

    val crossRefs = authors.map { author ->
        BookAuthorCrossRef(bookId = id, authorName = author)
    }

    return Triple(bookEntity, authorsList, crossRefs)
}


