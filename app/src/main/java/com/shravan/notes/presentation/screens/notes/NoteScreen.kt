package com.shravan.notes.presentation.screens.notes

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shravan.notes.domain.Note

@Composable
fun NoteScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
){
    val state by viewModel.state.collectAsState()

    LazyColumn (
        modifier = modifier
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            LazyRow (
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = state.pinnedNotes,
                    key = { it.id }
                ){ note ->
                    NotesCards(
                        note = note,
                        onNoteClick = {
                            viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                        }
                    )
                }
            }
        }

        items(
            items = state.otherNotes ,
            key = { it.id }
        ) { note ->
            NotesCards(
                note = note,
                onNoteClick = {
                    viewModel.processCommand(NotesCommand.SwitchPinnedStatus(it.id))
                }
            )
        }
    }
}


@Composable
fun NotesCards(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
){
    Text(
        modifier = modifier.clickable{
            onNoteClick(note)
        },
        text = "${note.title} - ${note.content}",
        fontSize = 24.sp
    )
}