package firok.tool.tinwrench.main;

import firok.tool.tinwrench.TinWrench;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import static firok.tool.tinwrench.main.TinWrenchController.PADDING_X;
import static firok.tool.tinwrench.main.TinWrenchController.PADDING_Y;

public class TinWrenchApplication extends Application
{
	static final File config = new File("./tin-wrench-config.json");
	Stage stage;
	@Override
	public void start(Stage stage) throws IOException
	{
		this.stage = stage;
		FXMLLoader fxmlLoader = new FXMLLoader(TinWrenchApplication.class.getResource("/firok/tool/tinwrench/tin-wrench-main.fxml"));
		Scene scene = new Scene(fxmlLoader.load(), 800, 600);
		var controller = (TinWrenchController) fxmlLoader.getController();
		controller.stage = this.stage;
		Configs.menu = controller.menuFile;
		Configs.bus = ws -> controller.loadCoco(ws.fileLabel(), ws.folderImage());
		Configs.load(config);

		controller.fxListImage.getSelectionModel().selectedItemProperty().addListener(controller::onImageSelectChange);
		controller.fxListCate.getCheckModel().getCheckedItems().addListener(controller::refreshVis);
		controller.fxListAnno.getCheckModel().getCheckedItems().addListener(controller::refreshVis);
		controller.paneImage.setBackground(controller.BACKGROUND);

		stage.setTitle(TinWrench.NAME + " " + TinWrench.VERSION + " by " + TinWrench.AUTHOR);
		stage.setScene(scene);
		stage.setMinWidth(800);
		stage.setMinHeight(600);
		stage.setOnCloseRequest(e -> Configs.save(config));
		stage.show();
//		SplitPane p = new SplitPane();
	}

	public static void main(String[] args)
	{
		launch();
	}
}
