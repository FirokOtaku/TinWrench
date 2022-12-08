package firok.tool.tinwrench.util;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;

public class FxFiles
{
	public static final FileChooser.ExtensionFilter ALL
			= new FileChooser.ExtensionFilter("All files", "*.*");
	public static final FileChooser.ExtensionFilter IMAGES
			= new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.img");
	public static final FileChooser.ExtensionFilter TEXTS
			= new FileChooser.ExtensionFilter("Text files", "*.txt");

	public static final FileChooser.ExtensionFilter JSONS
			= new FileChooser.ExtensionFilter("JSON Files", "*.json");



	public static File showFileChooser(Stage stage, String title, FileChooser.ExtensionFilter... filters) throws MalformedURLException
	{
		var fc = new FileChooser();
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filters == null ? new FileChooser.ExtensionFilter[] { ALL } : filters);
		return fc.showOpenDialog(stage);
	}

	public static File showFolderChooser(Stage stage, String title)
	{
		var dc = new DirectoryChooser();
		dc.setTitle(title);
		return dc.showDialog(stage);
	}
	public static String toURI(File file) throws MalformedURLException
	{
		return file == null ? null : file.toURI().toURL().toString();
	}
}
