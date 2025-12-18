package com.ashish.projects.airBnb.controller;

import com.ashish.projects.airBnb.dto.InventoryDto;
import com.ashish.projects.airBnb.dto.UpdateInventoryRequestDto;
import com.ashish.projects.airBnb.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping("/admin/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<List<InventoryDto>> getAllInventoryByRoom(@PathVariable Long roomId) throws AccessDeniedException {
        return ResponseEntity.ok(inventoryService.getAllInventoryByRoom(roomId));
    }

    @PatchMapping("/rooms/{roomId}")
    public ResponseEntity<Void> updateInventory(@PathVariable Long roomId, @RequestBody UpdateInventoryRequestDto updateInventoryRequestDto) throws AccessDeniedException {
        inventoryService.updateInventory(roomId,updateInventoryRequestDto);
        return ResponseEntity.noContent().build();
    }
}
