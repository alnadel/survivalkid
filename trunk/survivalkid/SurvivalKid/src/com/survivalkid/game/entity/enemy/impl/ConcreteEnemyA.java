package com.survivalkid.game.entity.enemy.impl;

import com.survivalkid.game.core.AnimatedSprite;
import com.survivalkid.game.entity.GameEntity;
import com.survivalkid.game.entity.enemy.EnemyEntity;

public class ConcreteEnemyA extends EnemyEntity {

	/**
	 * Create enemy
	 */
	public ConcreteEnemyA() {
		super("EnnemiA", new AnimatedSprite(), 10, 3);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void collide(GameEntity _gameEntity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void die() {
		// TODO Auto-generated method stub
		
	}

}