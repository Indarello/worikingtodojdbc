package com.sample.payloads;

import com.sample.entities.Note;

import java.util.List;

public class TodosResponse
{
    private List<Note> noteList;
    private boolean success;

    public void setNoteList(List<Note> noteList)
    {
        this.noteList = noteList;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public List<Note> getNoteList()
    {
        return noteList;
    }

    public boolean getSuccess()
    {
        return success;
    }

    public String toString()
    {

        return null;
    }
}
