package firok.tool.tinwrench.selectcoco;

import firok.tool.tinwrench.main.TinWrenchApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;

import java.util.function.Consumer;

public class SelectCocoDialog
{
	@SneakyThrows
	public static void open(Stage stage, Consumer<SelectCocoResult> after)
	{
		var loader = new FXMLLoader(TinWrenchApplication.class.getResource("/firok/tool/tinwrench/tin-wrench-select-coco.fxml"));
		var scene = new Scene(loader.load(), 400, 100);
		var dialog = new Stage();
		var controller = loader.<SelectCocoController>getController();
		controller.after = after;
		controller.stage = dialog;
		dialog.setResizable(false);
		dialog.setScene(scene);
		dialog.initStyle(StageStyle.UTILITY);
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(stage);
		dialog.setTitle("加载 COCO 数据集");
		dialog.show();
		dialog.centerOnScreen();
	}
}
