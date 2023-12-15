package com.celalkorucu.fruitninjastarter;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import org.w3c.dom.Text;

import java.util.Random;

public class FruitNinja extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture backGround;
	Texture apple ;
	Texture pear ;
	Texture bomb ;
	Texture heart ;

	BitmapFont bitmapFont ;
	FreeTypeFontGenerator fontGenerator ;

	int lives = 0 ;
	int score = 0 ;
	private double currentTime ;
	private double gameOverTime = -1.0f ;

	Random random = new Random();
	Array<Fruit> fruitArray = new Array<Fruit>();
	float genCounter = 0 ;
	private final float startGenSpeed = 1.1f;
	float genSpeed = startGenSpeed ;

	@Override
	public void create () {
		batch = new SpriteBatch();
		backGround = new Texture("back.png");
		apple = new Texture("apple.png");
		pear = new Texture("pear.png");
		bomb = new Texture("bomb.png");
		heart = new Texture("heart.png");


		Fruit.radius = Math.max(Gdx.graphics.getHeight() , Gdx.graphics.getWidth())/25f;

		Gdx.input.setInputProcessor(this);
		fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font.otf"));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.color = Color.BROWN;
		parameter.size = 60 ;
		parameter.characters = "0123456789 Score:+-CutThePlay";
		bitmapFont = fontGenerator.generateFont(parameter);


	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(backGround, 0, 0 , Gdx.graphics.getWidth() , Gdx.graphics.getHeight());
		//bitmapFont.draw(batch , "Score :0" , 50,60);
		//bitmapFont.draw(batch , "Cut The Play" , Gdx.graphics.getWidth()/2 -175, Gdx.graphics.getHeight()/2);

		double newTime = TimeUtils.millis()/1000.0;
		//System.out.println("New Time : "+newTime);
		double frameTime = Math.min(newTime - currentTime , 0.3);
		//System.out.println("Frame Time : "+frameTime);
		float deltaTime = (float) frameTime;
		//System.out.println("Delta Time : "+deltaTime);
		currentTime = newTime ;


		if(lives <= 0  && gameOverTime == 0f){
			// Game Over
			gameOverTime = currentTime ;
		}
		if(lives > 0){
			//Game Mod
			genSpeed -= deltaTime *0.015f ;

			System.out.println("genspeed : "+genSpeed);
			System.out.println("gencounter : "+genCounter);
			if(genCounter <= 0f){
				genCounter = genSpeed ;
				addItem();
			}else{
				genCounter -= deltaTime ;
			}

			//HEAL
			for(int i = 0 ; i<lives ; i++){
				batch.draw(heart , i*60f +40f , Gdx.graphics.getHeight()-80f , 60f , 60f);
			}

			for(Fruit fruit : fruitArray){
				fruit.update(deltaTime);

				switch (fruit.type){
					case LIFE:
						batch.draw(heart , fruit.getPos().x ,fruit.getPos().y , Fruit.radius  ,Fruit.radius );
						break ;
					case ENEMY:
						batch.draw(bomb , fruit.getPos().x ,fruit.getPos().y , Fruit.radius  ,Fruit.radius );
						break ;
					case EXTRA:
						batch.draw(pear , fruit.getPos().x ,fruit.getPos().y , Fruit.radius  ,Fruit.radius );
						break ;
					case REGULAR:
						batch.draw(apple , fruit.getPos().x ,fruit.getPos().y , Fruit.radius  ,Fruit.radius );
						break ;
				}
			}
			//remove out of screen
			boolean holdlives = false;
			Array<Fruit> toRemove = new Array<Fruit>();
			for(Fruit fruit : fruitArray){
				if(fruit.outOfScreen()){
					toRemove.add(fruit);
					if(fruit.living && fruit.type== Fruit.Type.REGULAR){
						lives--;
						holdlives=true;
						//holdlives=true;
						break;
					}
				}
			}
			if(holdlives){
				for(Fruit f : fruitArray){
					f.living = false;
				}
			}
			for(Fruit f : toRemove){
				fruitArray.removeValue(f, true);
			}
		}

		bitmapFont.draw(batch, "Score: "+ score, 30, 60);
		if(lives<=0){
			bitmapFont.draw(batch, "Cut to Play!", Gdx.graphics.getWidth()*0.45f, Gdx.graphics.getHeight()*0.45f);
		}




		batch.end();
	}

	public void addItem(){

		float r = random.nextFloat();
		float pos = random.nextFloat() * Math.max(Gdx.graphics.getHeight() , Gdx.graphics.getWidth());
		Fruit item = new Fruit(new Vector2(pos,-Fruit.radius),new Vector2((Gdx.graphics.getWidth()*0.5f - pos) * 0.3f+(random.nextFloat() - 0.5f),Gdx.graphics.getHeight()*0.5f));
		float type = random.nextFloat();

		if(type > 0.97){
			item.type = Fruit.Type.LIFE;
		} else if (type > 0.87){
			item.type = Fruit.Type.ENEMY;
		}else if (type > 0.75){
			item.type = Fruit.Type.EXTRA;
		}else {
			item.type = Fruit.Type.REGULAR;
		}

		fruitArray.add(item);


	}
	
	@Override
	public void dispose () {
		batch.dispose();
		backGround.dispose();
		fontGenerator.dispose();
		bitmapFont.dispose();

	}




	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		if(lives<=0 && currentTime - gameOverTime > 2f){//menu mode
			gameOverTime = 0f;
			score=0;
			lives=4;//restart game
			genSpeed = startGenSpeed;
			fruitArray.clear();
		}else{

			//game mode
			Array<Fruit> toRemove = new Array<Fruit>();
			Vector2 pos = new Vector2(screenX,Gdx.graphics.getHeight()-screenY);
			int plusScore = 0;
			for(Fruit f : fruitArray){
				/*
				System.out.println("getHeight - y: " + screenY);
				System.out.println("getHeight - y: " + (Gdx.graphics.getHeight()-screenY));
				System.out.println("getHeight - y: " + f.getPos());
				System.out.println("distance: " + pos.dst2(f.pos));
				System.out.println("distance: " + f.clicked(pos));
				System.out.println("distance: " + Fruit.radius * Fruit.radius + 1);

				 */
				if(f.clicked(pos)){
					toRemove.add(f);
					switch(f.type){
						case REGULAR:
							plusScore++;
							break;
						case EXTRA:
							plusScore+=2;
							score++;
							break;
						case ENEMY:
							lives--;
							break;
						case LIFE:
							lives++;
							break;
					}
				}
			}
			score += plusScore*plusScore;
			for(Fruit f : toRemove){
				fruitArray.removeValue(f, true);
			}
		}
		return false;

	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}
}
