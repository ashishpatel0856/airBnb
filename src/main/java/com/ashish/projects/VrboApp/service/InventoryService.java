package com.ashish.projects.VrboApp.service;

import com.ashish.projects.VrboApp.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);

}
