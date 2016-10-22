package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.mygdx.game.model.world.WorldModel;
import com.mygdx.game.screen.GameScreen;

public class MyGdxGame extends Game {
	
	WorldModel worldModel;
   	TiledMap tiledMap;


	@Override
	public void create() {
		// TODO Auto-generated method stub
    	tiledMap = new TmxMapLoader().load("Levels/test.tmx");
    	worldModel = new WorldModel((TiledMapTileLayer) tiledMap.getLayers().get(0));

		GameScreen screen = new GameScreen(tiledMap, worldModel);
		setScreen(screen);
	}
	

}