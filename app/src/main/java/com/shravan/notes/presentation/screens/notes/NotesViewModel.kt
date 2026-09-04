@file:OptIn(ExperimentalCoroutinesApi::class)

package com.shravan.notes.presentation.screens.notes

import androidx.core.app.NotificationChannelCompat
import androidx.lifecycle.ViewModel
import com.shravan.notes.data.TestNotesRepositoryImpl
import com.shravan.notes.domain.AddNoteUseCase
import com.shravan.notes.domain.DeleteNoteUseCase
import com.shravan.notes.domain.EditNoteUseCase
import com.shravan.notes.domain.GetAllNotesUseCase
import com.shravan.notes.domain.GetNoteUseCase
import com.shravan.notes.domain.Note
import com.shravan.notes.domain.SearchNoteUseCase
import com.shravan.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel: ViewModel() {
    private val repository = TestNotesRepositoryImpl
    private val addNotesUseCase = AddNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNoteUseCase = SearchNoteUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val query = MutableStateFlow("")

    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        query
            .onEach {input ->
                _state.update { it.copy(query = input) } }
            .flatMapLatest {input ->
                if (input.isBlank()){
                    getAllNotesUseCase()
                }else{
                    searchNoteUseCase(input)
                }
            }
            .onEach {notes ->
                val pinnedNotes = notes.filter { it.isPinned }
                val otherNotes = notes.filter { !it.isPinned }
                _state.update { it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes) }
            }
            .launchIn(scope)
    }

    fun processCommand(command: NotesCommand){
        when(command){
            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }
            is NotesCommand.EditNote -> {
                val title = command.note.title
                editNoteUseCase(command.note.copy(title = "$title edited"))
            }
            is NotesCommand.InputSearchQuery -> {
                query.update { command.query.trim() }
            }
            is NotesCommand.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }
        }
    }
}

sealed interface NotesCommand{
    data class InputSearchQuery(val query: String): NotesCommand

    data class SwitchPinnedStatus(val noteId: Int): NotesCommand

    data class DeleteNote(val noteId: Int): NotesCommand

    data class EditNote(val note: Note): NotesCommand
}

data class NoteScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes : List<Note> = listOf()
)