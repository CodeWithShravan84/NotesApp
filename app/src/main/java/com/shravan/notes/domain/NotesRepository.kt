package com.shravan.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun addNote(note: Note)

    fun deleteNote(noteInt: Int)

    fun editNote(note: Note)

    fun getAllNotes(): Flow<List<Note>>

    fun getNote(noteInt: Int): Note

    fun searchNotes(query: String): Flow<List<Note>>

    fun switchPinnedStatus(noteInt: Int)
}