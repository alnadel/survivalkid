package com.survivalkid.game.manager;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;

import com.survivalkid.game.entity.GameEntity;
import com.survivalkid.game.entity.enemy.EnemyEntity;
import com.survivalkid.game.entity.personage.Personage;

public class EnemyManager extends ObjectManager {

	private List<EnemyEntity> enemyList;
	private long lastEnemy;
	
	public EnemyManager() {
		enemyList = new ArrayList<EnemyEntity>();
	}
	
	public void create() {
		// TODO Auto-generated method stub
		enemyList = new ArrayList<EnemyEntity>();
	}

	public void update(long gameTime) {
		// TODO Auto-generated method stub
		for(EnemyEntity enemy : enemyList) {
			enemy.update(gameTime);
		}
	}

	public void draw(Canvas canvas, boolean displayHitBox) {
		// TODO Auto-generated method stub
		for(EnemyEntity enemy : enemyList) {
			enemy.draw(canvas, displayHitBox);
		}
	}

	@Override
	public void generate() {
		// TODO Auto-generated method stub

	}
	
	
	public List<EnemyEntity> getEnemyList() {
		return enemyList;
	}
	
	public EnemyEntity getEnemyList(int num) {
		return enemyList.get(num);
	}
	

	@Override
	public void addEntity(GameEntity ge) {
		// TODO Auto-generated method stub
		enemyList.add((EnemyEntity)ge);
	}

}
