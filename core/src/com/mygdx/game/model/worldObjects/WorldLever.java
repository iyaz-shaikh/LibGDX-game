package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityCollisionData;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.world.WorldModel;

public class WorldLever extends WorldObject {

	static final String objectToModifyUUIDKey = "ObjToModifyUUID";
	Integer objectToModifyUUID;
	
	public WorldLever(String name, MapProperties properties, ObjectListener objListener, SaveListener saveListener) {
		super(name, properties, objListener, saveListener);
		//get global lever properties from global json file
		
		//get specific lever properties from uuid
		
		this.objectToModifyUUID = (Integer) properties.get(objectToModifyUUIDKey);
	}

	@Override
	public void activateObjectOnWorld(WorldModel world) {
		WorldObject correspondingObject = null;
		for (WorldObject object : world.getObjects()) {
			if (object.uuid.equals(objectToModifyUUID)) {
				correspondingObject = object;
			}
		}
		if (correspondingObject != null) {
			correspondingObject.activateObjectOnWorld(world);
		}
		super.activateObjectOnWorld(world);
	}

	@Override
	public boolean shouldDeleteIfActivated() {
		return false;
	}

	@Override
	public boolean shouldCollideWithEntity() {
		return false;
	}

	@Override
	public UUIDType getUUIDType() {
		return UUIDType.OBJECT;
	}

	@Override
	public boolean shouldMove() {
		return false;
	}

	@Override
	public EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
		return null;
	}

	@Override
	public EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
		return null;
	}

	@Override
	public void refreshHurtBoxesX() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshHurtBoxesY() {
		// TODO Auto-generated method stub
		
	}



}
