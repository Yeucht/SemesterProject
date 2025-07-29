package controller;

import config.Config;
import dbmanager.DBManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.DBManagerService;

@CrossOrigin(origins = "http://localhost:7000")
@RestController
@RequestMapping("/dbmanager")
public class DBManagerController {

    private final DBManagerService dbManagerService;

    public DBManagerController(DBManagerService dbManagerService) {
        this.dbManagerService = dbManagerService;
    }

    @PostMapping("/clear")
    ResponseEntity<String> clearTables(){
        if (dbManagerService.clearTables()) {
            return ResponseEntity.ok("Successfully cleared tables");
        }else{
            return ResponseEntity.ok("Failed to clear tables");
        }
    }

    @GetMapping("/config")
    Config getConfig(){
        return dbManagerService.getConfig();
    }
}
