package com.sample.services;

import com.sample.entities.Note;
import com.sample.entities.repos.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.RowSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Сервис для выполнения операций по заметкам.
 * Реализация интерфейса.
 */

@Service
public class NoteServiceImpl
{

    public static final Logger logger = Logger.getLogger(NoteServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void saveNote(Note note)
    {
        String sql = "INSERT INTO Note(username, text, status, note_id) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, note.getUsername(), note.getText(), note.getStatus(), note.getNoteId());
    }

    public void deleteNote(Integer id)
    {
        String sql = "DELETE FROM Note WHERE note_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void setNoteStatus(Integer id, boolean status)
    {
        String sql = "UPDATE Note SET status = ? WHERE note_id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void updateNote(Integer id, String text, boolean status)
    {
        String sql = "UPDATE Note SET status = ?, text = ? WHERE note_id = ?";
        jdbcTemplate.update(sql, status, text, id);
    }

    public List<Note> findAllOrderByAsc(String username)
    {
        //return noteRepository.findAllByUsernameOrderByNoteIdAsc(username);

        String sql = String.format("select * from Note where username = '%s'", username);

        List<Note> notes = jdbcTemplate.query(sql,
                new RowMapper<Note>()
                {
                    public Note mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        Note note = new Note(username, rs.getString("text"));
                        note.setNoteId(rs.getInt("note_id"));
                        note.setStatus(rs.getBoolean("status"));
                        return note;
                    }
                });
        return notes;

    }

    public List<Note> findAllOrderByDesc(String username)
    {
        //return noteRepository.findAllByUsernameOrderByNoteIdDesc(username);

        String sql = String.format("select * from Note where username = '%s' ORDER BY note_id DESC", username);

        List<Note> notes = jdbcTemplate.query(sql,
                new RowMapper<Note>()
                {
                    public Note mapRow(ResultSet rs, int rowNum) throws SQLException
                    {
                        Note note = new Note(username, rs.getString("text"));
                        note.setNoteId(rs.getInt("note_id"));
                        note.setStatus(rs.getBoolean("status"));
                        return note;
                    }
                });
        return notes;

    }

    public int getlastid()
    {
        return jdbcTemplate.queryForObject("SELECT MAX(note_id) FROM Note", Integer.class);
    }

    public void deleteAllByUsername(String username)
    {
        String sql = String.format("delete from Note where username = '%s' ", username);

        jdbcTemplate.update(sql);
    }
}
