package com.sample.services;

import com.sample.services.NoteServiceImpl;
import com.sample.entities.Note;
import com.sample.controllers.NoteController;
import com.sample.services.UserDetailsServiceImpl;
import com.sample.entities.Note;
import com.sample.entities.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.logging.Logger;

/**
 * Сервис для ускорения работы всего приложения.
 */

@Service
public class Speedingservice
{
    private static final Logger logger = Logger.getLogger(NoteController.class.getName());

    @Autowired
    private NoteServiceImpl noteservice;

    private int temporalid = -1;

    private List<Note> userstodos = new ArrayList<Note>();

    private List<LastActivity> useractivity = new ArrayList<LastActivity>();

    private void checkforcached(String username)
    {
        boolean founduser = false;
        if(!userstodos.isEmpty())
        {
            for (Note onenote : userstodos)
            {
                if(onenote.getUsername().equals(username))
                {
                    founduser = true;
                    break;
                }
            }
        }
        if(!founduser)
        {
            getUsersNotes(username);
        }
    }

    private void updateactivity(String username)
    {
        boolean founded = false;
        if(!useractivity.isEmpty())
        {
            for (LastActivity tempuser : useractivity)
            {
                if (tempuser.getUsername().equals(username))
                {
                    founded = true;
                    tempuser.setactivity(System.currentTimeMillis());
                    break;
                }
            }
        }
        if (!founded)
        {
            useractivity.add(new LastActivity(username, System.currentTimeMillis()));
        }
    }

    public List<Note> getUsersNotes(String username)
    {
        updateactivity(username);
        List<Note> userNotes = new ArrayList<Note>();
        List<Note> tempuserNotes1;
        List<Note> tempuserNotes2;

        tempuserNotes1 = userstodos.stream()
                .filter(a -> a.getUsername().equals(username) && a.getNoteId() < 0)
                .sorted(Comparator.comparing(Note::getNoteId))
                .collect(Collectors.toList());

        tempuserNotes2 = userstodos.stream()
                .filter(a -> a.getUsername().equals(username) && a.getNoteId() > 0)
                .sorted(Comparator.comparing(Note::getNoteId).reversed())
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

    public void saveNote(Note note)
    {
        checkforcached(note.getUsername());
        updateactivity(note.getUsername());
        temporalid++;
        note.setNoteId(temporalid);
        userstodos.add(note);
    }

    public void deleteNote(String username, Integer id)
    {
        checkforcached(username);
        updateactivity(username);

        for (Note onenote : userstodos)
        {
            if (onenote.getNoteId().equals(id))
            {
                userstodos.remove(onenote);
                break;
            }
        }
    }

    public void setNoteStatus(String username, Integer id, boolean status)
    {
        checkforcached(username);
        updateactivity(username);

        for (Note onenote : userstodos)
        {
            if (onenote.getNoteId().equals(id))
            {
                onenote.setStatus(status);
                break;
            }
        }
    }

    @Scheduled(fixedRate = 15000)
    public void UpdateWithDataBase()
    {
        if(temporalid == -1)
        {
            temporalid = noteservice.getlastid();
        }
        logger.info("Making schedule  " + System.currentTimeMillis());


        if(!userstodos.isEmpty() && !useractivity.isEmpty())
        {
            List<Note> temptodos = new ArrayList<Note>(userstodos);
            List<LastActivity> tempactivity = new ArrayList<LastActivity>(useractivity);
            List<String> deletedusers = new ArrayList<String>();

            List<Note> temptodos1 = new ArrayList<Note>(userstodos);
            List<LastActivity> tempactivity1 = new ArrayList<LastActivity>(useractivity);

            for (LastActivity tempuser : tempactivity)
            {
                if (tempuser.getactivity() < System.currentTimeMillis() - 31000)
                {
                    logger.info("Deleteing: " + tempuser.getUsername());
                    noteservice.deleteAllByUsername(tempuser.getUsername());
                    deletedusers.add(tempuser.getUsername());
                    tempactivity1.remove(tempuser);
                    for (Note onenote : temptodos)
                    {
                        if(onenote.getUsername().equals(tempuser.getUsername()))
                        {
                            noteservice.saveNote(onenote);
                            temptodos1.remove(onenote);
                        }
                    }

                }
            }
            if(!deletedusers.isEmpty())
            {
                userstodos.clear();
                useractivity.clear();

                userstodos.addAll(temptodos1);
                useractivity.addAll(tempactivity1);
            }
        }
    }
}
