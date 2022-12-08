package firok.tool.tinwrench.main;

import firok.tool.alloywrench.bean.CocoData;
import firok.tool.tinwrench.selectcoco.SelectCocoDialog;
import firok.tool.tinwrench.selectcoco.SelectCocoResult;
import firok.topaz.Maths;
import firok.topaz.Threads;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import lombok.AccessLevel;
import lombok.Setter;
import org.controlsfx.control.CheckListView;
import org.controlsfx.dialog.ExceptionDialog;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.stream.Collectors;

public class TinWrenchController
{
	Stage stage;

	@FXML
	MenuBar mb;

	@FXML
	SplitPane baseSideList;

	@FXML
	ListView<RefImage> fxListImage;

	@FXML
	CheckListView<RefCate> fxListCate;

	@FXML
	CheckListView<RefAnno> fxListAnno;

	@FXML
	void onLoadCocoDataset()
	{
		if(!this.isReactive)
			return;

		SelectCocoDialog.open(stage, this::loadCocoDataset);
	}

	void loadCocoDataset(SelectCocoResult param)
	{
		if(!param.isConfirm())
			return;

		loadCoco(param.fileLabel(), param.folderImage());
	}


	Scale ivScale = new Scale(1, 1);

	@FXML
	Pane paneImage;
	Image image;
	File fileLabel;
	File folderImage;
	Map<Integer, RefImage> mapImage;
	Map<Integer, RefCate> mapCate;
	Map<Integer, RefAnno> mapAnno;

	@FXML
	void zoomIn()
	{
		;
	}
	@FXML
	void zoomOut()
	{
		;
	}

	@Setter(AccessLevel.PACKAGE)
	boolean isReactive = true;
	void loadCoco(File fileLabel, File folderImage)
	{
		var thread = new LoadCocoThread(fileLabel, folderImage, this);
		thread.start();
	}

	@FXML
	ImageView iv;

	List<AnnoRender> listAnnoRender = new ArrayList<>(50);

	@FXML
	void checkAllCate(ActionEvent ignored)
	{
		this.isReactive = false;
		this.fxListCate.getCheckModel().checkAll();
		this.isReactive = true;
		refreshVis(null);
	}
	@FXML
	void toggleAllCate(ActionEvent ignored)
	{
		this.isReactive = false;
		var cm = this.fxListCate.getCheckModel();
		var count = cm.getItemCount();
		for(int step = 0; step < count; step++)
			cm.toggleCheckState(step);
		this.isReactive = true;
		refreshVis(null);
	}
	@FXML
	void uncheckAllCate(ActionEvent ignored)
	{
		this.isReactive = false;
		this.fxListCate.getCheckModel().clearChecks();
		this.isReactive = true;
		refreshVis(null);
	}
	@FXML
	void moveToSelectedAnno(ActionEvent ignored)
	{
		var sm = this.fxListAnno.getSelectionModel();
		var si = sm.getSelectedItem();
		if(si == null) return;
		var aid = si.anno.getId();
		for(var render : listAnnoRender)
		{
			if(render.annoId != aid) continue;

			var posX = render.text.getLayoutX() - 50;
			var posY = render.text.getLayoutY() - 50;
			var x = Maths.range(posX / image.getWidth(), 0, 1);
			var y = Maths.range(posY / image.getHeight(), 0, 1);
			this.spImage.setHvalue(x);
			this.spImage.setVvalue(y);

			break;
		}
	}
	@FXML
	void checkAllAnno(ActionEvent ignored)
	{
		this.isReactive = false;
		this.fxListAnno.getCheckModel().checkAll();
		this.isReactive = true;
		refreshVis(null);
	}
	@FXML
	void uncheckAllAnno(ActionEvent ignored)
	{
		this.isReactive = false;
		this.fxListAnno.getCheckModel().clearChecks();
		this.isReactive = true;
		refreshVis(null);
	}
	@FXML
	void toggleAllAnno(ActionEvent ignored)
	{
		this.isReactive = false;
		var cm = this.fxListAnno.getCheckModel();
		var count = cm.getItemCount();
		for(int step = 0; step < count; step++)
		{
			cm.toggleCheckState(step);
		}
		this.isReactive = true;
		refreshVis(null);
	}

	static class AnnoRender
	{
		int annoId;
		CocoData.Annotation anno;
		CocoData.Category cate;
		List<Path> listPath;
		Text text;
	}

	@FXML
	void onImageSelectChange(
			ObservableValue<? extends RefImage> observable,
			TinWrenchController.RefImage oldValue,
			TinWrenchController.RefImage newValue
	)
	{
		if(!isReactive) return;
		loadSelectedImage();
	}

	void refreshVis(Observable ignored)
	{
//		System.out.println("refresh vis");
		if(!isReactive) return;

		var setCateId = fxListCate.getCheckModel().getCheckedItems().stream()
				.filter(Objects::nonNull)
				.map(ref -> ref.cate().getId())
				.collect(Collectors.toSet());
		var setAnnoId = fxListAnno.getCheckModel().getCheckedItems().stream()
				.filter(Objects::nonNull)
				.map(ref -> ref.anno().getId())
				.collect(Collectors.toSet());

		if(image != null)
		{
//			double x = PADDING_X + spImage.getHvalue() * image.getWidth(), width = spImage.getWidth();
//			double y = PADDING_Y + spImage.getVvalue() * image.getHeight(), height = spImage.getHeight();
//			var bb = new BoundingBox(x, y, width, height);

			for (AnnoRender render : listAnnoRender)
			{
				boolean display = setAnnoId.contains(render.annoId) && setCateId.contains(render.cate.getId());
				render.text.setVisible(display);
				for (var path : render.listPath)
				{
					path.setVisible(display);
				}
			}
		}

		iv.setVisible(true);
	}

