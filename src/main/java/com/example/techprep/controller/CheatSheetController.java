package com.example.techprep.controller;

import com.example.techprep.dto.CheatSheetDTO;
import com.example.techprep.service.CheatSheetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cheatsheets")
public class CheatSheetController {
    private final CheatSheetService service;

    public CheatSheetController(CheatSheetService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CheatSheetDTO>> getAllCheatSheets() {
        return ResponseEntity.ok(service.getAllCheatSheets());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CheatSheetDTO> getCheatSheetById(@PathVariable Long id) {
        return service.getCheatSheetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CheatSheetDTO> createCheatSheet(@RequestBody CheatSheetDTO dto) {
        return ResponseEntity.ok(service.createCheatSheet(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CheatSheetDTO> updateCheatSheet(@PathVariable Long id, @RequestBody CheatSheetDTO dto) {
        return ResponseEntity.ok(service.updateCheatSheet(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheatSheet(@PathVariable Long id) {
        service.deleteCheatSheet(id);
        return ResponseEntity.noContent().build();
    }
}
