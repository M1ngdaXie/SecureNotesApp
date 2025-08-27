package com.secure.notes.services.serviceImpl;

import com.secure.notes.models.AuditLog;
import com.secure.notes.models.Note;
import com.secure.notes.repositories.AuditLogRepository;
import com.secure.notes.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogServiceImpl implements AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Override
    public void logNoteCreation(String username, Note note){
        AuditLog log = new AuditLog();
        log.setAction("CREATION");
        log.setUsername(username);
        log.setNoteId(note.getId());
        log.setNoteContent(note.getContent());
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }
    @Override
    public void logNoteUpdate(String username, Note note){
        AuditLog log = new AuditLog();
        log.setAction("Update");
        log.setUsername(username);
        log.setNoteId(note.getId());
        log.setNoteContent(note.getContent());
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }


    @Override
    public void logNoteDeletion(String username, Long noteId){
        AuditLog log = new AuditLog();
        log.setAction("Deletion");
        log.setUsername(username);
        log.setNoteId(noteId);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Override
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    @Override
    public List<AuditLog> getAuditLogsByNoteId(Long noteId) {
        return auditLogRepository.findByNoteId(noteId);
    }
}