	@FXML
	Menu menuFile;

	@FXML
	ScrollPane spImage;

	static final int PADDING_X = 20;
	static final int PADDING_Y = 20;

	void loadSelectedImage()
	{
		this.isReactive = false;
		if(this.image != null)
		{
			iv.setImage(null);
		}

		var selected = this.fxListImage.getSelectionModel().getSelectedItem();
		if(selected == null) return;
		var img = selected.img;
//		System.out.println("加载图片: " + img);
		var filename = img.getFilename();
		var fileImage = new File(folderImage, filename);
//		var ele = this.paneImage.getChildren();

		var threadLoadImage = new Thread( () ->
		{
			Image image;
			try (var ifs = new FileInputStream(fileImage))
			{
				image = new Image(ifs);
				double progress;
				while ((progress = image.getProgress()) < 1)
				{
					Threads.sleep(100);
				}
				if (image.getException() != null)
					throw image.getException();

			}
			catch (Exception any)
			{
				System.out.println("加载图片发生错误");
				any.printStackTrace(System.err);

				image = new WritableImage(img.getWidth(), img.getHeight());
			}
			Image finalImage = image;
			Platform.runLater(() -> {
				this.image = finalImage;
				iv.setImage(finalImage);
				iv.setX(0);
				iv.setY(0);
				iv.setLayoutX(PADDING_X);
				iv.setLayoutY(PADDING_Y);
				iv.setFitWidth((int) finalImage.getWidth());
				iv.setFitHeight((int) finalImage.getHeight());
				this.isReactive = true;
			});
		});
		threadLoadImage.start();

		var entries = this.mapAnno.values().stream()
				.filter(anno -> Objects.equals(anno.anno.getImageId(), img.getId()))
				.toList();
		var setCateId = new HashSet<Integer>();
		for(var entry : entries) setCateId.add(entry.cate.getId());
		var cates = this.mapCate.values().stream()
				.filter(cate -> setCateId.contains(cate.cate.getId()))
				.collect(Collectors.toSet());

		var listCate = this.fxListCate.getItems();
		listCate.clear();
		listCate.addAll(cates);
		this.fxListCate.getCheckModel().checkAll();

		var listAnno = this.fxListAnno.getItems();
		listAnno.clear();
		listAnno.addAll(entries);
		this.fxListAnno.getCheckModel().checkAll();

		var panePaths = paneImage.getChildren();
		for(var render : listAnnoRender)
		{
			panePaths.remove(render.text);
			panePaths.removeAll(render.listPath);
		}

		for(var _anno : listAnno)
		{
			var render = new AnnoRender();
			render.annoId = _anno.anno.getId();
			render.anno = _anno.anno;
			render.cate = _anno.cate;
			var listShape = new ArrayList<Path>();
			var segs = _anno.anno.getSegmentation();
			var text = new Text(_anno.toString());
			text.setStroke(Color.DARKORANGE);
			double startX = 0, startY = 0;
			for(int stepSeg = 0; stepSeg < segs.size(); stepSeg++)
			{
				var seg = segs.get(stepSeg);
				var path = new Path();
				path.setFillRule(FillRule.EVEN_ODD);
				path.setFill(Color.TRANSPARENT);
				path.setStroke(Color.FUCHSIA);
				path.setStrokeWidth(1.5);
				path.setStrokeType(StrokeType.OUTSIDE);
				var pathElements = path.getElements();

				final int countPt = seg.size();
				for(int stepPt = 0; stepPt < countPt / 2; stepPt++)
				{
					var x = seg.get(stepPt * 2).doubleValue() + PADDING_X;
					var y = seg.get(stepPt * 2 + 1).doubleValue() + PADDING_Y;
					if(stepPt == 0)
					{
						var ele = new MoveTo(x, y);
						pathElements.add(ele);
						startX = x;
						startY = y;
					}
					else
					{
						var ele = new LineTo(x, y);
						pathElements.add(ele);
					}
				}
				pathElements.add(new LineTo(startX, startY));
				listShape.add(path);
			}
			text.setLayoutX(startX);
			text.setLayoutY(startY);
			render.listPath = listShape;
			render.text = text;
			panePaths.addAll(listShape);
			panePaths.add(text);
			listAnnoRender.add(render);
		}
	}

	Background BACKGROUND = new Background(
			new BackgroundFill(
					Color.rgb(222, 226, 227),
					null,
					null
			)
	);

	record RefImage(CocoData.Image img) {
		@Override
		public String toString()
		{
			return img.getFilename();
		}
	}
	record RefCate(CocoData.Category cate) {
		@Override
		public String toString()
		{
			return cate.getSuperCategory() + " - " + cate.getName();
		}
	}
	record RefAnno(CocoData.Annotation anno, CocoData.Category cate) {
		@Override
		public String toString()
		{
			return "%d (%s - %s)".formatted(anno.getId(), cate.getSuperCategory(), cate.getName());
		}
	}

	@FXML
	void clearHistory()
	{
		Configs.list.clear();
	}
	@FXML
	void showAbout()
	{
		;
	}
	@FXML
	void showGithub()
	{
		;
	}
}
