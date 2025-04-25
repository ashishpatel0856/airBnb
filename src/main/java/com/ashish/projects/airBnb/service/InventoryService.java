package com.ashish.projects.airBnb.service;

import com.ashish.projects.airBnb.entity.Room;

public interface InventoryService {

    void initializeRoomForAYear(Room room);
    void deleteFutureInventories(Room room);
}
