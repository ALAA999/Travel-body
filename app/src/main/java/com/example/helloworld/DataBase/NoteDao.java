package com.example.helloworld.DataBase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.helloworld.Model.Note;

import java.util.List;

@Dao
public interface NoteDao {

    @Query("SELECT * FROM NOTE ORDER BY ID")
    LiveData<List<Note>> loadAllNotes();

    @Insert
    void insertNote(Note note);

    @Update
    void updateNote(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM NOTE WHERE id = :id")
    Note loadNoteById(int id);
}