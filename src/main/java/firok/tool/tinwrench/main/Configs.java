package firok.tool.tinwrench.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.function.Consumer;

public class Configs
{
	public static Menu menu = null;
	public static Consumer<RecentWorkspace> bus = null;
	public static final ObservableList<RecentWorkspace> list = FXCollections.observableArrayList();
	static
	{
		list.addListener((ListChangeListener<RecentWorkspace>) c ->
		{
			if(menu == null) return;
			var items = menu.getItems();
			if(items.size() > 2)
				items.remove(2, items.size());
			for(var ws : list)
			{
				var mi = new MenuItem();
				mi.setText(ws.fileLabel.getAbsolutePath() + "\n" + ws.folderImage.getAbsolutePath());
				items.add(mi);
				if(bus != null)
					mi.setOnAction(e -> bus.accept(ws));
			}
		});
	}
	public static void save(File config)
	{
		try(var ofs = new FileOutputStream(config))
		{
			var om = new ObjectMapper();
			var json = om.createArrayNode();
			for(var ws : list)
			{
				var obj = om.createObjectNode();
				obj.put("fileLabel", ws.fileLabel.getAbsolutePath());
				obj.put("folderImage", ws.folderImage.getAbsolutePath());
				json.add(obj);
			}
			om.writeValue(ofs, json);
		}
		catch (Exception ignored) { }
	}
	public static void load(File config)
	{
		try(var ifs = new FileInputStream(config))
		{
			var om = new ObjectMapper();
			var json = om.readTree(ifs) instanceof ArrayNode arr ? arr : null;
			list.clear();
			if(json == null || json.isEmpty()) return;
			for(int step = 0; step < json.size(); step++)
			{
				var objRecord = json.get(step) instanceof ObjectNode obj ? obj : null;
				if(objRecord == null || !objRecord.has("fileLabel") || !objRecord.has("folderImage")) continue;
				var fileLabel = new File(objRecord.get("fileLabel").asText());
				var folderImage = new File(objRecord.get("folderImage").asText());
				list.add(new RecentWorkspace(fileLabel, folderImage));
			}
		}
		catch (Exception ignored) { }
	}
	public static void opened(File fileLabel, File folderImage)
	{
		var ws = new RecentWorkspace(fileLabel, folderImage);
		list.removeIf(wsOld -> Objects.equals(wsOld, ws));
		list.add(ws);
		if(list.size() > 5)
			list.remove(5, list.size());
	}

	public record RecentWorkspace(File fileLabel, File folderImage) { }
}
