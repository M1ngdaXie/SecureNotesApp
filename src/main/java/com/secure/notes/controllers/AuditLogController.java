package com.secure.notes.controllers;

import com.secure.notes.models.AuditLog;
import com.secure.notes.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {
    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public List<AuditLog> getAllLogs(){
        return auditLogService.getAllLogs();
    }

    @GetMapping("/note/{noteId}")
    public List<AuditLog> getNoteAuditLogs(@PathVariable Long noteId){
        return auditLogService.getAuditLogsByNoteId(noteId);
    }

}
