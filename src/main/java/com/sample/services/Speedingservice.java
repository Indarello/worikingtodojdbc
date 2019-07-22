package com.sample.services;
import com.sample.services.NoteServiceImpl;
import com.sample.entities.Note;
import com.sample.controllers.NoteController;
import com.sample.services.UserDetailsServiceImpl;
import com.sample.entities.Note;
import com.sample.entities.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Сервис для ускорения работы всего приложения.
 */

@Service
public class Speedingservice {
    public static final Logger logger = Logger.getLogger(NoteController.class.getName());

    @Autowired
    private NoteServiceImpl noteservice;

    @Autowired
    private SecurityService securityService;

    @Autowired
    UserDetailsService userDetailsService;
    private int temporalid = -10;

    //private
    private List<Note> userstodos = new ArrayList<Note>();

    private List<UserDetails> userdetails  = new ArrayList<UserDetails>();

    public List<Note> getUsersNotes(String username)
    {
        List<Note> userNotes = new ArrayList<Note>();
        List<Note> tempuserNotes1;
        List<Note> tempuserNotes2;

        tempuserNotes1 =  userstodos.stream()
            .filter(a -> a.getUsername().equals(username) && a.getNoteId() < 0)
                .sorted(Comparator.comparing(Note::getNoteId))
                .collect(Collectors.toList());

        tempuserNotes2 =  userstodos.stream()
                .filter(a -> a.getUsername().equals(username) && a.getNoteId() > 0)
                .collect(Collectors.toList());

        userNotes.addAll(tempuserNotes1);
        userNotes.addAll(tempuserNotes2);

        if (!userNotes.isEmpty())
        {
            return userNotes;
        }

        userNotes = noteservice.findAllOrderByDesc(username);
        userstodos.addAll(userNotes);
        return userNotes;
    }

    public void saveNote(Note note) {
        temporalid--;
        note.setNoteId(temporalid);
        userstodos.add(note);
    }

    public void deleteNote(Integer id)
    {
        for(Note onenote: userstodos)
        {
            if (onenote.getNoteId().equals(id))
            {
                userstodos.remove(onenote);
                break;
            }
        }
    }

    public void setNoteStatus(Integer id, boolean status)
    {
        for(Note onenote: userstodos)
        {
            if (onenote.getNoteId().equals(id))
            {
                onenote.setStatus(status);
                break;
            }
        }
    }

    public UserDetails getSpeedDetails(String username)
    {
        List<UserDetails> userDet = new ArrayList<UserDetails>();

        for(UserDetails tempdet: userdetails) {
            if (tempdet.getUsername().equals(username)) {
                return tempdet;
            }
        }
        UserDetails TempDetails = userDetailsService.loadUserByUsername(username);
        userdetails.add(TempDetails);
        return TempDetails;
    }
}
