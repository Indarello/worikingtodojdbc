package com.sample.services;

import com.sample.entities.Note;
import com.sample.entities.repos.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.sql.ResultSet;

import java.util.logging.Logger;
/**
 * Сервис для выполнения операций по заметкам.
 * Реализация интерфейса.
 */

@Service
public class NoteServiceImpl{

    public static final Logger logger = Logger.getLogger(NoteServiceImpl.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void saveNote(Note note) {
        String sql = "INSERT INTO Note(username, text, status) VALUES (?,?,false)";
        jdbcTemplate.update(sql, note.getUsername(), note.getText());
    }

    public void deleteNote(Integer id){
        String sql = "DELETE FROM Note WHERE note_id = ?";
        jdbcTemplate.update(sql, id);
    }

    public void setNoteStatus(Integer id, boolean status){
        String sql = "UPDATE Note SET status = ? WHERE note_id = ?";
        jdbcTemplate.update(sql, status, id);
    }

    public void updateNote(Integer id, String text, boolean status) {
        String sql = "UPDATE Note SET status = ?, text = ? WHERE note_id = ?";
        jdbcTemplate.update(sql, status, text, id);
    }

    public List<Note> findAllOrderByAsc(String username) {
        //return noteRepository.findAllByUsernameOrderByNoteIdAsc(username);

        String sql = String.format("select * from Note where username = '%s'", username);

        List<Note> notes = this.jdbcTemplate.query(sql,
        new RowMapper<Note>() {
            public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
                Note note = new Note(username, rs.getString("text"));
                note.setNoteId(rs.getInt("note_id"));
                note.setStatus(rs.getBoolean("status"));
                return note;
            }
        });
        return notes;

    }

    public List<Note> findAllOrderByDesc(String username) {
        //return noteRepository.findAllByUsernameOrderByNoteIdDesc(username);

        String sql = String.format("select * from Note where username = '%s' ORDER BY note_id DESC", username);

        List<Note> notes = this.jdbcTemplate.query(sql,
                new RowMapper<Note>() {
                    public Note mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Note note = new Note(username, rs.getString("text"));
                        note.setNoteId(rs.getInt("note_id"));
                        note.setStatus(rs.getBoolean("status"));
                        return note;
                    }
                });
        return notes;

    }



}
