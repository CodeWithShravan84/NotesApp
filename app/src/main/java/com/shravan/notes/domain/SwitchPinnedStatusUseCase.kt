package com.shravan.notes.domain

class SwitchPinnedStatusUseCase(
    private val repository: NotesRepository
) {
    operator fun invoke(noteId: Int){
        repository.switchPinnedStatus(noteInt = noteId)
    }
}