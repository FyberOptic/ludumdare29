package net.fybertech.ld29;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;

public class SoundManager 
{

	public class SoundEntry
	{
		String id;		
		Audio audio;
	}
	
	
	//public static ArrayList<SoundEntry> soundsList = new ArrayList<SoundEntry>();
	public static Hashtable<String, Audio> soundsHash = new Hashtable<String, Audio>();
	
	
	public SoundManager() throws IOException
	{
		addSound("gem", "res/gem.wav");
		addSound("thrust", "res/thrust.wav");
		addSound("land", "res/land.wav");
		addSound("head", "res/head.wav");
		addSound("shoot", "res/shoot.wav");
		addSound("shothit", "res/shothit.wav");
		addSound("dirtbreak", "res/dirtbreak2.wav");
		addSound("squeak", "res/squeak.wav");
		addSound("shatter", "res/shatter9.wav");
		
		//addSound("spiderdead", "res/spider2.wav");
		//addSound("spiderhurt", "res/spider8.wav");
		addSound("spiderhurt", "res/newspider1.wav");
		addSound("spiderdead", "res/newspider2.wav");


	}
	
	public void addSound(String id, String filename) throws IOException
	{	
		//SoundEntry se = new SoundEntry();
		//se.id = id;
		//se.audio = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(filename));
		
		Audio audio = AudioLoader.getAudio("WAV", ResourceLoader.getResourceAsStream(filename));
		
		//soundsList.add(se);
		soundsHash.put(id, audio);
	}
	
	public static Audio getSound(String id)
	{
		//for (SoundEntry se : soundsList) if (se.id.equals(id)) return se.audio;
		return soundsHash.get(id);
		//return null;
	}
	
	public static void playSound(String id, float pitch, float volume, boolean repeat)
	{
		Audio sound = SoundManager.getSound(id);
		if (sound != null) sound.playAsSoundEffect(pitch, volume, repeat);
	}
	
	
}
