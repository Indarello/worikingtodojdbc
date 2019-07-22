package com.sample.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sample.services.NoteServiceImpl;
import com.sample.entities.Note;
import com.sample.payloads.ApiResponse;
import com.sample.services.SecurityService;
import com.sample.services.Speedingservice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;
/**
 * Контроллер для работы с заметками (просмотр, добавление, изменение статуса, редактирование, удаление).
 * RestAPI, обеспечивает обработку запросов внешнего приложения.
 */

@RestController
public class NoteController {

    public static final Logger logger = Logger.getLogger(NoteController.class.getName());

    private String sortDateMethod = "DESC";

    @Autowired
    private NoteServiceImpl noteservice;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private Speedingservice speedingservice;

    @CrossOrigin("/*")
    @GetMapping(value="/todos", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public @ResponseBody ResponseEntity<?> list(@RequestHeader("Authorization") String token){
/*
        try {
            logger.info("time for todo:" + System.nanoTime());
            String username = securityService.getUserByToken(token.substring(7, token.length()));
            logger.info("time for todo2:" + System.nanoTime());
            List<Note> notes = filterAndSort(username);

            return new ResponseEntity<List<Note>>(notes, HttpStatus.OK);
        }catch (Exception e)
        {
            return new ResponseEntity<Object>(
                    new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
 */
        return new ResponseEntity<Object>(new ApiResponse(false, "testing"), HttpStatus.BAD_REQUEST);
    }

    private List<Note> filterAndSort(String username) {
        List<Note> notebook = null;
        switch (sortDateMethod) {
            case "ASC":
                notebook = noteservice.findAllOrderByAsc(username);
                break;
            case "DESC":
                notebook = noteservice.findAllOrderByDesc(username);
                //notebook = speedingservice.getUsersNotes(username);
                break;
        }
        System.out.println("Получено " + notebook.size());

        return notebook;
    }

    @CrossOrigin("/addNote")
    @PostMapping(value = "/addNote", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> updateNote(@RequestHeader("Authorization") String token, @RequestBody ObjectNode obj) {

        try {
            String username = securityService.getUserByToken(token.substring(7, token.length()));
            noteservice.saveNote(new Note(username, obj.get("text").asText()));
            //speedingservice.saveNote(new Note(username, obj.get("text").asText()));
            return new ResponseEntity<Object>(
                    new ApiResponse(true, "Note added successfully"), HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<Object>(
                    new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        //   return new ResponseEntity("User is unauthorized",HttpStatus.UNAUTHORIZED);

    }

    @CrossOrigin("/status")
    @PutMapping(value = "/status", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> edit(@RequestHeader("Authorization") String token, @RequestBody ObjectNode obj) {

        logger.info("request header is: \n" + token);

        String username = securityService.getUserByToken(token.substring(7,token.length()));
        Integer id = obj.get("id").asInt();
        boolean status = obj.get("status").asBoolean();

        try{
            noteservice.setNoteStatus(id, status);
            //speedingservice.setNoteStatus(id, status);
            return new ResponseEntity<Object>(new ApiResponse(true,"Note status updated"),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<Object>(
                    new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST); //возвращаем view с редактированием
        }
    }

    @PutMapping(value="/edit", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> saveNote(@RequestHeader("Authorization") String token, @RequestBody ObjectNode note) {

        logger.info("request header is: \n" + token);

        String username = securityService.getUserByToken(token.substring(7,token.length()));

        Integer id = note.get("id").asInt();
        String text = note.get("text").asText();
        boolean status = note.get("status").asBoolean();

        try{
            noteservice.updateNote(id, text, status);
            //speedingservice.updateNote(id, text, status);
            return new ResponseEntity<Object>(new ApiResponse(true,"Note edited"),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<Object>(
                    new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> delete(@RequestHeader("Authorization") String token, @RequestBody ObjectNode note) {

        logger.info("request header is: \n" + token);

        String username = securityService.getUserByToken(token.substring(7,token.length()));
        Integer id = note.get("id").asInt();
        try{
            noteservice.deleteNote(id);
            //speedingservice.deleteNote(id);
            return new ResponseEntity<Object>(new ApiResponse(true,"Note deleted"),HttpStatus.OK);
        }catch (Exception e) {
            return new ResponseEntity<Object>(
                    new ApiResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
