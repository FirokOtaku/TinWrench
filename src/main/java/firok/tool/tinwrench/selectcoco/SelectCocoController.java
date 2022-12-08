package firok.tool.tinwrench.selectcoco;

import firok.tool.tinwrench.util.FxFiles;
import javafx.fxml.FXML;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
import java.net.MalformedURLException;
import java.util.function.Consumer;

public class SelectCocoController
{
	Consumer<SelectCocoResult> after;

	Stage stage;

	@FXML
	GridPane base;

	@FXML
	TextField labelFileLabel;

	File fileLabel;

	@FXML
	TextField labelFolderImage;

	File folderImage;

	@FXML
	void clickSelectFileLabel()
	{
		try
		{
			var file = FxFiles.showFileChooser(
					stage,
					"选择标签文件",
					FxFiles.JSONS
			);
			this.fileLabel = file;
			this.labelFileLabel.setText(fileLabel == null ? "" : file.getAbsolutePath());
		}
		catch (MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}
	@FXML
	void clickSelectFolderImage()
	{
		var folder = FxFiles.showFolderChooser(
				stage,
				"选择图片目录"
		);
		this.folderImage = folder;
		this.labelFolderImage.setText(folder == null ? "" : folder.getAbsolutePath());
	}

	@FXML
	void clickConfirm()
	{
		var msg = fileLabel == null ? "未选择文件" : folderImage == null ? "未选择目录" : null;
		if(msg != null)
		{
			var exception = new IllegalArgumentException(msg);
			var dia = new ExceptionDialog(exception);
			dia.setHeaderText("缺了点什么");
			dia.showAndWait();
		}
		else
		{
			stage.close();
			after.accept(new SelectCocoResult(true, fileLabel, folderImage));
		}
	}
	@FXML
	void clickCancel()
	{
		stage.close();
		after.accept(new SelectCocoResult(false, fileLabel, folderImage));
	}
}
