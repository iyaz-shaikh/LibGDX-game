package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.world.WorldModel;

public class WorldItem extends WorldObject {
	
	private final String typeName = "itemName";

//	public enum ItemState implements State{
//		Fading;
//
//		@Override
//		public String getState() {
//			return toString();
//		}
//	}
	final String fadingState = "Fading";
	
	public static String name = "WorldItem";
	Item item;
	
	public WorldItem(String typeOfObject, MapProperties properties) {
		super(typeOfObject, properties);
		this.item = JSONController.items.get(properties.get(typeName));
		this.setShouldDeleteOnActivation(true);
	}
	
	@Override
	public void activateObjectOnWorld(WorldModel world) {
		Player player = world.getPlayer();
		PlayerModel model = (PlayerModel) player.getCharacterData();
		model.getPlayerProperties().getInventory().add(item);
	}

	@Override
	public Vector2 getDimensions() {
		return new Vector2(60, 60);
	}

}