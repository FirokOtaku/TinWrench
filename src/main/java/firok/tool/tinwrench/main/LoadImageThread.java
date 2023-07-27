package firok.tool.tinwrench.main;

import firok.topaz.thread.Threads;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

class LoadImageThread extends Thread
{
	final File fileImage;
	final Consumer<Image> callback;
	final TinWrenchController controller;
	LoadImageThread(File fileImage, Consumer<Image> callback, TinWrenchController controller)
	{
		this.fileImage = fileImage;
		this.callback = callback;
		this.controller = controller;
	}

	@Override
	public void run()
	{

	}
}
