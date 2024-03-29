package com.mygdx.game.model.characters.player;

import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.worldObjects.Item;

public class PlayerProperties implements Serializable{
    HashMap<String, ActionSequence> playerSpecificActions;
    Array <Item> inventory;
    Array <ItemInfo> quickItems;
    Array<Integer> acquiredItemsHistory;
    Array<Integer> objectsInteractedHistory;
    
	@Override
	public void write(Json json) {
		json.writeValue("playerActions", playerSpecificActions);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		playerSpecificActions = json.readValue("playerActions", HashMap.class, jsonData);
	}
	
	public HashMap<String, ActionSequence> getPlayerSpecificActions() {
		return playerSpecificActions;
	}
	
	public void populateWith(GameSave gameSave) {
		inventory = new Array <Item>();
		for (String itemKey : gameSave.getInventoryItemKeys()) {
			Item item = JSONController.items.get(itemKey);
			inventory.add(item);
		}
		
		quickItems = new Array <ItemInfo>();
		for (String itemKey : gameSave.getQuickItemKeys()) {
			Item itemToAdd = null;
			int numberOfItems = 0;
			for (Item item : inventory) {
				if (itemKey.equals(item.getName())) {
					if (itemToAdd == null) {
						itemToAdd = item;
					}
					numberOfItems += 1;
				}
			}
			
			quickItems.add(new ItemInfo(itemToAdd, numberOfItems));
		}
		
		this.acquiredItemsHistory = gameSave.acquiredItemsHistory;
		this.objectsInteractedHistory = gameSave.objectsInteractedHistory;
	}
	public Array <Item> getInventory() {
		return inventory;
	}
	public Array<ItemInfo> getQuickItems() {
		return quickItems;
	}
}
